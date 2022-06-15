package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;

@Repository
public interface TaskListRepository extends CrudRepository<TaskList, Long> {

}
