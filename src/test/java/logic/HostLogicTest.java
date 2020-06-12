/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import dal.EMFactory;
import entity.Host;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import common.ValidationException;
import java.util.Arrays;

/**
 *
 * @author mike
 */
public class HostLogicTest {

    private HostLogic logic;
    private Host expectedHost;

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
        Host host = new Host();
        host.setName("Junit5Test");
        host.setUrl("junit");
        host.setExtractionType("json");

        EntityManager em = EMFactory.getEMF().createEntityManager();

        em.getTransaction().begin();
        expectedHost = em.merge(host);
        em.getTransaction().commit();
        em.close();

        logic = LogicFactory.getFor("Host");
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedHost != null) {
            logic.delete(expectedHost);
        }
    }

    @Test
    final void testGetAll() {
        List<Host> list = logic.getAll();
        int originalSize = list.size();

        assertNotNull(expectedHost);
        logic.delete(expectedHost);

        list = logic.getAll();
        assertEquals(originalSize - 1, list.size());
    }

    private void assertHostEquals(Host expected, Host actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getExtractionType(), actual.getExtractionType());
    }

    @Test
    final void testGetWithId() {
        Host returnedAccount = logic.getWithId(expectedHost.getId());
        assertHostEquals(expectedHost, returnedAccount);
    }

    @Test
    final void testGetHostWithName() {
        Host returnedAccount = logic.getHostWithName(expectedHost.getName());
        assertHostEquals(expectedHost, returnedAccount);
    }

    @Test
    final void testGetHostWithUrl() {
        Host returnedHost = logic.getHostWithUrl(expectedHost.getUrl());
        assertHostEquals(expectedHost, returnedHost);
    }

    @Test
    final void testGetHostWithExtractionType() {
        List<Host> returnedHosts = logic.getHostWithExtractionType(expectedHost.getExtractionType());
        assertTrue(returnedHosts.size() >= 1);

    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(HostLogic.NAME, new String[]{"Test Create Entity"});
        sampleMap.put(HostLogic.URL, new String[]{"testCreateAccount"});
        sampleMap.put(HostLogic.EXTRACTION_TYPE, new String[]{"json"});

        Host returnedHost = logic.createEntity(sampleMap);
        logic.add(returnedHost);

        returnedHost = logic.getHostWithName(returnedHost.getName());

        assertEquals(sampleMap.get(HostLogic.NAME)[0], returnedHost.getName());
        assertEquals(sampleMap.get(HostLogic.URL)[0], returnedHost.getUrl());
        assertEquals(sampleMap.get(HostLogic.EXTRACTION_TYPE)[0], returnedHost.getExtractionType());

        logic.delete(returnedHost);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(HostLogic.ID, new String[]{Integer.toString(expectedHost.getId())});
        sampleMap.put(HostLogic.NAME, new String[]{expectedHost.getName()});
        sampleMap.put(HostLogic.URL, new String[]{expectedHost.getUrl()});
        sampleMap.put(HostLogic.EXTRACTION_TYPE, new String[]{expectedHost.getExtractionType()});

        Host returnedHost = logic.createEntity(sampleMap);

        assertHostEquals(expectedHost, returnedHost);
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(HostLogic.ID, new String[]{Integer.toString(expectedHost.getId())});
            map.put(HostLogic.NAME, new String[]{expectedHost.getName()});
            map.put(HostLogic.URL, new String[]{expectedHost.getUrl()});
            map.put(HostLogic.EXTRACTION_TYPE, new String[]{expectedHost.getExtractionType()});
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.ID, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.ID, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.NAME, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.NAME, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.URL, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.URL, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.EXTRACTION_TYPE, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.EXTRACTION_TYPE, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(HostLogic.ID, new String[]{Integer.toString(expectedHost.getId())});
            map.put(HostLogic.NAME, new String[]{expectedHost.getName()});
            map.put(HostLogic.URL, new String[]{expectedHost.getUrl()});
            map.put(HostLogic.EXTRACTION_TYPE, new String[]{expectedHost.getExtractionType()});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.NAME, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.NAME, new String[]{generateString.apply(101)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.URL, new String[]{generateString.apply(256)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(HostLogic.EXTRACTION_TYPE, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(HostLogic.EXTRACTION_TYPE, new String[]{"12b"});
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

        String extraction = new String("xml");

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(HostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(HostLogic.NAME, new String[]{generateString.apply(1)});
        sampleMap.put(HostLogic.URL, new String[]{generateString.apply(1)});
        sampleMap.put(HostLogic.EXTRACTION_TYPE, new String[]{extraction});

        //idealy every test should be in its own method
        Host returnedHost = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(HostLogic.ID)[0]), returnedHost.getId());
        assertEquals(sampleMap.get(HostLogic.NAME)[0], returnedHost.getName());
        assertEquals(sampleMap.get(HostLogic.URL)[0], returnedHost.getUrl());
        assertEquals(sampleMap.get(HostLogic.EXTRACTION_TYPE)[0], returnedHost.getExtractionType());

        sampleMap = new HashMap<>();
        sampleMap.put(HostLogic.ID, new String[]{Integer.toString(1)});
        sampleMap.put(HostLogic.NAME, new String[]{generateString.apply(45)});
        sampleMap.put(HostLogic.URL, new String[]{generateString.apply(45)});
        sampleMap.put(HostLogic.EXTRACTION_TYPE, new String[]{extraction});

        //idealy every test should be in its own method
        returnedHost = logic.createEntity(sampleMap);
        assertEquals(Integer.parseInt(sampleMap.get(HostLogic.ID)[0]), returnedHost.getId());
        assertEquals(sampleMap.get(HostLogic.NAME)[0], returnedHost.getName());
        assertEquals(sampleMap.get(HostLogic.URL)[0], returnedHost.getUrl());
        assertEquals(sampleMap.get(HostLogic.EXTRACTION_TYPE)[0], returnedHost.getExtractionType());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Name", "URL", "Extraction Type"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(HostLogic.ID, HostLogic.NAME, HostLogic.URL, HostLogic.EXTRACTION_TYPE), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedHost);
        assertEquals(expectedHost.getId(), list.get(0));
        assertEquals(expectedHost.getName(), list.get(1));
        assertEquals(expectedHost.getUrl(), list.get(2));
        assertEquals(expectedHost.getExtractionType(), list.get(3));
    }
}
