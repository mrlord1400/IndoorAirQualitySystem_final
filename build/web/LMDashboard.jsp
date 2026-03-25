<%@page import="java.util.List"%>
<%@page import="model.AlertDTO"%>
<%@page import="model.AlertDAO"%>
<%@page import="model.UserDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Lab Manager Dashboard</title>
        <style>
            body {
                font-family: sans-serif;
                background: #f0f2f5;
                padding: 20px;
            }
            .card {
                background: white;
                padding: 15px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
            }
            th, td {
                border: 1px solid #ddd;
                padding: 10px;
                text-align: left;
            }
            th {
                background: #eee;
            }
            .btn {
                padding: 10px 20px;
                text-decoration: none;
                background: #007bff;
                color: white;
                border-radius: 4px;
            }
            .status-OPEN {
                color: red;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <%
            UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
            if (loginUser == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            AlertDAO dao = new AlertDAO();
            List<AlertDTO> list = dao.getAllAlerts();
        %>

        <div class="card">
            <h2>Xin chào, <%= loginUser.getUsername()%></h2>
            <p>Vai trò: Lab Manager</p>
            <a href="login.jsp" style="color: red;">Đăng xuất</a>
        </div>

        <div class="card">
            <h3>Lối tắt hệ thống</h3>
            <a href="LMAlert.jsp" class="btn">Quản lý Cảnh báo</a>
            <a href="MainController?action=Rule" class="btn" style="background: #800000;">Quản lý Quy Luật</a>
            <a href="LMSensor.jsp" class="btn" style="background: #28a745;">Xem Sensor</a>
            <a href="MainController?action=Reading&subAction=list" class="btn" style="background: #e74c3c;">Xem Reading</a>
        </div>

        <div class="card">
            <h3>Cảnh báo mới nhất</h3>
            <table>
                <tr>
                    <th>ID</th>
                    <th>Phòng</th>
                    <th>Loại</th>
                    <th>Mức độ</th>
                    <th>Trạng thái</th>
                </tr>
                <%
                    int count = 0;
                    for (AlertDTO a : list) {
                        if (count++ > 4) {
                            break; // Chỉ hiện 5 cái mới nhất
                        }%>
                <tr>
                    <td><%= a.getAlertID()%></td>
                    <td><%= a.getRoomID()%></td>
                    <td><%= a.getAlertType()%></td>
                    <td><%= a.getSeverity()%></td>
                    <td class="status-<%= a.getStatus()%>"><%= a.getStatus()%></td>
                </tr>
                <% }%>
            </table>
        </div>
    </body>
</html>