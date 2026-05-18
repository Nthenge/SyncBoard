package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.SystemConfigRequestDTO;
import com.eclectics.collaboration.Tool.dto.SystemConfigResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.SystemConfigMapper;
import com.eclectics.collaboration.Tool.model.ConfigKey;
import com.eclectics.collaboration.Tool.model.SystemConfig;
import com.eclectics.collaboration.Tool.repository.SystemConfigRepository;
import com.eclectics.collaboration.Tool.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final SystemConfigMapper systemConfigMapper;

    @Override
    public SystemConfigResponseDTO create(SystemConfigRequestDTO requestDTO) {
        if (systemConfigRepository.existsByConfigKey(requestDTO.getConfigKey())) {
            throw new CollaborationExceptions.ResourceAlreadyExistsException(
                    "Config key " + requestDTO.getConfigKey() + " already exists. Use the update endpoint instead.");
        }
        SystemConfig saved = systemConfigRepository.save(systemConfigMapper.toEntity(requestDTO));
        log.info("SystemConfig created key={}", saved.getConfigKey());
        return systemConfigMapper.toResponse(saved);
    }

    @Override
    public SystemConfigResponseDTO update(ConfigKey configKey, SystemConfigRequestDTO requestDTO) {
        SystemConfig existing = findOrThrow(configKey);
        systemConfigMapper.updateEntityFromDTO(requestDTO, existing);
        SystemConfig updated = systemConfigRepository.save(existing);
        log.info("SystemConfig updated key={}", configKey);
        return systemConfigMapper.toResponse(updated);
    }

    @Override
    public SystemConfigResponseDTO getByKey(ConfigKey configKey) {
        return systemConfigMapper.toResponse(findOrThrow(configKey));
    }

    @Override
    public List<SystemConfigResponseDTO> getAll() {
        return systemConfigRepository.findAll().stream()
                .map(systemConfigMapper::toResponse)
                .toList();
    }

    // Used internally — e.g. emailService pulling the support email at send time
    @Override
    public String getValueByKey(ConfigKey configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "System config not set for key: " + configKey));
    }

    private SystemConfig findOrThrow(ConfigKey configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException(
                        "System config not found for key: " + configKey));
    }
}
