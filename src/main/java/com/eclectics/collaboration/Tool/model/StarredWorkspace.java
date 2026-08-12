package com.eclectics.collaboration.Tool.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "starred_workspaces",
        uniqueConstraints = @UniqueConstraint(columnNames = {"workspace_id", "user_id"})
)
public class StarredWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkSpace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime starredAt;

    protected StarredWorkspace() {}

    public StarredWorkspace(WorkSpace workspace, User user) {
        this.workspace = workspace;
        this.user = user;
        this.starredAt = LocalDateTime.now();
    }
}