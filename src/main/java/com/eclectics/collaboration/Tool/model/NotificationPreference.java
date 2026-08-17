package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Card assigned
    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnAssign = true;
    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnAssign = true;

    // Added to board
    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnBoardAdd = true;
    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnBoardAdd = true;

    // Mentioned in a comment
    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnMention = true;
    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnMention = true;

    // Due soon
    @Column(nullable = false)
    @Builder.Default
    private boolean emailOnDueSoon = true;
    @Column(nullable = false)
    @Builder.Default
    private boolean inAppOnDueSoon = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean weeklyDigest = true;
}
