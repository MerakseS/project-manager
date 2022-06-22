package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.List;
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
        if (task.getStartDate() != null && task.getEndDate() != null &&
            task.getStartDate().isAfter(task.getEndDate())) {
            throw new StartDateLaterThanEndDateException(
                format("Start date is later than end. Start date: %s; End date: %s",
                    task.getStartDate(), task.getEndDate()));
        }

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
        return null;
    }

    @Override
    public void update(long id, Task task) {

    }

    @Override
    public void delete(long id) {

    }
}
