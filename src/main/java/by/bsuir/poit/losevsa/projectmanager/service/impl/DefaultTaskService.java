package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.exception.StartDateLaterThanEndDateException;
import by.bsuir.poit.losevsa.projectmanager.repository.TaskRepository;
import by.bsuir.poit.losevsa.projectmanager.service.TaskService;

@Service
public class DefaultTaskService implements TaskService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultTaskService.class);

    private final TaskRepository taskRepository;

    public DefaultTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void create(Task task) {
        validateValues(task);

        if (task.getDescription() != null && task.getDescription().isBlank()) {
            task.setDescription(null);
        }

        task = taskRepository.save(task);
        LOG.info(format("Successfully created task with id %d", task.getId()));
    }

    @Override
    public List<Task> getAll() {
        return null;
    }

    @Override
    public Task get(long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        LOG.info(format("Successfully got task with id %d", id));
        return task;
    }

    @Override
    public void update(long id, Task newTask) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) {
            throw new NoSuchElementException(format("Task with id %d doesn't exist.", id));
        }

        validateValues(newTask);

        Task oldTask = optionalTask.get();
        oldTask.setName(newTask.getName());
        oldTask.setDescription(newTask.getDescription() != null && newTask.getDescription().isBlank() ?
            null : newTask.getDescription());
        oldTask.setStartDate(newTask.getStartDate());
        oldTask.setEndDate(newTask.getEndDate());
        oldTask.setTaskList(newTask.getTaskList());
        oldTask.setEmployee(newTask.getEmployee());

        taskRepository.save(oldTask);
        LOG.info(format("Successfully updated task with id %d", oldTask.getId()));
    }

    @Override
    public void delete(long id) {
        Task task = taskRepository.getReferenceById(id);
        taskRepository.delete(task);
        LOG.info(format("Successfully deleted task with id %d", id));
    }

    private void validateValues(Task task) {
        if (task.getStartDate() != null && task.getEndDate() != null &&
            task.getStartDate().isAfter(task.getEndDate())) {
            throw new StartDateLaterThanEndDateException(
                format("Start date is later than end. Start date: %s; End date: %s",
                    task.getStartDate(), task.getEndDate()));
        }
    }
}
