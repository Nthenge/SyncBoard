package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    public enum Type {
        CARD_ASSIGNED,
        ADDED_TO_BOARD,
        MENTIONED,
        DUE_SOON
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Type type;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "reference_type", length = 16)
    private String referenceType; // "CARD" or "BOARD"

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

