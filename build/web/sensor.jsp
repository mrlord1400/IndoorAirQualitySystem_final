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
                max-width: 1100px;
                margin: 0 auto;
            }

            /* Header Section */
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

            /* Buttons */
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

            /* Cards/Forms */
            .card {
                background: var(--card-bg);
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.1);
                margin-bottom: 20px;
            }

            .grid-forms {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
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
                transition: border-color 0.2s;
            }

            input:focus {
                border-color: var(--primary);
            }

            /* Table Style */
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

            /* Status Badges */
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

            @media (max-width: 768px) {
                .grid-forms {
                    grid-template-columns: 1fr;
                }
                .header-area {
                    flex-direction: column;
                    align-items: flex-start;
                    gap: 15px;
                }
            }
        </style>
    </head>
    <body>

        <div class="container">
            <div class="header-area">
                <h2>Quản lý thiết bị Sensor</h2>
                <a href="MainController?action=Sensor" class="btn btn-refresh">Xem tất cả</a>
                <a href="MainController?action=Dashboard" class="btn btn-back">← Quay lại Dashboard</a>
            </div>

            <div class="card">
                <form action="MainController" method="POST" class="form-group">
                    <input type="hidden" name="action" value="Sensor" />
                    <input type="hidden" name="subAction" value="add" />
                    <strong style="width: 100%; margin-bottom: 5px;">Thêm mới Sensor & Mapping:</strong>

                    <input type="number" name="roomID" placeholder="Phòng ID" required style="width: 80px" />
                    <input type="text" name="serialNo" placeholder="Số Serial" required />
                    <input type="text" name="model" placeholder="Model" required />

                    <%-- Mục chọn chất ô nhiễm để tự động Map index --%>
                    <select name="pollutantInfo" required class="form-select">
                        <option value="">-- Chọn loại chất đo (Toàn bộ danh sách) --</option>

                        <option value="2|CO(GT)">Khí CO (Ground Truth)</option>
                        <option value="4|NMHC(GT)">Chất hữu cơ NMHC (GT)</option>
                        <option value="5|C6H6(GT)">Khí Benzene C6H6 (GT)</option>
                        <option value="7|NOx(GT)">Oxit Nitơ NOx (GT)</option>
                        <option value="9|NO2(GT)">Khí NO2 (GT)</option>

                        <option value="3|PT08.S1(CO)">Cảm biến S1 (Oxit thiếc/CO)</option>
                        <option value="6|PT08.S2(NMHC)">Cảm biến S2 (Chất hữu cơ)</option>
                        <option value="8|PT08.S3(NOx)">Cảm biến S3 (Oxit Nitơ)</option>
                        <option value="10|PT08.S4(NO2)">Cảm biến S4 (NO2)</option>
                        <option value="11|PT08.S5(O3)">Cảm biến S5 (Indium Oxit/O3)</option>

                        <option value="12|T">Nhiệt độ (Temperature)</option>
                        <option value="13|RH">Độ ẩm tương đối (Relative Humidity)</option>
                        <option value="14|AH">Độ ẩm tuyệt đối (Absolute Humidity)</option>
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
                            <th>Ngày lắp đặt</th>
                            <th>Cập nhật cuối</th>
                        </tr>
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
                            <td><%= (s.getInstalledAt() != null) ? s.getInstalledAt() : "<span style='color:#ccc'>---</span>"%></td>
                            <td style="color: var(--text-muted); font-size: 13px;">
                                <%= (s.getLastSeenTs() != null) ? s.getLastSeenTs() : "Chưa có dữ liệu"%>
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
                                // Hiển thị thông báo cho từng ID bị hỏng
                                ids.forEach(id => {
                                    alert("Cảnh báo: Sensor #" + id + " vừa ngừng hoạt động (Lỗi -200)!");
                                });
                                // Reload để cập nhật màu sắc bảng
                                location.reload();
                            }
                        });
            }
            setInterval(checkSensorStatus, 5000);
        </script>
    </body>
</html>