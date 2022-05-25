package by.bsuir.poit.losevsa.projectmanager.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class EmployeeSignInDto {

    @NotBlank(message = "Логин должен быть заполнен.")
    @NotNull(message = "Логин должен быть заполнен.")
    @Length(message = "Длина логина от 4 до 30 символов.", min = 4, max = 30)
    private String login;

    @NotBlank(message = "Пароль должен быть заполнен.")
    @NotNull(message = "Пароль должен быть заполнен.")
    @Length(message = "Длина пароля меньше 4 символов.", min = 4, max = 255)
    private String password;

    private String passwordConfirm;

    @NotBlank(message = "Имя должно быть заполнено.")
    @NotNull(message = "Имя должно быть заполнено.")
    @Length(message = "Длина имени от 2 до 30 символов.", min = 2, max = 30)
    private String firstName;

    @NotBlank(message = "Фамилия должна быть заполнена.")
    @NotNull(message = "Фамилия должна быть заполнена.")
    @Length(message = "Длина фамилии от 2 до 30 символов.", min = 2, max = 30)
    private String surname;

    @Column(name = "patronymic", length = 30)
    private String patronymic;

    @NotBlank(message = "Должность должна быть заполнена.")
    @NotNull(message = "Должность должна быть заполнена.")
    @Length(message = "Длина должности от 2 до 30 символов.", min = 2, max = 30)
    private String position;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
