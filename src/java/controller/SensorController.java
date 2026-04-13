package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDateTime;
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
import utils.CSVSimulator;
import utils.DBUtils;

public class SensorController extends HttpServlet {

    private static final String SENSOR_PAGE = "sensor.jsp";
    private static final String MAIN_SENSOR_CONTROL = "MainController?action=Sensor";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String subAction = request.getParameter("subAction");
        SensorDAO dao = new SensorDAO();
        String url = SENSOR_PAGE; // Mặc định là sensor.jsp

        try ( Connection conn = DBUtils.getConnection()) {
            if ("add".equals(subAction)) {
                int roomID = Integer.parseInt(request.getParameter("roomID"));
                String serialNo = request.getParameter("serialNo");
                String model = request.getParameter("model");
                boolean status = Boolean.parseBoolean(request.getParameter("status"));
                String pollutantInfo = request.getParameter("pollutantInfo");

                if (pollutantInfo != null && pollutantInfo.contains("|")) {
                    String[] parts = pollutantInfo.split("\\|");
                    int csvIndex = Integer.parseInt(parts[0]);
                    String pollutantName = parts[1];

                    // Tạo object
                    SensorDTO sensor = new SensorDTO(0, roomID, serialNo, model, status, LocalDateTime.now(), null);

                    // Hàm add này bây giờ ĐÃ CẬP NHẬT ID vào chính biến 'sensor'
                    if (dao.addSensorWithMapping(sensor, pollutantName, csvIndex)) {
                        CSVSimulator sim = (CSVSimulator) getServletContext().getAttribute("GLOBAL_SIMULATOR");
                        if (sim != null) {
                            // Bây giờ 'sensor' đã có ID thật và csvIndex thật
                            sim.addSingleSensorSimulation(sensor);
                        }
                        // Redirect ngay và THOÁT HÀM để không chạy xuống finally forward
                        response.sendRedirect("MainController?action=Sensor&msg=AddSuccess");
                        return;
                    }
                }
            } else if ("checkStatus".equals(subAction)) {
                List<Integer> inactiveIds = dao.getRecentlyInactiveIds(10);
                response.setContentType("application/json");
                response.getWriter().print(inactiveIds.toString());
                response.getWriter().flush();
                return; // THOÁT HÀM ngay để tránh finally forward JSON sang JSP

            } else if ("toggle".equals(subAction)) {
                int sensorID = Integer.parseInt(request.getParameter("sensorID"));
                boolean currentStatus = Boolean.parseBoolean(request.getParameter("status"));
                // Đảo trạng thái: true -> false, false -> true
                dao.updateSensorStatus(sensorID, !currentStatus);
                response.sendRedirect("MainController?action=Sensor&msg=StatusUpdated");
                return;

            } else if ("delete".equals(subAction)) {
                int sensorID = Integer.parseInt(request.getParameter("sensorID"));
                if (dao.deleteSensor(sensorID)) {
                    response.sendRedirect("MainController?action=Sensor&msg=DeleteSuccess");
                    return;
                }
            } else {
                // MẶC ĐỊNH: Luôn load danh sách đầy đủ (Dùng bản JOIN để lấy được cả csvIndex nếu cần)
                request.setAttribute("SENSOR_LIST", dao.getAllSensors(conn));
            }

        } catch (Exception e) {
            log("Error in SensorController: " + e.getMessage());
            request.setAttribute("ERROR", e.getMessage());
        } finally {
            // CHỈ FORWARD khi phản hồi chưa được gửi đi (không bị redirect hay trả về JSON)
            if (!response.isCommitted()) {
                request.getRequestDispatcher(url).forward(request, response);
            }
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
