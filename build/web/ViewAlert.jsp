<%@page import="java.util.List"%>
<%@page import="model.AlertDTO"%>
<%@page import="model.AlertDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Lịch sử Cảnh báo - IAQ Monitoring</title>
    <style>
        body { font-family: Arial, sans-serif; background: #eceff1; padding: 30px; }
        .alert-container { max-width: 900px; margin: auto; }
        .alert-item { background: white; padding: 20px; border-radius: 8px; margin-bottom: 15px; border-left: 10px solid #bdc3c7; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
        .severity-HIGH { border-left-color: #e74c3c; }
        .severity-MID { border-left-color: #f1c40f; }
        .badge { display: inline-block; padding: 5px 12px; border-radius: 15px; font-size: 12px; color: white; font-weight: bold; }
        .bg-OPEN { background: #e74c3c; }
        .bg-ACK { background: #3498db; }
        .bg-CLOSE { background: #95a5a6; }
        .info-row { color: #7f8c8d; font-size: 0.9em; margin-top: 10px; border-top: 1px solid #f1f1f1; padding-top: 10px; }
    </style>
</head>
<body>
    <div class="alert-container">
        <a href="ViewDashboard.jsp" style="text-decoration: none; color: #34495e;">← Quay lại Tổng quan</a>
        <h2>Danh sách chi tiết Cảnh báo</h2>
        <p style="color: #666;">Chế độ: Chỉ xem (Viewer)</p>
        <hr>

        <%
            AlertDAO dao = new AlertDAO();
            List<AlertDTO> alerts = dao.getAllAlerts();
            if(alerts == null || alerts.isEmpty()){
        %>
            <p>Hiện không có dữ liệu cảnh báo nào.</p>
        <%
            } else {
                for (AlertDTO alert : alerts) {
        %>
            <div class="alert-item severity-<%= alert.getSeverity() %>">
                <div style="display: flex; justify-content: space-between; align-items: flex-start;">
                    <div>
                        <h4 style="margin: 0; color: #2c3e50;">#<%= alert.getAlertID() %> - <%= alert.getAlertType() %></h4>
                        <p style="margin: 8px 0;"><%= alert.getMessage() %></p>
                    </div>
                    <span class="badge bg-<%= alert.getStatus() %>"><%= alert.getStatus() %></span>
                </div>
                
                <div class="info-row">
                    <span><strong>Phòng:</strong> <%= alert.getRoomID() %></span> | 
                    <span><strong>Cảm biến:</strong> <%= alert.getSensorID() %></span> | 
                    <span><strong>Thời gian:</strong> <%= alert.getStartTs() %></span> |
                    <span><strong>Mức độ:</strong> <%= alert.getSeverity() %></span>
                </div>
            </div>
        <% 
                }
            } 
        %>
    </div>
</body>
</html>