/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

/**
 * @author Admin
 */
public class UserDAO {

    private static final String LOGIN = "SELECT u.*, ur.role_id\n"
            + "FROM Users u\n"
            + "JOIN UserRole ur ON u.user_id = ur.user_id\n"
            + "WHERE u.username = ? AND u.password_hash = ? AND u.status = 1";

    public UserDTO login(String username, String password) throws SQLException {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(LOGIN);
                ptm.setString(1, username);
                ptm.setString(2, password);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    int userID = rs.getInt("user_id");
                    String fullName = rs.getString("full_name");
                    int roleID = rs.getInt("role_ID");
                    String email = rs.getString("email");
                    boolean status = rs.getBoolean("Status");
                    Timestamp ts = rs.getTimestamp("created_at");
                    LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;
                    user = new UserDTO(userID, username, password, fullName, email, roleID, status, createdAt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) rs.close();
            if (ptm != null) ptm.close();
            if (conn != null) conn.close();
        }
        return user;
    }

    //Phần Huy chèn vào

    public boolean insertUser(UserDTO user) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement pstmUser = null;
        PreparedStatement pstmRole = null;
        boolean result = false;

        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false);
                String sqlUser = "INSERT INTO Users (username, password_hash, full_name, email, status) VALUES (?, ?, ?, ?, ?)";
                pstmUser = conn.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmUser.setString(1, user.getUsername());
                pstmUser.setString(2, user.getPassword());
                pstmUser.setNString(3, user.getFullname());
                pstmUser.setString(4, user.getEmail());
                pstmUser.setBoolean(5, user.isStatus());

                int affectedRows = pstmUser.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet rs = pstmUser.getGeneratedKeys();
                    if (rs.next()) {
                        int userId = rs.getInt(1);
                        String sqlRole = "INSERT INTO UserRole (user_id, role_id) VALUES (?, ?)";
                        pstmRole = conn.prepareStatement(sqlRole);
                        pstmRole.setInt(1, userId);
                        pstmRole.setInt(2, user.getRoleID());
                        pstmRole.executeUpdate();
                        conn.commit();
                        result = true;
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmRole != null) pstmRole.close();
            if (pstmUser != null) pstmUser.close();
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
        return result;
    }

    public boolean updateUser(UserDTO user) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement pstmUser = null;
        PreparedStatement pstmRole = null;
        boolean result = false;

        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false);
                String sqlUser = "UPDATE Users SET password_hash = ?, full_name = ?, email = ?, status = ? WHERE user_id = ?";
                pstmUser = conn.prepareStatement(sqlUser);
                pstmUser.setString(1, user.getPassword());
                pstmUser.setNString(2, user.getFullname());
                pstmUser.setString(3, user.getEmail());
                pstmUser.setBoolean(4, user.isStatus());
                pstmUser.setInt(5, user.getUserID());

                int userUpdated = pstmUser.executeUpdate();
                String sqlRole = "UPDATE UserRole SET role_id = ? WHERE user_id = ?";
                pstmRole = conn.prepareStatement(sqlRole);
                pstmRole.setInt(1, user.getRoleID());
                pstmRole.setInt(2, user.getUserID());
                int roleUpdated = pstmRole.executeUpdate();

                if (userUpdated > 0 && roleUpdated > 0) {
                    conn.commit();
                    result = true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmRole != null) pstmRole.close();
            if (pstmUser != null) pstmUser.close();
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
        return result;
    }

    public boolean deleteUser(int userId) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement pstmRole = null;
        PreparedStatement pstmUser = null;
        boolean result = false;

        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                conn.setAutoCommit(false);
                String sqlRole = "DELETE FROM UserRole WHERE user_id = ?";
                pstmRole = conn.prepareStatement(sqlRole);
                pstmRole.setInt(1, userId);
                pstmRole.executeUpdate();

                String sqlUser = "DELETE FROM Users WHERE user_id = ?";
                pstmUser = conn.prepareStatement(sqlUser);
                pstmUser.setInt(1, userId);
                int affectedRows = pstmUser.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit();
                    result = true;
                } else {
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (pstmRole != null) pstmRole.close();
            if (pstmUser != null) pstmUser.close();
            if (conn != null) { conn.setAutoCommit(true); conn.close(); }
        }
        return result;
    }

    public List<UserDTO> getAllUsers() throws SQLException, ClassNotFoundException {
        List<UserDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.status, u.created_at, ur.role_id FROM Users u LEFT JOIN UserRole ur ON u.user_id = ur.user_id";
                pstm = conn.prepareStatement(sql);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String user = rs.getString("username");
                    String name = rs.getNString("full_name");
                    String email = rs.getString("email");
                    boolean status = rs.getBoolean("status");
                    LocalDateTime date = rs.getTimestamp("created_at").toLocalDateTime();
                    int roleID = rs.getInt("role_id");
                    list.add(new UserDTO(id, user, "***", name, email, roleID, status, date));
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (pstm != null) pstm.close();
            if (conn != null) conn.close();
        }
        return list;
    }
    
    public List<UserDTO> searchUsers(String searchTerm) throws SQLException, ClassNotFoundException {
        List<UserDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.status, u.created_at, ur.role_id FROM Users u LEFT JOIN UserRole ur ON u.user_id = ur.user_id WHERE u.username LIKE ?";
                String searchTerm2 = "%" + (searchTerm == null ? "" : searchTerm) + "%";
                pstm = conn.prepareStatement(sql);
                pstm.setString(1, searchTerm2);
                rs = pstm.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String user = rs.getString("username");
                    String name = rs.getNString("full_name");
                    String email = rs.getString("email");
                    boolean status = rs.getBoolean("status");
                    LocalDateTime date = rs.getTimestamp("created_at").toLocalDateTime();
                    int roleID = rs.getInt("role_id");
                    list.add(new UserDTO(id, user, "***", name, email, roleID, status, date));
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (pstm != null) pstm.close();
            if (conn != null) conn.close();
        }
        return list;
    }
}
