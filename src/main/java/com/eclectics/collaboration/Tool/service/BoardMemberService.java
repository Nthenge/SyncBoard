package com.eclectics.collaboration.Tool.service;

import com.eclectics.collaboration.Tool.dto.BoardMemberResponseDTO;
import com.eclectics.collaboration.Tool.model.BoardRole;

import java.util.List;

public interface BoardMemberService {
    public List<BoardMemberResponseDTO> addMembers(Long boardId, Long requesterId, List<Long> userIds);
    public void removeMember(Long boardId, Long requesterId, Long targetUserId);
    public void changeRole(Long boardId, Long requesterId, Long targetUserId, BoardRole newRole);
}
