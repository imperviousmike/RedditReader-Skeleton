/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.ImageDAL;
import entity.Board;
import entity.Host;
import entity.Image;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import static logic.BoardLogic.HOST_ID;
import static logic.BoardLogic.ID;
import static logic.BoardLogic.NAME;
import static logic.BoardLogic.URL;

/**
 *
 * @author jonat
 */
public class ImageLogic extends GenericLogic<Image, ImageDAL> {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
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
    public Image createEntity(Map<String, String[]> parameterMap) {

        //do not create any logic classes in this method.
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Image entity = new Image();

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
        String date = parameterMap.get(DATE)[0];
        String url = parameterMap.get(URL)[0];
        String title = parameterMap.get(TITLE)[0];
        String localPath = parameterMap.get(LOCAL_PATH)[0];
        
        Date extractedDate;
        try {
            extractedDate = FORMATTER.parse(date);
        } catch (ParseException ex) {
            throw new ValidationException(ex);
        }
        
        //validate the data
        validator.accept(url, 255);
        validator.accept(title, 1000);
        validator.accept(localPath, 255);
        
        //set values on entity
        entity.setUrl(url);
        entity.setTitle(title);
        entity.setDate(extractedDate);

        return entity;
    }

    public Image updateEntity(Map<String, String[]> parameterMap) {
        
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID, URL, Title, Date, LocalPath,BoardID");
    }

    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, TITLE, DATE, LOCAL_PATH, BOARD_ID);
    }

    @Override
    public List extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getTitle(), e.getDate(), e.getLocalPath(), e.getBoard());
    }
}
