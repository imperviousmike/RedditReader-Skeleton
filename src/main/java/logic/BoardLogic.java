/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.BoardDAL;
import entity.Board;
import entity.Host;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

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

    @Override
    public Board createEntity(Map<String, String[]> parameterMap) {
        //do not create any logic classes in this method.

        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Board entity = new Board();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
        if (parameterMap.containsKey(ID)) {
            try {
                entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
            } catch (java.lang.NumberFormatException ex) {
                throw new ValidationException(ex);
            }
        }

        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = (value, length) -> {
            if (value == null || value.trim().isEmpty() || value.length() > length) {
                throw new ValidationException("value cannot be null, empty or larger than " + length + " characters");
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.
        String url = parameterMap.get(URL)[0];
        String name = parameterMap.get(NAME)[0];
        Integer hostId = Integer.parseInt(parameterMap.get(HOST_ID)[0]);

        //validate the data
        validator.accept(url, 255);
        validator.accept(name, 100);

        //set values on entity
        entity.setUrl(url);
        entity.setName(name);
        entity.setHostid(new Host(Integer.parseInt(hostId)));

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

    public Board getBoardWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

}
