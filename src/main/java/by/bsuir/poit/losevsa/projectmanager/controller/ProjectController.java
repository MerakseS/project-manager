package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
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

import by.bsuir.poit.losevsa.projectmanager.dto.EditProjectDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.Role;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectCreatorException;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectParticipantException;
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

    private static final String PROJECT_LIST_REDIRECT = "redirect:/project";
    private static final String PROJECT_DETAILS_REDIRECT = "redirect:/project/%d/details";

    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final EmployeeService employeeService;

    public ProjectController(ModelMapper modelMapper, ProjectService projectService, EmployeeService employeeService) {
        this.modelMapper = modelMapper;
        this.projectService = projectService;
        this.employeeService = employeeService;
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
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) EditProjectDto project,
        Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<Employee> employeeList = getParticipantsList(userDetails.getUsername());
        model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
        return NEW_PROJECT_PAGE_PATH;
    }

    @PostMapping
    public String createProject(Authentication authentication,
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) @Valid EditProjectDto projectDto,
        BindingResult bindingResult, Model model) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (bindingResult.hasErrors()) {
            LOG.warn(format("Can't save project cause: %s", bindingResult));
            List<Employee> employeeList = getParticipantsList(userDetails.getUsername());
            model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
            return NEW_PROJECT_PAGE_PATH;
        }

        Project project = convertToProject(projectDto);
        project.setCreator(employeeService.getByLogin(userDetails.getUsername()));
        projectService.create(project);

        return PROJECT_LIST_REDIRECT;
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = getEmployeeProject(id, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            return PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show project with id %d", id, e, model);
        }
    }

    @GetMapping("/{id}/details")
    public String showProjectDetails(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = getEmployeeProject(id, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            return PROJECT_DETAILS_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show details of project with id %d", id, e, model);
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

            EditProjectDto projectDto = convertToProjectDto(project);
            model.addAttribute(PROJECT_DTO_ATTRIBUTE_NAME, projectDto);

            List<Employee> employeeList = getParticipantsList(userDetails.getUsername());
            model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);

            return EDIT_PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show edit page of project with id %d", id, e, model);
        }
        catch (NotAProjectCreatorException e) {
            return handleNotAProjectCreatorException("Can't show edit page of project with id %d", id, e, model);
        }
    }

    @PutMapping("/{id}")
    public String updateProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        Authentication authentication,
        @ModelAttribute(PROJECT_DTO_ATTRIBUTE_NAME) @Valid EditProjectDto projectDto,
        BindingResult bindingResult, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update project cause: %s", bindingResult));
                List<Employee> employeeList = getParticipantsList(userDetails.getUsername());
                model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
                return EDIT_PROJECT_PAGE_PATH;
            }

            Project project = convertToProject(projectDto);
            project.setCreator(employeeService.getByLogin(userDetails.getUsername()));
            projectService.update(id, project);

            return format(PROJECT_DETAILS_REDIRECT, id);
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show edit page of project with id %d", id, e, model);
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
            return handleNoSuchElementException("Can't delete project with id %d", id, e, model);
        }
        catch (NotAProjectCreatorException e) {
            return handleNotAProjectCreatorException("Can't delete project with id %d", id, e, model);
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
        catch (NotAProjectCreatorException e) {
            LOG.warn(format("Can't remove participant from project with id %d", id), e);
            model.addAttribute(ERROR_ATTRIBUTE_NAME, "Вы не являетесь создателем данного проекта :(");
            return FORBIDDEN_PAGE_PATH;
        }
    }

    private List<Employee> getParticipantsList(String creatorLogin) {
        List<Employee> employeeList = employeeService.getAll();
        for (Iterator<Employee> iterator = employeeList.iterator(); iterator.hasNext(); ) {
            Employee employee = iterator.next();
            if (employee.getLogin().equals(creatorLogin)) {
                iterator.remove();
                continue;
            }

            for (Role role : employee.getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    iterator.remove();
                }
            }
        }
        return employeeList;
    }

    private Project getEmployeeProject(long projectId, String login) {
        Employee employee = employeeService.getByLogin(login);
        Project project = null;
        for (Project employeeProject : employee.getProjects()) {
            if (employeeProject.getId() == projectId) {
                project = employeeProject;
                break;
            }
        }
        if (project == null) {
            throw new NoSuchElementException(format("Employee with login %s doesn't have project with id %d.",
                login, projectId));
        }
        return project;
    }

    private String handleNoSuchElementException(String logMessage, long id, NoSuchElementException exception, Model model) {
        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format("Проекта с id %d не существует :(", id));
        return NOT_FOUND_PAGE_PATH;
    }

    private String handleNotAProjectCreatorException(String logMessage, long id, NotAProjectCreatorException e, Model model) {
        LOG.warn(format(logMessage, id), e);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, "Вы не являетесь создателем данного проекта :(");
        return FORBIDDEN_PAGE_PATH;
    }

    private Project convertToProject(EditProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        List<Employee> participants = new ArrayList<>();
        for (long participantsId : projectDto.getParticipantsId()) {
            participants.add(employeeService.get(participantsId));
        }
        project.setParticipants(participants);

        return project;
    }

    private EditProjectDto convertToProjectDto(Project project) {
        EditProjectDto projectDto = modelMapper.map(project, EditProjectDto.class);
        List<Long> participantsIdList = new ArrayList<>();
        for (Employee employee : project.getParticipants()) {
            participantsIdList.add(employee.getId());
        }
        projectDto.setParticipantsId(participantsIdList);

        return projectDto;
    }
}
