package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.dto.CreateProjectDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;

@Controller
@RequestMapping("/project")
public class ProjectController {

    public static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

    private static final String USER_PROJECT_LIST_PATH = "project/userProjectList";
    private static final String ADMIN_PROJECT_LIST_PATH = "project/adminProjectList";
    private static final String NEW_PROJECT_PAGE_PATH = "project/newProject";

    private static final String PROJECT_LIST_ATTRIBUTE_NAME = "projectList";
    private static final String PROJECT_ATTRIBUTE_NAME = "project";

    private static final String LOGIN_REDIRECT = "redirect:/login";
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
        List<Project> projectList = projectService.getListByEmployeeLogin(userDetails.getUsername());
        model.addAttribute(PROJECT_LIST_ATTRIBUTE_NAME, projectList);
        return USER_PROJECT_LIST_PATH;
    }

    @GetMapping("/new")
    public String showNewProjectPage(@ModelAttribute(PROJECT_ATTRIBUTE_NAME) Project project) {
        return NEW_PROJECT_PAGE_PATH;
    }

    @PostMapping
    public String createProject(Authentication authentication,
        @ModelAttribute(PROJECT_ATTRIBUTE_NAME) @Valid CreateProjectDto projectDto,
        BindingResult bindingResult) {

        if (authentication == null) {
            return LOGIN_REDIRECT;
        }

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

}
