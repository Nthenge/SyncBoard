package com.eclectics.collaboration.Tool.dto;

public class UserLoginResponseDTO {
    private Long id;
    private String email;
    private String token;

    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(Long id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
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
}
