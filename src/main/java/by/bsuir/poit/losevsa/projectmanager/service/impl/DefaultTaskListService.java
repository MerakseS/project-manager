package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.List;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;
import by.bsuir.poit.losevsa.projectmanager.repository.TaskListRepository;
import by.bsuir.poit.losevsa.projectmanager.service.TaskListService;

@Service
public class DefaultTaskListService implements TaskListService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultTaskListService.class);

    private final TaskListRepository taskListRepository;

    public DefaultTaskListService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Override
    @Transactional
    public void create(TaskList taskList) {
        taskList = taskListRepository.save(taskList);
        LOG.info(format("Successfully created taskList with id %d in project with id %d",
            taskList.getId(), taskList.getProject().getId()));
    }

    @Override
    public TaskList get(long id) {
        TaskList taskList = taskListRepository.findById(id).orElseThrow();
        LOG.info(format("Successfully got task list with id %d", id));
        return taskList;
    }

    @Override
    @Transactional
    public void update(long id, TaskList newTaskList) {
        TaskList oldTaskList = taskListRepository.findById(id).orElseThrow();
        oldTaskList.setName(newTaskList.getName());

        taskListRepository.save(oldTaskList);
        LOG.info(format("Successfully updated task list with id %d", id));
    }

    @Override
    @Transactional
    public void delete(long id) {
        TaskList taskList = taskListRepository.getReferenceById(id);
        taskListRepository.delete(taskList);
        LOG.info(format("Successfully deleted task list with id %d", id));
    }
}
