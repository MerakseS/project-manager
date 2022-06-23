package by.bsuir.poit.losevsa.projectmanager.service;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskStatus;

public interface TaskStatusService {

    void create(TaskStatus taskStatus);

    TaskStatus get(long id);

    void update(long id, TaskStatus taskStatus);

    void delete(long id);
}
