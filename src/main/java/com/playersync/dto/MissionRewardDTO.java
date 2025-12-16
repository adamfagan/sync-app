package com.playersync.dto;

import lombok.Data;

/*
 * TODO:
 * - rename to class according to the java naming conventions
 * - @Data lombok annotation already provides getters and setters, explicit methods can be removed
 * - amount is wrapper class, but other numeric fields are primitive
 * -
 * -
 */
@Data
public class MissionRewardDTO {

    private Long id;
    private Long missionId;
    private String rewardType;
    private Integer amount;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }

    public String getRewardType() { return rewardType; }
    public void setRewardType(String rewardType) { this.rewardType = rewardType; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}
