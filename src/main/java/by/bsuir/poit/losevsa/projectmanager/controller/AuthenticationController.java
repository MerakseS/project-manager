package by.bsuir.poit.losevsa.projectmanager.controller;

import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import by.bsuir.poit.losevsa.projectmanager.dto.EmployeeEditDto;
import by.bsuir.poit.losevsa.projectmanager.dto.EmployeeSignInDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.exception.EmployeeAlreadyExistsException;
import by.bsuir.poit.losevsa.projectmanager.exception.IncorrectPasswordException;
import by.bsuir.poit.losevsa.projectmanager.exception.PasswordsEqualException;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Controller
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private static final String EMPLOYEE_ATTRIBUTE_NAME = "employee";

    private static final String LOGIN_PAGE_PATH = "auth/login";
    private static final String SIGN_UP_PAGE_PATH = "auth/signup";
    private static final String EMPLOYEE_PAGE_PATH = "employee/employee";
    private static final String EDIT_EMPLOYEE_PAGE_PATH = "employee/editEmployee";

    private static final String LOGIN_REDIRECT = "redirect:/login";
    private static final String PROFILE_REDIRECT = "redirect:/profile";

    private final EmployeeService employeeService;

    private final ModelMapper modelMapper;

    public AuthenticationController(EmployeeService employeeService, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE_PATH;
    }

    @GetMapping("/signup")
    public String showSignUpPage(@ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) EmployeeSignInDto employee) {
        return SIGN_UP_PAGE_PATH;
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) @Valid EmployeeSignInDto employeeDto,
        BindingResult bindingResult) {
        try {
            if (!employeeDto.getPassword().equals(employeeDto.getPasswordConfirm())) {
                ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "passwordConfirm", "Пароли не совпадают");
                bindingResult.addError(error);
            }

            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't sign up employee cause: %s", bindingResult));
                return SIGN_UP_PAGE_PATH;
            }

            Employee employee = modelMapper.map(employeeDto, Employee.class);
            employeeService.create(employee);
            return LOGIN_REDIRECT;
        }
        catch (EmployeeAlreadyExistsException e) {
            LOG.warn("Can't register user cause", e);
            ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "login", "Данный логин уже занят");
            bindingResult.addError(error);
            return SIGN_UP_PAGE_PATH;
        }
    }

    @GetMapping("/profile")
    public String getUserProfilePage(Authentication authentication, Model model) {
        if (authentication == null) {
            return LOGIN_REDIRECT;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Employee employee = employeeService.getByLogin(userDetails.getUsername());
        model.addAttribute(EMPLOYEE_ATTRIBUTE_NAME, employee);

        return EMPLOYEE_PAGE_PATH;
    }

    @GetMapping("/profile/edit")
    public String showEditProfilePage(Authentication authentication, Model model) {
        if (authentication == null) {
            return LOGIN_REDIRECT;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Employee employee = employeeService.getByLogin(userDetails.getUsername());
        model.addAttribute(EMPLOYEE_ATTRIBUTE_NAME, employee);

        return EDIT_EMPLOYEE_PAGE_PATH;
    }

    @PutMapping("/profile")
    public String updateEmployee(Authentication authentication,
        @ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) @Valid EmployeeEditDto employeeDto, BindingResult bindingResult) {
        try {
            if (authentication == null) {
                return LOGIN_REDIRECT;
            }

            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update employee cause: %s", bindingResult));
                return EDIT_EMPLOYEE_PAGE_PATH;
            }

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Employee employee = modelMapper.map(employeeDto, Employee.class);
            employeeService.update(userDetails.getUsername(), employee);

            return PROFILE_REDIRECT;
        }
        catch (IncorrectPasswordException e) {
            LOG.warn("Can't update user cause", e);
            ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "oldPassword", "Неправильный пароль");
            bindingResult.addError(error);
            return EDIT_EMPLOYEE_PAGE_PATH;
        }
        catch (PasswordsEqualException e) {
            LOG.warn("Can't update user cause", e);
            ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "passwordConfirm", "Пароли не совпадают");
            bindingResult.addError(error);
            return EMPLOYEE_PAGE_PATH;
        }
    }
}
