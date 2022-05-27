package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    }

    @Override
    public List<Project> getAll() {
        LOG.info("Getting all projects");
        return projectRepository.findAll();
    }

    @Override
    public List<Project> getListByEmployeeLogin(String login) {
        LOG.info(format("Getting projects by login %s", login));
        return projectRepository.findDistinctProjectByParticipants_LoginContaining(login);
    }

    @Override
    public Project get(long id) {
        return null;
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
