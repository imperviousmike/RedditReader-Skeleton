/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import common.ValidationUtil;
import dal.HostDAL;
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
public class HostLogic extends GenericLogic<Host, HostDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables.
     *
     * by using the same name as column id and HTML element names we can make
     * our code simpler. this is not recommended for proper production project.
     */
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String URL = "url";
    public static final String EXTRACTION_TYPE = "extractionType";

    HostLogic() {
        super(new HostDAL());
    }

    @Override
    public List<Host> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Host getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Host getHostWithName(String name) {
        return get(() -> dal().findByName(name));
    }

    public Host getHostWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    public List<Host> getHostWithExtractionType(String type) {
        return get(() -> dal().findByExtractionType(type));
    }

    @Override
    public Host createEntity(Map<String, String[]> parameterMap) {
        
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Host entity = new Host();
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
                    case EXTRACTION_TYPE:
                        String extractionType = parameterMap.get(EXTRACTION_TYPE)[0];
                        ValidationUtil.validateExtractionType(extractionType);
                        entity.setExtractionType(extractionType);
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
        return Arrays.asList("ID", "Name", "URL", "Extraction Type");
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
        return Arrays.asList(ID, NAME, URL, EXTRACTION_TYPE);
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
    public List<?> extractDataAsList(Host e) {
        return Arrays.asList(e.getId(), e.getName(), e.getUrl(), e.getExtractionType());
    }

}
