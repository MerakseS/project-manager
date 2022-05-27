package by.bsuir.poit.losevsa.projectmanager.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class CreateProjectDto implements Serializable {

    @NotBlank(message = "Название должно быть заполнено")
    @Length(message = "Длина от 2 до 50 символов", min = 2, max = 50)
    @NotNull(message = "Название должно быть заполнено")
    private String name;
    private String description;
    private Long creatorId;
    private List<Long> participantsId;

    public CreateProjectDto() {}

    public CreateProjectDto(String name, String description, Long creatorId, List<Long> participantsId) {
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.participantsId = participantsId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
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
        CreateProjectDto entity = (CreateProjectDto) o;
        return Objects.equals(this.name, entity.name) &&
            Objects.equals(this.description, entity.description) &&
            Objects.equals(this.creatorId, entity.creatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, creatorId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
            "name = " + name + ", " +
            "description = " + description + ", " +
            "creatorId = " + creatorId + ")";
    }
}
