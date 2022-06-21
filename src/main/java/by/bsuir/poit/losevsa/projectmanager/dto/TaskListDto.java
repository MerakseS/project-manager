package by.bsuir.poit.losevsa.projectmanager.dto;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class TaskListDto implements Serializable {

    private Long id;

    @Length(message = "Длина названия списка задач от 2 до 30", min = 2, max = 30)
    @NotNull(message = "Название списка задач должно быть заполнено")
    @NotBlank(message = "Название списка задач должно быть заполнено")
    private String name;

    private Long projectId;

    public TaskListDto(Long id, String name, Long projectId) {
        this.id = id;
        this.name = name;
        this.projectId = projectId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TaskListDto entity = (TaskListDto) o;
        return Objects.equals(this.id, entity.id) &&
            Objects.equals(this.name, entity.name) &&
            Objects.equals(this.projectId, entity.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, projectId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "id = " + id + ", " +
            "name = " + name + ", " +
            "projectId = " + projectId + ")";
    }
}
