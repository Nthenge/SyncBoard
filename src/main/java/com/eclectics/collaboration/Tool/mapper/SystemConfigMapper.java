package com.eclectics.collaboration.Tool.mapper;

import com.eclectics.collaboration.Tool.dto.SystemConfigRequestDTO;
import com.eclectics.collaboration.Tool.dto.SystemConfigResponseDTO;
import com.eclectics.collaboration.Tool.model.SystemConfig;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigMapper {

    public SystemConfig toEntity(SystemConfigRequestDTO dto) {
        return SystemConfig.builder()
                .configKey(dto.getConfigKey())
                .configValue(dto.getConfigValue())
                .description(dto.getDescription())
                .build();
    }

    public SystemConfigResponseDTO toResponse(SystemConfig config) {
        return SystemConfigResponseDTO.builder()
                .id(config.getId())
                .configKey(config.getConfigKey())
                .configValue(config.getConfigValue())
                .description(config.getDescription())
                .createdAt(config.getCreatedAt())
                .updatedAt(config.getUpdatedAt())
                .build();
    }

    public void updateEntityFromDTO(SystemConfigRequestDTO dto, SystemConfig existing) {
        existing.setConfigValue(dto.getConfigValue());
        if (dto.getDescription() != null) existing.setDescription(dto.getDescription());
    }
}
