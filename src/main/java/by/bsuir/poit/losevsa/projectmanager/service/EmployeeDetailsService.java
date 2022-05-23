package by.bsuir.poit.losevsa.projectmanager.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.repository.EmployeeRepository;

@Service
public class EmployeeDetailsService implements UserDetailsService {

    private final EmployeeRepository userRepository;

    public EmployeeDetailsService(EmployeeRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = userRepository.findByLogin(username);
        if (employee == null) {
            throw new UsernameNotFoundException(String.format("Employee with login %s doesn't exist", username));
        }

        return User.builder()
            .username(employee.getLogin())
            .password(employee.getPassword())
            .roles(employee.getRole())
            .build();
    }
}
