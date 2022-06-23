package by.bsuir.poit.losevsa.projectmanager.service;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;

public interface TaskListService {

    void create(TaskList taskList);

    TaskList get(long id);

    void update(long id, TaskList taskList);

    void delete(long id);
}
