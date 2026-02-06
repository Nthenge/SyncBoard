package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private HttpServletRequest request;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(
            @RequestPart("user") UserRegistrationRequestDTO requestDTO,
            @RequestPart(value = "avatarUrl", required = false) MultipartFile avatarUrl
    )throws java.io.IOException {
        UserRegistrationResponseDTO response = userService.createUser(requestDTO,avatarUrl);
        return ResponseHandler.generateResponse("Registration successful", HttpStatus.CREATED,response,request.getRequestURI());
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(
            @RequestBody UserLoginRequestDTO loginRequest
    ) {
        UserLoginResponseDTO response = userService.userLogin(loginRequest);
        return ResponseHandler.generateResponse("Login successful", HttpStatus.OK, response,request.getRequestURI());
    }

    @GetMapping("/confirm")
    public ResponseEntity<Object> confirmAccount(
            @RequestParam("token") String token
    ) {
        userService.userConfirmAccount(token);
        return ResponseHandler.generateResponse("Account confirmed successfully",HttpStatus.CREATED,null,request.getRequestURI());
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<Object> sendResetPasswordEmail(
            @RequestBody UserEmailDTO userEmailDTO
    ) {
        UserEmailDTO response = userService.userSendResetPassword(userEmailDTO);
        return ResponseHandler.generateResponse("Reset password send",HttpStatus.CREATED,response,request.getRequestURI());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword
    ) {
        userService.userUpdatePassword(token, newPassword);
        return ResponseHandler.generateResponse("Password updated successfully.",HttpStatus.CREATED,null,request.getRequestURI());
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateProfile(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody UserRegistrationRequestDTO userDTO
    ) {
        String token = tokenHeader.replace("Bearer ", "");
        UserRegistrationRequestDTO response = userService.updateUser(token, userDTO);
        return ResponseHandler.generateResponse("User updated successfully",HttpStatus.CREATED,null, request.getRequestURI());
    }

    @PutMapping("/logout")
    public ResponseEntity<Object> userLogOut(
            @RequestHeader("Authorization") String tokenHeader
    ){
        String token = tokenHeader.replace("Bearer ", "");
        userService.logOutUser(token);
        return ResponseHandler.generateResponse("Logged Out",HttpStatus.OK, null, request.getRequestURI());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteAccount(
            @RequestHeader("Authorization") String tokenHeader
    ) {
        String token = tokenHeader.replace("Bearer ", "");
        userService.userDeleteAccount(token);
        return ResponseHandler.generateResponse("Account deleted successfully",HttpStatus.OK,null, request.getRequestURI());
    }

}

