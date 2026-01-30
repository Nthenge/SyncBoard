package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.*;

public interface UserService {
    UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO user);
    UserLoginResponseDTO userLogin(UserLoginRequestDTO user);
    UserEmailDTO userSendResetPassword(UserEmailDTO user);
    void userUpdatePassword(String token, String newPassword);
    UserRegistrationRequestDTO updateUser(String token, UserRegistrationRequestDTO userDTO);
    void userConfirmAccount(String token);
    void userDeleteAccount(String token);
}
