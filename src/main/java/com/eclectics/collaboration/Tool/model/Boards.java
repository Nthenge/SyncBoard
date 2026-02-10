package com.eclectics.collaboration.Tool.model;

import com.eclectics.collaboration.Tool.exception.CollaborationExceptions;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "boards")
public class Boards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workSpace_Id")
    @JsonIgnore
    private WorkSpace workSpaceId;

    private String boardName;
    private String boardDescription;
    private String boardCreatedBy;
    private LocalDateTime boardCreatedAt;

    public void assertWorkspaceMember(User user) {
        if (!workSpaceId.getMembers().contains(user)) {
            throw new CollaborationExceptions.ForbiddenException(
                    "User is not a workspace member"
            );
        }
    }

    public BoardMember addMember(User user) {
        return BoardMember.create(user, this);
    }

    public void assertMemberCanBeRemoved(BoardMember member, long adminCount) {
        if (member.isAdmin() && adminCount <= 1) {
            throw new CollaborationExceptions.ForbiddenException(
                    "Board must have at least one admin"
            );
        }
    }



}
