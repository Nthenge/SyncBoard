package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteResponseDTO {

    private List<InviteResultDTO> results;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InviteResultDTO {
        private String email;
        private boolean success;
        private String message;
    }
}
