package com.eclectics.collaboration.Tool.service;

public interface CardAssigneeService {

    void removeAssignee(Long cardId, Long requesterId, Long targetUserId);

    void reassignCard(Long cardId, Long requesterId, Long newUserId);
}

