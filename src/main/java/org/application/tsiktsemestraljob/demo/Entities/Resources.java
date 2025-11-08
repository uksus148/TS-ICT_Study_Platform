package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@Entity
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resource_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroups studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @Column(nullable = false)
    private String title;

    @Column
    private String type;

    @Column(name = "path_or_url")
    private String pathOrUrl;

    @Column(name = "uploaded_at")
    private Timestamp uploadedAt;
}
