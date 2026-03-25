package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import utils.DBUtils;

/**
 * Lớp PollutantDAO quản lý các tương tác cơ sở dữ liệu cho thực thể 'Pollutant'.
 * Cung cấp các phương thức CRUD để vận hành danh mục chất đo không khí.
 */
public class PollutantDAO {

    /**
     * Khởi tạo DAO với DataSource từ container (Dependency Injection).
     * @param dataSource Kết nối nguồn dữ liệu SQL Server.
     */
    public PollutantDAO() {
    }

    /**
     * Truy xuất toàn bộ danh mục chất ô nhiễm hiện có trong hệ thống.
     * @return Danh sách các đối tượng PollutantDTO.
     */
    public List<PollutantDTO> getAllPollutants() {
        List<PollutantDTO> list = new ArrayList<>();
        // Query cơ bản lấy danh sách danh mục
        String sql = "SELECT * FROM Pollutant";
        
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            // Map từng bản ghi từ SQL ResultSet sang object DTO
            while (rs.next()) {
                list.add(new PollutantDTO(
                    rs.getInt("pollutant_id"),
                    rs.getString("name"),
                    rs.getString("unit")
                ));
            }
        } catch (Exception e) {
            // Ghi log lỗi để phục vụ debug hệ thống
            e.printStackTrace(); 
        }
        return list;
    }

    /**
     * Thực hiện thêm một loại chất đo mới vào database.
     * @param p Đối tượng PollutantDTO chứa thông tin định danh và đơn vị.
     * @return true nếu ghi dữ liệu thành công, ngược lại trả về false.
     */
    public boolean insertPollutant(PollutantDTO p) {
        String sql = "INSERT INTO Pollutant (name, unit) VALUES (?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // Sử dụng PreparedStatement để ngăn chặn SQL Injection
            ps.setString(1, p.getName());
            ps.setString(2, p.getUnit());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật thông tin chi tiết của một chất đo hiện có.
     * @param p Đối tượng PollutantDTO đã thay đổi thông tin.
     * @return true nếu cập nhật thành công, false nếu có lỗi xảy ra.
     */
    public boolean updatePollutant(PollutantDTO p) {
        String sql = "UPDATE Pollutant SET name = ?, unit = ? WHERE pollutant_id = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getUnit());
            ps.setInt(3, p.getPollutantID()); // Xác định ID để update đúng hàng
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
