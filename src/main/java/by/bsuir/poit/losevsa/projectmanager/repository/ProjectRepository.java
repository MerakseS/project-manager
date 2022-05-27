package by.bsuir.poit.losevsa.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import by.bsuir.poit.losevsa.projectmanager.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}