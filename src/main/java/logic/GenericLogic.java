package logic;

import common.ValidationException;
import dal.DataAccessLayer;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Shariar (Shawn) Emami
 * @param <E> - entity type
 * @param <T> - DAL type
 */
abstract class GenericLogic< E, T extends DataAccessLayer<E>> implements Logic<E> {

    private final T DAL;

    GenericLogic(T dal) {
        this.DAL = dal;
    }

    protected final T dal() {
        return DAL;
    }

    protected <R> R get(Supplier<R> supplier) {
        R r;
        DAL.beginTransaction();
        r = supplier.get();
        DAL.closeTransaction();
        return r;
    }

    @Override
    public void add(E entity) {
        DAL.beginTransaction();
        DAL.save(entity);
        DAL.commitAndCloseTransaction();
    }

    @Override
    public void delete(E entity) {
        DAL.beginTransaction();
        DAL.delete(entity);
        DAL.commitAndCloseTransaction();
    }

    @Override
    public void detach(E entity) {
        DAL.beginTransaction();
        DAL.detach(entity);
        DAL.commitAndCloseTransaction();
    }

    @Override
    public E update(E entity) {
        DAL.beginTransaction();
        E e = DAL.update(entity);
        DAL.commitAndCloseTransaction();
        return e;
    }

    /**
     * this method is only needed for bonus.
     * the search process is dependent on the @NamedQuery used.
     *
     * @param search - string word to search the db.
     *
     * @return a list of entities which contain the string argument
     */
    @Override
    public List<E> search(String search) {
        throw new UnsupportedOperationException("Method=\"search( String)\" is not implemented, only needed for bonus");
    }

    /**
     * this method is only needed for bonus.
     * this method needs to be overridden if the entity has dependencies.
     * within the method other logic's can be created to menupilate the dependencies.
     * by default this method does the exact same thing as createEntity method.
     *
     * @param parameterMap - new data with which to update an entity
     *
     * @return an updated entity with given requestData
     */
    @Override
    public E updateEntity(Map<String, String[]> parameterMap) {
        return createEntity(parameterMap);
    }

    protected void validateString(Map<String, String[]> map, String key, int length) {
        if (map.containsKey(key) && map.get(key)[0] != null) {
            String name = map.get(key)[0];
            if (name.isEmpty() || name.length() > length) {
                throw new ValidationException("Key=\"" + key + "\" must be betwwen 1 and " + length + " char's");
            }
        } else {
            throw new ValidationException("Key=\"" + key + "\" does not exist");
        }
    }
}
