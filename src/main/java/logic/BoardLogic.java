/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import common.ValidationUtil;
import dal.BoardDAL;
import entity.Board;
import entity.Host;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Earl_Grey_Hot
 */
public class BoardLogic extends GenericLogic<Board, BoardDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables.
     *
     * by using the same name as column id and HTML element names we can make
     * our code simpler. this is not recommended for proper production project.
     */
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String HOST_ID = "hostid";

    BoardLogic() {
        super(new BoardDAL());
    }

    @Override
    public List<Board> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Board getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<Board> getBoardsWithHostID(int hostid) {
        return get(() -> dal().findByHostid(hostid));
    }

    public List<Board> getBoardsWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    public Board getBoardWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    @Override
    public Board createEntity(Map<String, String[]> parameterMap) {
        
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Board entity = new Board();

        for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
            try {
                switch (map.getKey()) {
                    case URL:
                        String url = parameterMap.get(URL)[0];
                        ValidationUtil.validateString(url, 255);
                        entity.setUrl(url);
                        break;
                    case NAME:
                        String name = parameterMap.get(NAME)[0];
                        ValidationUtil.validateString(name, 100);
                        entity.setName(name);
                        break;
                    case ID:
                        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
                        break;
                    case HOST_ID:
                        entity.setHostid(new Host(Integer.parseInt(parameterMap.get(HOST_ID)[0])));
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                throw new ValidationException(ex);
            }
        }

        return entity;
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Url", "Name", "Host ID");
    }

    /**
     * this method returns a list of column names that match the official column
     * names in the db. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnNames and
     * extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, NAME, HOST_ID);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList(Board e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getName(), e.getHostid());
    }

}
