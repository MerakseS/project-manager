package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.NoSuchElementException;
import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import by.bsuir.poit.losevsa.projectmanager.dto.TaskListDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskListService;

@Controller
@RequestMapping("/taskList")
@PreAuthorize("isAuthenticated()")
public class TaskListController {

    public static final Logger LOG = LoggerFactory.getLogger(TaskListController.class);

    private static final String ID_PATH_VARIABLE_NAME = "id";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";

    private static final String TASK_LIST_DTO_ATTRIBUTE_NAME = "taskListDto";

    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";

    private static final String PROJECT_REDIRECT = "redirect:/project/%d";

    private final ModelMapper modelMapper;
    private final ProjectService projectService;
    private final TaskListService taskListService;

    public TaskListController(ModelMapper modelMapper, ProjectService projectService, TaskListService taskListService) {
        this.modelMapper = modelMapper;
        this.projectService = projectService;
        this.taskListService = taskListService;
    }

    @PostMapping
    public String createProjectTaskList(@ModelAttribute(TASK_LIST_DTO_ATTRIBUTE_NAME) @Valid TaskListDto taskListDto,
        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            LOG.warn(format("Can't create task list cause: %s", bindingResult));
            return format(PROJECT_REDIRECT, taskListDto.getProjectId());
        }

        TaskList taskList = convertToTaskList(taskListDto);
        taskListService.create(taskList);

        return format(PROJECT_REDIRECT, taskListDto.getProjectId());
    }

    @PutMapping("/{id}")
    String updateProjectTaskList(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        @ModelAttribute(TASK_LIST_DTO_ATTRIBUTE_NAME) @Valid TaskListDto taskListDto,
        BindingResult bindingResult, Model model) {
        try {
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't create task list cause: %s", bindingResult));
                return format(PROJECT_REDIRECT, taskListDto.getProjectId());
            }

            TaskList taskList = convertToTaskList(taskListDto);
            taskListService.update(id, taskList);

            return format(PROJECT_REDIRECT, taskListDto.getProjectId());
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't update taskList with id %d", id, e, model);
        }
    }

    @DeleteMapping("/{id}")
    public String deleteTaskList(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        @RequestParam("projectId") long projectId,
        Model model) {
        try {
            taskListService.delete(id);
            return format(PROJECT_REDIRECT, projectId);
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't delete taskList with id %d", id, e, model);
        }
    }

    private String handleNoSuchElementException(String logMessage, long id, NoSuchElementException exception, Model model) {
        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format("Списка задач с id %d не существует :(", id));
        return NOT_FOUND_PAGE_PATH;
    }

    private TaskList convertToTaskList(TaskListDto taskListDto) {
        TaskList taskList = modelMapper.map(taskListDto, TaskList.class);
        Project project = projectService.get(taskListDto.getProjectId());
        taskList.setProject(project);

        return taskList;
    }
}
