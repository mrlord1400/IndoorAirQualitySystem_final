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
public class AlertDTO {

    private long alertID;
    private int roomID;
    private int sensorID;
    private int ruleID;
    private String alertType;
    private int pollutantID;
    private LocalDateTime startTs;
    private LocalDateTime endTs;
    private String severity;
    private String status;
    private String message;
    private LocalDateTime createdAt;
    private int assignedTo;

    public AlertDTO() {
    }

    public AlertDTO(long alertID, int roomID, int sensorID, int ruleID, String alertType, int pollutantID, LocalDateTime startTs, LocalDateTime endTs, String severity, String status, String message, LocalDateTime createdAt, int assignedTo) {
        this.alertID = alertID;
        this.roomID = roomID;
        this.sensorID = sensorID;
        this.ruleID = ruleID;
        this.alertType = alertType;
        this.pollutantID = pollutantID;
        this.startTs = startTs;
        this.endTs = endTs;
        this.severity = severity;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.assignedTo = assignedTo;
    }

    public long getAlertID() {
        return alertID;
    }

    public void setAlertID(long alertID) {
        this.alertID = alertID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public int getSensorID() {
        return sensorID;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public int getRuleID() {
        return ruleID;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public int getPollutantID() {
        return pollutantID;
    }

    public void setPollutantID(int pollutantID) {
        this.pollutantID = pollutantID;
    }

    public LocalDateTime getStartTs() {
        return startTs;
    }

    public void setStartTs(LocalDateTime startTs) {
        this.startTs = startTs;
    }

    public LocalDateTime getEndTs() {
        return endTs;
    }

    public void setEndTs(LocalDateTime endTs) {
        this.endTs = endTs;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }

}
