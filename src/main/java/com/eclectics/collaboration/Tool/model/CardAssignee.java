package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "card_assignees",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "user_id"})
)
public class CardAssignee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected CardAssignee() {
    }

    public CardAssignee(Card card, User user) {
        this.card = card;
        this.user = user;
    }
}



