package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskList;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {

}
