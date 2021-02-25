package com.seraphic.lightapp.home_module.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("currentStreak")
    @Expose
    private Integer currentStreak;
    @SerializedName("healingTime")
    @Expose
    private Integer healingTime;
    @SerializedName("healingDays")
    @Expose
    private Integer healingDays;

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(Integer currentStreak) {
        this.currentStreak = currentStreak;
    }

    public Integer getHealingTime() {
        return healingTime;
    }

    public void setHealingTime(Integer healingTime) {
        this.healingTime = healingTime;
    }

    public Integer getHealingDays() {
        return healingDays;
    }

    public void setHealingDays(Integer healingDays) {
        this.healingDays = healingDays;
    }

}
