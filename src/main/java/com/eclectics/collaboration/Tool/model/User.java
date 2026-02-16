package com.eclectics.collaboration.Tool.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String sirName;
    private String email;
    private String password;
    private boolean deleted = false;
    @Column(name = "avatarUrl", nullable = true)
    private String avatarUrl;

    private LocalDateTime createdAt;
    private boolean enabled = false;

    @OneToMany(mappedBy = "workSpaceOwnerId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<WorkSpace> workSpaces;

    public String getFullName() {
        return String.format("%s %s", this.firstName, this.sirName);
    }
}
