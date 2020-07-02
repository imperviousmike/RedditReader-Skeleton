/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.Board;
import entity.Image;
import logic.BoardLogic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.ImageLogic;
import logic.LogicFactory;

/**
 *
 * @author jonat
 */
@WebServlet(name = "CreateImage", urlPatterns = {"/CreateImage"})
public class CreateImage extends HttpServlet {

    private String errorMessage = null;
    private static final String SAVE_DIR = System.getProperty("user.home") + "\\Documents\\RedditImages\\";
    private BoardLogic bLogic = LogicFactory.getFor("Board");

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
            out.println("<title>Create Image</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"login-div\" >");              
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: center;\">");
            out.println("<h2>Create an Image</h2>");
            out.println("<form method=\"post\">");
            //instead of typing the name of column manualy use the static vraiable in logic
            //use the same name as column id of the table. will use this name to get date
            //from parameter map.
            out.println("<div class=\"fields\">");
            out.printf("<div class=\"boardname\"><input type=\"text\" name=\"%s\" value=\"\" placeholder=\"Enter URL\"></div>", ImageLogic.URL);
            out.printf("<div class=\"boardname\"><input type=\"text\" name=\"%s\" value=\"\" placeholder=\"Enter Title\"></div>", ImageLogic.TITLE);
            out.printf("<div class=\"boardname\"><input type=\"file\" accept=\"image/png, image/jpeg\" name=\"%s\" value=\"%s\"></div>", ImageLogic.LOCAL_PATH, SAVE_DIR + "");
            out.printf("<select class=\"select-css\" name=\"%s\" required>", ImageLogic.BOARD_ID);
            out.println("<option value=\"\" selected>Select a board</option>");
            
            List<Board> bList = bLogic.getAll();
            bList.forEach(board -> out.printf("<option value=\"%d\">%s</option>", board.getId(), board.getName()));
            out.printf("</select>");
            out.println("<br>");
            ImageLogic iLogic = LogicFactory.getFor("Image");
            String date = iLogic.convertDate(Calendar.getInstance().getTime());
            
            out.println("<br>Date and time:");
            out.printf("<input type=\"text\" name=\"%s\" value=\"%s\" readonly><br>", ImageLogic.DATE, date);
            out.println("</div>");
            out.println("<br>");
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

        ImageLogic iLogic = LogicFactory.getFor("Image");
        String url = request.getParameter(ImageLogic.URL);
        if (iLogic.getImageWithUrl(url) == null) {
            Image img = iLogic.createEntity(request.getParameterMap());
            Board board = bLogic.getWithId(Integer.parseInt(request.getParameter(ImageLogic.BOARD_ID)));
            img.setBoard(board);
            img.setLocalPath(SAVE_DIR + img.getLocalPath());
            iLogic.add(img);
        }
        if (request.getParameter("add") != null) {
            //if add button is pressed return the same page
            processRequest(request, response);
        } else if (request.getParameter("view") != null) {
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect("ImageTableJSP");
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
}
