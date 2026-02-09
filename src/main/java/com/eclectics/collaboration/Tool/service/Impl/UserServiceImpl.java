package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.UserMapper;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.repository.UserRespository;
import com.eclectics.collaboration.Tool.security.JwtUtil;
import com.eclectics.collaboration.Tool.service.UserService;
import com.eclectics.collaboration.Tool.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRespository userRepository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final OSSService ossService;
    private final StringRedisTemplate redisTemplate;

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
                .orElseThrow(() -> new CollaborationExceptions.BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new CollaborationExceptions.BadRequestException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new CollaborationExceptions.BadRequestException("Account not confirmed. Please check your email.");
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
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public UserRegistrationRequestDTO updateUser(String token, UserRegistrationRequestDTO userDTO) {

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
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    public void userDeleteAccount(String token) {

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    @Override
    public void logOutUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            String tokenId = jwtUtil.extractId(jwt);
            Date expiration = jwtUtil.extractExpiration(jwt);
            long ttl = expiration.getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        "revoked_token:" + tokenId,
                        "true",
                        Duration.ofMillis(ttl)
                );
            }
        }
        SecurityContextHolder.clearContext();
    }
}
