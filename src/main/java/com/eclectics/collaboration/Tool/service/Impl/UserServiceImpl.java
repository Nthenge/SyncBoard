package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.mapper.UserMapper;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.security.JwtUtil;
import com.eclectics.collaboration.Tool.service.UserService;
import com.eclectics.collaboration.Tool.service.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRespository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public UserServiceImpl(UserRespository userRepository,
                           UserMapper mapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // ---------------- CREATE USER ----------------
    @Override
    public UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO requestDTO) {

        User user = mapper.toEntity(requestDTO);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        // Generate token for email confirmation
        String token = jwtUtil.generateEmailConfirmationToken(savedUser.getEmail());
        String confirmLink = "https://yourapp.com/confirm-account?token=" + token;

        // Send email
        emailService.sendAccountConfirmationEmail(savedUser.getEmail(), confirmLink);

        // Return only firstName + token
        return new UserRegistrationResponseDTO(savedUser.getFirstName(), token);
    }


    // ---------------- LOGIN ----------------
    @Override
    public UserLoginResponseDTO userLogin(UserLoginRequestDTO loginRequestDTO) {

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not confirmed. Please check your email.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new UserLoginResponseDTO(user.getId(), user.getEmail(), token);
    }

    // ---------------- SEND RESET PASSWORD ----------------
    @Override
    public UserEmailDTO userSendResetPassword(UserEmailDTO userEmailDTO) {

        userRepository.findByEmail(userEmailDTO.getEmail())
                .ifPresent(user -> {
                    String resetToken = jwtUtil.generateResetPasswordToken(user.getEmail());
                    String resetLink = "https://yourapp.com/reset-password?token=" + resetToken;
                    emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
                });

        return userEmailDTO;
    }

    // ---------------- UPDATE PASSWORD ----------------
    @Override
    public void userUpdatePassword(String token, String newPassword) {

        String email = jwtUtil.validateAndExtractEmailFromResetToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ---------------- UPDATE PROFILE ----------------
    @Override
    public UserRegistrationRequestDTO updateUser(String token, UserRegistrationRequestDTO userDTO) {

        // Identify user by token
        String email = jwtUtil.extractEmail(token);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFirstName() != null) existingUser.setFirstName(userDTO.getFirstName());
        if (userDTO.getSirName() != null) existingUser.setSirName(userDTO.getSirName());

        // For security, do not allow password update here (use userUpdatePassword)
        User updatedUser = userRepository.save(existingUser);

        return mapper.toResponse(updatedUser);
    }

    // ---------------- CONFIRM ACCOUNT ----------------
    @Override
    public void userConfirmAccount(String token) {

        String email = jwtUtil.validateAndExtractEmailFromConfirmationToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    // ---------------- DELETE ACCOUNT ----------------
    @Override
    public void userDeleteAccount(String token) {

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}
