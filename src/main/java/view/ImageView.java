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

    private static final String BOARD_NAME = "Wallpaper";
    private static final String SAVE_DIR = "/Documents/RedditImages/";
    private Board wallpaperBoard;

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
            out.println("<title>ImagesViewNormal</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">");
            out.println("<caption>Images</caption>");

            ImageLogic logic = LogicFactory.getFor("Image");

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

        String saveDir = System.getProperty("user.home") + SAVE_DIR;
        FileUtility.createDirectory(saveDir);

        //Using LogicFactory get the logics you need to create and add an Image 
        //to DB. Don’t forget the dependencies
        ImageLogic logic = LogicFactory.getFor("Image");

        //3)Get the board you want to use. You will use the name of this board object 
        //in reddit buildRedditPageConfig method. 
        BoardLogic blogic = LogicFactory.getFor("Board");
        List<Board> wallpaperList = blogic.getBoardsWithName(BOARD_NAME);
        
      
        if (wallpaperList.size() > 1) {
            for (Board board : wallpaperList) {
                if (board.getName().equals(BOARD_NAME)) 
                    wallpaperBoard = board;   
            }
        } else {
            wallpaperBoard = wallpaperList.get(0);
        }
       
        
        //4) Use the example provided in reddit.TestRunReddit::exampleForReadingNextPage 
        //to see how to use the reddit object. 
        //create a lambda that accepts post
        Consumer<Post> saveImage = (Post post) -> {
            //if post is an image and SFW
            if (post.isImage() && !post.isOver18()) {

                //image should not already exist in the db - correctly checking?
                List<Image> imageList = logic.getAll();
                Boolean exists = false;
                for (Image i : imageList) {
                    if (i.getUrl().equals(post.getUrl())) {
                        exists = true;
                    }
                }

                FileUtility.downloadAndSaveFile(post.getUrl(), saveDir);

                if (!exists) {
                    for(int i = 0; i < 5; i++){
                        Map<String, String[]> imageMap = new HashMap<>();
                        imageMap.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(wallpaperBoard.getId())});
                        imageMap.put(ImageLogic.TITLE, new String[]{post.getTitle()});
                        imageMap.put(ImageLogic.URL, new String[]{post.getUrl()});
                        imageMap.put(ImageLogic.LOCAL_PATH, new String[]{saveDir + post.getUrl()});
                        imageMap.put(ImageLogic.DATE, new String[]{logic.convertDate(post.getDate())});

                        Image returnedImage = logic.createEntity(imageMap);
                        logic.add(returnedImage);
                    }
                }
                
            }
        };

        //create a new scraper
        Reddit scrap = new Reddit();
        //authenticate and set up a page for wallpaper subreddit with 5 posts sorted by HOT order

        scrap.authenticate().buildRedditPagesConfig(wallpaperBoard.getName(), 5, Sort.BEST);
        //get the next page 3 times and save the images.
        scrap.requestNextPage().proccessNextPage(saveImage);

        //5) Create your custom lambda to create an Image entity, download it and 
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
        return "Sample of Host View Normal";
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
