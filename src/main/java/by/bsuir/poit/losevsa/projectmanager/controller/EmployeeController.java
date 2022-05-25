package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Controller
@RequestMapping(("/employee"))
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private static final String EMPLOYEE_LIST_PAGE_PATH = "employee/employeeList";
    private static final String EMPLOYEE_PAGE_PATH = "employee/employee";
    private static final String EMPLOYEE_EDIT_PAGE_PATH = "employee/editEmployee";

    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String PROFILE_REDIRECT = "redirect:/profile";

    private static final String EMPLOYEE_ATTRIBUTE_NAME = "employee";
    private static final String EMPLOYEE_LIST_ATTRIBUTE_NAME = "employeeList";

    private final EmployeeService employeeService;

    private final ModelMapper modelMapper;

    public EmployeeController(EmployeeService employeeService, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public String showEmployeeListPage(Model model) {
        List<Employee> employeeList = employeeService.getAll();
        model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
        return EMPLOYEE_LIST_PAGE_PATH;
    }
}
