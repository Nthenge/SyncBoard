package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.ConfigKey;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfigRequestDTO {

    @NotNull(message = "Config key is required")
    private ConfigKey configKey;

    @NotBlank(message = "Config value is required")
    private String configValue;

    @Size(max = 300)
    private String description;
}
