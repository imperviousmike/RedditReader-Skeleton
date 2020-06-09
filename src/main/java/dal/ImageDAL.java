/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Image;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mike
 */
public class ImageDAL extends GenericDAL<Image> {

    public ImageDAL() {
        super(Image.class);
    }

    @Override
    public List<Image> findAll() {
        return findResults("Image.findAll", null);
    }

    @Override
    public Image findById(int id) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Image.findById", map);
    }

    public List<Image> findByBoardId(int boardId) {
        Map<String, Object> map = new HashMap<>();
        map.put("Board_id", boardId);
        return findResults("Image.findByBoardId", map);
    }

    public List<Image> findByTitle(String title) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        return findResults("Image.findByTitle", map);
    }

    public Image findByUrl(String url) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", url);
        return findResult("Image.findByUrl", map);
    }

    public List<Image> findByDate(Date date) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", date);
        return findResults("Image.findByDate", map);
    }

    public Image findByLocalPath(String localPath) {
        Map<String, Object> map = new HashMap<>();
        map.put("local_path", localPath);
        return findResult("Image.findByLocalPath", map);
    }

}
