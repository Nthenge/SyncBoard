package com.eclectics.collaboration.Tool.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegistrationRequestDTO {

    private String firstName;
    private String sirName;
    private String email;
    private String avatarUrl;
    private String password;

}
