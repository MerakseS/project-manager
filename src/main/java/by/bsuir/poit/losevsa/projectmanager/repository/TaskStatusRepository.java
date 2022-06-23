package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.bsuir.poit.losevsa.projectmanager.entity.TaskStatus;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

}
