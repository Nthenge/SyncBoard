package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRespository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
