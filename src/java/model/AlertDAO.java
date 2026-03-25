/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utils.DBUtils;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class AlertDAO {

    public boolean updateAlertStatus(int alertId, String newStatus) {
        String sql = "UPDATE Alert SET status = ? WHERE alert_id = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, alertId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra xem có Alert nào cùng Sensor, Room, Rule đang mở (OPEN/ACK) không
    public boolean hasActiveAlert(int sensorID, int roomID, int ruleID) {
        String sql = "SELECT 1 FROM Alert WHERE sensor_id = ? AND room_id = ? AND rule_id = ? " +
                     "AND status IN ('OPEN', 'ACK')";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sensorID);
            ps.setInt(2, roomID);
            ps.setInt(3, ruleID);
            ResultSet rs = ps.executeQuery();
            return rs.next(); 
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public void createAlert(AlertDTO alert) {
        String sql = "INSERT INTO Alert (room_id, sensor_id, rule_id, alert_type, pollutant_id, start_ts, severity, status, message) " +
                     "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alert.getRoomID());
            ps.setInt(2, alert.getSensorID());
            ps.setInt(3, alert.getRuleID());
            ps.setString(4, "THRESHOLD");
            ps.setInt(5, alert.getPollutantID());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(alert.getStartTs()));
            ps.setString(7, alert.getSeverity());
            ps.setString(8, "OPEN");
            ps.setString(9, alert.getMessage());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }
    
    public List<AlertDTO> getAllAlerts() {
        List<AlertDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Alert ORDER BY created_at DESC"; // Dùng 'Alert' theo SQL của bạn
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AlertDTO alert = new AlertDTO();
                alert.setAlertID(rs.getLong("alert_id"));
                alert.setRoomID(rs.getInt("room_id"));
                alert.setSensorID(rs.getInt("sensor_id"));
                alert.setAlertType(rs.getString("alert_type"));
                alert.setStartTs(rs.getTimestamp("start_ts").toLocalDateTime());
                alert.setSeverity(rs.getString("severity"));
                alert.setStatus(rs.getString("status"));
                alert.setMessage(rs.getNString("message"));
                list.add(alert);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}


