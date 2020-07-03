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
import entity.Host;
import java.util.Arrays;
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
 * BoardLogic class.
 *
 * @author Earl_Grey_Hot
 */
public class BoardLogicTest {

    private BoardLogic logic;
    private Board expectedBoard;

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

        Board board = new Board();
        board.setUrl("junitTestURL");
        board.setName("junit");
        board.setHostid(new Host(1));
        EntityManager em = EMFactory.getEMF().createEntityManager();
        em.getTransaction().begin();
        expectedBoard = em.merge(board);
        em.getTransaction().commit();
        em.close();

        logic = LogicFactory.getFor("Board");
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedBoard != null) {
            logic.delete(expectedBoard);
        }
    }

    @Test
    final void testGetAll() {
        List<Board> list = logic.getAll();
        int originalSize = list.size();
        assertNotNull(expectedBoard);
        logic.delete(expectedBoard);
        list = logic.getAll();
        assertEquals(originalSize - 1, list.size());
    }

    private void assertBoardEquals(Board expected, Board actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getName(), actual.getName());
    }

    /**
     * Tests that the correct Board is retrieved with the getWithId() method
     */
    @Test
    final void testGetWithId() {
        Board returnedBoard = logic.getWithId(expectedBoard.getId());
        assertBoardEquals(expectedBoard, returnedBoard);
    }

    /**
     * Tests that the getBoardsWithHostID() method can correctly return a List
     * of Board objects of the correct size.
     */
    @Test
    final void testGetBoardsWithHostID() {
        List<Board> list = logic.getBoardsWithHostID(expectedBoard.getHostid().getId());
        int originalSize = list.size();
        assertNotNull(expectedBoard);
        logic.delete(expectedBoard);
        list = logic.getBoardsWithHostID(expectedBoard.getHostid().getId());
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * Tests that the getBoardsWithName() method can correctly return a List of
     * Board objects of the correct size.
     */
    @Test
    final void testGetBoardsWithName() {
        List<Board> list = logic.getBoardsWithName(expectedBoard.getName());
        int originalSize = list.size();
        assertNotNull(expectedBoard);
        logic.delete(expectedBoard);
        list = logic.getBoardsWithName(expectedBoard.getName());
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * Tests that the correct Board is retrieved with the getBoardWithUrl()
     * method
     */
    @Test
    final void testGetBoardWithUrl() {
        Board returnedBoard = logic.getBoardWithUrl(expectedBoard.getUrl());
        assertBoardEquals(expectedBoard, returnedBoard);
    }

    /**
     * Tests that the createEntity() method correctly instantiates a Board
     * object under normal conditions.
     */
    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
        sampleMap.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
        sampleMap.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});

        Board returnedBoard = logic.createEntity(sampleMap);

        assertBoardEquals(expectedBoard, returnedBoard);
    }

    /**
     * Test to confirm that a ValidationException is thrown if a Board object is
     * instantiated with a null or empty property.
     */
    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
            map.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
            map.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.ID, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.ID, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.URL, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.URL, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.NAME, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.NAME, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    }

    /**
     * Test to confirm that a ValidationException is thrown if a Board object is
     * instantiated with a String value that exceeds the character limit.
     */
    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
            sampleMap.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
            sampleMap.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.URL, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.NAME, new String[]{generateString.apply(101)});
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
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
        sampleMap.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});

        Board returnedBoard = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(BoardLogic.ID)[0]), returnedBoard.getId());
        assertEquals(sampleMap.get(BoardLogic.URL)[0], returnedBoard.getUrl());
        assertEquals(sampleMap.get(BoardLogic.NAME)[0], returnedBoard.getName());

        sampleMap = new HashMap<>();
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(BoardLogic.URL, new String[]{generateString.apply(255)});
        sampleMap.put(BoardLogic.NAME, new String[]{generateString.apply(100)});

        returnedBoard = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(BoardLogic.ID)[0]), returnedBoard.getId());
        assertEquals(sampleMap.get(BoardLogic.URL)[0], returnedBoard.getUrl());
        assertEquals(sampleMap.get(BoardLogic.NAME)[0], returnedBoard.getName());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Url", "Name", "Host ID"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(BoardLogic.ID, BoardLogic.URL, BoardLogic.NAME, BoardLogic.HOST_ID), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedBoard);
        assertEquals(expectedBoard.getId(), list.get(0));
        assertEquals(expectedBoard.getUrl(), list.get(1));
        assertEquals(expectedBoard.getName(), list.get(2));

        Host host = (Host) list.get(3);
        assertEquals(expectedBoard.getHostid().getId(), host.getId());

    }
}
