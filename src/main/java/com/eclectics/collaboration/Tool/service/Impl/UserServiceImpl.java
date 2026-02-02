package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.mapper.UserMapper;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.security.JwtUtil;
import com.eclectics.collaboration.Tool.service.OSSService;
import com.eclectics.collaboration.Tool.service.UserService;
import com.eclectics.collaboration.Tool.service.EmailService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);
    private final UserRespository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final OSSService ossService;

    public UserServiceImpl(UserRespository userRepository,
                           UserMapper mapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           EmailService emailService, OSSService ossService) {
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
        this.ossService = ossService;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    @Override
    public UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO requestDTO, MultipartFile avatarUrl) throws IOException {

        LocalDateTime now = LocalDateTime.now();
        User user = mapper.toEntity(requestDTO);

        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setEnabled(false);
        user.setCreatedAt(now);

        if ( avatarUrl!= null && !avatarUrl.isEmpty()) {
            String ext = getFileExtension(avatarUrl.getOriginalFilename());
            String path = "SYNCBOARD/avatar/" + user.getId() + "-" + UUID.randomUUID() + ext;
            user.setAvatarUrl(ossService.uploadFile(path, avatarUrl.getInputStream()));
        }

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateEmailConfirmationToken(savedUser.getEmail());
        String confirmLink = "https://yourapp.com/confirm-account?token=" + token;

        try{
            emailService.sendAccountConfirmationEmail(savedUser.getEmail(), confirmLink);
        }catch (Exception e) {
            UserServiceImpl.log.error("Failed to send Email to user, but user saved", e);
        }

        return new UserRegistrationResponseDTO(savedUser.getFirstName(), token);
    }


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

    @Override
    public void userUpdatePassword(String token, String newPassword) {

        String email = jwtUtil.validateAndExtractEmailFromResetToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserRegistrationRequestDTO updateUser(String token, UserRegistrationRequestDTO userDTO) {

        // Identify user by token
        String email = jwtUtil.extractEmail(token);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getFirstName() != null) existingUser.setFirstName(userDTO.getFirstName());
        if (userDTO.getSirName() != null) existingUser.setSirName(userDTO.getSirName());

        User updatedUser = userRepository.save(existingUser);

        return mapper.toResponse(updatedUser);
    }

    @Override
    public void userConfirmAccount(String token) {

        String email = jwtUtil.validateAndExtractEmailFromConfirmationToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void userDeleteAccount(String token) {

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }
}
