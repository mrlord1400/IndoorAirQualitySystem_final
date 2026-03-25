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
public class AlertActionDTO {
    private long actionID;
    private long alertID;
    private int actorUserID;
    private String actionType;
    private String note;
    private LocalDateTime actionTs;

    public AlertActionDTO(long actionID, long alertID, int actorUserID, String actionType, String note, LocalDateTime actionTs) {
        this.actionID = actionID;
        this.alertID = alertID;
        this.actorUserID = actorUserID;
        this.actionType = actionType;
        this.note = note;
        this.actionTs = actionTs;
    }

    public long getActionID() {
        return actionID;
    }

    public void setActionID(long actionID) {
        this.actionID = actionID;
    }

    public long getAlertID() {
        return alertID;
    }

    public void setAlertID(long alertID) {
        this.alertID = alertID;
    }

    public int getActorUserID() {
        return actorUserID;
    }

    public void setActorUserID(int actorUserID) {
        this.actorUserID = actorUserID;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getActionTs() {
        return actionTs;
    }

    public void setActionTs(LocalDateTime actionTs) {
        this.actionTs = actionTs;
    }

    
}
