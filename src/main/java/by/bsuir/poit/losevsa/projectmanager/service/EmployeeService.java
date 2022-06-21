package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;

public interface EmployeeService {

    void create(Employee employee);

    Employee get(long id);

    Employee getByLogin(String login);

    List<Employee> getAll();

    List<Employee> getParticipantsList(String creatorLogin);

    void update(long id, Employee employee);

    void update(String login, Employee employee);

    void delete(long id);
}
