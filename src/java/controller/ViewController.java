package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.AlertDAO;
import model.AlertDTO;
import model.SensorDAO;
import model.SensorDTO;

// Đường dẫn để truy cập trang dashboard (ví dụ: http://localhost:8080/ProjectName/dashboard)
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class ViewController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            // 1. Khởi tạo các DAO
            AlertDAO alertDAO = new AlertDAO();
            SensorDAO sensorDAO = new SensorDAO();

            // 2. Lấy dữ liệu từ Database thông qua DAO
            List<AlertDTO> alertList = alertDAO.getAllAlerts();
            List<SensorDTO> sensorList = sensorDAO.getAllSensorsWithMap();

            // 3. Đẩy dữ liệu vào request attribute để JSP có thể lấy ra
            request.setAttribute("alertList", alertList);
            request.setAttribute("sensorList", sensorList);

            // 4. Chuyển hướng sang file JSP bạn đã tạo
            request.getRequestDispatcher("ViewDashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Nếu lỗi, có thể forward sang một trang thông báo lỗi
            response.getWriter().println("Đã xảy ra lỗi: " + e.getMessage());
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
