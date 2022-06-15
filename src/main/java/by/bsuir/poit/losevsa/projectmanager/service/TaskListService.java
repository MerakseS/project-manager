package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;

public interface TaskListService {

    void create(TaskList taskList);

    List<TaskList> getAllByProjectId(long projectId);

    TaskList get(long id);

    void update(long id, TaskList task);

    void delete(long id);
}
