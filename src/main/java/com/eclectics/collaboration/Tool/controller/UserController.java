package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponseDTO> registerUser(
            @RequestPart("user") UserRegistrationRequestDTO requestDTO,
            @RequestPart(value = "avatarUrl", required = false) MultipartFile avatarUrl
    )throws java.io.IOException {
        UserRegistrationResponseDTO response = userService.createUser(requestDTO,avatarUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> loginUser(
            @RequestBody UserLoginRequestDTO loginRequest
    ) {
        UserLoginResponseDTO response = userService.userLogin(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmAccount(
            @RequestParam("token") String token
    ) {
        userService.userConfirmAccount(token);
        return ResponseEntity.ok("Account confirmed successfully!");
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<UserEmailDTO> sendResetPasswordEmail(
            @RequestBody UserEmailDTO userEmailDTO
    ) {
        UserEmailDTO response = userService.userSendResetPassword(userEmailDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword
    ) {
        userService.userUpdatePassword(token, newPassword);
        return ResponseEntity.ok("Password updated successfully!");
    }

    @PutMapping("/update")
    public ResponseEntity<UserRegistrationRequestDTO> updateProfile(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody UserRegistrationRequestDTO userDTO
    ) {
        String token = tokenHeader.replace("Bearer ", "");
        UserRegistrationRequestDTO response = userService.updateUser(token, userDTO);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(
            @RequestHeader("Authorization") String tokenHeader
    ) {
        String token = tokenHeader.replace("Bearer ", "");
        userService.userDeleteAccount(token);
        return ResponseEntity.ok("Account deleted successfully!");
    }
}

