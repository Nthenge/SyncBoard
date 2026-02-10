package com.eclectics.collaboration.Tool.model;

import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "board_members",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"board_id", "user_id"})
        }
)
public class BoardMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Boards board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardRole role;

    private LocalDateTime joinedAt;

    public BoardMember(Boards board, User user, BoardRole boardRole) {
    }

    public void assertAdmin() {
        if (this.role != BoardRole.ADMIN) {
            throw new CollaborationExceptions.UnauthorizedException(
                    "Only board admins can perform this action"
            );
        }
    }

    public static BoardMember create(User user, Boards board) {
        BoardMember member = new BoardMember();
        member.user = user;
        member.board = board;
        member.role = BoardRole.MEMBER;
        return member;
    }

    public void changeRole(BoardRole newRole) {
        if (this.role == newRole) {
            return;
        }
        this.role = newRole;
    }

    public boolean isAdmin() {
        return this.role == BoardRole.ADMIN;
    }
}

