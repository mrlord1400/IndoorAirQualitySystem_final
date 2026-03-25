package controller;

import model.PollutantDAO;
import model.PollutantDTO;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet(name = "PollutantController", urlPatterns = {"/PollutantController"})
public class PollutantController extends HttpServlet {

    private PollutantDAO pollutantDAO;

    @Override
    public void init() throws ServletException {
        this.pollutantDAO = new PollutantDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("subAction");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "list":
                    List<PollutantDTO> list = pollutantDAO.getAllPollutants();
                    request.setAttribute("POLLUTANT_LIST", list);
                    request.getRequestDispatcher("pollutant.jsp").forward(request, response);
                    break;

                case "save":
                    String idStr = request.getParameter("pollutantID");
                    String name = request.getParameter("name");
                    String unit = request.getParameter("unit");

                    if (name == null || name.trim().isEmpty()) {
                        request.setAttribute("ERROR", "Tên chất ô nhiễm không được để trống!");
                        request.getRequestDispatcher("pollutant.jsp").forward(request, response);
                        return;
                    }

                    if (idStr == null || idStr.isEmpty()) {
                        pollutantDAO.insertPollutant(new PollutantDTO(0, name, unit));
                    } else {
                        pollutantDAO.updatePollutant(new PollutantDTO(Integer.parseInt(idStr), name, unit));
                    }
                    response.sendRedirect("MainController?action=PollutantsubAction=list");
                    break;

                default:
                    response.sendRedirect("MainController?action=Pollutant&subAction=list");
            }
        } catch (Exception e) {
            log("Lỗi trong PollutantController: " + e.getMessage());
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
