package by.bsuir.poit.losevsa.projectmanager.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import by.bsuir.poit.losevsa.projectmanager.dto.TaskListDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;

@Component
public class TaskListMapper implements Mapper<TaskList, TaskListDto> {

    private final ModelMapper modelMapper;
    private final ProjectService projectService;

    public TaskListMapper(ModelMapper modelMapper, ProjectService projectService) {
        this.modelMapper = modelMapper;
        this.projectService = projectService;
    }

    @Override
    public TaskList toEntity(TaskListDto taskListDto) {
        TaskList taskList = modelMapper.map(taskListDto, TaskList.class);
        Project project = projectService.get(taskListDto.getProjectId());
        taskList.setProject(project);

        return taskList;
    }

    @Override
    public TaskListDto toDto(TaskList taskList) {
        TaskListDto taskListDto = modelMapper.map(taskList, TaskListDto.class);
        taskListDto.setProjectId(taskList.getProject().getId());

        return taskListDto;
    }
}
