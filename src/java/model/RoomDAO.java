package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import utils.DBUtils;

/**
 * Lớp DAO (Data Access Object) thực hiện các thao tác CRUD với bảng 'Room'. Sử
 * dụng DataSource để kết nối DB, tách biệt logic kết nối khỏi DAO[cite: 60].
 */
public class RoomDAO {

    // Khởi tạo DAO bằng cách tiêm DataSource (Dependency Injection)
    public RoomDAO() {
    }

    /**
     * Lấy toàn bộ danh sách phòng từ database[cite: 22, 23].
     *
     * @return Danh sách các đối tượng RoomDTO.
     */
    public List<RoomDTO> getAllRooms() {
        List<RoomDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Room";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            // Duyệt qua từng dòng kết quả và map vào DTO
            while (rs.next()) {
                list.add(mapResultSetToRoom(rs));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi để truy vết
        }
        return list;
    }

    /**
     * Thêm một phòng mới vào hệ thống[cite: 22].
     *
     * @param room Đối tượng RoomDTO chứa thông tin cần thêm.
     * @return true nếu thêm thành công, false nếu lỗi.
     */
    public boolean insertRoom(RoomDTO room) {
        String sql = "INSERT INTO Room (code, name, room_type, location, status) VALUES (?, ?, ?, ?, ?)";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getCode());
            ps.setString(2, room.getName());
            ps.setString(3, room.getType());
            ps.setString(4, room.getLocation());
            ps.setBoolean(5, room.isStatus());
            // executeUpdate trả về số dòng bị ảnh hưởng
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin chi tiết của một phòng dựa trên roomID[cite: 22].
     *
     * @param room Đối tượng RoomDTO đã cập nhật.
     */
    public boolean updateRoom(RoomDTO room) {
        String sql = "UPDATE Room SET code = ?, name = ?, room_type = ?, location = ?, status = ? WHERE room_id = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getCode());
            ps.setString(2, room.getName());
            ps.setString(3, room.getType());
            ps.setString(4, room.getLocation());
            ps.setBoolean(5, room.isStatus());
            ps.setInt(6, room.getRoomID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa mềm (Soft delete) bằng cách đổi trạng thái hoạt động thành 0[cite:
     * 106].
     *
     * @param roomID ID của phòng cần xóa.
     */
    public boolean disableRoom(int roomID) {
        String sql = "UPDATE Room SET status = 0 WHERE room_id = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int roomID) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmRole = null;
        PreparedStatement pstmUser = null;
        boolean result = false;

        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false);
                String sqlAlert = "DELETE FROM Alert WHERE room_id = ?";
                pstmRole = conn.prepareStatement(sqlAlert);
                pstmRole.setInt(1, roomID);
                pstmRole.executeUpdate();

                String sqlSensor = "DELETE FROM Sensor WHERE room_id = ?";
                pstmRole = conn.prepareStatement(sqlAlert);
                pstmRole.setInt(1, roomID);
                pstmRole.executeUpdate();

                String sqlRule = "DELETE FROM ThresholdRule WHERE room_id = ?";
                pstmRole = conn.prepareStatement(sqlRule);
                pstmRole.setInt(1, roomID);
                pstmRole.executeUpdate();

                String sqlRoom = "DELETE FROM Room WHERE room_id = ?";
                pstmUser = conn.prepareStatement(sqlRoom);
                pstmUser.setInt(1, roomID);
                int affectedRows = pstmUser.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit();
                    result = true;
                } else {
                    conn.rollback();
                }
            }
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
        } finally {
            if (pstmRole != null) {
                pstmRole.close();
            }
            if (pstmUser != null) {
                pstmUser.close();
            }
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
        return result;
    }

    /**
     * Phương thức hỗ trợ: Chuyển đổi dữ liệu từ ResultSet sang DTO[cite: 73].
     * Giúp code gọn hơn và tái sử dụng được ở nhiều hàm READ.
     */
    private RoomDTO mapResultSetToRoom(ResultSet rs) throws SQLException {
        return new RoomDTO(
                rs.getInt("room_id"),
                rs.getString("code"),
                rs.getString("name"),
                rs.getString("room_type"),
                rs.getString("location"),
                rs.getBoolean("status"),
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    }
}
