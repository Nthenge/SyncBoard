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
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "board_id"})
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

    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;

    public UserRecentBoard(User user, Boards board) {
        this.user = user;
        this.board = board;
        this.lastAccessedAt = LocalDateTime.now();
    }
}
