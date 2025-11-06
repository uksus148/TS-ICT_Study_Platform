package org.application.tsiktsemestraljob.demo.Entities;

import jakarta.persistence.*;

import java.sql.Timestamp;
@Entity(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long id;
    @Column(nullable = false, name = "user_id")
    private Long userId;
    @Column(nullable = false, name = "group_id")
    private int groupId;
    @Column(nullable = false)
    private String role;
    @Column(name = "joined_at")
    private Timestamp joinedAt;
}
