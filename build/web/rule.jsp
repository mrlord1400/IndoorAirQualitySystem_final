<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.RuleDTO" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Quy tắc ngưỡng | Lab Care</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f0f2f5;
            }
            .card {
                border: none;
                border-radius: 10px;
                box-shadow: 0 5px 15px rgba(0,0,0,0.05);
            }
            .status-active {
                color: #28a745;
                font-weight: bold;
            }
            .status-inactive {
                color: #dc3545;
                font-weight: bold;
            }
            .severity-badge {
                min-width: 80px;
            }
        </style>
    </head>
    <body>

        <div class="container py-5">
            <div class="card p-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="text-dark fw-bold">Cấu hình Quy tắc ngưỡng (Threshold Rules)</h2>
                        <p class="text-muted mb-0">Thiết lập điều kiện cảnh báo cho các phòng Lab</p>
                    </div>
                    <button class="btn btn-primary shadow-sm" onclick="openRuleModal('insert')">
                        + Thêm quy tắc mới
                    </button>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>ID</th>
                                <th>Room ID</th>
                                <th>Pollutant ID</th>
                                <th>Ngưỡng (Dưới - Trên)</th>
                                <th>Duy trì (Phút)</th>
                                <th>Mức độ</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                // 1. Lấy danh sách từ request và ép kiểu
                                List<RuleDTO> ruleList = (List<RuleDTO>) request.getAttribute("RULE_LIST");

                                // 2. Kiểm tra nếu danh sách có dữ liệu
                                if (ruleList != null && !ruleList.isEmpty()) {
                                    for (RuleDTO r : ruleList) {
                            %>
                            <tr>
                                <td><strong>#<%= r.getRuleID()%></strong></td>
                                <td><span class="badge bg-secondary">Phòng <%= r.getRoomID()%></span></td>
                                <td><span class="badge bg-info text-dark">Chất <%= r.getPollutantID()%></span></td>
                                <td>
                                    <span class="text-primary"><%= r.getLowerBound()%></span> 
                                    <span class="text-muted">→</span> 
                                    <span class="text-danger"><%= r.getUpperBound()%></span>
                                </td>
                                <td><%= r.getDurationMin()%> phút</td>
                                <td>
                                    <%
                                        // Chuyển đổi logic <c:choose> của Severity
                                        String sev = r.getSeverity();
                                        if ("HIGH".equals(sev) || "CRITICAL".equals(sev)) {
                                    %>
                                    <span class="badge bg-danger severity-badge"><%= sev%></span>
                                    <% } else {%>
                                    <span class="badge bg-warning text-dark severity-badge"><%= sev%></span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (r.isActive()) { %>
                                    <span class="status-active">● Đang bật</span>
                                    <% } else { %>
                                    <span class="status-inactive">○ Đang tắt</span>
                                    <% }%>
                                </td>
                                <td class="text-center">
                                    <div class="btn-group">
                                        <button class="btn btn-sm btn-outline-warning" 
                                                onclick="editRule('<%= r.getRuleID()%>', '<%= r.getRoomID()%>', '<%= r.getPollutantID()%>', '<%= r.getLowerBound()%>', '<%= r.getUpperBound()%>', '<%= r.getDurationMin()%>', '<%= r.getSeverity()%>', <%= r.isActive()%>)">
                                            Sửa
                                        </button>
                                        <a href="MainController?action=Rule&subAction=delete&ruleID=<%= r.getRuleID()%>" 
                                           class="btn btn-sm btn-outline-danger" 
                                           onclick="return confirm('Bạn có chắc muốn xóa quy tắc này?')">Xóa</a>
                                    </div>
                                </td>
                            </tr>
                            <%
                                } // Kết thúc vòng lặp for
                            } else {
                                // Tương đương với <c:if test="${empty RULE_LIST}">
                            %>
                            <tr>
                                <td colspan="8" class="text-center py-4 text-muted">Chưa có quy tắc nào được thiết lập.</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal fade" id="ruleModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content border-0 shadow-lg">
                    <form action="MainController" method="POST">
                        <div class="modal-header bg-dark text-white">
                            <h5 class="modal-title" id="modalTitle">Quy tắc ngưỡng</h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body p-4">
                            <input type="hidden" name="action" value="Rule">
                            <input type="hidden" name="subAction" id="formAction" value="insert">
                            <input type="hidden" name="ruleID" id="formRuleID">

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Room ID</label>
                                    <input type="number" name="roomID" id="formRoomID" class="form-control" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Pollutant ID</label>
                                    <input type="number" name="pollutantID" id="formPollutantID" class="form-control" required>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Ngưỡng dưới (Lower)</label>
                                    <input type="number" step="0.01" name="lowerBound" id="formLower" class="form-control" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Ngưỡng trên (Upper)</label>
                                    <input type="number" step="0.01" name="upperBound" id="formUpper" class="form-control" required>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Thời gian duy trì (Phút)</label>
                                <input type="number" name="durationMin" id="formDuration" class="form-control" required>
                                <small class="text-muted">Cảnh báo sẽ kích hoạt nếu vi phạm duy trì quá X phút.</small>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold">Mức độ nghiêm trọng</label>
                                <select name="severity" id="formSeverity" class="form-select">
                                    <option value="LOW">LOW</option>
                                    <option value="MEDIUM">MEDIUM</option>
                                    <option value="HIGH">HIGH</option>
                                    <option value="CRITICAL">CRITICAL</option>
                                </select>
                            </div>

                            <div class="form-check form-switch mt-3">
                                <input class="form-check-input" type="checkbox" name="active" id="formActive" value="true">
                                <label class="form-check-input-label fw-bold" for="formActive">Kích hoạt quy tắc này</label>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                            <button type="submit" class="btn btn-primary px-4">Lưu cấu hình</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                               const ruleModal = new bootstrap.Modal(document.getElementById('ruleModal'));

                                               function openRuleModal(action) {
                                                   document.getElementById('formAction').value = action;
                                                   document.getElementById('formRuleID').value = '';
                                                   document.getElementById('modalTitle').innerText = "Thêm quy tắc mới";
                                                   // Reset form
                                                   document.getElementById('formRoomID').value = '';
                                                   document.getElementById('formPollutantID').value = '';
                                                   document.getElementById('formLower').value = '0';
                                                   document.getElementById('formUpper').value = '';
                                                   document.getElementById('formDuration').value = '5';
                                                   document.getElementById('formSeverity').value = 'MEDIUM';
                                                   document.getElementById('formActive').checked = true;

                                                   ruleModal.show();
                                               }

                                               function editRule(id, room, pollutant, lower, upper, duration, severity, active) {
                                                   document.getElementById('formAction').value = 'update';
                                                   document.getElementById('formRuleID').value = id;
                                                   document.getElementById('modalTitle').innerText = "Cập nhật quy tắc #" + id;

                                                   document.getElementById('formRoomID').value = room;
                                                   document.getElementById('formPollutantID').value = pollutant;
                                                   document.getElementById('formLower').value = lower;
                                                   document.getElementById('formUpper').value = upper;
                                                   document.getElementById('formDuration').value = duration;
                                                   document.getElementById('formSeverity').value = severity;
                                                   document.getElementById('formActive').checked = active === true;

                                                   ruleModal.show();
                                               }
        </script>

    </body>
</html>