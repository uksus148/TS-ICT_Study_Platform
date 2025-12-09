package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.application.tsiktsemestraljob.demo.Enums.MembershipRole;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private StudyGroups studyGroup;

    @Enumerated(EnumType.STRING)
    private MembershipRole membershipRole;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDate.now();
    }

    @Column(name = "joined_at")
    private LocalDate joinedAt;
}
