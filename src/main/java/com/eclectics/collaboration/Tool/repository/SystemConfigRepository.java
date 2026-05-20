package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.enums.ConfigKey;
import com.eclectics.collaboration.Tool.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    Optional<SystemConfig> findByConfigKey(ConfigKey configKey);
    boolean existsByConfigKey(ConfigKey configKey);
}
