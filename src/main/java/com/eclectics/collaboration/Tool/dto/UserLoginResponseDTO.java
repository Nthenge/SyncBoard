package com.eclectics.collaboration.Tool.dto;

public class UserLoginResponseDTO {
    private Long id;
    private String email;
    private String token;
    private String firstName;

    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(Long id, String email, String token, String firstName) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.firstName = firstName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
