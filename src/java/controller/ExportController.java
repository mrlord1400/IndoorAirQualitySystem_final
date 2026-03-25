package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.ReadingDAO;
import model.ReadingDTO;

@WebServlet("/ExportController")
public class ExportController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. ĐỌC CÁC THAM SỐ LỌC TỪ URL
        String rIdStr = request.getParameter("roomId");
        String pIdStr = request.getParameter("pollutantId");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");

        Integer roomId = (rIdStr != null && !rIdStr.isEmpty()) ? Integer.parseInt(rIdStr) : null;
        Integer pollutantId = (pIdStr != null && !pIdStr.isEmpty()) ? Integer.parseInt(pIdStr) : null;

        // 2. GỌI DAO LẤY TOÀN BỘ DỮ LIỆU KHỚP VỚI BỘ LỌC
        ReadingDAO dao = new ReadingDAO();
        List<ReadingDTO> listToExport = dao.searchAdvanced(roomId, pollutantId, fromDate, toDate);

        // 3. THIẾT LẬP PHẢN HỒI HTTP (CSV)
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"full_reading_report.csv\"");

        

        // 4. GHI DỮ LIỆU RA LUỒNG XUẤT
        try (PrintWriter writer = response.getWriter()) {
            writer.write('\ufeff'); // BOM cho Excel
            writer.println("ReadingID,SensorID,PollutantID,Timestamp,Value,QualityFlag");

            for (ReadingDTO r : listToExport) {
                writer.println(String.format("%d,%d,%d,%s,%.2f,%s",
                        r.getReadingID(), r.getSensorID(), r.getPollutantID(),
                        r.getTs(), r.getValue(), r.getQualityFlag()));
            }
        } catch (Exception e) {
            log("Export Error: " + e.getMessage());
        }
    }
}