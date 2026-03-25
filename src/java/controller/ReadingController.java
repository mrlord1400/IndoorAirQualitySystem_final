package controller;

import java.io.IOException;
import java.io.PrintWriter;
import model.ReadingDAO;
import model.ReadingDTO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ReadingDAO;
import model.ReadingDTO;

public class ReadingController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("exportCSV".equals(action)) {
            // NHÁNH 1: Xử lý tải file CSV
            handleExportCSV(response);
        } else {
            // NHÁNH 2: Mặc định hiển thị bảng dữ liệu lên JSP
            handleViewWithPaging(request, response);
        }
    }

    private void handleViewWithPaging(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 1. Lấy tham số tìm kiếm từ giao diện
        String rIdStr = request.getParameter("roomId");
        String pIdStr = request.getParameter("pollutantId");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        // 2. Xử lý logic phân trang (Pagination)
        int pageIndex = 1;
        int pageSize = 20; // Số dòng hiển thị mỗi trang
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            pageIndex = Integer.parseInt(pageStr);
        }

        // Chuyển đổi kiểu dữ liệu cho DAO
        Integer roomId = (rIdStr != null && !rIdStr.isEmpty()) ? Integer.parseInt(rIdStr) : null;
        Integer pollutantId = (pIdStr != null && !pIdStr.isEmpty()) ? Integer.parseInt(pIdStr) : null;

        ReadingDAO dao = new ReadingDAO();

        // 3. Thực hiện truy vấn dữ liệu theo trang
        List<ReadingDTO> list = dao.searchAdvancedWithPaging(roomId, pollutantId, fromDate, toDate, pageIndex, pageSize);

        // 4. Tính toán tổng số trang để hiển thị thanh điều hướng
        int totalRecords = dao.getTotalRecords(roomId, pollutantId, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        // 5. Gửi dữ liệu sang JSP
        request.setAttribute("READINGS_LIST", list);
        request.setAttribute("TOTAL_PAGES", totalPages);
        request.setAttribute("CURRENT_PAGE", pageIndex);

        HttpSession session = request.getSession();
        session.setAttribute("READINGS_LIST_TO_EXPORT", list);

        // Giả sử trang hiển thị của bạn là reading-list.jsp hoặc search.jsp
        request.getRequestDispatcher("ViewReading.jsp").forward(request, response);
    }

    private void handleExportCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"AirQualityData.csv\"");

        ReadingDAO dao = new ReadingDAO();
        List<ReadingDTO> data = dao.getAllReadings();

        try ( PrintWriter writer = response.getWriter()) {
            writer.println("ID,SensorID,PollutantID,Timestamp,Value,QualityFlag");
            for (ReadingDTO r : data) {
                StringBuilder sb = new StringBuilder();
                sb.append(r.getReadingID()).append(",")
                        .append(r.getSensorID()).append(",")
                        .append(r.getPollutantID()).append(",")
                        .append(r.getTs()).append(",")
                        .append(r.getValue()).append(",")
                        .append(r.getQualityFlag());
                writer.println(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
