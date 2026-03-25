package controller;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.AdminDAO;

public class DashboardController extends HttpServlet {

    private static final int admin = 1;
    private static final int labManager = 2;
    private static final int technician = 3;
    private static final int viewer = 4;
    private static final String ERROR = "login.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            int roleID = 0;
            HttpSession session = request.getSession();
            roleID = (int) session.getAttribute("USER_ROLE");
            if (roleID != 0) {
                if (roleID == 1){
                   AdminDAO adminDao = new AdminDAO();
                    Map<String, Integer> stats = adminDao.getDashboardStats();

                    // Đẩy dữ liệu thống kê sang trang JSP
                    request.setAttribute("STATS", stats);
                    url = "adminDashboard.jsp";
                } else if (roleID == 2){
                    url = "LMDashboard.jsp";
                } else if (roleID == 3){
                    url = "TechDashboard.jsp";
                } else if (roleID == 4){
                    url = "ViewController";
                }
            } else {
                request.setAttribute("ERROR", "Invalid role!");
            }
        } catch (Exception e) {
            log("Error at DashboardController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}

