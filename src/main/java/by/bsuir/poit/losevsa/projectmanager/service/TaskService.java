package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;

public interface TaskService {

    void create(Task task);

    List<Task> getAll();

    Task get(long id);

    void update(long id, Task task);

    void delete(long id);
}
