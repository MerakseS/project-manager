package by.bsuir.poit.losevsa.projectmanager.service.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.entity.Role;
import by.bsuir.poit.losevsa.projectmanager.exception.EmployeeAlreadyExistsException;
import by.bsuir.poit.losevsa.projectmanager.repository.EmployeeRepository;
import by.bsuir.poit.losevsa.projectmanager.repository.RoleRepository;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Service
public class DefaultEmployeeService implements EmployeeService, UserDetailsService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public DefaultEmployeeService(EmployeeRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByLogin(username);
        if (employee == null) {
            throw new UsernameNotFoundException(format("Employee with login %s doesn't exist", username));
        }

        String[] roles = employee.getRoles().stream().map(Role::getName).toArray(value -> new String[employee.getRoles().size()]);

        return User.builder()
            .username(employee.getLogin())
            .password(employee.getPassword())
            .roles(roles)
            .build();
    }

    @Override
    @Transactional
    public void create(Employee employee) {
        Employee oldEmployee = employeeRepository.findByLogin(employee.getLogin());
        if (oldEmployee != null) {
            throw new EmployeeAlreadyExistsException(format("Employee with login %s already exists.", employee.getLogin()));
        }

        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        if (employee.getPatronymic() != null && employee.getPatronymic().isBlank()) {
            employee.setPatronymic(null);
        }

        Role userRole = roleRepository.getReferenceByName("USER");
        employee.setRoles(Collections.singleton(userRole));

        employee = employeeRepository.save(employee);
        LOG.info(format("Successfully registered user %s with id %d", employee.getLogin(), employee.getId()));
    }

    @Override
    public Employee get(long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow();
        LOG.info(format("Successfully got employee with id %d", id));
        return employee;
    }

    @Override
    public Employee getByLogin(String login) {
        Employee employee = employeeRepository.findByLogin(login);
        if (employee == null) {
            throw new NoSuchElementException(format("Employee with login %s doesn't exist.", login));
        }

        LOG.info(format("Successfully got employee with id %d", employee.getId()));
        return employee;
    }

    @Override
    public List<Employee> getAll() {
        LOG.info("Getting all employees");
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getParticipantsList(String creatorLogin) {
        List<Employee> employeeList = employeeRepository.findAll();
        for (Iterator<Employee> iterator = employeeList.iterator(); iterator.hasNext(); ) {
            Employee employee = iterator.next();
            if (employee.getLogin().equals(creatorLogin)) {
                iterator.remove();
                continue;
            }

            for (Role role : employee.getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    iterator.remove();
                }
            }
        }

        return employeeList;
    }

    @Override
    @Transactional
    public void update(long id, Employee newEmployee) {
        Optional<Employee> oldEmployee = employeeRepository.findById(id);
        if (oldEmployee.isEmpty()) {
            throw new NoSuchElementException(format("Employee with id %d doesn't exist.", id));
        }

        update(oldEmployee.get(), newEmployee);
    }

    @Override
    @Transactional
    public void update(String login, Employee newEmployee) {
        Employee oldEmployee = employeeRepository.findByLogin(login);
        if (newEmployee == null) {
            throw new NoSuchElementException(format("Employee with login %s doesn't exist.", login));
        }

        update(oldEmployee, newEmployee);
    }

    private void update(Employee oldEmployee, Employee newEmployee) {
        oldEmployee.setFirstName(newEmployee.getFirstName());
        oldEmployee.setSurname(newEmployee.getSurname());
        oldEmployee.setPosition(newEmployee.getPosition());
        oldEmployee.setPatronymic(newEmployee.getPatronymic() != null && newEmployee.getPatronymic().isBlank() ?
            null : newEmployee.getPatronymic());

        employeeRepository.save(oldEmployee);
        LOG.info(format("Successfully updated user %s with id %d", oldEmployee.getLogin(), oldEmployee.getId()));
    }

    @Override
    @Transactional
    public void delete(long id) {
        Employee employee = employeeRepository.getReferenceById(id);
        employeeRepository.delete(employee);
        LOG.info(format("Successfully deleted employee with id %d", id));
    }
}
