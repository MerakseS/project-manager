package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import by.bsuir.poit.losevsa.projectmanager.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}
