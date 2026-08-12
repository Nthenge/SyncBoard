package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "user_recent_boards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "board_id", "list_id", "card_id"})
)
public class UserRecentBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Boards board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private ListEntity list;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    public UserRecentBoard(User user, Boards board, ListEntity list, Card card) {
        this.user = user;
        this.board = board;
        this.list = list;
        this.card = card;
        this.lastAccessedAt = LocalDateTime.now();
    }
}