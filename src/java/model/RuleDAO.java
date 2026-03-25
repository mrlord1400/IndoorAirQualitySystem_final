/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class RuleDAO {

    // Phương thức lấy danh sách, nhận vào connection
    public List<RuleDTO> getAllRules() {
        List<RuleDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ThresholdRule";

        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new RuleDTO(
                        rs.getInt("rule_id"),
                        rs.getInt("room_id"),
                        rs.getInt("pollutant_id"),
                        rs.getDouble("lower_bound"),
                        rs.getDouble("upper_bound"),
                        rs.getInt("duration_min"),
                        rs.getString("severity"),
                        rs.getBoolean("active")
                ));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<RuleDTO> getRulesByRoomAndPollutant(int roomID, int pollutantID) {
        List<RuleDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ThresholdRule WHERE room_id = ? AND pollutant_id = ?";

        try ( Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            ps.setInt(2, pollutantID);
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new RuleDTO(
                        rs.getInt("rule_id"),
                        rs.getInt("room_id"),
                        rs.getInt("pollutant_id"),
                        rs.getDouble("lower_bound"),
                        rs.getDouble("upper_bound"),
                        rs.getInt("duration_min"),
                        rs.getString("severity"),
                        rs.getBoolean("active")
                ));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public boolean insertRule(RuleDTO dto) {
        String sql = "INSERT INTO ThresholdRule (room_id, pollutant_id, lower_bound, upper_bound, duration_min, severity, active) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getRoomID());
            ps.setInt(2, dto.getPollutantID());
            ps.setDouble(3, dto.getLowerBound());
            ps.setDouble(4, dto.getUpperBound());
            ps.setInt(5, dto.getDurationMin());
            ps.setString(6, dto.getSeverity());
            ps.setBoolean(7, dto.isActive());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateRule(RuleDTO dto) {
        String sql = "UPDATE ThresholdRule SET room_id=?, pollutant_id=?, lower_bound=?, upper_bound=?, "
                   + "duration_min=?, severity=?, active=? WHERE rule_id=?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getRoomID());
            ps.setInt(2, dto.getPollutantID());
            ps.setDouble(3, dto.getLowerBound());
            ps.setDouble(4, dto.getUpperBound());
            ps.setInt(5, dto.getDurationMin());
            ps.setString(6, dto.getSeverity());
            ps.setBoolean(7, dto.isActive());
            ps.setInt(8, dto.getRuleID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    
    // Xóa quy tắc
    public boolean deleteRule(int id) {
        String sql = "DELETE FROM ThresholdRule WHERE rule_id=?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
