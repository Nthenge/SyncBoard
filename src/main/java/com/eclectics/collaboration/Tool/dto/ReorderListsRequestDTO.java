package com.eclectics.collaboration.Tool.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReorderListsRequestDTO {
    private List<Long> listIds;
}
