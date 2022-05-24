package by.bsuir.poit.losevsa.projectmanager.service;

import java.util.List;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;

public interface EmployeeService {

    void create(Employee employee);

    Employee get(long id);

    List<Employee> getAll();

    void update(long id, Employee employee);

    void delete(long id);
}
