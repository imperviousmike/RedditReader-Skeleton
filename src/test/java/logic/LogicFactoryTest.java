/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.util.ArrayList;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * This class is used to test the LogicFactory class.
 *
 * @author mike
 */
public class LogicFactoryTest {

    /**
     * Runs all of the other tests sequentially.
     */
    @Test
    public void run() {
        testGetForAccount();
        testGetForHost();
        testGetForBoard();
    }

    /**
     * Verifies that the getFor methods work correctly when used with the
     * Account class. Static members of the AccountLogic class are added to an
     * ArrayList of Strings. An AccountLogic object is created using the
     * getFor() method. getColumnCodes() is called on the AccountLogic object,
     * which returns an ArrayList of Strings that contains all of the elements
     * that were added to the initial list.
     */
    @Test
    private void testGetForAccount() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, AccountLogic.ID, AccountLogic.NICKNAME,
                AccountLogic.PASSWORD, AccountLogic.USERNAME);
        AccountLogic aL = LogicFactory.getFor("Account");
        assertTrue(aL.getColumnCodes().containsAll(list));
    }

    /**
     * Verifies that the getFor methods work correctly when used with the
     * Host class. Static members of the HostLogic class are added to an
     * ArrayList of Strings. A HostLogic object is created using the
     * getFor() method. getColumnCodes() is called on the HostLogic object,
     * which returns an ArrayList of Strings that contains all of the elements
     * that were added to the initial list.
     */
    @Test
    private void testGetForHost() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, HostLogic.EXTRACTION_TYPE, HostLogic.ID,
                HostLogic.NAME, HostLogic.URL);
        HostLogic hL = LogicFactory.getFor("Host");
        assertTrue(hL.getColumnCodes().containsAll(list));
    }

    /**
     * Verifies that the getFor methods work correctly when used with the
     * Board class. Static members of the BoardLogic class are added to an
     * ArrayList of Strings. A BoardLogic object is created using the
     * getFor() method. getColumnCodes() is called on the BoardLogic object,
     * which returns an ArrayList of Strings that contains all of the elements
     * that were added to the initial list.
     */
    @Test
    private void testGetForBoard() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, BoardLogic.HOST_ID, BoardLogic.ID,
                BoardLogic.NAME, BoardLogic.URL);
        BoardLogic bL = LogicFactory.getFor("Board");
        assertTrue(bL.getColumnCodes().containsAll(list));
    }

}
