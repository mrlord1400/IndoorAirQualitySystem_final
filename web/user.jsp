<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="java.util.List, model.UserDTO" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý người dùng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body class="bg-light">


        <div class="container mt-5">
            <div class="mb-3">
                <a href="MainController?action=Dashboard" class="btn btn-secondary text-white shadow-sm">
                    &larr; Quay lại Dashboard
                </a>
            </div>
            
            <h2 class="text-center mb-4">Hệ Thống Quản Lý Người Dùng</h2>

            <%
                // 1. Lấy parameter "msg" từ URL
                String msg = request.getParameter("msg");

                // 2. Kiểm tra nếu msg có tồn tại (không null)
                if (msg != null) {
                    String displayMsg = "";

                    // 3. Logic phân loại thông báo (tương đương c:choose)
                    if (msg.equals("InsertSuccess")) {
                        displayMsg = "Thêm mới thành công!";
                    } else if (msg.equals("UpdateSuccess")) {
                        displayMsg = "Cập nhật thành công!";
                    } else if (msg.equals("DeleteSuccess")) {
                        displayMsg = "Xóa người dùng thành công!";
                    }

                    // Chỉ hiển thị div nếu tìm thấy thông báo phù hợp
                    if (!displayMsg.isEmpty()) {
            %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <strong>Thông báo:</strong> <%= displayMsg%>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <%
                    }
                }
            %>

            <div class="row">
                <div class="col-md-4">
                    <div class="card shadow-sm">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0">Thông tin người dùng</h5>
                        </div>
                        <div class="card-body">
                            <form action="MainController" method="POST">
                                <input type="hidden" name="action" value="User">
                                <input type="hidden" name="subAction" id="formAction" value="insert">
                                <input type="hidden" name="txtUserID" id="txtUserID">

                                <div class="mb-3">
                                    <label class="form-label">Tên đăng nhập</label>
                                    <input type="text" name="txtUsername" id="txtUsername" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Mật khẩu</label>
                                    <input type="password" name="txtPassword" id="txtPassword" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Họ và tên</label>
                                    <input type="text" name="txtFullname" id="txtFullname" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Email</label>
                                    <input type="email" name="txtEmail" id="txtEmail" class="form-control" required>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Vai trò (Role ID)</label>
                                    <select name="roleID" id="roleID" class="form-select">
                                        <option value="1">Admin (1)</option>
                                        <option value="2">Lab Manager (2)</option>
                                        <option value="3">Technician (3)</option>
                                        <option value="4">Viewer (4)</option>
                                    </select>
                                </div>
                                <div class="mb-3 form-check">
                                    <input type="checkbox" name="chkStatus" id="chkStatus" class="form-check-input" checked>
                                    <label class="form-check-label">Kích hoạt tài khoản</label>
                                </div>

                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-success" id="btnSubmit">Thêm người dùng</button>
                                    <button type="reset" class="btn btn-secondary" onclick="resetForm()">Làm mới</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-md-8">
                    <div class="card shadow-sm">
                        <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">Danh sách người dùng</h5>
                            <form action="MainController" method="GET" class="d-flex">
                                <input type="hidden" name="action" value="User">
                                <input type="hidden" name="subAction" value="search">
                                <input type="text" name="txtSearch" class="form-control form-control-sm me-2" placeholder="Tìm theo tên..." value="${param.txtSearch}">
                                <button class="btn btn-outline-light btn-sm" type="submit">Tìm</button>
                            </form>
                        </div>
                        <div class="card-body p-0">
                            <table class="table table-hover mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Họ tên</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Trạng thái</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <%
                                        List<UserDTO> list = (List<UserDTO>) request.getAttribute("LIST_USERS");

                                        if (list != null && !list.isEmpty()) {
                                            for (UserDTO u : list) {
                                    %>
                                    <tr>
                                        <td><%= u.getUserID()%></td>
                                        <td><%= u.getUsername()%></td>
                                        <td><%= u.getFullname()%></td>
                                        <td><%= u.getEmail()%></td>
                                        <td>
                                            <%
                                                // Chuyển đổi logic c:choose của Vai trò
                                                int role = u.getRoleID();
                                                if (role == 1) { %>
                                            <span class="badge bg-danger">Admin</span>
                                            <% } else if (role == 2) { %>
                                            <span class="badge bg-warning text-dark">Manager</span>
                                            <% } else if (role == 3) { %>
                                            <span class="badge bg-primary">Technician</span>
                                            <% } else { %>
                                            <span class="badge bg-secondary">Viewer</span>
                                            <% }
                                            %>
                                        </td>
                                        <td>
                                            <%
                                                // Chuyển đổi logic c:choose của Trạng thái
                                                if (u.isStatus()) { %>
                                            <span class="text-success">✔ Hoạt động</span>
                                            <% } else { %>
                                            <span class="text-danger">✘ Khóa</span>
                                            <% }
                                            %>
                                        </td>
                                        <td>
                                            <button class="btn btn-sm btn-warning" 
                                                    onclick="editUser('<%= u.getUserID()%>', '<%= u.getUsername()%>', '<%= u.getFullname()%>', '<%= u.getEmail()%>', '<%= u.getRoleID()%>', <%= u.isStatus()%>)">
                                                Sửa
                                            </button>
                                            <a href="MainController?action=User&subAction=delete&userId=<%= u.getUserID()%>" 
                                               class="btn btn-sm btn-danger" 
                                               onclick="return confirm('Bạn có chắc chắn muốn xóa?')">Xóa</a>
                                        </td>
                                    </tr>
                                    <%
                                        } // Kết thúc vòng lặp for
                                    } else {
                                    %>
                                    <tr>
                                        <td colspan="7" class="text-center p-3">Không tìm thấy người dùng nào.</td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function editUser(id, user, name, email, role, status) {
                document.getElementById('formAction').value = 'update';
                document.getElementById('txtUserID').value = id;
                document.getElementById('txtUsername').value = user;
                document.getElementById('txtUsername').readOnly = true; // Không cho sửa username
                document.getElementById('txtFullname').value = name;
                document.getElementById('txtEmail').value = email;
                document.getElementById('roleID').value = role;
                document.getElementById('chkStatus').checked = status;
                document.getElementById('txtPassword').required = false; // Khi update có thể ko cần nhập lại pass
                document.getElementById('btnSubmit').innerHTML = 'Cập nhật thay đổi';
                document.getElementById('btnSubmit').className = 'btn btn-warning';
            }

            function resetForm() {
                document.getElementById('formAction').value = 'insert';
                document.getElementById('txtUsername').readOnly = false;
                document.getElementById('btnSubmit').innerHTML = 'Thêm người dùng';
                document.getElementById('btnSubmit').className = 'btn btn-success';
                document.getElementById('txtPassword').required = true;
            }
        </script>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
