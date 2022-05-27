package by.bsuir.poit.losevsa.projectmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import by.bsuir.poit.losevsa.projectmanager.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findDistinctProjectByParticipants_LoginContaining(String login);
}