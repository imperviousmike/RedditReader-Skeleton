/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import common.ValidationUtil;
import dal.ImageDAL;
import entity.Board;
import entity.Image;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author jonat
 */
public class ImageLogic extends GenericLogic<Image, ImageDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables.
     *
     * by using the same name as column id and HTML element names we can make
     * our code simpler. this is not recommended for proper production project.
     */
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String LOCAL_PATH = "localPath";
    public static final String BOARD_ID = "boardId";

    ImageLogic() {
        super(new ImageDAL());

    }

    @Override
    public List<Image> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Image getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public List<Image> getImagesWithBoardId(int boardID) {
        return get(() -> dal().findByBoardId(boardID));
    }

    public List<Image> getImagesWithTitle(String title) {
        return get(() -> dal().findByTitle(title));
    }

    public Image getImageWithUrl(String url) {
        return get(() -> dal().findByUrl(url));
    }

    public Image getImageWithLocalPath(String path) {
        return get(() -> dal().findByLocalPath(path));
    }

    public List<Image> getImagesWithDate(Date date) {
        return get(() -> dal().findByDate(date));
    }

    public String convertDate(Date date) {
        return FORMATTER.format(date);
    }

    @Override
    public List<Image> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    @Override
    public Image createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Image entity = new Image();
        for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
            try {
                switch (map.getKey()) {
                    case ID:
                        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
                        break;
                    case URL:
                        String url = parameterMap.get(URL)[0];
                        ValidationUtil.validateString(url, 255);
                        entity.setUrl(url);
                        break;
                    case TITLE:
                        String title = parameterMap.get(TITLE)[0];
                        ValidationUtil.validateString(title, 1000);
                        entity.setTitle(title);
                        break;
                    case DATE:
                        entity.setDate(FORMATTER.parse(parameterMap.get(DATE)[0]));
                        break;
                    case LOCAL_PATH:
                        String path = parameterMap.get(LOCAL_PATH)[0];
                        ValidationUtil.validateString(path, 255);
                        entity.setLocalPath(path);
                        break;
                    case BOARD_ID:
                        entity.setBoard(new Board(Integer.parseInt(parameterMap.get(BOARD_ID)[0])));
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
     * This method is used to update an image in the database. It takes a new
     * set of parameters, validates them using methods from the ValidationUtil
     * class, and sets them to fields of an existing Image object. If the values
     * in the parameterMap do not fit the criteria required by the
     * ValidationUtil methods, then a ValidationException is thrown.
     */
    public Image updateEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Image entity = createEntity(parameterMap);
        if (findDuplicate(entity)) {
            throw new ValidationException("Duplicate found, cannot update");
        }
        return entity;
    }

<<<<<<< HEAD
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
=======
    private boolean findDuplicate(Image entity) {
        List<Image> images = getAll();
        images.remove(getWithId(entity.getId()));
        List duplicateEntries = images.stream()
                .filter(e -> e.getId().equals(entity.getId()))
                .filter(e -> e.getUrl().equals(entity.getUrl()))
                .filter(e -> e.getLocalPath().equals(entity.getLocalPath()))
                .collect(Collectors.toList());
        return !duplicateEntries.isEmpty();
    }

>>>>>>> 920b9bb70d2223c0239db70a11256938d76aca2f
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Title", "Date", "LocalPath", "BoardID");
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
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, TITLE, DATE, LOCAL_PATH, BOARD_ID);
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
    public List extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getTitle(), e.getDate(), e.getLocalPath(), e.getBoard());
    }
}
