package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;
import by.bsuir.poit.losevsa.projectmanager.repository.TaskListRepository;

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
    public List<TaskList> getAllByProjectId(long projectId) {
        return null;
    }

    @Override
    public TaskList get(long id) {
        return null;
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
