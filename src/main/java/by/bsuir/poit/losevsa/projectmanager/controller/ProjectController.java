package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import static java.lang.String.format;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.dto.ProjectDto;
import by.bsuir.poit.losevsa.projectmanager.dto.TaskListDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectCreatorException;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectParticipantException;
import by.bsuir.poit.losevsa.projectmanager.mapper.Mapper;
import by.bsuir.poit.losevsa.projectmanager.mapper.ProjectMapper;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;

@Controller
@RequestMapping("/project")
@PreAuthorize("isAuthenticated()")
public class ProjectController {

    public static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

    private static final String ID_PATH_VARIABLE_NAME = "id";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";
    private static final String PROJECT_ATTRIBUTE_NAME = "project";
    private static final String PROJECT_DTO_ATTRIBUTE_NAME = "projectDto";
    private static final String PROJECT_LIST_ATTRIBUTE_NAME = "projectList";
    private static final String EMPLOYEE_LIST_ATTRIBUTE_NAME = "employeeList";

    private static final String USER_PROJECT_LIST_PATH = "project/userProjectList";
    private static final String ADMIN_PROJECT_LIST_PATH = "project/adminProjectList";
    private static final String NEW_PROJECT_PAGE_PATH = "project/newProject";
    private static final String PROJECT_PAGE_PATH = "project/project";
    private static final String PROJECT_DETAILS_PATH = "project/projectDetails";
    private static final String EDIT_PROJECT_PAGE_PATH = "project/editProject";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";
    private static final String FORBIDDEN_PAGE_PATH = "pageForbidden";

    private static final String PROJECT_LIST_REDIRECT = "redirect:/";
    private static final String PROJECT_DETAILS_REDIRECT = "redirect:/project/%d/details";

    private final ProjectService projectService;
    private final EmployeeService employeeService;

    private final Mapper<Project, ProjectDto> projectMapper;

    public ProjectController(ProjectService projectService, EmployeeService employeeService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.employeeService = employeeService;
        this.projectMapper = projectMapper;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAdminProjectList(Model model) {
        List<Project> projectList = projectService.getAll();
        model.addAttribute(PROJECT_LIST_ATTRIBUTE_NAME, projectList);
        return ADMIN_PROJECT_LIST_PATH;
    }

    @GetMapping
    public String showProjectList(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Employee employee = employeeService.getByLogin(userDetails.getUsername());
        List<Project> projectList = employee.getProjects();
        model.addAttribute(PROJECT_LIST_ATTRIBUTE_NAME, projectList);
        return USER_PROJECT_LIST_PATH;
    }

    @GetMapping("/new")
    public String showNewProjectPage(Authentication authentication,
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) ProjectDto project,
        Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Employee> employeeList = employeeService.getParticipantsList(userDetails.getUsername());
        model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
        return NEW_PROJECT_PAGE_PATH;
    }

    @PostMapping
    public String createProject(Authentication authentication,
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) @Valid ProjectDto projectDto,
        BindingResult bindingResult, Model model) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (bindingResult.hasErrors()) {
            LOG.warn(format("Can't save project cause: %s", bindingResult));
            List<Employee> employeeList = employeeService.getParticipantsList(userDetails.getUsername());
            model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
            return NEW_PROJECT_PAGE_PATH;
        }

        Project project = projectMapper.toEntity(projectDto);
        project.setCreator(employeeService.getByLogin(userDetails.getUsername()));
        projectService.create(project);

        return PROJECT_LIST_REDIRECT;
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        @ModelAttribute("taskListDto") TaskListDto taskListDto,
        Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(id, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            return PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show project with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @GetMapping("/{id}/details")
    public String showProjectDetails(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(id, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            return PROJECT_DETAILS_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show details of project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show details of project with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditProjectPage(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Employee employee = employeeService.getByLogin(userDetails.getUsername());
            Project project = projectService.get(id);
            if (!Objects.equals(employee.getId(), project.getCreator().getId())) {
                throw new NotAProjectCreatorException(format("User %s is not a creator of project with id %d",
                    userDetails.getUsername(), id));
            }

            ProjectDto projectDto = projectMapper.toDto(project);
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            model.addAttribute(PROJECT_DTO_ATTRIBUTE_NAME, projectDto);

            List<Employee> employeeList = employeeService.getParticipantsList(userDetails.getUsername());
            model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);

            return EDIT_PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show edit page of project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectCreatorException e) {
            return handleException("Can't show edit page of project with id %d", id, e,
                "Вы не являетесь создателем данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @PutMapping("/{id}")
    public String updateProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        Authentication authentication,
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) @Valid ProjectDto projectDto,
        BindingResult bindingResult, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update project cause: %s", bindingResult));
                List<Employee> employeeList = employeeService.getParticipantsList(userDetails.getUsername());
                model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
                return EDIT_PROJECT_PAGE_PATH;
            }

            Project project = projectMapper.toEntity(projectDto);
            project.setCreator(employeeService.getByLogin(userDetails.getUsername()));
            projectService.update(id, project);

            return format(PROJECT_DETAILS_REDIRECT, id);
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show edit page of project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
    }

    @DeleteMapping("{id}")
    public String deleteProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Employee employee = employeeService.getByLogin(userDetails.getUsername());
            Project project = projectService.get(id);
            if (!Objects.equals(employee.getId(), project.getCreator().getId())) {
                throw new NotAProjectCreatorException(format("User %s is not a creator of project with id %d",
                    userDetails.getUsername(), id));
            }

            projectService.delete(id);
            return PROJECT_LIST_REDIRECT;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't delete project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectCreatorException e) {
            return handleException("Can't delete project with id %d", id, e,
                "Вы не являетесь создателем данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @DeleteMapping("/{id}/exit")
    public String exitProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.get(id);
            Employee employee = null;
            for (Employee participant : project.getParticipants()) {
                if (participant.getLogin().equals(userDetails.getUsername())) {
                    employee = participant;
                }
            }
            if (employee == null) {
                throw new NotAProjectParticipantException(format("User %s is not a participant of project with id %d",
                    userDetails.getUsername(), id));
            }

            projectService.deleteParticipant(project, employee);
            return PROJECT_LIST_REDIRECT;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't remove participant from project with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't remove participant from project with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    private String handleException(String logMessage, long id, Exception exception,
        String clientMessage, String pagePath, Model model) {

        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format(clientMessage, id));
        return pagePath;
    }
}
