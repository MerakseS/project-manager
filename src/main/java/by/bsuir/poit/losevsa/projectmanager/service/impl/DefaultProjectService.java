package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.entity.Role;
import by.bsuir.poit.losevsa.projectmanager.exception.NotAProjectParticipantException;
import by.bsuir.poit.losevsa.projectmanager.repository.ProjectRepository;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;
import by.bsuir.poit.losevsa.projectmanager.service.ProjectService;

@Service
public class DefaultProjectService implements ProjectService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultProjectService.class);

    private final EmployeeService employeeService;

    private final ProjectRepository projectRepository;

    public DefaultProjectService(EmployeeService employeeService, ProjectRepository projectRepository) {
        this.employeeService = employeeService;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public void create(Project project) {
        project.getParticipants().add(project.getCreator());

        if (project.getDescription() != null && project.getDescription().isBlank()) {
            project.setDescription(null);
        }

        project = projectRepository.save(project);
        LOG.info(format("Successfully created project with id %d", project.getId()));
    }

    @Override
    public List<Project> getAll() {
        LOG.info("Getting all projects");
        return projectRepository.findAll();
    }

    @Override
    public Project get(long id) {
        Project project = projectRepository.findById(id).orElseThrow();
        LOG.info(format("Successfully got project with id %d", id));
        return project;
    }

    @Override
    public Project getByEmployeeLogin(long id, String employeeLogin) {
        Employee employee = employeeService.getByLogin(employeeLogin);
        for (Role role : employee.getRoles()) {
            if (role.getName().equals("ADMIN")) {
                return projectRepository.findById(id).orElseThrow();
            }
        }

        Project project = projectRepository.findById(id).orElseThrow();
        for (Project employeeProject : employee.getProjects()) {
            if (Objects.equals(employeeProject.getId(), project.getId())) {
                return project;
            }
        }

        throw new NotAProjectParticipantException(format("Employee with login %s doesn't have project with id %d.",
            employeeLogin, id));

    }

    @Override
    @Transactional
    public void update(long id, Project newProject) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        if (optionalProject.isEmpty()) {
            throw new NoSuchElementException(format("Project with id %d doesn't exist.", id));
        }

        Project oldProject = optionalProject.get();
        oldProject.setName(newProject.getName());
        oldProject.setDescription(newProject.getDescription() != null && newProject.getDescription().isBlank() ?
            null : newProject.getDescription());

        newProject.getParticipants().add(newProject.getCreator());
        oldProject.setParticipants(newProject.getParticipants());

        projectRepository.save(oldProject);
        LOG.info(format("Successfully updated project with id %d", oldProject.getId()));
    }

    @Override
    @Transactional
    public void delete(long id) {
        Project project = projectRepository.getReferenceById(id);
        projectRepository.delete(project);
        LOG.info(format("Successfully deleted project with id %d", id));
    }

    @Override
    public void deleteParticipant(Project project, Employee employee) {
        project.getParticipants().remove(employee);
        projectRepository.save(project);
        LOG.info(format("Successfully deleted participant from project with id %d", project.getId()));
    }
}
