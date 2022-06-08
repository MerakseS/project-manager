package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.NoSuchElementException;
import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskService;

@Controller
@RequestMapping("/task")
@PreAuthorize("isAuthenticated()")
public class TaskController {

    public static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    private static final String ID_PATH_VARIABLE_NAME = "id";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";
    private static final String PROJECT_ATTRIBUTE_NAME = "task";
    private static final String PROJECT_LIST_ATTRIBUTE_NAME = "taskList";

    private static final String USER_PROJECT_LIST_PATH = "task/userTaskList";
    private static final String ADMIN_PROJECT_LIST_PATH = "task/adminTaskList";
    private static final String NEW_PROJECT_PAGE_PATH = "task/newTask";
    private static final String PROJECT_PAGE_PATH = "task/task";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";

    private static final String PROJECT_REDIRECT = "redirect:/task";

    private final ModelMapper modelMapper;
    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public TaskController(ModelMapper modelMapper, TaskService taskService, EmployeeService employeeService, ProjectService projectService) {
        this.modelMapper = modelMapper;
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAdminTaskList(Model model) {
        return ADMIN_PROJECT_LIST_PATH;
    }

    @GetMapping
    public String showTaskList(Authentication authentication, Model model) {
        return USER_PROJECT_LIST_PATH;
    }

    @GetMapping("/new")
    public String showNewTaskPage(@ModelAttribute("taskDto") Task task,
        Model model) {

        return NEW_PROJECT_PAGE_PATH;
    }

    @PostMapping
    public String createTask(Authentication authentication,
        @ModelAttribute("taskDto") @Valid Task taskDto,
        BindingResult bindingResult) {

        return PROJECT_REDIRECT;
    }

    @GetMapping("/{id}")
    public String showTask(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {

            return PROJECT_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show task with id %d", id, e, model);
        }
    }

    private String handleNoSuchElementException(String logMessage, long id, NoSuchElementException exception, Model model) {
        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format("Проекта с id %d не существует :(", id));
        return NOT_FOUND_PAGE_PATH;
    }
}
