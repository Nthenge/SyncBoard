package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.ListMapper;
import com.eclectics.collaboration.Tool.model.BoardMember;
import com.eclectics.collaboration.Tool.model.BoardRole;
import com.eclectics.collaboration.Tool.model.Boards;
import com.eclectics.collaboration.Tool.model.ListEntity;
import com.eclectics.collaboration.Tool.repository.BoardMemberRepository;
import com.eclectics.collaboration.Tool.repository.BoardsRepository;
import com.eclectics.collaboration.Tool.repository.ListEntityRepository;
import com.eclectics.collaboration.Tool.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService {

    private final ListEntityRepository listRepository;
    private final BoardsRepository boardsRepository;
    private final ListMapper listMapper;
    private final BoardMemberRepository boardMemberRepository;

    @Override
    @Transactional
    public ListResponseDTO createList(Long boardId, ListRequestDTO dto, Long userId) {

        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only admins can create lists");
        }

        ListEntity list = listMapper.toEntity(dto, board);

        if (list.getPosition() == null) {
            Integer maxPos = listRepository.findByBoardIdOrderByPosition(boardId).stream()
                    .map(ListEntity::getPosition)
                    .max(Integer::compareTo)
                    .orElse(0);
            list.setPosition(maxPos + 1);
        }

        ListEntity saved = listRepository.save(list);
        return listMapper.toDto(saved);
    }


    @Override
    public List<ListResponseDTO> getListsByBoard(Long boardId) {
        return listRepository.findByBoardIdOrderByPosition(boardId)
                .stream()
                .map(listMapper::toDto)
                .toList();
    }
}

