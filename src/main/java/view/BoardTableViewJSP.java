package view;

import common.ValidationException;
import entity.Board;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.annotation.WebServlet;
import logic.BoardLogic;
import logic.LogicFactory;

/**
 *
 * @author Shariar (Shawn) Emami
 */
@WebServlet(name = "BoardTableJSP", urlPatterns = {"/BoardTableJSP"})
public class BoardTableViewJSP extends HttpServlet {

    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("entities", extractTableData(req));
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/ShowTable.jsp").forward(req, resp);
    }

    private List<?> extractTableData(HttpServletRequest req) {
        String search = req.getParameter("searchText");
        BoardLogic logic = LogicFactory.getFor("Board");
        req.setAttribute("columnName", logic.getColumnNames());
        req.setAttribute("columnCode", logic.getColumnCodes());
        List<Board> list;
        if (search != null) {
            list = logic.search(search);
        } else {
            list = logic.getAll();
        }
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return appendDatatoNewList(list, logic::extractDataAsList);
    }

    private <T> List<?> appendDatatoNewList(List<T> list, Function<T, List<?>> toArray) {
        List<List<?>> newlist = new ArrayList<>(list.size());
        list.forEach(i -> newlist.add(toArray.apply(i)));
        return newlist;
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        m.keySet().forEach((k) -> {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("POST");
        BoardLogic logic = LogicFactory.getFor("Board");
        Map<String, String[]> map = req.getParameterMap();
        if (map.containsKey("deleteMark")) {
            for (String delete : map.get("deleteMark")) {
                logic.delete(logic.getWithId(Integer.valueOf(delete)));
            }
        } else if (req.getParameter("edit") != null) {
            Board board = logic.updateEntity(req.getParameterMap());
            try {
                Board oldBoard = logic.getBoardWithUrl(board.getUrl());
                if (oldBoard != null) {
                    logic.delete(oldBoard);
                }
                logic.update(board);
            } catch (javax.persistence.RollbackException ex) {
                throw new ValidationException(ex);
            }
        }
        fillTableData(req, resp);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("GET");
        doPost(req, resp);
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("PUT");
        doPost(req, resp);
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param req servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("DELETE");
        doPost(req, resp);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Board Table using JSP";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

}
