/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Board;
import entity.Host;
import logic.BoardLogic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.HostLogic;
import logic.LogicFactory;

/**
 *
 * @author jonat
 */
@WebServlet(name = "CreateBoard", urlPatterns = {"/CreateBoard"})
public class CreateBoard extends HttpServlet {

    private String errorMessage = null;
    private HostLogic hLogic = LogicFactory.getFor("Host");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/login.css\" />");
            out.println("<title>Create Board</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"login-div\" >");              
            out.println("<div style=\"display: inline-block; text-align: center;\">");
            out.println("<h2>Create a Board</h2>");
            out.println("<form method=\"post\">");
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.println("<div class=\"fields\">");
            out.printf("<div class=\"boardurl\"><input type=\"text\" name=\"%s\" class=\"urlinput\" placeholder=\"Enter board URL\" value=\"\"></div>", BoardLogic.URL);
            out.printf("<div class=\"boardname\"><input type=\"text\" name=\"%s\" class=\"bnameinput\" placeholder=\"Enter board name\" value=\"\"></div>", BoardLogic.NAME);
            out.printf("<select class=\"select-css\" name=\"%s\" placeholder\"Select Host\" required>", BoardLogic.HOST_ID);
            out.println("<option value=\"\" selected>Select a host</option>");
            List<Host> hList = hLogic.getAll();
            for (Host h : hList) {
                out.printf("<option value=\"%d\">%s</option>", h.getId(), h.getName());
            }
            out.printf("</select></div>");
       
            out.println("<br><br>");
            out.println("<input type=\"submit\" class=\"button\" name=\"view\" value=\"Add and View\">");
            out.println("<input type=\"submit\" class=\"button\" name=\"add\" value=\"Add\">");
            out.println("</form>");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(errorMessage);
                out.println("</font>");
                out.println("</p>");
            }
            out.println("<pre>");
            out.println("Submitted keys and values:");
            out.println(toStringMap(request.getParameterMap()));
            out.println("</pre>");
            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> values) {
        StringBuilder builder = new StringBuilder();
        values.forEach((k, v) -> builder.append("Key=").append(k)
                .append(", ")
                .append("Value/s=").append(Arrays.toString(v))
                .append(System.lineSeparator()));
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will
     * create a host this method simple delivers the html code. creation will be
     * done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user
     * submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");

        BoardLogic bLogic = LogicFactory.getFor("Board");
        String url = request.getParameter(BoardLogic.URL);
        if (bLogic.getBoardWithUrl(url) == null) {
            Board board = bLogic.createEntity(request.getParameterMap());
            Host host = hLogic.getWithId(Integer.parseInt(request.getParameter(BoardLogic.HOST_ID)));
            board.setHostid(host);
            bLogic.add(board);
        }
        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("BoardTableJSP");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Board Entity";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
