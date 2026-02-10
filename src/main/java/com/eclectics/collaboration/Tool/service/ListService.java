package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ListService {

    ListResponseDTO createList(Long boardId, ListRequestDTO dto,  Long userId);
    List<ListResponseDTO> getListsByBoard(Long boardId);

    @Transactional
    ListResponseDTO updateList(Long listId, Long userId, ListRequestDTO dto);

    @Transactional
    void deleteList(Long listId, Long userId);
}
