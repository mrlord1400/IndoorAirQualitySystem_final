package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

/**
 * Lớp SensorDAO đóng vai trò là tầng truy xuất dữ liệu (Data Access Object).
 * Công dụng: Quản lý các thao tác CRUD (Create, Read, Update, Delete) với bảng
 * Cảm biến trong cơ sở dữ liệu.
 */
public class SensorDAO {

    public boolean addSensorWithMapping(SensorDTO sensor, String pollutantName, int csvIndex) {
        String sqlSensor = "INSERT INTO Sensor (room_id, serial_no, model, installed_at, status) VALUES (?, ?, ?, ?, 1)";
        String sqlMap = "INSERT INTO SensorPollutantMap (sensor_id, pollutant_name, csv_column_index) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            // 1. Tắt chế độ tự động lưu để bắt đầu Transaction
            conn.setAutoCommit(false);

            // 2. Thêm vào bảng Sensor và yêu cầu lấy lại ID tự tăng
            PreparedStatement ps1 = conn.prepareStatement(sqlSensor, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, sensor.getRoomID());
            ps1.setString(2, sensor.getSerialNo());
            ps1.setString(3, sensor.getModel());
            ps1.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis())); // installed_at

            int affectedRows = ps1.executeUpdate();

            if (affectedRows > 0) {
                // 3. Lấy sensor_id vừa mới được SQL Server sinh ra
                ResultSet rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    int newSensorId = rs.getInt(1);
                    sensor.setSensorID(newSensorId);
                    sensor.setCsvColumnIndex(csvIndex);
                    // 4. Thêm vào bảng SensorPollutantMap dùng newSensorId vừa lấy
                    PreparedStatement ps2 = conn.prepareStatement(sqlMap);
                    ps2.setInt(1, newSensorId);
                    ps2.setString(2, pollutantName);
                    ps2.setInt(3, csvIndex);

                    ps2.executeUpdate();

                    // 5. Nếu mọi thứ tốt đẹp, nhấn nút "Lưu vĩnh viễn"
                    conn.commit();
                    return true;
                }
            }
        } catch (Exception e) {
            // 6. Nếu có bất kỳ lỗi nào, hủy bỏ toàn bộ thao tác trước đó
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            // Đóng kết nối và trả về chế độ mặc định
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Công dụng: Truy xuất toàn bộ danh sách cảm biến hiện có. Cơ chế: Sử dụng
     * helper method để map dữ liệu từ ResultSet sang Object.
     */
    public List<SensorDTO> getAllSensors(Connection conn) {
        List<SensorDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Sensor";
        try ( PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToSensor(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Công dụng: Lọc danh sách cảm biến thuộc về một phòng cụ thể (roomID). Cơ
     * chế: Truy vấn có điều kiện (WHERE clause) giúp tối ưu hóa hiệu năng thay
     * vì lấy toàn bộ dữ liệu.
     */
    public List<SensorDTO> getSensorsByRoom(int roomID, Connection conn) {
        List<SensorDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Sensor WHERE room_id = ?";
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSensor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Công dụng: Phương thức hỗ trợ (Helper) để chuyển đổi từ dòng dữ liệu SQL
     * (ResultSet) sang đối tượng (DTO). Lợi ích: Tăng khả năng bảo trì, tránh
     * lặp code (DRY - Don't Repeat Yourself).
     */
    private SensorDTO mapResultSetToSensor(ResultSet rs) throws SQLException {
        return new SensorDTO(
                rs.getInt("sensor_id"),
                rs.getInt("room_id"),
                rs.getString("serial_no"),
                rs.getString("model"),
                rs.getBoolean("status"),
                rs.getTimestamp("installed_at") != null ? rs.getTimestamp("installed_at").toLocalDateTime() : null,
                rs.getTimestamp("last_seen_ts") != null ? rs.getTimestamp("last_seen_ts").toLocalDateTime() : null
        );
    }

    public List<SensorDTO> getAllSensorsWithMap() {
        List<SensorDTO> list = new ArrayList<>();
        String sql = "SELECT s.sensor_id, s.room_id, s.serial_no, s.model, s.status, "
                + "s.installed_at, s.last_seen_ts, m.csv_column_index "
                + "FROM dbo.Sensor s "
                + "LEFT JOIN SensorPollutantMap m ON s.sensor_id = m.sensor_id " // Dùng LEFT JOIN
                + "WHERE s.status = 1";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SensorDTO dto = new SensorDTO();
                dto.setSensorID(rs.getInt("sensor_id"));
                dto.setRoomID(rs.getInt("room_id"));
                dto.setSerialNo(rs.getString("serial_no"));
                dto.setModel(rs.getString("model"));
                dto.setStatus(rs.getBoolean("status"));
                dto.setCsvColumnIndex(rs.getInt("csv_column_index"));

                // CHỈ GỌI toLocalDateTime() NẾU DỮ LIỆU KHÔNG NULL
                Timestamp installedAt = rs.getTimestamp("installed_at");
                if (installedAt != null) {
                    dto.setInstalledAt(installedAt.toLocalDateTime());
                }

                Timestamp lastSeen = rs.getTimestamp("last_seen_ts");
                if (lastSeen != null) {
                    dto.setLastSeenTs(lastSeen.toLocalDateTime());
                }

                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //Update sensor status for when the sensor is inactive
    public void updateSensorStatus(int sensorID, boolean status) {
        String sql = "UPDATE Sensor SET status = ? WHERE sensor_id = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, sensorID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getRecentlyInactiveIds(int seconds) {
        List<Integer> list = new ArrayList<>();
        // Tìm các sensor Inactive (status=0) mà thời điểm cập nhật cuối cùng là trong vòng X giây qua
        String sql = "SELECT sensor_id FROM Sensor WHERE status = 0 AND last_seen_ts >= DATEADD(second, ?, GETDATE())";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, -seconds); // Lùi lại X giây
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("sensor_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteSensor(int sensorID) {
        String sqlMap = "DELETE FROM SensorPollutantMap WHERE sensor_id = ?";
        String sqlSensor = "DELETE FROM Sensor WHERE sensor_id = ?";

        try ( Connection conn = DBUtils.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu giao dịch
            try ( PreparedStatement ps1 = conn.prepareStatement(sqlMap);  PreparedStatement ps2 = conn.prepareStatement(sqlSensor)) {

                ps1.setInt(1, sensorID);
                ps1.executeUpdate();

                ps2.setInt(1, sensorID);
                int rows = ps2.executeUpdate();

                conn.commit(); // Lưu thay đổi
                return rows > 0;
            } catch (Exception e) {
                conn.rollback(); // Lỗi thì quay lại
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isSensorActive(int sensorID) {
        String sql = "SELECT status FROM Sensor WHERE sensor_id = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sensorID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("status");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
