package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.AlertActionDAO;
import model.AlertActionDTO;
import model.AlertDAO;
import model.UserDTO;

/**
 * AlertController đã được tinh chỉnh để chạy thông qua MainController
 */
@WebServlet(name = "AlertController", urlPatterns = {"/AlertController"})
public class AlertController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // 1. Kiểm tra Login
        HttpSession session = request.getSession(false);
        UserDTO loginUser = (session != null) ? (UserDTO) session.getAttribute("LOGIN_USER") : null;

        if (loginUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Xác định đích đến mặc định (Dùng cho redirect sau khi xử lý xong)
        int roleID = loginUser.getRoleID();
        String defaultDashboard = "login.jsp";
        if (roleID == 1) {
            defaultDashboard = "adminDashboard.jsp";
        } else if (roleID == 2) {
            defaultDashboard = "LMDashboard.jsp";
        } else if (roleID == 3) {
            defaultDashboard = "TechDashboard.jsp";
        }

        String referer = request.getHeader("Referer");
        String finalUrl = (referer != null && !referer.isEmpty()) ? referer : defaultDashboard;

        try {
            // Lấy subAction để không trùng với 'action' của MainController
            String subAction = request.getParameter("subAction");
            String alertIdRaw = request.getParameter("alertId");
            String note = request.getParameter("note");

            // 3. Phân quyền: Chỉ Admin(1), Manager(2), Tech(3) mới được xử lý
            if (roleID >= 1 && roleID <= 3) {
                if (alertIdRaw != null && subAction != null) {
                    int alertId = Integer.parseInt(alertIdRaw);
                    AlertDAO alertDao = new AlertDAO();
                    AlertActionDAO actionDao = new AlertActionDAO();

                    String status = "";

                    // Xử lý các case của subAction
                    if ("ack".equals(subAction)) {
                        status = "ACK";
                    } else if ("close".equals(subAction)) {
                        status= "CLOSE";
                    }

                    if (!status.isEmpty()) {
                        boolean isUpdated = alertDao.updateAlertStatus(alertId, status);
                        if (isUpdated) {
                            // Ghi log vào database
                            AlertActionDTO log = new AlertActionDTO(
                                    0,
                                    (long) alertId,
                                    loginUser.getUserID(),
                                    status,
                                    (note != null && !note.isEmpty() ? note : "Processed via " + status),
                                    java.time.LocalDateTime.now()
                            );
                            actionDao.insertAlertAction(log);
                            session.setAttribute("MSG_SUCCESS", "Successfully " + status.toLowerCase() + "ed alert #" + alertId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log("Error at AlertController: " + e.toString());
        } finally {
            // Sau khi xử lý dữ liệu xong (Update/Insert), nên dùng Redirect để tránh trùng lặp dữ liệu khi F5
            response.sendRedirect(finalUrl);
        }
    }

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
