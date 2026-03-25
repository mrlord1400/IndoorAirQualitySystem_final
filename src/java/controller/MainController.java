/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReadingDAO;
import utils.CSVSimulator;

/**
 *
 * @author hd
 */
public class MainController extends HttpServlet {

    private static final String ERROR = "error.jsp";

    private static final String LOGIN = "Login";
    private static final String LOGIN_CONTROLLER = "LoginController";
    private static final String ALERT = "Alert";
    private static final String ALERT_CONTROLLER = "AlertController";
    private static final String DASHBOARD = "Dashboard";
    private static final String DASHBOARD_CONTROLLER = "DashboardController";
    private static final String READING = "Reading";
    private static final String READING_CONTROLLER = "ReadingController";
    private static final String POLLUTANT = "Pollutant";
    private static final String POLLUTANT_CONTROLLER = "PollutantController";
    private static final String ROOM = "Room";
    private static final String ROOM_CONTROLLER = "RoomController";
    private static final String RULE = "Rule";
    private static final String RULE_CONTROLLER = "RuleController";
    private static final String SENSOR = "Sensor";
    private static final String SENSOR_CONTROLLER = "SensorController";
    private static final String SENSOR_LM = "SensorLM";
    private static final String SENSOR_LM_CONTROLLER = "SensorForLMController";
    private static final String USER = "User";
    private static final String USER_CONTROLLER = "UserController";
    private static final String SYSTEMRESET = "SystemReset";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            String action = request.getParameter("action");
            if (LOGIN.equals(action)) {
                url = LOGIN_CONTROLLER;
            } else if (ALERT.equals(action)) {
                url = ALERT_CONTROLLER;
            } else if (DASHBOARD.equals(action)) {
                url = DASHBOARD_CONTROLLER;
            } else if (READING.equals(action)) {
                url = READING_CONTROLLER;
            } else if (POLLUTANT.equals(action)) {
                url = POLLUTANT_CONTROLLER;
            } else if (ROOM.equals(action)) {
                url = ROOM_CONTROLLER;
            } else if (RULE.equals(action)) {
                url = RULE_CONTROLLER;
            } else if (SENSOR.equals(action)) {
                url = SENSOR_CONTROLLER;
            } else if (SENSOR_LM.equals(action)) {
                url = SENSOR_LM_CONTROLLER;
            } else if (USER.equals(action)) {
                url = USER_CONTROLLER;
            } else if (SYSTEMRESET.equals(action)) {
                // 1. Lấy Simulator từ bộ nhớ dùng chung của Server
                CSVSimulator sim = (CSVSimulator) getServletContext().getAttribute("GLOBAL_SIMULATOR");
                if (sim != null) {
                    // 2. Dừng luồng giả lập hiện tại
                    sim.stopSimulation();

                    // 3. Xóa sạch dữ liệu cũ trong Database
                    ReadingDAO dao = new ReadingDAO();
                    dao.deleteAllReadings();

                    // 4. Khởi động lại luồng giả lập (đọc lại file từ đầu)
                    sim.startSimulation();

                    // 5. Điều hướng về Dashboard Action thay vì file JSP
                    // Chúng ta dùng sendRedirect để trình duyệt tạo request mới sạch sẽ
                    response.sendRedirect("MainController?action=Dashboard&msg=ResetSuccess");
                    return;
                }
            } else {
                request.setAttribute("ERROR", "Your action not support");
            }
        } catch (Exception e) {
            log("Error at MainController: " + e.toString());
        } finally {
            if (!response.isCommitted()) {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
