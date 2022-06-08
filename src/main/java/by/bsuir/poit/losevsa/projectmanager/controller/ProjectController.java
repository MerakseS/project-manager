package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.dto.CreateProjectDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
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
    private static final String PROJECT_LIST_ATTRIBUTE_NAME = "projectList";

    private static final String USER_PROJECT_LIST_PATH = "project/userProjectList";
    private static final String ADMIN_PROJECT_LIST_PATH = "project/adminProjectList";
    private static final String NEW_PROJECT_PAGE_PATH = "project/newProject";
    private static final String PROJECT_PAGE_PATH = "project/project";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";

    private static final String PROJECT_REDIRECT = "redirect:/project";

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
    public String showNewProjectPage(@ModelAttribute("projectDto") CreateProjectDto project,
        Model model) {
        List<Employee> employeeList = employeeService.getAll();
        model.addAttribute("employeeList", employeeList);
        return NEW_PROJECT_PAGE_PATH;
    }

    @PostMapping
    public String createProject(Authentication authentication,
        @ModelAttribute("projectDto") @Valid CreateProjectDto projectDto,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            LOG.warn(format("Can't save project cause: %s", bindingResult));
            return NEW_PROJECT_PAGE_PATH;
        }

        Project project = modelMapper.map(projectDto, Project.class);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        project.setCreator(employeeService.getByLogin(userDetails.getUsername()));

        List<Employee> participants = new ArrayList<>();
        for (long participantsId : projectDto.getParticipantsId()) {
            participants.add(employeeService.get(participantsId));
        }
        project.setParticipants(participants);

        projectService.create(project);

        return PROJECT_REDIRECT;
    }

    @GetMapping("/{id}")
    public String showProject(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Employee employee = employeeService.getByLogin(userDetails.getUsername());
            Project project = null;
            for (Project employeeProject : employee.getProjects()) {
                if (employeeProject.getId() == id) {
                    project = employeeProject;
                    break;
                }
            }

            if (project == null) {
                throw new NoSuchElementException(format("Employee with login %s doesn't have project with id %d.",
                    userDetails.getUsername(), id));
            }

            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            return PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show project with id %d", id, e, model);
        }
    }

    private String handleNoSuchElementException(String logMessage, long id, NoSuchElementException exception, Model model) {
        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format("Проекта с id %d не существует :(", id));
        return NOT_FOUND_PAGE_PATH;
    }
}