package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.repository.ProjectRepository;
import by.bsuir.poit.losevsa.projectmanager.repository.TaskRepository;
import by.bsuir.poit.losevsa.projectmanager.service.TaskService;

@Service
public class DefaultTaskService implements TaskService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultProjectService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public DefaultTaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }


    @Override
    public void create(Task task) {

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
