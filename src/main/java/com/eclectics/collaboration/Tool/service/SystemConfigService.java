package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.SystemConfigRequestDTO;
import com.eclectics.collaboration.Tool.dto.SystemConfigResponseDTO;
import com.eclectics.collaboration.Tool.enums.ConfigKey;

import java.util.List;

public interface SystemConfigService {
    SystemConfigResponseDTO create(SystemConfigRequestDTO requestDTO);
    SystemConfigResponseDTO update(ConfigKey configKey, SystemConfigRequestDTO requestDTO);
    SystemConfigResponseDTO getByKey(ConfigKey configKey);
    List<SystemConfigResponseDTO> getAll();
    String getValueByKey(ConfigKey configKey);  // convenience method for internal use
}
