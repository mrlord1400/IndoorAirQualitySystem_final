package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.RuleDAO;
import model.RuleDTO;

@WebServlet(name = "RuleController", urlPatterns = {"/RuleController"})
public class RuleController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("subAction");
        if (action == null) {
            action = "list";
        }

        RuleDAO dao = new RuleDAO();

        try {
            if (action.equals("list")) {
                List<RuleDTO> list = dao.getAllRules();
                request.setAttribute("RULE_LIST", list);
                request.getRequestDispatcher("rule.jsp").forward(request, response);
            } else if (action.equals("insert") || action.equals("update")) {
                // Lấy các thông số mới từ Form
                int roomID = Integer.parseInt(request.getParameter("roomID"));
                int pollutantID = Integer.parseInt(request.getParameter("pollutantID"));
                double lower = Double.parseDouble(request.getParameter("lowerBound"));
                double upper = Double.parseDouble(request.getParameter("upperBound"));
                int duration = Integer.parseInt(request.getParameter("durationMin"));
                String severity = request.getParameter("severity");
                boolean active = request.getParameter("active") != null;

                if ("insert".equals(action)) {
                    dao.insertRule(new RuleDTO(0, roomID, pollutantID, lower, upper, duration, severity, active));
                } else {
                    int ruleID = Integer.parseInt(request.getParameter("ruleID"));
                    dao.updateRule(new RuleDTO(ruleID, roomID, pollutantID, lower, upper, duration, severity, active));
                }
                response.sendRedirect("MainController?action=Rule&subAction=list");
            } else if (action.equals("delete")) {
                int id = Integer.parseInt(request.getParameter("ruleID"));
                if (dao.deleteRule(id)) {
                    response.sendRedirect("MainController?action=Rule&subAction=list");
                }
            }
        } catch (Exception e) {
            log("Error at RuleController: " + e.toString());
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
