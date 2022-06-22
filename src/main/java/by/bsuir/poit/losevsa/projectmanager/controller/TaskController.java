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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import by.bsuir.poit.losevsa.projectmanager.dto.TaskDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectCreatorException;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectParticipantException;
import by.bsuir.poit.losevsa.projectmanager.exception.StartDateLaterThanEndDateException;
import by.bsuir.poit.losevsa.projectmanager.mapper.Mapper;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskListService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskService;

@Controller
@RequestMapping("/task")
@PreAuthorize("isAuthenticated()")
public class TaskController {

    public static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    private static final String ID_PATH_VARIABLE_NAME = "id";
    private static final String TASK_ATTRIBUTE_NAME = "task";
    private static final String TASK_DTO_ATTRIBUTE_NAME = "taskDto";
    private static final String TASK_LIST_ATTRIBUTE_NAME = "taskList";
    private static final String PROJECT_ATTRIBUTE_NAME = "project";
    private static final String PARTICIPANTS_ATTRIBUTE_NAME = "participants";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";

    private static final String ADMIN_TASK_LIST_PATH = "task/adminTaskList";
    private static final String NEW_TASK_PAGE_PATH = "task/newTask";
    private static final String TASK_PAGE_PATH = "task/task";
    private static final String EDIT_TASK_PAGE_PATH = "task/editTask";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";
    private static final String FORBIDDEN_PAGE_PATH = "pageForbidden";

    private static final String TASK_REDIRECT = "redirect:/task/%d";
    private static final String PROJECT_REDIRECT = "redirect:/project/%d";

    private final TaskService taskService;
    private final ProjectService projectService;
    private final TaskListService taskListService;

    private final Mapper<Task, TaskDto> taskMapper;

    public TaskController(TaskService taskService, ProjectService projectService, TaskListService taskListService, Mapper<Task, TaskDto> taskMapper) {
        this.taskService = taskService;
        this.projectService = projectService;
        this.taskListService = taskListService;
        this.taskMapper = taskMapper;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showAdminTaskList(Model model) {
        List<Task> taskList = taskService.getAll();
        model.addAttribute(TASK_LIST_ATTRIBUTE_NAME, taskList);
        return ADMIN_TASK_LIST_PATH;
    }

    @GetMapping("/new")
    public String showNewTaskPage(@RequestParam long projectId, @RequestParam long taskListId,
        @ModelAttribute("taskDto") TaskDto taskDto, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());

            TaskList taskList = taskListService.get(taskListId);
            taskDto.setTaskListId(taskList.getId());

            return NEW_TASK_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show new task page", projectId, e,
                "Проекта или списка задач не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show new task page", projectId, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @PostMapping
    public String createTask(Authentication authentication, @RequestParam long projectId,
        @ModelAttribute(TASK_DTO_ATTRIBUTE_NAME) @Valid TaskDto taskDto,
        BindingResult bindingResult, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't save task cause: %s", bindingResult));
                Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
                model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
                model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());
                return NEW_TASK_PAGE_PATH;
            }

            Task task = taskMapper.toEntity(taskDto);
            taskService.create(task);

            return format(PROJECT_REDIRECT, projectId);
        }
        catch (NoSuchElementException e) {
            return handleException("Can't create new task in project with id %d", projectId, e,
                "Проекта или списка задач с  не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't create new task in project with id %d", projectId, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
        catch (StartDateLaterThanEndDateException e) {
            LOG.warn("Can't create new task", e);
            ObjectError error = new FieldError(TASK_DTO_ATTRIBUTE_NAME, "startDate",
                "Дата начала позже даты окончания.");
            bindingResult.addError(error);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());

            return NEW_TASK_PAGE_PATH;
        }
    }

    @GetMapping("/{id}")
    public String showTask(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            Task task = taskService.get(id);
            model.addAttribute(TASK_ATTRIBUTE_NAME, task);

            Project project = projectService.getByEmployeeLogin(task.getTaskList().getProject().getId(),
                userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);

            return TASK_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show task with id %d", id, e,
                "Задачи с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show task with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditTaskPage(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Authentication authentication, Model model) {
        try {
            Task task = taskService.get(id);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(task.getTaskList().getProject().getId(),
                userDetails.getUsername());

            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());

            TaskDto taskDto = taskMapper.toDto(task);
            model.addAttribute(TASK_DTO_ATTRIBUTE_NAME, taskDto);

            return EDIT_TASK_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show edit page of task with id %d", id, e,
                "Задачи с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show edit page of task with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @PutMapping("/{id}")
    public String updateTask(Authentication authentication,
        @PathVariable(ID_PATH_VARIABLE_NAME) long id, @RequestParam long projectId,
        @ModelAttribute(TASK_DTO_ATTRIBUTE_NAME) @Valid TaskDto taskDto,
        BindingResult bindingResult, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update task cause: %s", bindingResult));
                Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
                model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
                model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());
                return EDIT_TASK_PAGE_PATH;
            }

            Task task = taskMapper.toEntity(taskDto);
            taskService.update(id, task);

            return format(TASK_REDIRECT, id);
        }
        catch (NoSuchElementException e) {
            return handleException("Can't update task with id %d", id, e,
                "Задачи с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't update task with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
        catch (StartDateLaterThanEndDateException e) {
            LOG.warn(format("Can't update task with id %d", id), e);
            ObjectError error = new FieldError(TASK_DTO_ATTRIBUTE_NAME, "startDate",
                "Дата начала позже даты окончания.");
            bindingResult.addError(error);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
            model.addAttribute(PARTICIPANTS_ATTRIBUTE_NAME, project.getParticipants());

            return EDIT_TASK_PAGE_PATH;
        }
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        Authentication authentication, Model model) {
        try {
            Task task = taskService.get(id);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(task.getTaskList().getProject().getId(),
                userDetails.getUsername());

            taskService.delete(id);

            return format(PROJECT_REDIRECT, project.getId());
        }
        catch (NoSuchElementException e) {
            return handleException("Can't delete task with id %d", id, e,
                "Задачи с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't delete task with id %d", id, e,
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
