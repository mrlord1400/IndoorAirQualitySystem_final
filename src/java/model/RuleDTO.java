/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class RuleDTO {
    private int ruleID;
    private int roomID;
    private int pollutantID;
    private double lowerBound;
    private double upperBound;
    private int durationMin;
    private String severity;
    private boolean active;
    private LocalDateTime createdAt;

    public RuleDTO(int ruleID, int roomID, int pollutantID, double lowerBound, double upperBound, int durationMin, String severity, boolean active) {
        this.ruleID = ruleID;
        this.roomID = roomID;
        this.pollutantID = pollutantID;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.durationMin = durationMin;
        this.severity = severity;
        this.active = active;
        this.createdAt = LocalDateTime.now();
    }

    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getPollutantID() {
        return pollutantID;
    }

    public void setPollutantID(int pollutantID) {
        this.pollutantID = pollutantID;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(int durationMin) {
        this.durationMin = durationMin;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
