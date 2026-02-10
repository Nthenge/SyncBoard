package com.eclectics.collaboration.Tool.dto;

import com.eclectics.collaboration.Tool.model.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardMemberResponseDTO {

    private Long id;
    private Long userId;
    private String userFullName;
    private BoardRole role;
    private Long boardId;

}
