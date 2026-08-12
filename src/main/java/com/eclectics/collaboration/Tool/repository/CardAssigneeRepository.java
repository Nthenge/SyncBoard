package com.eclectics.collaboration.Tool.repository;

import com.eclectics.collaboration.Tool.dto.AssignedCardResponseDTO;
import com.eclectics.collaboration.Tool.model.CardAssignee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardAssigneeRepository extends JpaRepository<CardAssignee, Long> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);

    Optional<CardAssignee> findByCardIdAndUserId(Long cardId, Long userId);

    List<CardAssignee> findByCardId(Long cardId);

    void deleteByCardIdAndUserId(Long cardId, Long userId);

    void deleteByCardId(Long cardId);

    @Query("""
        SELECT new com.eclectics.collaboration.Tool.dto.AssignedCardResponseDTO(
            c.id,
            c.title,
            c.priority,
            c.dueDate,
            l.id,
            l.title,
            b.id,
            b.boardName,
            w.id,
            w.workSpaceName
        )
        FROM CardAssignee ca
        JOIN ca.card c
        JOIN c.list l
        JOIN l.board b
        JOIN b.workSpaceId w
        WHERE ca.user.id = :userId
        ORDER BY c.updatedAt DESC
    """)
    List<AssignedCardResponseDTO> findAssignedCardsByUserId(@Param("userId") Long userId);
}



