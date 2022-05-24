package by.bsuir.poit.losevsa.projectmanager.controller;

import static java.lang.String.format;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.exception.EmployeeAlreadyExistsException;
import by.bsuir.poit.losevsa.projectmanager.exception.PasswordsEqualException;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Controller
public class AuthenticationController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    private static final String EMPLOYEE_ATTRIBUTE_NAME = "employee";

    private static final String LOGIN_PAGE_PATH = "auth/login";
    private static final String SIGN_UP_PAGE = "auth/signup";
    private static final String LOGIN_REDIRECT = "redirect:/login";

    private final EmployeeService employeeService;

    public AuthenticationController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return LOGIN_PAGE_PATH;
    }

    @GetMapping("/signup")
    public String showSignUpPage(@ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) Employee employee) {
        return SIGN_UP_PAGE;
    }

    @PostMapping("/signup")
    public String signUp(@ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) @Valid Employee employee,
        BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't sign up employee cause: %s", bindingResult));
                return SIGN_UP_PAGE;
            }

            employeeService.create(employee);
            return LOGIN_REDIRECT;
        }
        catch (PasswordsEqualException e) {
            LOG.warn("Can't register user cause", e);
            ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "passwordConfirm", "Пароли не совпадают");
            bindingResult.addError(error);
            return SIGN_UP_PAGE;
        }
        catch (EmployeeAlreadyExistsException e) {
            LOG.warn("Can't register user cause", e);
            ObjectError error = new FieldError(EMPLOYEE_ATTRIBUTE_NAME, "login", "Данный логин уже занят");
            bindingResult.addError(error);
            return SIGN_UP_PAGE;
        }
    }
}
