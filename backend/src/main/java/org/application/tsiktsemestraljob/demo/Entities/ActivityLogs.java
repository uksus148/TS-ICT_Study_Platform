package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity(name = "activity_log")
public class ActivityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String action;
    @Column
    private Timestamp timestamp;
    @Column
    private String details;
}
