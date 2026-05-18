package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;
    private final HttpServletRequest request;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerUser(
            @RequestBody UserRegistrationRequestDTO requestDTO
    ) throws java.io.IOException {
        log.info("Register request received → email={}", requestDTO.getEmail());
        UserRegistrationResponseDTO response = userService.createUser(requestDTO, null);
        return ResponseHandler.generateResponse(
                "Registration successful, open email and confirm your account",
                HttpStatus.CREATED, response, request.getRequestURI()
        );
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
        try {
            userService.userConfirmAccount(token);
            return ResponseHandler.generateResponse(
                    "Account confirmed successfully, please login",
                    HttpStatus.OK, null, request.getRequestURI()
            );
        } catch (CollaborationExceptions.ResourceAlreadyExistsException e) {
            return ResponseHandler.generateResponse(
                    "Account already confirmed, please login",
                    HttpStatus.CONFLICT, null, request.getRequestURI()
            );
        } catch (Exception e) {
            return ResponseHandler.generateResponse(
                    "Account confirmation failed, please try again",
                    HttpStatus.BAD_REQUEST, null, request.getRequestURI()
            );
        }
    }

    @PostMapping("/reset-password-request")
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
        return ResponseHandler.generateResponse("User updated successfully",HttpStatus.CREATED, response, request.getRequestURI());
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

