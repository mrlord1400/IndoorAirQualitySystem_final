<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.ReadingDTO"%>
<%
    // Lấy dữ liệu từ request
    List<ReadingDTO> readingsList = (List<ReadingDTO>) request.getAttribute("READINGS_LIST");

    // Lấy các tham số phân trang và tìm kiếm
    Integer totalPages = (request.getAttribute("TOTAL_PAGES") != null) ? (Integer) request.getAttribute("TOTAL_PAGES") : 0;
    Integer currentPage = (request.getAttribute("CURRENT_PAGE") != null) ? (Integer) request.getAttribute("CURRENT_PAGE") : 1;

    String roomId = request.getParameter("roomId") != null ? request.getParameter("roomId") : "";
    String pollutantId = request.getParameter("pollutantId") != null ? request.getParameter("pollutantId") : "";
    String fromDate = request.getParameter("fromDate") != null ? request.getParameter("fromDate") : "";
    String toDate = request.getParameter("toDate") != null ? request.getParameter("toDate") : "";

    // Chuỗi tham số dùng chung cho các link phân trang
    String searchParams = "&roomId=" + roomId + "&pollutantId=" + pollutantId + "&fromDate=" + fromDate + "&toDate=" + toDate;
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Truy vấn lịch sử chất lượng không khí | Lab Care</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
        <style>
            /* GIỮ NGUYÊN CẤU TRÚC CSS BAN ĐẦU */
            body {
                background-color: #f1f3f5;
            }
            .search-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border: none;
            }
            .data-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                border: none;
            }
            .table thead {
                background-color: #212529;
                color: white;
            }
            .badge-normal {
                background-color: #d1e7dd;
                color: #0f5132;
            }
            .badge-warning {
                background-color: #fff3cd;
                color: #856404;
            }
            .badge-critical {
                background-color: #f8d7da;
                color: #842029;
            }
            .pagination .page-link {
                color: #212529;
            }
            .pagination .active .page-link {
                background-color: #212529;
                border-color: #212529;
                color: white;
            }
        </style>
    </head>
    <body>

        <div class="container-fluid py-4 px-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="fw-bold text-dark mb-0">Dữ liệu quan trắc (Time-series Readings)</h2>
                    <p class="text-muted">Quản lý và truy vấn lịch sử vận hành phòng Lab</p>
                </div>

                <div class="d-flex gap-2">
                    <a href="MainController?action=Dashboard" class="btn btn-dark shadow-sm px-4">
                        <i class="bi bi-speedometer2"></i> ← Quay lại Dashboard
                    </a>

                    <a href="ReadingController?action=clear" class="btn btn-outline-danger shadow-sm px-4" 
                       onclick="return confirm('Bạn có chắc chắn muốn xóa sạch lịch sử để làm mới dữ liệu?');">
                        <i class="bi bi-trash"></i> Xóa lịch sử
                    </a>

                    <a href="ExportController?action=exportCSV<%= searchParams%>" class="btn btn-outline-dark shadow-sm px-4">
                        <i class="bi bi-download"></i> Xuất CSV
                    </a>
                </div>
            </div>

            <div class="card search-card p-4 mb-4">
                <form action="ReadingController" method="GET" class="row g-3">
                    <input type="hidden" name="action" value="list">

                    <div class="col-md-2">
                        <label class="form-label fw-semibold">Room ID</label>
                        <input type="number" name="roomId" class="form-control" value="<%= roomId%>" placeholder="VD: 1">
                    </div>

                    <div class="col-md-2">
                        <label class="form-label fw-semibold">Pollutant ID</label>
                        <input type="number" name="pollutantId" class="form-control" value="<%= pollutantId%>" placeholder="VD: 1">
                    </div>

                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Từ ngày</label>
                        <input type="date" name="fromDate" class="form-control" value="<%= fromDate%>">
                    </div>

                    <div class="col-md-3">
                        <label class="form-label fw-semibold">Đến ngày</label>
                        <input type="date" name="toDate" class="form-control" value="<%= toDate%>">
                    </div>

                    <div class="col-md-2 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary w-100 fw-bold shadow-sm">Tìm kiếm</button>
                    </div>
                </form>
            </div>

            <div class="card data-card p-0 overflow-hidden">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead>
                            <tr>
                                <th class="ps-4">ID</th>
                                <th>Cảm biến (Sensor)</th>
                                <th>Chất đo (Pollutant)</th>
                                <th>Thời điểm đo</th>
                                <th>Giá trị</th>
                                <th>Trạng thái</th>
                                <th class="pe-4 text-end">Hệ thống tạo lúc</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                if (readingsList != null && !readingsList.isEmpty()) {
                                    for (ReadingDTO r : readingsList) {
                            %>
                            <tr>
                                <td class="ps-4 text-muted small">#<%= r.getReadingID()%></td>
                                <td><span class="badge bg-secondary">ID: <%= r.getSensorID()%></span></td>
                                <td><span class="badge bg-info text-dark">ID: <%= r.getPollutantID()%></span></td>
                                <td class="fw-semibold"><%= r.getTs()%></td>
                                <td>
                                    <span class="fs-5 fw-bold text-primary"><%= r.getValue()%></span>
                                </td>
                                <td>
                                    <%
                                        String qf = r.getQualityFlag();
                                        if ("NORMAL".equals(qf) || "GOOD".equals(qf)) {
                                    %>
                                    <span class="badge badge-normal px-3">NORMAL</span>
                                    <% } else if ("WARNING".equals(qf)) { %>
                                    <span class="badge badge-warning px-3">WARNING</span>
                                    <% } else {%>
                                    <span class="badge badge-critical px-3"><%= qf%></span>
                                    <% }%>
                                </td>
                                <td class="pe-4 text-end text-muted small"><%= r.getCreatedAt()%></td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="7" class="text-center py-5">
                                    <p class="text-muted">Không tìm thấy dữ liệu nào khớp với bộ lọc.</p>
                                    <a href="ReadingController?action=list" class="btn btn-sm btn-link">Xóa bộ lọc</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <% if (totalPages > 1) {%>
                <nav class="d-flex justify-content-center py-4 border-top">
                    <ul class="pagination mb-0">
                        <li class="page-item <%= (currentPage == 1) ? "disabled" : ""%>">
                            <a class="page-link" href="ReadingController?action=list&page=<%= currentPage - 1%><%= searchParams%>">Trước</a>
                        </li>
                        <% for (int i = 1; i <= totalPages; i++) {%>
                        <li class="page-item <%= (i == currentPage) ? "active" : ""%>">
                            <a class="page-link" href="ReadingController?action=list&page=<%= i%><%= searchParams%>"><%= i%></a>
                        </li>
                        <% }%>
                        <li class="page-item <%= (currentPage.equals(totalPages)) ? "disabled" : ""%>">
                            <a class="page-link" href="ReadingController?action=list&page=<%= currentPage + 1%><%= searchParams%>">Sau</a>
                        </li>
                    </ul>
                </nav>
                <% }%>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                           const autoRefreshInterval = 5000;
                           function startAutoRefresh() {
                               setInterval(function () {
                                   const activeElement = document.activeElement;
                                   const isTyping = activeElement.tagName === 'INPUT' || activeElement.tagName === 'SELECT';
                                   const fromDate = document.querySelector('input[name="fromDate"]').value;
                                   const toDate = document.querySelector('input[name="toDate"]').value;
                                   const isViewingHistory = fromDate !== "" || toDate !== "";

                                   // Chỉ reload khi không bận nhập liệu hoặc xem lịch sử quá khứ
                                   if (!isTyping && !isViewingHistory) {
                                       window.location.reload();
                                   }
                               }, autoRefreshInterval);
                           }
                           document.addEventListener('DOMContentLoaded', startAutoRefresh);
        </script>
    </body>
</html>
