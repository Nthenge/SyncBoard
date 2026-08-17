package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "comment_mentions")
public class CommentMention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentioned_user_id", nullable = false)
    private User mentionedUser;

    protected CommentMention() {}

    public CommentMention(Comment comment, User mentionedUser) {
        this.comment = comment;
        this.mentionedUser = mentionedUser;
    }
}