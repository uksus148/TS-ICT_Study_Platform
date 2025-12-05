package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.application.tsiktsemestraljob.demo.Enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroups studyGroup;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private TaskStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }

    @Column
    private LocalDateTime deadline;

    @Column(name = "created_at")
    private LocalDate createdAt;
}
