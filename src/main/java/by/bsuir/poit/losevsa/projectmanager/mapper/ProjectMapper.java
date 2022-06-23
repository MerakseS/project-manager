package by.bsuir.poit.losevsa.projectmanager.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import by.bsuir.poit.losevsa.projectmanager.dto.ProjectDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Project;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Component
public class ProjectMapper implements Mapper<Project, ProjectDto> {

    private final ModelMapper modelMapper;
    private final EmployeeService employeeService;

    public ProjectMapper(ModelMapper modelMapper, EmployeeService employeeService) {
        this.modelMapper = modelMapper;
        this.employeeService = employeeService;
    }

    public Project toEntity(ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);

        List<Employee> participants = new ArrayList<>();
        for (long participantsId : projectDto.getParticipantsId()) {
            participants.add(employeeService.get(participantsId));
        }
        project.setParticipants(participants);

        return project;
    }

    public ProjectDto toDto(Project project) {
        ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);

        List<Long> participantsIdList = new ArrayList<>();
        for (Employee employee : project.getParticipants()) {
            participantsIdList.add(employee.getId());
        }
        projectDto.setParticipantsId(participantsIdList);

        return projectDto;
    }
}
