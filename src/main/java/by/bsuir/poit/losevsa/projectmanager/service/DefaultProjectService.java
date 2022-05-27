package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.repository.ProjectRepository;

@Service
public class DefaultProjectService implements ProjectService {

    public static final Logger LOG = LoggerFactory.getLogger(DefaultProjectService.class);

    private final ProjectRepository projectRepository;

    public DefaultProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional
    public void create(Project project) {
        project.getParticipants().add(project.getCreator());

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
    @Transactional
    public void update(long id, Project project) {

    }

    @Override
    @Transactional
    public void delete(long id) {

    }
}
