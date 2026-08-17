package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.enums.OveralRole;

public class UserLoginResponseDTO {
    private Long id;
    private String email;
    private String token;
    private String refreshToken;
    private String firstName;
    private String sirName;
    private String avatarUrl;
    private OveralRole role;

    public UserLoginResponseDTO() {
    }

    public UserLoginResponseDTO(Long id, String email, String token, String firstName, String refreshToken, OveralRole role) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.firstName = firstName;
        this.refreshToken = refreshToken;
        this.role = role;
    }

    public UserLoginResponseDTO(Long id, String email, String token, String firstName, String sirName, String avatarUrl, String refreshToken, OveralRole role) {
        this.id = id;
        this.email = email;
        this.token = token;
        this.firstName = firstName;
        this.sirName = sirName;
        this.avatarUrl = avatarUrl;
        this.refreshToken = refreshToken;
        this.role = role;
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

    public String getSirName() {
        return sirName;
    }

    public void setSirName(String sirName) {
        this.sirName = sirName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OveralRole getRole() {
        return role;
    }

    public void setRole(OveralRole role) {
        this.role = role;
    }
}