/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.io.Serializable;
/**
 *
 * @author Admin
 */
public class ReadingDTO implements Serializable{
    private long readingID;
    private int sensorID;
    private int pollutantID;
    private LocalDateTime ts;
    private double value;
    private String qualityFlag;
    private LocalDateTime createdAt;

    public ReadingDTO(long readingID, int sensorID, int pollutantID, LocalDateTime ts, double value, String qualityFlag, LocalDateTime createdAt) {
        this.readingID = readingID;
        this.sensorID = sensorID;
        this.pollutantID = pollutantID;
        this.ts = ts;
        this.value = value;
        this.qualityFlag = qualityFlag;
        this.createdAt = createdAt;
    }

    public long getReadingID() {
        return readingID;
    }

    public void setReadingID(long readingID) {
        this.readingID = readingID;
    }

    public int getSensorID() {
        return sensorID;
    }

    public void setSensorID(int sensorID) {
        this.sensorID = sensorID;
    }

    public int getPollutantID() {
        return pollutantID;
    }

    public void setPollutantID(int pollutantID) {
        this.pollutantID = pollutantID;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getQualityFlag() {
        return qualityFlag;
    }

    public void setQualityFlag(String qualityFlag) {
        this.qualityFlag = qualityFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    
}
