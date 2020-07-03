/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Board;
import entity.Image;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * This class is used to test the normal, edge, and invalid states of the
 * ImageLogic class.
 *
 * @author mike
 */
public class ImageLogicTest {

    private ImageLogic logic;
    private Image expectedImage;
    private Calendar cal = (Calendar) new Calendar.Builder().setFields(Calendar.YEAR,
            2020, Calendar.MONTH, 9, Calendar.DAY_OF_MONTH, 10,
            Calendar.HOUR_OF_DAY, 5, Calendar.MINUTE, 5, Calendar.SECOND, 5)
            .build();

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditReader", "common.ServletListener");
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {

        Image image = new Image();
        image.setLocalPath("C:\\junit");
        image.setTitle("Junit");
        image.setUrl("https://junit.com");
        Date date = ImageLogic.FORMATTER.parse("2020-10-10 5:5:5");
        image.setDate(date);
        image.setBoard(new Board(1));

        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedImage = em.merge(image);
        em.getTransaction().commit();
        em.close();

        logic = LogicFactory.getFor("Image");
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }

    @Test
    final void testGetAll() {
        List<Image> list = logic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);
        list = logic.getAll();
        assertEquals(originalSize - 1, list.size());
    }

    private void assertImageEquals(Image expected, Image actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getLocalPath(), actual.getLocalPath());
    }

    /**
     * Tests that the correct Image is retrieved with the getWithId() method
     */
    @Test
    final void testGetWithId() {
        Image returnedImage = logic.getWithId(expectedImage.getId());
        assertImageEquals(expectedImage, returnedImage);
    }

    /**
     * Tests that the correct Image is returned with the getImageWithLocalPath()
     * method
     */
    @Test
    final void testGetWithLocalPath() {
        Image returnedImage = logic.getImageWithLocalPath(expectedImage.getLocalPath());
        assertImageEquals(expectedImage, returnedImage);
    }

    /**
     * Tests that the correct Image is returned with the getImageWithUrl()
     * method
     */
    @Test
    final void testGetWithUrl() {
        Image returnedImage = logic.getImageWithUrl(expectedImage.getUrl());
        assertImageEquals(expectedImage, returnedImage);
    }

    /**
     * Tests that the getImagesWithTitle() method can correctly return a List of
     * Image objects of the correct size.
     */
    @Test
    final void testImagesGetWithTitle() {
        List<Image> list = logic.getImagesWithTitle(expectedImage.getTitle());
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);

        list = logic.getImagesWithTitle(expectedImage.getTitle());
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * Tests that the getImagesWithImageId() method can correctly return a List
     * of Image objects of the correct size.
     */
    @Test
    final void testGetImagesWithImageId() {
        List<Image> list = logic.getImagesWithBoardId(expectedImage.getBoard().getId());
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);
        list = logic.getImagesWithBoardId(expectedImage.getBoard().getId());
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * Tests that the getImagesWithDate() method can correctly return a List of
     * Image objects of the correct size.
     */
    @Test
    final void testGetImagesWithDate() {
        List<Image> list = logic.getImagesWithDate(expectedImage.getDate());
        int originalSize = list.size();
        assertNotNull(expectedImage);
        logic.delete(expectedImage);

        list = logic.getImagesWithDate(expectedImage.getDate());
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * Tests that the testCovertDate() method correctly converts the Date
     * property of an image object to the desired format.
     */
    @Test
    final void testConvertDate() {
        assertEquals(logic.convertDate(expectedImage.getDate()), logic.convertDate(cal.getTime()));
    }

    /**
     * Tests that the createEntity() method correctly instantiates an Image
     * object under normal conditions.
     */
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        sampleMap.put(ImageLogic.DATE, new String[]{logic.convertDate(expectedImage.getDate())});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        sampleMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        sampleMap.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(expectedImage.getBoard().getId())});

        Image returnedImage = logic.createEntity(sampleMap);

        assertImageEquals(expectedImage, returnedImage);
    }

    /**
     * Test to confirm that a ValidationException is thrown if an Image object
     * is instantiated with a null or empty property.
     */
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.DATE, new String[]{logic.convertDate(expectedImage.getDate())});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(expectedImage.getBoard().getId())});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.DATE, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.LOCAL_PATH, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.TITLE, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.BOARD_ID, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.BOARD_ID, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    }

    /**
     * Test to confirm that a ValidationException is thrown if an Image object
     * is instantiated with a String value that exceeds the character limit.
     */
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            map.put(ImageLogic.DATE, new String[]{logic.convertDate(expectedImage.getDate())});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(expectedImage.getBoard().getId())});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.URL, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.DATE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.DATE, new String[]{generateString.apply(20)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.TITLE, new String[]{generateString.apply(1001)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(ImageLogic.BOARD_ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(ImageLogic.BOARD_ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    }

    
    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        sampleMap.put(ImageLogic.DATE, new String[]{logic.convertDate(cal.getTime())});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        sampleMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        sampleMap.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(expectedImage.getBoard().getId())});

        Image returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ImageLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.DATE)[0], logic.convertDate(returnedImage.getDate()));
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(sampleMap.get(ImageLogic.BOARD_ID)[0], Integer.toString(returnedImage.getBoard().getId()));

        sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(ImageLogic.URL, new String[]{generateString.apply(255)});
        sampleMap.put(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(255)});
        sampleMap.put(ImageLogic.DATE, new String[]{logic.convertDate(expectedImage.getDate())});
        sampleMap.put(ImageLogic.TITLE, new String[]{generateString.apply(1000)});
        sampleMap.put(ImageLogic.BOARD_ID, new String[]{Integer.toString(expectedImage.getBoard().getId())});

        returnedImage = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(ImageLogic.ID)[0]), returnedImage.getId());
        assertEquals(sampleMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(sampleMap.get(ImageLogic.DATE)[0], logic.convertDate(returnedImage.getDate()));
        assertEquals(sampleMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
        assertEquals(sampleMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(sampleMap.get(ImageLogic.BOARD_ID)[0], Integer.toString(returnedImage.getBoard().getId()));
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Title", "Date", "LocalPath", "BoardID"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.URL, ImageLogic.TITLE, ImageLogic.DATE, ImageLogic.LOCAL_PATH, ImageLogic.BOARD_ID), list);

    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getUrl(), list.get(1));
        assertEquals(expectedImage.getTitle(), list.get(2));
        assertEquals(expectedImage.getDate(), list.get(3));
        assertEquals(expectedImage.getLocalPath(), list.get(4));
        Board board = (Board) list.get(5);  //test fails here
        assertEquals(expectedImage.getBoard().getId(), board.getId());

    }
}
