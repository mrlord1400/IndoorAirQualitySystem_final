<%@page import="model.SensorDTO"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xem danh sách Sensor</title>
        <style>
            :root {
                --primary: #2563eb;
                --success: #22c55e;
                --bg: #f8fafc;
                --text-main: #1e293b;
                --text-muted: #64748b;
                --card-bg: #ffffff;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: var(--bg);
                color: var(--text-main);
                margin: 0;
                padding: 30px;
            }

            .container {
                max-width: 1100px;
                margin: 0 auto;
            }

            .header-area {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 25px;
            }

            .actions-right {
                display: flex;
                gap: 10px;
            }

            h2 {
                margin: 0;
                font-size: 1.5rem;
                color: #0f172a;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                padding: 8px 16px;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 500;
                text-decoration: none;
                transition: all 0.2s;
                cursor: pointer;
                border: none;
            }

            .btn-refresh {
                background: var(--primary);
                color: white;
            }
            .btn-refresh:hover {
                background: #1d4ed8;
            }

            .btn-back {
                background: var(--success);
                color: white;
            }
            .btn-back:hover {
                opacity: 0.9;
            }

            .table-container {
                background: var(--card-bg);
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
            }

            table {
                width: 100%;
                border-collapse: collapse;
                text-align: left;
            }
            th {
                background-color: #f1f5f9;
                color: var(--text-muted);
                padding: 12px 15px;
                text-transform: uppercase;
                font-size: 12px;
                letter-spacing: 0.5px;
            }
            td {
                padding: 12px 15px;
                border-bottom: 1px solid #f1f5f9;
                font-size: 14px;
            }
            tr:hover {
                background-color: #f8fafc;
            }

            .badge {
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: bold;
                display: inline-block;
            }
            .status-on {
                background: #dcfce7;
                color: #166534;
            }
            .status-off {
                background: #fee2e2;
                color: #991b1b;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header-area">
                <h2>Dữ liệu thiết bị Sensor (Chế độ xem)</h2>
                <div class="actions-right">
                    <a href="MainController?action=SensorLM" class="btn btn-refresh">Xem tất cả</a>
                    <a href="LMDashboard.jsp" class="btn btn-back">← Quay lại Dashboard</a>
                </div>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Vị trí</th>
                            <th>Số Serial</th>
                            <th>Model</th>
                            <th>Trạng thái</th>
                            <th>Cập nhật cuối</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
                            // Lấy danh sách từ request mà SensorForLMController đã set
                            List<SensorDTO> list = (List<SensorDTO>) request.getAttribute("SENSOR_LIST");

                            if (list != null && !list.isEmpty()) {
                                for (SensorDTO s : list) {
                        %>
                        <tr>
                            <td><strong>#<%= s.getSensorID()%></strong></td>
                            <td>Phòng <%= s.getRoomID()%></td>
                            <td><code><%= s.getSerialNo()%></code></td>
                            <td><%= s.getModel()%></td>
                            <td>
                                <% if (s.isStatus()) { %>
                                <span class="badge status-on">ONLINE</span>
                                <% } else { %>
                                <span class="badge status-off">OFFLINE</span>
                                <% }%>
                            </td>
                            <td style="color: var(--text-muted);">
                                <%= (s.getLastSeenTs() != null) ? s.getLastSeenTs() : "N/A"%>
                            </td>
                        </tr>
                        <%
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 50px; color: var(--text-muted);">
                                <i>Đang tải dữ liệu hoặc không có cảm biến nào...</i>
                            </td>
                        </tr>
                        <% }%>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>