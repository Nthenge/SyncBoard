package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;

import java.util.List;

public interface ListService {

    ListResponseDTO createList(Long boardId, ListRequestDTO dto,  Long userId);
    List<ListResponseDTO> getListsByBoard(Long boardId);

}
