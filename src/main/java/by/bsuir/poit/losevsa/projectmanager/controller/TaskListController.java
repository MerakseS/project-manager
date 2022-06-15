package by.bsuir.poit.losevsa.projectmanager.controller;

import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    private static final String TASK_LIST_DTO_ATTRIBUTE_NAME = "taskListDto";

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
    public String createProjectTask(@ModelAttribute(TASK_LIST_DTO_ATTRIBUTE_NAME) @Valid TaskListDto taskListDto,
        BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            LOG.warn(format("Can't create task list cause: %s", bindingResult));
            return format(PROJECT_REDIRECT, taskListDto.getProjectId());
        }

        TaskList taskList = convertToTaskList(taskListDto);
        taskListService.create(taskList);

        return format(PROJECT_REDIRECT, taskListDto.getProjectId());
    }

    private TaskList convertToTaskList(TaskListDto taskListDto) {
        TaskList taskList = modelMapper.map(taskListDto, TaskList.class);
        Project project = projectService.get(taskListDto.getProjectId());
        taskList.setProject(project);

        return taskList;
    }
}
