package com.example.user_service.dtos;

public class UserRegistrationRequestDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    public UserRegistrationRequestDto() {

    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }
}
