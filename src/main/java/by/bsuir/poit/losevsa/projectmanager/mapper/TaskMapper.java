package by.bsuir.poit.losevsa.projectmanager.mapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import by.bsuir.poit.losevsa.projectmanager.dto.TaskDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskStatus;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskListService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskStatusService;

@Component
public class TaskMapper implements Mapper<Task, TaskDto> {

    private final ModelMapper modelMapper;
    private final TaskListService taskListService;
    private final TaskStatusService taskStatusService;
    private final EmployeeService employeeService;

    public TaskMapper(ModelMapper modelMapper, TaskListService taskListService, TaskStatusService taskStatusService, EmployeeService employeeService) {
        this.modelMapper = modelMapper;
        this.taskListService = taskListService;
        this.taskStatusService = taskStatusService;
        this.employeeService = employeeService;
    }

    @Override
    public Task toEntity(TaskDto taskDto) {
        Task task = modelMapper.map(taskDto, Task.class);
        task.setTaskList(taskListService.get(taskDto.getTaskListId()));

        if (taskDto.getEmployeeId() != null) {
            task.setEmployee(employeeService.get(taskDto.getEmployeeId()));
        }

        Set<TaskStatus> taskStatuses = new LinkedHashSet<>();
        for (long taskStatusId : taskDto.getTaskStatusesId()) {
            taskStatuses.add(taskStatusService.get(taskStatusId));
        }
        task.setTaskStatuses(taskStatuses);

        return task;
    }

    @Override
    public TaskDto toDto(Task task) {
        TaskDto taskDto = modelMapper.map(task, TaskDto.class);
        taskDto.setTaskListId(task.getTaskList().getId());

        if (task.getEmployee() != null) {
            taskDto.setEmployeeId(task.getEmployee().getId());
        }

        List<Long> taskStatusesId = new ArrayList<>();
        for (TaskStatus taskStatus : task.getTaskStatuses()) {
            taskStatusesId.add(taskStatus.getId());
        }
        taskDto.setTaskStatusesId(taskStatusesId);

        return taskDto;
    }
}
