package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import by.bsuir.poit.losevsa.projectmanager.entity.Project;

public interface ProjectService {

    void create(Project project);

    List<Project> getAll();

    Project get(long id);

    void update(long id, Project project);

    void delete(long id);
}
