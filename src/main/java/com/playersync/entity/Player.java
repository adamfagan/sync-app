package com.playersync.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/*
 * TODO:
 * - Remove explicit getters and setters since Lombok is used
 * - createdAt is non nullable false
 * - For createdAt and updatedAt consider using jpa auditing annotations
 * - status is also not nullable
 * -
 */
@Entity
@Table(name = "players")
@Getter @Setter
public class Player {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One-to-One with Profile
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PlayerProfile profile;

    // One-to-Many with Missions
    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Mission> missions = new HashSet<>();

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public PlayerProfile getProfile() { return profile; }
    public void setProfile(PlayerProfile profile) { this.profile = profile; }

    public Set<Mission> getMissions() { return missions; }
    public void setMissions(Set<Mission> missions) { this.missions = missions; }
}