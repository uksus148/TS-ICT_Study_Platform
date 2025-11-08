package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "study_groups")
@Getter
@Setter
public class StudyGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User createdBy;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resources> resources;

    @OneToMany(mappedBy = "studyGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Membership> memberships;
}
