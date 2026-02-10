package com.eclectics.collaboration.Tool.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddBoardMemberRequestDTO {

    private List<Long> userIds;

    public List<Long> getUserIds() {
        return userIds;
    }

}
