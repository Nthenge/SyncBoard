package com.eclectics.collaboration.Tool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspaces")
public class WorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String workSpaceName;

    @Column(length = 500)
    private String workSpaceDescription;

    private String workSpaceCreatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime workSpaceCreatedAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;                  // was missing entirely

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User workSpaceOwnerId;

    @ManyToMany
    @JoinTable(
            name = "workspace_members",
            joinColumns = @JoinColumn(name = "workspace_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore                                       // was missing — causes infinite recursion
    private Set<User> members = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        workSpaceCreatedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
