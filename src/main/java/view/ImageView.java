/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import common.FileUtility;
import entity.Board;
import entity.Image;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.BoardLogic;
import logic.ImageLogic;
import logic.LogicFactory;
import reddit.Post;
import reddit.Reddit;
import reddit.Sort;

/**
 *
 * @author Earl_Grey_Hot
 */
@WebServlet(name = "ImageView", urlPatterns = {"/ImageView"})
public class ImageView extends HttpServlet {

    private static String board_name = "Wallpaper";
    private static final String SAVE_DIR = System.getProperty("user.home") + "\\Documents\\RedditImages\\";

    private Board board;

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
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/ImageView.css\"> ");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>ImagesViewNormal</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>Images</caption>");
            out.println("<div align=\"center\">");
            out.printf("<label for=\"%s\">%s</label>", "BoardSelect", "Board:&emsp;");
            out.println("<form method=\"post\">");
            out.printf("<select name=\"%s\">", "BoardSelect", board_name);
            BoardLogic bLogic = LogicFactory.getFor("Board");
            List<Board> bList = bLogic.getAll();

            for (Board b : bList) {
                if (b.getName().equals(board_name)) {
                    out.printf("<option value=\"%s\" selected>%s</option>", b.getName(), b.getName());
                    board = b;
                } else {
                    out.printf("<option value=\"%s\">%s</option>", b.getName(), b.getName());
                }
            }
            out.printf("</select>");
            out.println("<input type=\"submit\" name=\"view\" value=\"View\">");
            out.println("</div>");
            ImageLogic logic = LogicFactory.getFor("Image");
            out.println("</form>");
            List<Image> imageList = null;
            if (board == null) {
                imageList = logic.getAll();
            } else {
                imageList = logic.getImagesWithBoardId(board.getId());
            }
            out.println("<div align=\"center\" class=\"imageContainer\">");
            for (Image i : imageList) {
                out.printf("<a href=\"%s\">", "image/" + FileUtility.getFileName(i.getLocalPath()));
                out.printf("<img class=\"imageThumb\" src=\"%s\"/>", "image/" + FileUtility.getFileName(i.getLocalPath()));
                out.println("</a>");
            }
            out.println("</div>");
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
        FileUtility.createDirectory(SAVE_DIR);

        ImageLogic imageLogic = LogicFactory.getFor("Image");
        BoardLogic boardLogic = LogicFactory.getFor("Board");
        List<Board> boardList = boardLogic.getBoardsWithName(board_name);

        for (Board b : boardList) {
            if (b.getName().equals(board_name)) {
                board = b;
            }
        }

        Consumer<Post> saveImage = (Post post) -> {

            if (post.isImage() && !post.isOver18()) {
                String path = post.getUrl();
                List<Image> imageList;
                try {
                    imageList = imageLogic.getAll();
                } catch (NullPointerException e) {
                    imageList = new ArrayList<>();
                }
                Boolean exists = false;

                if (imageList.isEmpty()) {
                    imageLogic.add(createImageEntity(imageLogic, post));
                    FileUtility.downloadAndSaveFile(path, SAVE_DIR);
                } else {
                    for (Image i : imageList) {
                        if (i.getUrl().equals(post.getUrl()) || i.getLocalPath().equals(SAVE_DIR + FileUtility.getFileName(post.getUrl()))) {
                            exists = true;
                        }
                    }
                    if (!exists) {
                        imageLogic.add(createImageEntity(imageLogic, post));
                        FileUtility.downloadAndSaveFile(path, SAVE_DIR);
                    }
                }
            }
        };

        //create a new scraper
        Reddit scrap = new Reddit();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order

        scrap.authenticate()
                .buildRedditPagesConfig(board.getName(), 15, Sort.BEST);
        //get the next page 3 times and save the images.
        scrap.requestNextPage()
                .proccessNextPage(saveImage);

        processRequest(request, response);
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

        if (request.getParameter("view") != null) {
            board_name = request.getParameter("BoardSelect");
        }
        doGet(request, response);
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "ImageView servlet";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    private Image createImageEntity(ImageLogic logic, Post post) {
        Map<String, String[]> inputMap = new HashMap<>();
        inputMap.put(ImageLogic.URL, new String[]{post.getUrl()});
        inputMap.put(ImageLogic.DATE, new String[]{logic.convertDate(post.getDate())});
        inputMap.put(ImageLogic.LOCAL_PATH, new String[]{SAVE_DIR + FileUtility.getFileName(post.getUrl())});
        inputMap.put(ImageLogic.TITLE, new String[]{post.getTitle()});
        inputMap.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(board.getId())});
        return logic.createEntity(inputMap);
    }

    public static String getSelectedBoard() {
        return board_name;
    }
}
