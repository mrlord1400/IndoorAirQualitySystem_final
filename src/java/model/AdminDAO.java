package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import utils.DBUtils;

public class AdminDAO {
    // Hàm này trả về một Map chứa số lượng tổng cộng của các bảng
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        try (Connection conn = DBUtils.getConnection()) {
            // Đếm Users
            stats.put("totalUsers", getCount(conn, "SELECT COUNT(*) FROM Users"));
            // Đếm Rooms
            stats.put("totalRooms", getCount(conn, "SELECT COUNT(*) FROM Room"));
            // Đếm Sensors
            stats.put("totalSensors", getCount(conn, "SELECT COUNT(*) FROM Sensor"));
            // Đếm Pollutants
            stats.put("totalPollutants", getCount(conn, "SELECT COUNT(*) FROM Pollutant"));
            // Đếm Rules
            stats.put("totalRules", getCount(conn, "SELECT COUNT(*) FROM ThresholdRule"));
            // Đếm Cảnh báo đang mở (Open Alerts)
            stats.put("openAlerts", getCount(conn, "SELECT COUNT(*) FROM Alert WHERE status = 'OPEN'"));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }

    private int getCount(Connection conn, String sql) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
