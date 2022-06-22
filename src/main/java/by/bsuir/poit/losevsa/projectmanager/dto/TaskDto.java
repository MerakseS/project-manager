package by.bsuir.poit.losevsa.projectmanager.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

public class TaskDto implements Serializable {

    private Long id;

    @NotBlank(message = "Название должно быть заполнено")
    @Length(message = "Длина от 2 до 30 символов", min = 2, max = 30)
    @NotNull(message = "Название должно быть заполнено")
    private String name;

    @Length(message = "Длина не больше 255 символов.", max = 255)
    private String description;

    private Long taskListId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private Long employeeId;

    public TaskDto(Long id, String name, String description, Long taskListId, LocalDate startDate, LocalDate endDate, Long employeeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskListId = taskListId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employeeId = employeeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(Long taskListId) {
        this.taskListId = taskListId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskDto entity = (TaskDto) o;
        return Objects.equals(this.id, entity.id) &&
            Objects.equals(this.name, entity.name) &&
            Objects.equals(this.description, entity.description) &&
            Objects.equals(this.taskListId, entity.taskListId) &&
            Objects.equals(this.startDate, entity.startDate) &&
            Objects.equals(this.endDate, entity.endDate) &&
            Objects.equals(this.employeeId, entity.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskListId, startDate, endDate, employeeId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "id = " + id + ", " +
            "name = " + name + ", " +
            "description = " + description + ", " +
            "taskListId = " + taskListId + ", " +
            "startDate = " + startDate + ", " +
            "endDate = " + endDate + ", " +
            "employeeId = " + employeeId + ")";
    }
}
