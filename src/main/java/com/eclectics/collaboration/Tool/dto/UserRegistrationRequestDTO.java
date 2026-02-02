package com.eclectics.collaboration.Tool.dto;

public class UserRegistrationRequestDTO {

    private String firstName;
    private String sirName;
    private String email;
    private String avatarUrl;
    private String password;

    public UserRegistrationRequestDTO() {
    }

    public UserRegistrationRequestDTO(String firstName,String avatarUrl, String sirName, String email, String password) {
        this.firstName = firstName;
        this.sirName = sirName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
