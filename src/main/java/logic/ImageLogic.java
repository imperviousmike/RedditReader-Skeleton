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

/**
 *
 * @author jonat
 */
public class ImageLogic extends GenericLogic<Image, ImageDAL> {

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

    public Image updateEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Image entity = getWithId(Integer.parseInt(parameterMap.get(ID)[0]));
        for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
            try {
                switch (map.getKey()) {
                    case ID:
                        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
                        break;
                    case URL:
                        String url = parameterMap.get(URL)[0];
                        ValidationUtil.validateString(url, 255);
                        entity.setUrl(URL);
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

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "URL", "Title", "Date", "LocalPath", "BoardID");
    }

    public List<String> getColumnCodes() {
        return Arrays.asList(ID, URL, TITLE, DATE, LOCAL_PATH, BOARD_ID);
    }

    @Override
    public List extractDataAsList(Image e) {
        return Arrays.asList(e.getId(), e.getUrl(), e.getTitle(), e.getDate(), e.getLocalPath(), e.getBoard().getId());
    }
}
