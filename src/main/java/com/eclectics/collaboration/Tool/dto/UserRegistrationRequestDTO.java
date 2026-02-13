package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRegistrationRequestDTO {

    private String firstName;
    private String sirName;
    private String email;
    private String avatarUrl;
    private String password;

}
