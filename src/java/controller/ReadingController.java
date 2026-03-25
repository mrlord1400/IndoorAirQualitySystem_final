package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ReadingDAO;
import model.ReadingDTO;

public class ReadingController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        ReadingDAO dao = new ReadingDAO();

        if ("clear".equals(action)) {
            dao.deleteAllReadings();
            response.sendRedirect("ReadingController?action=list&msg=Cleared");
            return;
        }

        // Mặc định là hiển thị danh sách
        handleViewWithPaging(request, response);
    }

    private void handleViewWithPaging(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rIdStr = request.getParameter("roomId");
        String pIdStr = request.getParameter("pollutantId");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        int pageIndex = 1;
        int pageSize = 20;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            pageIndex = Integer.parseInt(pageStr);
        }

        Integer roomId = (rIdStr != null && !rIdStr.isEmpty()) ? Integer.parseInt(rIdStr) : null;
        Integer pollutantId = (pIdStr != null && !pIdStr.isEmpty()) ? Integer.parseInt(pIdStr) : null;

        ReadingDAO dao = new ReadingDAO();
        List<ReadingDTO> list = dao.searchAdvancedWithPaging(roomId, pollutantId, fromDate, toDate, pageIndex, pageSize);
        int totalRecords = dao.getTotalRecords(roomId, pollutantId, fromDate, toDate);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        request.setAttribute("READINGS_LIST", list);
        request.setAttribute("TOTAL_PAGES", totalPages);
        request.setAttribute("CURRENT_PAGE", pageIndex);

        request.getRequestDispatcher("ViewReading.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
