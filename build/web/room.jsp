<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, model.RoomDTO" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Phòng Lab | Air Quality Lab Care</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background-color: #f8f9fa;
            }
            .container {
                margin-top: 50px;
            }
            .table-container {
                background: white;
                padding: 25px;
                border-radius: 15px;
                box-shadow: 0 0 20px rgba(0,0,0,0.05);
            }
            .badge-status-1 {
                background-color: #d1e7dd;
                color: #0f5132;
            }
            .badge-status-0 {
                background-color: #f8d7da;
                color: #842029;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <div class="table-container">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2 class="fw-bold text-dark">Quản lý danh sách Phòng</h2>
                        <p class="text-muted small mb-0">Hệ thống giám sát Air Quality Lab Care</p>
                    </div>
                    <button class="btn btn-primary px-4 shadow-sm" data-bs-toggle="modal" data-bs-target="#addRoomModal">
                        + Thêm phòng mới
                    </button>
                </div>

                <%
                    // 1. Lấy parameter "msg" từ thanh địa chỉ URL
                    String msg = request.getParameter("msg");

                    // 2. Kiểm tra nếu msg tồn tại và không bị rỗng
                    if (msg != null && !msg.isEmpty()) {
                        String alertMessage = "";

                        // 3. Logic phân loại thông báo cho Room
                        if ("InsertSuccess".equals(msg)) {
                            alertMessage = "Thêm phòng mới thành công!";
                        } else if ("DisableSuccess".equals(msg)) {
                            alertMessage = "Đã vô hiệu hóa phòng thành công!";
                        } else if ("DeleteSuccess".equals(msg)) {
                            alertMessage = "Đã xóa phòng thành công!";
                        }

                        // 4. Chỉ hiển thị khung HTML nếu tìm thấy nội dung phù hợp
                        if (!alertMessage.isEmpty()) {
                %>
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <%= alertMessage%>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <%
                        }
                    }
                %>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-dark">
                            <tr>
                                <th>ID</th>
                                <th>Mã Phòng</th>
                                <th>Tên Phòng</th>
                                <th>Loại</th>
                                <th>Vị trí</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                                List<RoomDTO> listRooms = (List<RoomDTO>) request.getAttribute("listRooms");

                                if (listRooms != null && !listRooms.isEmpty()) {
                                    for (RoomDTO r : listRooms) {
                            %>
                            <tr>
                                <td><%= r.getRoomID()%></td>
                                <td><code class="fw-bold"><%= r.getCode()%></code></td>
                                <td><%= r.getName()%></td>
                                <td><span class="text-secondary"><%= r.getType()%></span></td>
                                <td><small><%= r.getLocation()%></small></td>
                                <td>
                                    <% if (r.isStatus()) { %>
                                    <span class="badge badge-status-1">Đang hoạt động</span>
                                    <% } else { %>
                                    <span class="badge badge-status-0">Ngừng hoạt động</span>
                                    <% } %>
                                </td>
                                <td class="text-center">
                                    <% if (r.isStatus()) {%>
                                    <%-- Đường dẫn chuyển về RoomController thông qua MainController hoặc trực tiếp --%>
                                    <a href="MainController?action=Room&subAction=disable&id=<%= r.getRoomID()%>" 
                                       class="btn btn-sm btn-outline-danger" 
                                       onclick="return confirm('Bạn có chắc muốn vô hiệu hóa phòng này không?')">
                                        Vô hiệu hóa
                                    </a>
                                    <a href="MainController?action=Room&subAction=delete&id=<%= r.getRoomID()%>" 
                                       class="btn btn-sm btn-danger mb-1" 
                                       onclick="return confirm('CẢNH BÁO: Bạn có chắc chắn muốn XÓA VĨNH VIỄN phòng này? Thao tác này không thể hoàn tác!')">
                                        Xóa vĩnh viễn
                                    </a>
                                    <% } else {%>
                                    <button class="btn btn-sm btn-secondary" disabled>Disabled</button>
                                    <a href="MainController?action=Room&subAction=delete&id=<%= r.getRoomID()%>" 
                                       class="btn btn-sm btn-danger mb-1" 
                                       onclick="return confirm('CẢNH BÁO: Bạn có chắc chắn muốn XÓA VĨNH VIỄN phòng này? Thao tác này không thể hoàn tác!')">
                                        Xóa vĩnh viễn
                                    </a>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                } // Kết thúc vòng lặp for
                            } else {
                            %>
                            <tr>
                                <td colspan="7" class="text-center py-4 text-muted">Không tìm thấy phòng nào trong hệ thống.</td>
                            </tr>
                            <%
                                }
                            %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal fade" id="addRoomModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content border-0">
                    <div class="modal-header bg-primary text-white">
                        <h5 class="modal-title">Đăng ký phòng Lab mới</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="MainController" method="POST">
                        <input type="hidden" name="action" value="Room">
                        <input type="hidden" name="subAction" value="add">

                        <div class="modal-body p-4">
                            <div class="mb-3">
                                <label class="form-label fw-bold">Mã phòng (Code)</label>
                                <input type="text" name="code" class="form-control" placeholder="VD: LAB-101" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Tên phòng</label>
                                <input type="text" name="name" class="form-control" placeholder="VD: Phòng Lab Hóa học" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Loại phòng</label>
                                <select name="roomType" class="form-select">
                                    <option value="Lab">Phòng Lab</option>
                                    <option value="Server Room">Phòng Server</option>
                                    <option value="Office">Văn phòng</option>
                                    <option value="Storage">Kho thiết bị</option>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-bold">Vị trí (Location)</label>
                                <input type="text" name="location" class="form-control" placeholder="VD: Tòa Alpha, Tầng 2">
                            </div>
                        </div>
                        <div class="modal-footer bg-light">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary px-4">Xác nhận thêm</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>