package com.playersync.dto;

import lombok.Data;
import java.util.List;

/*
 * TODO:
 * - rename to class according to the java naming conventions
 * - @Data lombok annotation already provides getters and setters, explicit methods can be removed
 * - **question** are primitive types prefered over wrapper classes?
 * -
 * -
 */
@Data
public class MissionDTO {
    private Long id;
    private String name;
    private boolean completed;
    private int progress;
    private List<MissionRewardDTO> rewards;
    private List<String> tags;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public List<MissionRewardDTO> getRewards() { return rewards; }
    public void setRewards(List<MissionRewardDTO> rewards) { this.rewards = rewards; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}