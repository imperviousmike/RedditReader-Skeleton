/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

/**
 *
 * @author mike
 */
public class LogicFactoryTest {
    
    @Test
    public void testGetFor() {
        AccountLogic aL = LogicFactory.getFor("Account");
        aL.getColumnNames().forEach(System.out::println);
        
    }
    
}
