<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.AlertDTO" %>
<%@ page import="model.SensorDTO" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Hệ thống Quản lý Phòng Lab - Dashboard</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f4f6f9;
                font-family: 'Segoe UI', Tahoma, sans-serif;
            }
            .navbar {
                background-color: #1a237e !important;
            }
            .stat-card {
                border-radius: 12px;
                border: none;
                box-shadow: 0 4px 6px rgba(0,0,0,0.07);
                transition: 0.3s;
            }
            .stat-card:hover {
                transform: translateY(-3px);
            }
            .badge-open {
                background-color: #ef5350;
                color: white;
            }
            .badge-ack {
                background-color: #ffa726;
                color: white;
            }
            .severity-high {
                border-left: 5px solid #d32f2f;
                background-color: #fff5f5;
            }
            .severity-normal {
                border-left: 5px solid #ffa000;
                background-color: #fffdf2;
            }
            .status-on {
                color: #2e7d32;
                font-weight: bold;
            }
            .status-off {
                color: #c62828;
                font-weight: bold;
            }
            .table-container {
                background: white;
                border-radius: 12px;
                padding: 20px;
                box-shadow: 0 4px 6px rgba(0,0,0,0.05);
            }
        </style>
    </head>
    <body>

        <%
            // 1. Lấy dữ liệu từ túi 'request' mà Servlet đã gửi sang
            List<AlertDTO> alertList = (List<AlertDTO>) request.getAttribute("alertList");
            List<SensorDTO> sensorList = (List<SensorDTO>) request.getAttribute("sensorList");

            // 2. Định dạng thời gian cho LocalDateTime (mặc định trong DTO của bạn)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

            // 3. Tính toán thống kê nhanh
            int totalS = (sensorList != null) ? sensorList.size() : 0;
            int activeS = 0;
            if (sensorList != null) {
                for (SensorDTO s : sensorList) {
                    if (s.isStatus()) {
                        activeS++; // status trong DTO là boolean
                    }
                }
            }

            int openA = 0;
            if (alertList != null) {
                for (AlertDTO a : alertList) {
                    if ("OPEN".equalsIgnoreCase(a.getStatus())) {
                        openA++;
                    }
                }
            }
        %>

        <nav class="navbar navbar-dark mb-4 shadow-sm">
            <div class="container">
                <a class="navbar-brand d-flex align-items-center" href="#">
                    <span class="fs-4 fw-bold">AIR QUALITY LAB CARE</span>
                </a>
                <div class="d-flex align-items-center text-white">
                    <span class="me-3 small">Vai trò: <span class="badge bg-info text-dark">VIEWER</span></span>
                    <a href="login.jsp" class="btn btn-sm btn-outline-light">Đăng xuất</a>
                </div>
            </div>
        </nav>

        <div class="container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h3 class="m-0 text-secondary fw-bold">Tổng quan vận hành</h3>
                <a href="MainController?action=Reading&subAction=list" class="btn btn-primary">
                    Xem chỉ số đo
                </a>
            </div>

            <div class="row mb-4">
                <div class="col-md-4">
                    <div class="card stat-card p-3 bg-white">
                        <div class="text-muted small fw-bold">TỔNG SỐ CẢM BIẾN</div>
                        <div class="d-flex align-items-end justify-content-between mt-2">
                            <h2 class="m-0 text-primary"><%= totalS%></h2>
                            <span class="text-muted small">Thiết bị</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card p-3 bg-white border-bottom border-success border-4">
                        <div class="text-muted small fw-bold">ĐANG HOẠT ĐỘNG</div>
                        <div class="d-flex align-items-end justify-content-between mt-2">
                            <h2 class="m-0 text-success"><%= activeS%></h2>
                            <span class="text-muted small">Cảm biến Online</span>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card p-3 bg-white border-bottom border-danger border-4">
                        <div class="text-muted small fw-bold">CẢNH BÁO CHƯA XỬ LÝ</div>
                        <div class="d-flex align-items-end justify-content-between mt-2">
                            <h2 class="m-0 text-danger"><%= openA%></h2>
                            <span class="text-muted small">Trạng thái OPEN</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-lg-5 mb-4">
                    <div class="table-container h-100">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="m-0 fw-bold">Trạng thái Cảm biến</h5>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>Mã số (Serial)</th>
                                        <th>Phòng ID</th>
                                        <th>Kết nối</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (sensorList != null) {
                                            for (SensorDTO s : sensorList) {%>
                                    <tr>
                                        <td><code><%= s.getSerialNo()%></code></td>
                                        <td>Phòng <%= s.getRoomID()%></td>
                                        <td>
                                            <% if (s.isStatus()) { %>
                                            <span class="status-on">● Hoạt động</span>
                                            <% } else { %>
                                            <span class="status-off">○ Mất kết nối</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                    <% }
                                        } %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-lg-7 mb-4">
                    <div class="table-container h-100">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="m-0 fw-bold">Cảnh báo hệ thống gần đây</h5>
                        </div>
                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead class="table-dark">
                                    <tr>
                                        <th>Thời gian</th>
                                        <th>Nội dung cảnh báo</th>
                                        <th>Mức độ</th>
                                        <th>Trạng thái</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <% if (alertList != null && !alertList.isEmpty()) {
                                            for (AlertDTO a : alertList) {
                                                String rowStyle = ("HIGH".equalsIgnoreCase(a.getSeverity()) || "CRITICAL".equalsIgnoreCase(a.getSeverity()))
                                                        ? "severity-high" : "severity-normal";
                                    %>
                                    <tr class="<%= rowStyle%>">
                                        <td><small><%= a.getStartTs().format(formatter)%></small></td>
                                        <td>
                                            <div class="fw-bold small text-dark">Room ID: <%= a.getRoomID()%></div>
                                            <div class="text-muted" style="font-size: 0.85rem;"><%= a.getMessage()%></div>
                                        </td>
                                        <td>
                                            <span class="badge <%= rowStyle.equals("severity-high") ? "bg-danger" : "bg-warning text-dark"%>">
                                                <%= a.getSeverity()%>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge <%= "OPEN".equalsIgnoreCase(a.getStatus()) ? "badge-open" : "badge-ack"%>">
                                                <%= a.getStatus()%>
                                            </span>
                                        </td>
                                    </tr>
                                    <% }
                                    } else { %>
                                    <tr><td colspan="4" class="text-center py-4 text-muted">Hệ thống hiện tại an toàn, không có cảnh báo.</td></tr>
                                    <% }%>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <footer class="text-center text-muted mt-5 py-4 border-top">
            <small>&copy; 2026 - AIR QUALITY LAB CARE - FPT University</small>
        </footer>

    </body>
</html>