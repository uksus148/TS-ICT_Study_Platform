package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
public class Resources {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recource_id", nullable = false)
    private Long id;
    @Column(name = "groupId", nullable = false)
    private Long groupId;
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;
    @Column(nullable = false)
    private String title;
    @Column
    private String type;
    @Column(name = "path_or_url")
    private String pathOrUrl;
    @Column(name = "uploaded_at")
    private Timestamp uploadedAt;
}
