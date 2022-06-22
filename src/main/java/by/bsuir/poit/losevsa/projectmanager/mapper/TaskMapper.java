package by.bsuir.poit.losevsa.projectmanager.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import by.bsuir.poit.losevsa.projectmanager.dto.TaskDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.TaskListService;

@Component
public class TaskMapper implements Mapper<Task, TaskDto> {

    private final ModelMapper modelMapper;
    private final TaskListService taskListService;
    private final EmployeeService employeeService;

    public TaskMapper(ModelMapper modelMapper, TaskListService taskListService, EmployeeService employeeService) {
        this.modelMapper = modelMapper;
        this.taskListService = taskListService;
        this.employeeService = employeeService;
    }

    @Override
    public Task toEntity(TaskDto taskDto) {
        Task task = modelMapper.map(taskDto, Task.class);
        task.setTaskList(taskListService.get(taskDto.getTaskListId()));

        if (taskDto.getEmployeeId() != null) {
            task.setEmployee(employeeService.get(taskDto.getEmployeeId()));
        }

        return task;
    }

    @Override
    public TaskDto toDto(Task task) {
        TaskDto taskDto = modelMapper.map(task, TaskDto.class);
        taskDto.setTaskListId(task.getTaskList().getId());

        if (task.getEmployee() != null) {
            taskDto.setEmployeeId(task.getEmployee().getId());
        }

        return taskDto;
    }
}
