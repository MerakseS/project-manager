package by.bsuir.poit.losevsa.projectmanager.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class ProjectDto implements Serializable {

    private long id;

    @NotBlank(message = "Название должно быть заполнено")
    @Length(message = "Длина от 2 до 50 символов", min = 2, max = 50)
    @NotNull(message = "Название должно быть заполнено")
    private String name;

    @Length(message = "Длина не больше 255 символов.", max = 255)
    private String description;

    private List<Long> participantsId;

    public ProjectDto() {
    }

    public ProjectDto(long id, String name, String description, List<Long> participantsId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.participantsId = participantsId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public List<Long> getParticipantsId() {
        return participantsId;
    }

    public void setParticipantsId(List<Long> participantsId) {
        this.participantsId = participantsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectDto that = (ProjectDto) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(participantsId, that.participantsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, participantsId);
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", participantsId=" + participantsId +
            '}';
    }
}
