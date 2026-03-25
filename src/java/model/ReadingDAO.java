package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class ReadingDAO {

    // Hàm này add đối tượng ReadingDTO và lưu nó vào bảng Reading trong SQL
    public boolean insertReading(ReadingDTO reading) {
        String sql = "INSERT INTO Reading (sensor_id, pollutant_id, ts, value, quality_flag)"
                + " VALUES (?, ?, ?, ?, ?)";

        try ( Connection conn = DBUtils.getConnection()) {
            if (conn != null) {
                PreparedStatement ps = conn.prepareStatement(sql);

                ps.setInt(1, reading.getSensorID());
                ps.setInt(2, reading.getPollutantID());
                // Chuyển LocalDateTime của Java sang Timestamp của SQL
                ps.setTimestamp(3, Timestamp.valueOf(reading.getTs()));
                ps.setDouble(4, reading.getValue());
                ps.setString(5, reading.getQualityFlag());

                int row = ps.executeUpdate();

                return row > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ReadingDTO> getAllReadings() {
        List<ReadingDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Reading";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReadingDTO dto = new ReadingDTO(
                        rs.getLong("reading_id"),
                        rs.getInt("sensor_id"),
                        rs.getInt("pollutant_id"),
                        rs.getTimestamp("ts").toLocalDateTime(),
                        rs.getDouble("value"),
                        rs.getString("quality_flag"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy N bản ghi gần nhất để kiểm tra xem có vi phạm liên tục không
    public List<ReadingDTO> getRecentReadings(int roomID, int pollutantID, int limit) {
        List<ReadingDTO> list = new ArrayList<>();
        // Sử dụng JOIN với bảng Sensor để lọc theo RoomID
        String sql = "SELECT TOP (?) r.* FROM Reading r " +
                     "JOIN Sensor s ON r.sensor_id = s.sensor_id " +
                     "WHERE s.room_id = ? AND r.pollutant_id = ? " +
                     "ORDER BY r.ts DESC";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, roomID);
            ps.setInt(3, pollutantID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime ts = rs.getTimestamp("ts").toLocalDateTime();
                list.add(new ReadingDTO(rs.getLong("reading_id"), rs.getInt("sensor_id"), 
                                     rs.getInt("pollutant_id"), ts, rs.getDouble("value"), 
                                     rs.getString("quality_flag"), null));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /* ===========================================================
       BỔ SUNG: CHỨC NĂNG ADVANCED SEARCH (KHÔNG THAY ĐỔI CODE CŨ)
       =========================================================== */
    
    /**
     * Thực hiện tìm kiếm nâng cao kết hợp nhiều tiêu chí lọc.
     * Đáp ứng mục tiêu Giai đoạn 3 của đồ án[cite: 109].
     */
    public List<ReadingDTO> searchAdvanced(Integer roomID, Integer pollutantID, String fromDate, String toDate) {
        List<ReadingDTO> list = new ArrayList<>();
        // Sử dụng JOIN để lọc theo RoomID vì bảng Reading không chứa room_id trực tiếp
        StringBuilder sql = new StringBuilder(
            "SELECT r.* FROM Reading r " +
            "JOIN Sensor s ON r.sensor_id = s.sensor_id " +
            "WHERE 1=1"
        );

        // Cộng dồn điều kiện tìm kiếm động
        if (roomID != null) sql.append(" AND s.room_id = ?");
        if (pollutantID != null) sql.append(" AND r.pollutant_id = ?");
        if (fromDate != null && !fromDate.isEmpty()) sql.append(" AND r.ts >= ?");
        if (toDate != null && !toDate.isEmpty()) sql.append(" AND r.ts <= ?");

        sql.append(" ORDER BY r.ts DESC");

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIdx = 1;
            if (roomID != null) ps.setInt(paramIdx++, roomID);
            if (pollutantID != null) ps.setInt(paramIdx++, pollutantID);
            if (fromDate != null && !fromDate.isEmpty()) ps.setString(paramIdx++, fromDate);
            if (toDate != null && !toDate.isEmpty()) ps.setString(paramIdx++, toDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReadingDTO(
                    rs.getLong("reading_id"),
                    rs.getInt("sensor_id"),
                    rs.getInt("pollutant_id"),
                    rs.getTimestamp("ts").toLocalDateTime(),
                    rs.getDouble("value"),
                    rs.getString("quality_flag"),
                    rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    //Đếm tổng số bản ghi dựa trên bộ lọc (Để tính tổng số trang)
    public int getTotalRecords(Integer roomID, Integer pollutantID, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Reading r " +
            "JOIN Sensor s ON r.sensor_id = s.sensor_id " +
            "WHERE 1=1"
        );

        // Cộng dồn điều kiện lọc động
        if (roomID != null) sql.append(" AND s.room_id = ?");
        if (pollutantID != null) sql.append(" AND r.pollutant_id = ?");
        if (fromDate != null && !fromDate.isEmpty()) sql.append(" AND r.ts >= ?");
        if (toDate != null && !toDate.isEmpty()) sql.append(" AND r.ts <= ?");

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIdx = 1;
            if (roomID != null) ps.setInt(paramIdx++, roomID);
            if (pollutantID != null) ps.setInt(paramIdx++, pollutantID);
            if (fromDate != null && !fromDate.isEmpty()) ps.setString(paramIdx++, fromDate);
            if (toDate != null && !toDate.isEmpty()) ps.setString(paramIdx++, toDate);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Lấy dữ liệu theo trang (Phân trang thực tế từ Database)
    public List<ReadingDTO> searchAdvancedWithPaging(Integer roomID, Integer pollutantID, 
            String fromDate, String toDate, int pageIndex, int pageSize) {
        
        List<ReadingDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT r.* FROM Reading r " +
            "JOIN Sensor s ON r.sensor_id = s.sensor_id " +
            "WHERE 1=1"
        );

        if (roomID != null) sql.append(" AND s.room_id = ?");
        if (pollutantID != null) sql.append(" AND r.pollutant_id = ?");
        if (fromDate != null && !fromDate.isEmpty()) sql.append(" AND r.ts >= ?");
        if (toDate != null && !toDate.isEmpty()) sql.append(" AND r.ts <= ?");

        // Cú pháp phân trang cho SQL Server
        sql.append(" ORDER BY r.ts DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIdx = 1;
            if (roomID != null) ps.setInt(paramIdx++, roomID);
            if (pollutantID != null) ps.setInt(paramIdx++, pollutantID);
            if (fromDate != null && !fromDate.isEmpty()) ps.setString(paramIdx++, fromDate);
            if (toDate != null && !toDate.isEmpty()) ps.setString(paramIdx++, toDate);
            
            // Tính toán OFFSET (vị trí bắt đầu)
            ps.setInt(paramIdx++, (pageIndex - 1) * pageSize);
            ps.setInt(paramIdx++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ReadingDTO(
                        rs.getLong("reading_id"),
                        rs.getInt("sensor_id"),
                        rs.getInt("pollutant_id"),
                        rs.getTimestamp("ts").toLocalDateTime(),
                        rs.getDouble("value"),
                        rs.getString("quality_flag"),
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean deleteAllReadings() {
        String sql = "DELETE FROM Reading"; // Xóa toàn bộ dữ liệu trong bảng
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
