package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.application.tsiktsemestraljob.demo.Enums.AcceptStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity(name = "invitation_tokens")
public class Invitations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private StudyGroups groupId;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "used_by_user_id")
    private Long UsedByUserId;

    @Enumerated(EnumType.STRING)
    private AcceptStatus status;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
