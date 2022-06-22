package by.bsuir.poit.losevsa.projectmanager.controller;

import java.util.List;
import java.util.NoSuchElementException;
import static java.lang.String.format;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import by.bsuir.poit.losevsa.projectmanager.dto.EmployeeEditDto;
import by.bsuir.poit.losevsa.projectmanager.entity.Employee;
import by.bsuir.poit.losevsa.projectmanager.service.EmployeeService;

@Controller
@RequestMapping(("/employee"))
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private static final String ID_PATH_VARIABLE_NAME = "id";
    private static final String ERROR_ATTRIBUTE_NAME = "errorMessage";
    private static final String EMPLOYEE_ATTRIBUTE_NAME = "employee";
    private static final String EMPLOYEE_LIST_ATTRIBUTE_NAME = "employeeList";

    private static final String EMPLOYEE_LIST_PAGE_PATH = "employee/employeeList";
    private static final String EMPLOYEE_PAGE_PATH = "employee/employee";
    private static final String EDIT_EMPLOYEE_PAGE_PATH = "employee/editEmployee";
    private static final String NOT_FOUND_PAGE_PATH = "pageNotFound";

    private static final String EMPLOYEE_LIST_REDIRECT = "redirect:/employee";

    private final EmployeeService employeeService;

    private final ModelMapper modelMapper;

    public EmployeeController(EmployeeService employeeService, ModelMapper modelMapper) {
        this.employeeService = employeeService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEmployeeListPage(Model model) {
        List<Employee> employeeList = employeeService.getAll();
        model.addAttribute(EMPLOYEE_LIST_ATTRIBUTE_NAME, employeeList);
        return EMPLOYEE_LIST_PAGE_PATH;
    }

    @GetMapping("/{id}")
    public String showEmployee(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Model model) {
        try {
            Employee employee = employeeService.get(id);
            model.addAttribute(EMPLOYEE_ATTRIBUTE_NAME, employee);
            return EMPLOYEE_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show employee with id %d", id, e, model);
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String showEditProfilePage(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Model model) {
        try {
            Employee employee = employeeService.get(id);
            model.addAttribute(EMPLOYEE_ATTRIBUTE_NAME, employee);
            return EDIT_EMPLOYEE_PAGE_PATH;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't show edit form of employee with id %d", id, e, model);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateEmployee(@PathVariable(ID_PATH_VARIABLE_NAME) long id,
        @ModelAttribute(EMPLOYEE_ATTRIBUTE_NAME) @Valid EmployeeEditDto employeeDto,
        BindingResult bindingResult,
        Model model) {
        try {
            if (bindingResult.hasErrors()) {
                LOG.warn(format("Can't update employee cause: %s", bindingResult));
                return EDIT_EMPLOYEE_PAGE_PATH;
            }

            Employee employee = modelMapper.map(employeeDto, Employee.class);
            employeeService.update(id, employee);

            return EMPLOYEE_LIST_REDIRECT;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't update employee with id %d", id, e, model);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteEmployee(@PathVariable(ID_PATH_VARIABLE_NAME) long id, Model model) {
        try {
            employeeService.delete(id);
            return EMPLOYEE_LIST_REDIRECT;
        }
        catch (NoSuchElementException e) {
            return handleNoSuchElementException("Can't delete employee with id %d", id, e, model);
        }
    }

    private String handleNoSuchElementException(String logMessage, long id, NoSuchElementException exception, Model model) {
        LOG.warn(format(logMessage, id), exception);
        model.addAttribute(ERROR_ATTRIBUTE_NAME, format("Пользователя с id %d не существует :(", id));
        return NOT_FOUND_PAGE_PATH;
    }
}
