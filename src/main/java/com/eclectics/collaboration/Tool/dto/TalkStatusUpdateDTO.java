package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.TalkStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TalkStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private TalkStatus status;
}
