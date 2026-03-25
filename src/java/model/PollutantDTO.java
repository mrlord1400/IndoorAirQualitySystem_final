/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class PollutantDTO {
    private int pollutantID;
    private String name;
    private String unit;

    public PollutantDTO(int pollutantID, String name, String unit) {
        this.pollutantID = pollutantID;
        this.name = name;
        this.unit = unit;
    }

    public int getPollutantID() {
        return pollutantID;
    }

    public void setPollutantID(int pollutantID) {
        this.pollutantID = pollutantID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    
}
