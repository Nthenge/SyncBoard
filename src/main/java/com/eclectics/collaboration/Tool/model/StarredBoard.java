package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "starred_boards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"board_id", "user_id"})
)
public class StarredBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Boards board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime starredAt;

    protected StarredBoard() {}

    public StarredBoard(Boards board, User user) {
        this.board = board;
        this.user = user;
        this.starredAt = LocalDateTime.now();
    }
}