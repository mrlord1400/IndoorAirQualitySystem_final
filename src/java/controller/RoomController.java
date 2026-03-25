package controller;

import model.RoomDAO;
import model.RoomDTO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

public class RoomController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("subAction");
        RoomDAO roomDAO = new RoomDAO();

        try {
            if (action == null || action.equals("list")) {
                // 1. Hiển thị danh sách phòng (chỉ lấy các phòng đang hoạt động - status = 1)
                List<RoomDTO> list = roomDAO.getAllRooms();
                request.setAttribute("listRooms", list);
                request.getRequestDispatcher("room.jsp").forward(request, response);

            } else if ("add".equals(action)) {
                // 2. Xử lý thêm phòng mới
                String code = request.getParameter("code");
                String name = request.getParameter("name");
                String type = request.getParameter("roomType");
                String location = request.getParameter("location");

                RoomDTO dto = new RoomDTO(0, code, name, type, location, true, null);
                if (roomDAO.insertRoom(dto)) {
                    response.sendRedirect("MainController?action=Room&subAction=list&msg=InsertSuccess");
                } else {
                    request.setAttribute("ERROR", "Không thể thêm phòng. Có thể mã Code bị trùng!");
                    request.getRequestDispatcher("error.jsp").forward(request, response);
                }

            } else if ("disable".equals(action)) {
                // 3. Xử lý xóa (vô hiệu hóa) phòng
                int id = Integer.parseInt(request.getParameter("id"));
                if (roomDAO.disableRoom(id)) {
                    response.sendRedirect("MainController?action=Room&subAction=list&msg=DisableSuccess");
                }
            } else if ("delete".equals(action)) {
                // 3. Xử lý xóa (vô hiệu hóa) phòng
                int id = Integer.parseInt(request.getParameter("id"));
                if (roomDAO.deleteRoom(id)) {
                    response.sendRedirect("MainController?action=Room&subAction=list&msg=DeleteSuccess");
                }
            }
        } catch (Exception e) {
            log("Error at RoomController: " + e.toString());
            response.sendRedirect("error.jsp");
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
