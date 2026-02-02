package com.eclectics.collaboration.Tool.dto;

public class UserRegistrationResponseDTO {
    private String firstName;
    private String token;

    // Constructor
    public UserRegistrationResponseDTO(String firstName, String token) {
        this.firstName = firstName;
        this.token = token;
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
