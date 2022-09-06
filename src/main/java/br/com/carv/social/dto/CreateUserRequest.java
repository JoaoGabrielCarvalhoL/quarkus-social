package br.com.carv.social.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateUserRequest {

    @NotBlank(message = "Name is required!")
    private String name;
    @NotNull(message = "Age is required")
    private Integer age;

    public CreateUserRequest() {

    }
    public CreateUserRequest(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
}
