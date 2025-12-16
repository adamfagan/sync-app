package com.playersync.dto;

import lombok.Data;
import java.util.List;

/*
 * TODO:
 * - rename to class according to the java naming conventions
 * - @Data lombok annotation already provides getters and setters, explicit methods can be removed
 * -
 * -
 * -
 */
@Data
public class PlayerSyncDTO {
    private Long id;
    private String username;
    private String country;
    private String status;
    private List<MissionDTO> missions;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<MissionDTO> getMissions() { return missions; }
    public void setMissions(List<MissionDTO> missions) { this.missions = missions; }
}