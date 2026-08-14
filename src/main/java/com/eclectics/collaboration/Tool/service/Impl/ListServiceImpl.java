package com.eclectics.collaboration.Tool.service.Impl;

import com.eclectics.collaboration.Tool.dto.ListRequestDTO;
import com.eclectics.collaboration.Tool.dto.ListResponseDTO;
import com.eclectics.collaboration.Tool.enums.BoardRole;
import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.eclectics.collaboration.Tool.mapper.ListMapper;
import com.eclectics.collaboration.Tool.model.*;
import com.eclectics.collaboration.Tool.repository.*;
import com.eclectics.collaboration.Tool.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListServiceImpl implements ListService {

    private final ListEntityRepository listRepository;
    private final BoardsRepository boardsRepository;
    private final CardRepository cardRepository;
    private final ListMapper listMapper;
    private final BoardMemberRepository boardMemberRepository;
    private final CardAssigneeRepository cardAssigneeRepository;
    private final CardLabelRepository cardLabelRepository;
    private final UserRecentBoardRepository recentBoardRepository;

    @Override
    @Transactional
    public ListResponseDTO createList(Long boardId, ListRequestDTO dto, Long userId) {
        Boards board = boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only workspace admin can create a list.");
        }

        ListEntity list = listMapper.toEntity(dto, board);

        if (list.getPosition() == null) {
            Integer maxPos = listRepository.findByBoard_IdOrderByPosition(boardId).stream()
                    .map(ListEntity::getPosition)
                    .max(Integer::compareTo)
                    .orElse(0);
            list.setPosition(maxPos + 1);
        }

        return listMapper.toDto(listRepository.save(list));
    }

    @Override
    public List<ListResponseDTO> getListsByBoard(Long boardId) {
        return listRepository.findByBoard_IdOrderByPosition(boardId)
                .stream()
                .map(listMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public ListResponseDTO updateList(Long listId, Long userId, ListRequestDTO dto) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("List not found"));

        Boards board = list.getBoard();

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only admins can update lists");
        }

        listMapper.updateEntityFromDto(dto, list);
        return listMapper.toDto(listRepository.save(list));
    }

    @Transactional
    @Override
    public void deleteList(Long listId, Long userId) {
        ListEntity list = listRepository.findById(listId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("List not found"));

        Boards board = list.getBoard();

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(board.getId(), userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only admins can delete lists");
        }

        list.getCards().forEach(card -> {
            cardAssigneeRepository.deleteByCardId(card.getId());
            cardLabelRepository.deleteByCardId(card.getId());
            recentBoardRepository.deleteByCardId(card.getId());
            cardRepository.delete(card);
        });
        listRepository.delete(list);
    }
    @Transactional
    @Override
    public List<ListResponseDTO> reorderLists(Long boardId, List<Long> listIds, Long userId) {
        boardsRepository.findById(boardId)
                .orElseThrow(() -> new CollaborationExceptions.ResourceNotFoundException("Board not found"));

        BoardMember member = boardMemberRepository
                .findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new CollaborationExceptions.ForbiddenException("User is not a member of the board"));

        if (member.getRole() != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException("Only admins can reorder lists");
        }
        Map<Long, ListEntity> listMap = listRepository.findByBoard_IdOrderByPosition(boardId)
                .stream()
                .collect(Collectors.toMap(ListEntity::getId, l -> l));

        for (Long id : listIds) {
            if (!listMap.containsKey(id)) {
                throw new CollaborationExceptions.BadRequestException(
                        "List " + id + " does not belong to board " + boardId);
            }
        }

        List<ListEntity> toSave = new ArrayList<>();
        for (int i = 0; i < listIds.size(); i++) {
            ListEntity list = listMap.get(listIds.get(i));
            list.setPosition(i + 1);
            toSave.add(list);
        }

        listRepository.saveAll(toSave);

        return toSave.stream()
                .map(listMapper::toDto)
                .toList();
    }
}
