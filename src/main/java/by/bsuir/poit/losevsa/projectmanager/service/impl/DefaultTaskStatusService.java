package by.bsuir.poit.losevsa.projectmanager.service.impl;

import static java.lang.String.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskStatus;
import by.bsuir.poit.losevsa.projectmanager.repository.TaskStatusRepository;
import by.bsuir.poit.losevsa.projectmanager.service.TaskStatusService;

@Service
public class DefaultTaskStatusService implements TaskStatusService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultTaskStatusService.class);

    private final TaskStatusRepository taskStatusRepository;

    public DefaultTaskStatusService(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    @Override
    public void create(TaskStatus taskStatus) {
        taskStatus = taskStatusRepository.save(taskStatus);
        LOG.info(format("Successfully created task status with id %d in project with id %d",
            taskStatus.getId(), taskStatus.getProject().getId()));
    }

    @Override
    public TaskStatus get(long id) {
        return null;
    }

    @Override
    public void update(long id, TaskStatus taskStatus) {

    }

    @Override
    public void delete(long id) {

    }
}
