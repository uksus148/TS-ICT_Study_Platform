package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Getter
@Setter
@Entity(name = "tasks")
public class Task {
    @Id //primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Value is AUTO INCREMENT in mySql
    @Column(name = "task_id")
    private int id;
    @Column(name = "group_id", nullable = true)
    private int groupId;
    @Column(name = "created_by", nullable = true)
    private Long createdBy;
    @Column(nullable = false)
    private String title;
    @Column
    private String description;
    @Column
    private String status;
    @Column
    private Timestamp deadline;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
