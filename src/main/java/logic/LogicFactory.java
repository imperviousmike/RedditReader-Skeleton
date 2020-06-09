package logic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Michael
 */
public class LogicFactory {

    private static final String PACKAGE = "logic";
    private static final String SUFFIX = "Logic";

    public static <T> T getFor(String entityName) {

        try {
            return getFor((Class< T>) Class.forName(PACKAGE + "." + entityName + SUFFIX));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static <T> T getFor(Class<T> type) throws ClassNotFoundException {
        try {
            Constructor declaredConstructor = type.getDeclaredConstructor();
            return (T) declaredConstructor.newInstance();

        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new ClassNotFoundException();
        }

    }

}
