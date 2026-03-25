package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.SensorDAO;
import model.SensorDTO;
import utils.DBUtils;

/**
 * Servlet này dành riêng cho Lab Manager Chỉ thực hiện chức năng xem danh sách,
 * không có thêm/sửa/xóa/lọc.
 */
@WebServlet(name = "SensorForLMController", urlPatterns = {"/SensorForLMController"})
public class SensorForLMController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        SensorDAO dao = new SensorDAO();
        // Luôn điều hướng về trang LMSensor.jsp (trang đã lược bỏ nút bấm)
        String url = "LMSensor.jsp";

        try ( Connection conn = DBUtils.getConnection()) {
            /* CHỈ THỰC HIỆN ĐÚNG 1 NHIỆM VỤ:
               Load toàn bộ danh sách cảm biến để hiển thị.
               Mọi tham số 'subAction' gửi lên (nếu có) đều bị lờ đi ở đây.
             */
            List<SensorDTO> list = dao.getAllSensors(conn);
            request.setAttribute("SENSOR_LIST", list);

        } catch (Exception e) {
            log("Error in SensorForLM: " + e.getMessage());
            // Có thể set thông báo lỗi nếu cần
            request.setAttribute("ERROR", "Không thể tải dữ liệu cảm biến.");
        } finally {
            // Forward dữ liệu sang trang hiển thị
            request.getRequestDispatcher(url).forward(request, response);
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
        // Chặn luôn các thao tác POST nếu muốn cực kỳ nghiêm ngặt, 
        // hoặc đơn giản là điều hướng ngược lại về danh sách.
        processRequest(request, response);
    }
}
