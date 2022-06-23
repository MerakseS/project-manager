package by.bsuir.poit.losevsa.projectmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;
import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    int countAllByTaskList(TaskList taskList);

    List<Task> findByTaskListOrderByPosition(TaskList taskList);
}
