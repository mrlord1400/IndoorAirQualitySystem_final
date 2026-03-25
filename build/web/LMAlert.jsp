<%@page import="java.util.List"%>
<%@page import="model.AlertDTO"%>
<%@page import="model.AlertDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Danh sách Cảnh báo</title>
    <style>
        body { font-family: sans-serif; padding: 20px; line-height: 1.6; }
        .alert-box { border: 1px solid #ccc; padding: 15px; margin-bottom: 10px; border-radius: 5px; background: #fff; }
        .HIGH { border-left: 10px solid red; }
        .MID { border-left: 10px solid orange; }
        .status-badge { padding: 2px 8px; border-radius: 3px; font-size: 0.8em; color: white; }
        .bg-OPEN { background: red; }
        .bg-ACK { background: blue; }
        .bg-CLOSE { background: gray; }
        .success-msg { color: green; font-weight: bold; margin-bottom: 15px; }
    </style>
</head>
<body>
    <h2>Hệ thống Quản lý Cảnh báo</h2>
    <a href="LMDashboard.jsp"> < Quay lại Dashboard</a>
    <hr>

    <%
        String msg = (String) session.getAttribute("MSG_SUCCESS");
        if (msg != null) {
            out.print("<div class='success-msg'>" + msg + "</div>");
            session.removeAttribute("MSG_SUCCESS");
        }

        AlertDAO dao = new AlertDAO();
        List<AlertDTO> alerts = dao.getAllAlerts();
        for (AlertDTO alert : alerts) {
    %>
        <div class="alert-box <%= alert.getSeverity() %>">
            <div style="display: flex; justify-content: space-between;">
                <strong>Mã cảnh báo: #<%= alert.getAlertID() %></strong>
                <span class="status-badge bg-<%= alert.getStatus() %>"><%= alert.getStatus() %></span>
            </div>
            <p><strong>Nội dung:</strong> <%= alert.getMessage() %></p>
            <p style="font-size: 0.9em; color: #555;">
                Phòng: <%= alert.getRoomID() %> | 
                Cảm biến: <%= alert.getSensorID() %> | 
                Bắt đầu: <%= alert.getStartTs() %>
            </p>

            <% if (!"CLOSE".equals(alert.getStatus())) { %>
                <form action="AlertController" method="POST">
                    <input type="hidden" name="alertId" value="<%= alert.getAlertID() %>">
                    
                    <input type="text" name="note" placeholder="Nhập ghi chú xử lý..." style="width: 250px;">
                    
                    <% if ("OPEN".equals(alert.getStatus())) { %>
                        <button type="submit" name="subAction" value="ack" style="background: #ffc107;">Xác nhận (ACK)</button>
                    <% } %>
                    
                    <button type="submit" name="subAction" value="close" style="background: #28a745; color: white;">Đóng (CLOSE)</button>
                </form>
            <% } else { %>
                <p style="color: green;"> Cảnh báo này đã được xử lý hoàn tất.</p>
            <% } %>
        </div>
    <% } %>

</body>
</html>