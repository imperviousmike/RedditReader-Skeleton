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
 *
 * @author mike
 */
public class LogicFactoryTest {

    @Test
    public void run() {
        testGetForAccount();
        testGetForHost();
        testGetForBoard();
    }

    @Test
    private void testGetForAccount() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, AccountLogic.ID, AccountLogic.NICKNAME,
                AccountLogic.PASSWORD, AccountLogic.USERNAME);
        AccountLogic aL = LogicFactory.getFor("Account");
        assertTrue(aL.getColumnCodes().containsAll(list));
    }

    @Test
    private void testGetForHost() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, HostLogic.EXTRACTION_TYPE, HostLogic.ID,
                HostLogic.NAME, HostLogic.URL);
        HostLogic hL = LogicFactory.getFor("Host");
        assertTrue(hL.getColumnCodes().containsAll(list));
    }

    @Test
    private void testGetForBoard() {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, BoardLogic.HOST_ID, BoardLogic.ID,
                BoardLogic.NAME, BoardLogic.URL);
        BoardLogic bL = LogicFactory.getFor("Board");
        assertTrue(bL.getColumnCodes().containsAll(list));
    }

}
