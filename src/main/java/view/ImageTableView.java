/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;
import common.FileUtility;
import entity.Image;
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
import logic.ImageLogic;
import logic.LogicFactory;

/**
 *
 * @author Earl_Grey_Hot
 */
@WebServlet(name = "ImageTable", urlPatterns = {"/ImageTable"})
public class ImageTableView extends HttpServlet {
    
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
            out.println("<!DOCTYPE html>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/tablestyle.css\"> ");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>HostViewNormal</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>Host</caption>");

            ImageLogic logic = LogicFactory.getFor("Image");

            //making table headers
            out.println("<tr>");
            for (String columnName : logic.getColumnNames()) {
                out.println("<th>" + columnName + "</th>");
            }
            out.println("</tr>");

            List<Image> entities = logic.getAll();
            for (Image image : entities) {
                out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        logic.extractDataAsList(image).toArray());
            }

            //making headers again for the bottom of the table I guess?
            out.println("<tr>");
            for (String columnName : logic.getColumnNames()) {
                out.println("<th>" + columnName + "</th>");
            }
            out.println("</tr>");
            out.println("</table>");
            out.printf("<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap(request.getParameterMap()));
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
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
         
        FileUtility.createDirectory(System.getProperty("user.home"));
        FileUtility.createDirectory(System.getProperty("/My Documents/RedditImages/"));
        
        //Using LogicFactory get the logics you need to create and add an Image 
        //to DB. Don’t forget the dependencies
        LogicFactory.getFor("Image");
        
    //3)Get the board you want to use. You will use the name of this board object 
    //in reddit buildRedditPageConfig method. 
    
    //4) Use the example provided in reddit.TestRunReddit::exampleForReadingNextPage 
    //to see how to use the reddit object. 
    
    //5) Create your custom lambda to create and Image entity, download it and 
    //add it to DB. 
    
    //a) Only accept post that are over18 and are images. 
    
    //b) The image should not already exist in the db. 
    
    //c) You must use createEntity to create entities, do not use the constructor 
    //      of entities outside of createEntity method. 
    
    //d) Download it to your computer using FileUtility::downloadAndSaveFile(String url, String dest). 
    //      1) Destination is the folder you made in step 1. 
    
    //e) Extract the data from the Post object which is the argument of your lambda. 
    //      Add them to a newly created Map using ImageLogic static variables as keys. 
    
    //f) Create the Entity image, set the dependency and add it to db. 
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of Image View Normal";
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
