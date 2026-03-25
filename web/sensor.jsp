<%@page import="model.SensorDTO"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Sensor</title>
        <style>
            /* Giữ nguyên toàn bộ CSS của bạn */
            :root {
                --primary: #2563eb;
                --primary-hover: #1d4ed8;
                --success: #22c55e;
                --danger: #ef4444;
                --bg: #f8fafc;
                --text-main: #1e293b;
                --text-muted: #64748b;
                --card-bg: #ffffff;
            }

            body {
                font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                background-color: var(--bg);
                color: var(--text-main);
                margin: 0;
                padding: 30px;
                line-height: 1.5;
            }

            .container {
                max-width: 1200px; /* Tăng nhẹ độ rộng để đủ chỗ cho cột mới */
                margin: 0 auto;
            }

            .header-area {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 25px;
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
                cursor: pointer;
                transition: all 0.2s;
                border: none;
                text-decoration: none;
            }

            .btn-primary {
                background: var(--primary);
                color: white;
            }
            .btn-primary:hover {
                background: var(--primary-hover);
            }

            .btn-secondary {
                background: #e2e8f0;
                color: var(--text-main);
                margin-right: 5px;
            }
            .btn-secondary:hover {
                background: #cbd5e1;
            }

            .btn-back {
                background: var(--success);
                color: white;
            }
            .btn-back:hover {
                opacity: 0.9;
            }

            .card {
                background: var(--card-bg);
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }

            .form-group {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                align-items: center;
            }

            input, select {
                padding: 8px 12px;
                border: 1px solid #ddd;
                border-radius: 6px;
                outline: none;
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
                font-weight: 600;
                padding: 12px 15px;
                text-transform: uppercase;
                font-size: 12px;
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
                <h2>Quản lý thiết bị Sensor</h2>
                <div>
                    <a href="MainController?action=Sensor" class="btn btn-secondary">Làm mới</a>
                    <a href="MainController?action=Dashboard" class="btn btn-back">← Quay lại Dashboard</a>
                </div>
            </div>

            <div class="card">
                <form action="MainController" method="POST" class="form-group">
                    <input type="hidden" name="action" value="Sensor" />
                    <input type="hidden" name="subAction" value="add" />
                    <strong style="width: 100%; margin-bottom: 5px;">Thêm mới Sensor & Mapping:</strong>

                    <input type="number" name="roomID" placeholder="Phòng ID" required style="width: 80px" />
                    <input type="text" name="serialNo" placeholder="Số Serial" required />
                    <input type="text" name="model" placeholder="Model" required />

                    <select name="pollutantInfo" required>
                        <option value="">-- Chọn loại chất đo --</option>
                        <option value="2|CO(GT)">Khí CO (Ground Truth)</option>
                        <option value="5|C6H6(GT)">Khí Benzene C6H6 (GT)</option>
                        <option value="12|T">Nhiệt độ (Temperature)</option>
                        <option value="13|RH">Độ ẩm tương đối (RH)</option>
                    </select>

                    <select name="status">
                        <option value="true">Active</option>
                        <option value="false">Inactive</option>
                    </select>

                    <button type="submit" class="btn btn-primary">Thêm</button>
                </form>
            </div>

            <div class="table-container">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Phòng</th>
                            <th>Serial No</th>
                            <th>Model</th>
                            <th>Trạng thái</th>
                            <th>Cập nhật cuối</th>
                            <th>Thao tác</th> </tr>
                    </thead>
                    <tbody>
                        <%
                            Object obj = request.getAttribute("SENSOR_LIST");
                            List<SensorDTO> list = (obj instanceof List) ? (List<SensorDTO>) obj : null;

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
                                <span class="badge status-on">ĐANG HOẠT ĐỘNG</span>
                                <% } else { %>
                                <span class="badge status-off">NGỪNG HOẠT ĐỘNG</span>
                                <% }%>
                            </td>
                            <td style="color: var(--text-muted); font-size: 13px;">
                                <%= (s.getLastSeenTs() != null) ? s.getLastSeenTs() : "Chưa có dữ liệu"%>
                            </td>
                            <td>
                                <form action="MainController" method="POST" style="display:inline;">
                                    <input type="hidden" name="action" value="Sensor" />
                                    <input type="hidden" name="subAction" value="toggle" />
                                    <input type="hidden" name="sensorID" value="<%= s.getSensorID()%>" />
                                    <input type="hidden" name="status" value="<%= s.isStatus()%>" />
                                    <button type="submit" class="btn btn-secondary" style="font-size: 12px; padding: 5px 10px;">
                                        <%= s.isStatus() ? "Vô hiệu hóa" : "Kích hoạt"%>
                                    </button>
                                </form>

                                <form action="MainController" method="POST" style="display:inline;" 
                                      onsubmit="return confirm('Bạn có chắc chắn muốn xóa Sensor #<%= s.getSensorID()%>?');">
                                    <input type="hidden" name="action" value="Sensor" />
                                    <input type="hidden" name="subAction" value="delete" />
                                    <input type="hidden" name="sensorID" value="<%= s.getSensorID()%>" />
                                    <button type="submit" class="btn btn-primary" style="background: var(--danger); font-size: 12px; padding: 5px 10px;">
                                        Xóa
                                    </button>
                                </form>
                            </td>
                        </tr>
                        <%
                            }
                        } else {
                        %>
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 40px; color: var(--text-muted);">
                                <i>Không tìm thấy dữ liệu cảm biến nào.</i>
                            </td>
                        </tr>
                        <%
                            }
                        %>
                    </tbody>
                </table>
            </div>
        </div>

        <script>
            function checkSensorStatus() {
                fetch('MainController?action=Sensor&subAction=checkStatus')
                        .then(response => response.json())
                        .then(ids => {
                            if (ids.length > 0) {
                                ids.forEach(id => {
                                    alert("Cảnh báo: Sensor #" + id + " vừa ngừng hoạt động (Lỗi -200)!");
                                });
                                location.reload();
                            }
                        });
            }
            setInterval(checkSensorStatus, 10000); // Tăng lên 10s để đỡ phiền người dùng khi đang thao tác
        </script>
    </body>
</html>
