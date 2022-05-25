package by.bsuir.poit.losevsa.projectmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Controller
@RequestMapping(("/profile"))
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private static final String EMPLOYEE_PAGE_PATH = "employee/employee";
    private static final String EMPLOYEE_EDIT_PAGE_PATH = "employee/editEmployee";

    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String PROFILE_REDIRECT = "redirect:/profile";

    private static final String EMPLOYEE_ATTRIBUTE_NAME = "employee";

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public String getUserProfilePage(Authentication authentication, Model model) {
        if (authentication == null) {
            return LOGIN_REDIRECT;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Employee employee = employeeService.getByLogin(userDetails.getUsername());
        model.addAttribute(EMPLOYEE_ATTRIBUTE_NAME, employee);

        return EMPLOYEE_PAGE_PATH;
    }
}
