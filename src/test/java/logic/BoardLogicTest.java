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
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing

        HostLogic hLogic = LogicFactory.getFor("Host");
        Host testHost = hLogic.getWithId(1);
        
        Board board = new Board();
        board.setUrl("junitTestURL"); //test whatever site? Test improper URLs?
        board.setName("junit");
        board.setHostid(testHost);

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedBoard = em.merge(board);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
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
        //get all the accounts from the DB
        List<Board> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(expectedBoard);
        //delete the new account
        logic.delete(expectedBoard);

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertBoardEquals(Board expected, Board actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getHostid().getId(), actual.getHostid().getId());
    }

    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Board returnedBoard = logic.getWithId(expectedBoard.getId());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertBoardEquals(expectedBoard, returnedBoard);
    }
    
    @Test
    final void testGetBoardsWithHostID() {
        List<Board> boards = logic.getBoardsWithHostID(expectedBoard.getHostid().getId());
        //TODO follow pseudo code
    }

    @Test
    final void testGetBoardsWithName() {
        List<Board> boards = logic.getBoardsWithName(expectedBoard.getName());
        //TODO follow pseudo code
    }

    @Test
    final void testGetBoardWithUrl() {
        Board returnedBoard = logic.getBoardWithUrl(expectedBoard.getUrl());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertBoardEquals(expectedBoard, returnedBoard);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
        sampleMap.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
        sampleMap.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});
        sampleMap.put(BoardLogic.HOST_ID, new String[]{Integer.toString(1)});

        Board returnedBoard = logic.createEntity(sampleMap);

        assertBoardEquals(expectedBoard, returnedBoard);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
        map.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
        map.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
        map.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});
        map.put(BoardLogic.HOST_ID, new String[]{Integer.toString(1)});
        };
        
        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.URL, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(BoardLogic.NAME, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(BoardLogic.NAME, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(expectedBoard.getId())});
        sampleMap.put(BoardLogic.URL, new String[]{expectedBoard.getUrl()});
        sampleMap.put(BoardLogic.NAME, new String[]{expectedBoard.getName()});
        sampleMap.put(BoardLogic.HOST_ID, new String[]{Integer.toString(1)});
        };
                
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
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
        sampleMap.put(BoardLogic.HOST_ID, new String[]{Integer.toString(1)});

        //idealy every test should be in its own method
        Board returnedBoard = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(BoardLogic.ID)[0]), returnedBoard.getId());
        assertEquals(sampleMap.get(BoardLogic.URL)[0], returnedBoard.getUrl());
        assertEquals(sampleMap.get(BoardLogic.NAME)[0], returnedBoard.getName());
        assertEquals(sampleMap.get(BoardLogic.HOST_ID)[0], Integer.toString(returnedBoard.getHostid().getId()));

        sampleMap = new HashMap<>();
        sampleMap.put(BoardLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(BoardLogic.URL, new String[]{generateString.apply(255)});
        sampleMap.put(BoardLogic.NAME, new String[]{generateString.apply(100)});
        sampleMap.put(BoardLogic.HOST_ID, new String[]{Integer.toString(1)});

        //idealy every test should be in its own method
        returnedBoard = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(BoardLogic.ID)[0]), returnedBoard.getId());
        assertEquals(sampleMap.get(BoardLogic.URL)[0], returnedBoard.getUrl());
        assertEquals(sampleMap.get(BoardLogic.NAME)[0], returnedBoard.getName());
        assertEquals(sampleMap.get(BoardLogic.HOST_ID)[0], Integer.toString(returnedBoard.getHostid().getId()));
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
        
        Host host = (Host)list.get(3);
        assertEquals(expectedBoard.getHostid().getId(), host.getId());

    }
}
