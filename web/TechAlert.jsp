<%@page import="java.util.List"%>
<%@page import="model.AlertDTO"%>
<%@page import="model.AlertDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Technician - Alert Handling</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f4f4; padding: 25px; }
        .alert-item { background: white; border: 1px solid #ddd; padding: 20px; border-radius: 5px; margin-bottom: 15px; position: relative; }
        .border-HIGH { border-left: 10px solid #ff4757; }
        .border-MID { border-left: 10px solid #ffa502; }
        .badge { padding: 3px 10px; border-radius: 10px; font-size: 12px; color: white; }
        .status-OPEN { background: #ff4757; }
        .status-ACK { background: #2f3542; }
        .status-CLOSE { background: #2ed573; }
        .form-group { margin-top: 15px; padding-top: 15px; border-top: 1px dashed #eee; }
        input[type="text"] { padding: 8px; width: 300px; border: 1px solid #ccc; }
        button { padding: 8px 15px; cursor: pointer; border: none; border-radius: 3px; color: white; font-weight: bold; }
        .btn-ack { background: #1e90ff; }
        .btn-close { background: #2ed573; margin-left: 5px; }
        .msg { color: #2ed573; font-weight: bold; background: #e3f9eb; padding: 10px; margin-bottom: 15px; }
    </style>
</head>
<body>
    <div style="max-width: 900px; margin: auto;">
        <a href="TechDashboard.jsp" style="text-decoration: none;">← Quay lại Dashboard</a>
        <h2>Khu vực xử lý sự cố Kỹ thuật</h2>

        <%-- Hiển thị thông báo khi Controller xử lý xong --%>
        <%
            String success = (String) session.getAttribute("MSG_SUCCESS");
            if (success != null) {
        %>
            <div class="msg"><%= success %></div>
        <% 
                session.removeAttribute("MSG_SUCCESS");
            } 
        %>

        <%
            AlertDAO dao = new AlertDAO();
            List<AlertDTO> list = dao.getAllAlerts();
            for (AlertDTO alert : list) {
        %>
            <div class="alert-item border-<%= alert.getSeverity() %>">
                <span class="badge status-<%= alert.getStatus() %>"><%= alert.getStatus() %></span>
                <h4 style="margin: 10px 0;">Sự cố #<%= alert.getAlertID() %> - <%= alert.getAlertType() %></h4>
                <p><strong>Thông điệp:</strong> <%= alert.getMessage() %></p>
                <p style="color: #666; font-size: 13px;">
                    Thiết bị: Sensor <%= alert.getSensorID() %> | 
                    Vị trí: Phòng <%= alert.getRoomID() %> | 
                    Thời gian: <%= alert.getStartTs() %>
                </p>

                <% if (!"CLOSE".equals(alert.getStatus())) { %>
                    <div class="form-group">
                        <form action="AlertController" method="POST">
                            <input type="hidden" name="alertId" value="<%= alert.getAlertID() %>">
                            <input type="text" name="note" placeholder="Báo cáo tình trạng xử lý kỹ thuật..." required>
                            
                            <% if ("OPEN".equals(alert.getStatus())) { %>
                                <button type="submit" name="subAction" value="ack" class="btn-ack">Tiếp nhận (ACK)</button>
                            <% } %>
                            
                            <button type="submit" name="subAction" value="close" class="btn-close">Hoàn tất sửa chữa (CLOSE)</button>
                        </form>
                    </div>
                <% } else { %>
                    <div style="margin-top: 10px; color: #2ed573;"> Đã khắc phục xong.</div>
                <% } %>
            </div>
        <% } %>
    </div>
</body>
</html>