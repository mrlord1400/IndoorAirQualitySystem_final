<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.PollutantDTO"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Chất ô nhiễm | Air Quality Lab Care</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f4f7f6;
                font-family: 'Segoe UI', sans-serif;
            }
            .main-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                border:none;
            }
            .badge-unit {
                background-color: #e9ecef;
                color: #495057;
                font-weight: 600;
            }
            .btn-back {
                text-decoration: none;
                font-weight: 500;
            }
        </style>
    </head>
    <body>

        <div class="container py-5">
            <div class="mb-4">
                <a href="MainController?action=Dashboard" class="btn btn-outline-secondary btn-back shadow-sm">← Quay lại Dashboard</a>
            </div>

            <div class="main-card p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="fw-bold text-primary mb-0">Hệ thống Quản lý Chất ô nhiễm</h2>
                        <small class="text-muted">Dự án: AIR QUALITY LAB CARE</small>
                    </div>

                    <div>                
                        <form action="MainController" method="POST">
                            <input type="hidden" name="action" value="Pollutant">
                            <input type="hidden" name="subAction" value="list">
                            <button type="submit">Show Pollutants</button>
                        </form>
                    </div>
                    <button class="btn btn-primary px-4 shadow-sm" onclick="openForm('', '', '')">+ Thêm chất mới</button>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-dark">
                            <tr>
                                <th class="ps-4">ID</th>
                                <th>Tên chất ô nhiễm</th>
                                <th>Đơn vị đo</th>
                                <th class="text-end pe-4">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<PollutantDTO> list = (List<PollutantDTO>) request.getAttribute("POLLUTANT_LIST");
                                if (list != null && !list.isEmpty()) {
                                    for (PollutantDTO p : list) {
                            %>
                            <tr>
                                <td class="ps-4 text-muted">#<%= p.getPollutantID()%></td>
                                <td><span class="fw-bold text-dark"><%= p.getName()%></span></td>
                                <td><span class="badge badge-unit px-3 py-2 text-dark"><%= p.getUnit()%></span></td>
                                <td class="text-end pe-4">
                                    <button class="btn btn-outline-warning btn-sm px-3 fw-semibold me-2" 
                                            onclick="openForm('<%= p.getPollutantID()%>', '<%= p.getName()%>', '<%= p.getUnit()%>')">
                                        Sửa
                                    </button>

                                    <a href="MainController?action=Pollutant&subAction=delete&pollutantID=<%= p.getPollutantID()%>" 
                                       class="btn btn-outline-danger btn-sm px-3 fw-semibold"
                                       onclick="return confirm('Bạn có chắc chắn muốn xóa chất này không?')">
                                        Xóa
                                    </a>
                                </td>
                            </tr>
                            <%
                                }
                            } else {
                            %>
                            <tr>
                                <td colspan="4" class="text-center py-5 text-muted">Chưa có dữ liệu chất ô nhiễm nào.</td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal fade" id="pollutantModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title" id="modalTitle">Thông tin chất đo</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <form action="MainController" method="POST">
                        <div class="modal-body p-4">
                            <input type="hidden" name="action" value="Pollutant">
                            <input type="hidden" name="subAction" value="save">
                            <input type="hidden" name="pollutantID" id="formID">

                            <div class="mb-3">
                                <label class="form-label fw-semibold">Tên chất ô nhiễm</label>
                                <input type="text" name="name" id="formName" class="form-control" placeholder="VD: CO2, PM2.5..." required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Đơn vị đo</label>
                                <input type="text" name="unit" id="formUnit" class="form-control" placeholder="VD: ppm, ug/m3..." required>
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary px-4">Lưu lại</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                           function openForm(id, name, unit) {
                                               const modal = new bootstrap.Modal(document.getElementById('pollutantModal'));
                                               document.getElementById('formID').value = id;
                                               document.getElementById('formName').value = name;
                                               document.getElementById('formUnit').value = unit;
                                               document.getElementById('modalTitle').innerText = (id === '') ? "Thêm chất ô nhiễm mới" : "Cập nhật chất ô nhiễm";
                                               modal.show();
                                           }
        </script>
    </body>
</html>