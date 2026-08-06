package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "card_labels",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "label_id"})
)
public class CardLabel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    protected CardLabel() {
    }

    public CardLabel(Card card, Label label) {
        this.card = card;
        this.label = label;
    }
}