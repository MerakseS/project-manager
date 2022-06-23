package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.NoSuchElementException;
import java.util.Set;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskStatus;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectParticipantException;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskStatusService;

@Controller
@RequestMapping("/project/{projectId}/taskStatus")
public class TaskStatusController {

    public static final Logger LOG = LoggerFactory.getLogger(TaskStatusController.class);

    private static final String TASK_STATUS_ATTRIBUTE_NAME = "taskStatus";
    private static final String TASK_STATUS_LIST_ATTRIBUTE_NAME = "taskStatusList";
    private static final String PROJECT_ATTRIBUTE_NAME = "project";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";

    private static final String TASK_STATUS_LIST_PAGE_PATH = "taskStatus/taskStatusList";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";
    private static final String FORBIDDEN_PAGE_PATH = "pageForbidden";

    private static final String TASK_STATUS_LIST_REDIRECT = "redirect:/project/%d/taskStatus";

    private final ProjectService projectService;
    private final TaskStatusService taskStatusService;

    public TaskStatusController(ProjectService projectService, TaskStatusService taskStatusService) {
        this.projectService = projectService;
        this.taskStatusService = taskStatusService;
    }

    @GetMapping
    public String getProjectTaskStatusList(@PathVariable long projectId,
        @ModelAttribute(TASK_STATUS_ATTRIBUTE_NAME) TaskStatus taskStatus,
        Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());
            model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);

            Set<TaskStatus> taskStatusList = project.getTaskStatuses();
            model.addAttribute(TASK_STATUS_LIST_ATTRIBUTE_NAME, taskStatusList);

            return TASK_STATUS_LIST_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleException("Can't show task status list of project %d page", projectId, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't show task status list of project %d page", projectId, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @PostMapping
    public String createTaskStatus(@PathVariable long projectId,
        @ModelAttribute(TASK_STATUS_ATTRIBUTE_NAME) TaskStatus taskStatus,
        BindingResult bindingResult, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());

            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't save task status cause: %s", bindingResult));
                model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
                return format(TASK_STATUS_LIST_REDIRECT, projectId);
            }

            taskStatus.setProject(project);
            taskStatusService.create(taskStatus);

            return format(TASK_STATUS_LIST_REDIRECT, projectId);
        }
        catch (NoSuchElementException e) {
            return handleException("Can't create task status", projectId, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't create task status", projectId, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @PutMapping("/{id}")
    public String updateTaskStatus(@PathVariable long projectId, @PathVariable long id,
        @ModelAttribute(TASK_STATUS_ATTRIBUTE_NAME) TaskStatus taskStatus,
        BindingResult bindingResult, Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());

            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update task status cause: %s", bindingResult));
                model.addAttribute(PROJECT_ATTRIBUTE_NAME, project);
                return format(TASK_STATUS_LIST_REDIRECT, projectId);
            }

            taskStatusService.update(id, taskStatus);

            return format(TASK_STATUS_LIST_REDIRECT, projectId);
        }
        catch (NoSuchElementException e) {
            return handleException("Can't update task status with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't update task status with id %d", id, e,
                "Вы не являетесь участником данного проекта :(", FORBIDDEN_PAGE_PATH, model);
        }
    }

    @DeleteMapping("/{id}")
    public String deleteTaskStatus(@PathVariable long projectId, @PathVariable long id,
        Authentication authentication, Model model) {
        try {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Project project = projectService.getByEmployeeLogin(projectId, userDetails.getUsername());

            taskStatusService.delete(id);

            return format(TASK_STATUS_LIST_REDIRECT, project.getId());
        }
        catch (NoSuchElementException e) {
            return handleException("Can't delete task status with id %d", id, e,
                "Проекта с id %d не существует :(", NOT_FOUND_PAGE_PATH, model);
        }
        catch (NotAProjectParticipantException e) {
            return handleException("Can't delete task status with id %d", id, e,
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
