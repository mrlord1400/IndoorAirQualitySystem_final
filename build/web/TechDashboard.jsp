<%@page import="java.util.List"%>
<%@page import="model.AlertDTO"%>
<%@page import="model.AlertDAO"%>
<%@page import="model.UserDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Technician Dashboard - Air Quality</title>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; background: #e9ecef; margin: 0; padding: 20px; }
        .container { max-width: 1100px; margin: auto; }
        .nav { background: #343a40; color: white; padding: 15px; border-radius: 5px; margin-bottom: 20px; display: flex; justify-content: space-between; }
        .card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; }
        .stat-box { padding: 20px; color: white; border-radius: 5px; text-align: center; }
        .bg-tech { background: #17a2b8; } /* Màu xanh Teal cho Technician */
        .bg-danger { background: #dc3545; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { border-bottom: 1px solid #dee2e6; padding: 12px; text-align: left; }
        th { background: #f8f9fa; }
        .btn-action { text-decoration: none; background: #007bff; color: white; padding: 8px 15px; border-radius: 4px; display: inline-block; }
    </style>
</head>
<body>
    <%
        UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
        // Kiểm tra quyền: Chỉ cho phép Technician (Role 3) hoặc Admin (Role 1)
        if (loginUser == null || (loginUser.getRoleID() != 3 && loginUser.getRoleID() != 1)) {
            response.sendRedirect("login.jsp");
            return;
        }

        AlertDAO dao = new AlertDAO();
        List<AlertDTO> allAlerts = dao.getAllAlerts();
    %>

    <div class="container">
        <div class="nav">
            <span><strong>TECH PANEL:</strong> <%= loginUser.getUsername() %></span>
            <a href="MainController?action=Logout" style="color: #ffc107; text-decoration: none;">Đăng xuất</a>
        </div>

        <div class="grid">
            <div class="stat-box bg-tech">
                <h3>Vận hành</h3>
                <p>Hệ thống đang chạy</p>
            </div>
            <div class="stat-box bg-danger">
                <h3>Cảnh báo</h3>
                <p>Cần kiểm tra thiết bị</p>
            </div>
        </div>

        <div class="card">
            <h3>Nhiệm vụ kỹ thuật</h3>
            <div style="gap: 10px; display: flex;">
                <a href="TechAlert.jsp" class="btn-action">Xử lý sự cố (Alerts)</a>
                <a href="sensor.jsp" class="btn-action" style="background: #6c757d;">Bảo trì cảm biến</a>
                <a href="MainController?action=Reading&subAction=list" class="btn-action" style="background: #e74c3c;">Xem Reading</a>
            </div>
        </div>

        <div class="card">
            <h3>Cảnh báo thiết bị gần đây</h3>
            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Phòng</th>
                        <th>Loại lỗi</th>
                        <th>Mức độ</th>
                        <th>Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                        int limit = 0;
                        for (AlertDTO a : allAlerts) { 
                            if(limit++ >= 5) break; 
                    %>
                    <tr>
                        <td>#<%= a.getAlertID() %></td>
                        <td>Room <%= a.getRoomID() %></td>
                        <td><%= a.getAlertType() %></td>
                        <td><strong><%= a.getSeverity() %></strong></td>
                        <td><%= a.getStatus() %></td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>