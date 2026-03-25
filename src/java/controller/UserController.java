package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.UserDAO;
import model.UserDTO;

public class UserController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Lấy tham số 'action' từ URL hoặc Form để biết người dùng muốn thực hiện chức năng gì
        String action = request.getParameter("subAction");
        UserDAO dao = new UserDAO();

        try {
            // Mặc định hiển thị danh sách nếu không có hành động nào được chỉ định
            if (action == null) {
                action = "list";
            }

            switch (action) {
                case "list": {
                    // Lấy toàn bộ dữ liệu người dùng từ database thông qua DAO
                    List<UserDTO> list = dao.getAllUsers();
                    // Lưu dữ liệu vào request scope để JSP có thể hiển thị bằng JSTL
                    request.setAttribute("LIST_USERS", list);
                    request.getRequestDispatcher("user.jsp").forward(request, response);
                    break;
                }

                case "search": {
                    // Lấy từ khóa tìm kiếm từ form trên JSP
                    String searchTerm = request.getParameter("txtSearch");
                    // Gọi hàm tìm kiếm theo tên/từ khóa
                    List<UserDTO> list = dao.searchUsers(searchTerm); 
                    request.setAttribute("LIST_USERS", list);
                    request.getRequestDispatcher("user.jsp").forward(request, response);
                    break;
                }

                case "insert": {
                    // Lấy dữ liệu từ form thêm mới (giả sử form dùng method POST)
                    String user = request.getParameter("txtUsername");
                    String pass = request.getParameter("txtPassword");
                    String name = request.getParameter("txtFullname");
                    String email = request.getParameter("txtEmail");
                    int roleID = Integer.parseInt(request.getParameter("roleID"));
                    
                    // Tạo DTO với status mặc định là true (Hoạt động) và thời gian hiện tại
                    UserDTO dto = new UserDTO(0, user, pass, name, email, roleID, true, LocalDateTime.now());
                    // Thực hiện chèn vào DB
                    if (dao.insertUser(dto)) {
                        // Dùng Redirect để tránh lỗi lặp lại request khi người dùng nhấn F5
                        response.sendRedirect("MainController?action=User&subAction=list");
                    }
                    break;
                }

                case "update": {
                    // Lấy thông tin từ form sửa (cần ID để xác định user nào cần update)
                    int id = Integer.parseInt(request.getParameter("txtUserID"));
                    String pass = request.getParameter("txtPassword");
                    String name = request.getParameter("txtFullname");
                    String email = request.getParameter("txtEmail");
                    int roleID = Integer.parseInt(request.getParameter("roleID"));
                    // Kiểm tra checkbox trạng thái: nếu null nghĩa là không check (bị khóa)
                    boolean status = request.getParameter("chkStatus") != null;

                    UserDTO dto = new UserDTO(id, null, pass, name, email, roleID, status, null);
                    if (dao.updateUser(dto)) {
                        response.sendRedirect("UserController?action=list&msg=UpdateSuccess");
                    }
                    break;
                }

                case "delete": {
                    // Nhận ID từ URL (ví dụ: UserController?action=delete&userId=5)
                    String idRaw = request.getParameter("userId");
                    if (idRaw != null) {
                        int id = Integer.parseInt(idRaw);
                        // Xóa user theo ID
                        if (dao.deleteUser(id)) {
                            response.sendRedirect("UserController?action=list&msg=DeleteSuccess");
                        }
                    }
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException | NumberFormatException | SQLException e) {
            // Ghi log lỗi vào console hoặc server log để dev kiểm tra
            log("Error at UserController: " + e.toString());
            // Hiển thị thông báo lỗi thân thiện hơn cho người dùng
            request.setAttribute("ERROR", "Hệ thống gặp lỗi: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Các hàm này chuyển tiếp toàn bộ request đến processRequest để xử lý tập trung
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
