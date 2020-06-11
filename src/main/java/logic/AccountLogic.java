package logic;

import common.ValidationException;
import common.ValidationUtil;
import dal.AccountDAL;
import entity.Account;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author Shariar (Shawn) Emami
 */
public class AccountLogic extends GenericLogic<Account, AccountDAL> {

    /**
     * create static final variables with proper name of each column. this way
     * you will never manually type it again, instead always refer to these
     * variables.
     *
     * by using the same name as column id and HTML element names we can make
     * our code simpler. this is not recommended for proper production project.
     */
    public static final String NICKNAME = "nickname";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String ID = "id";

    AccountLogic() {
        super(new AccountDAL());
    }

    @Override
    public List<Account> getAll() {
        return get(() -> dal().findAll());
    }

    @Override
    public Account getWithId(int id) {
        return get(() -> dal().findById(id));
    }

    public Account getAccountWithNickname(String nickname) {
        return get(() -> dal().findByNickname(nickname));
    }

    public Account getAccountWithUsername(String username) {
        return get(() -> dal().findByUsername(username));
    }

    public List<Account> getAccountsWithPassword(String password) {
        return get(() -> dal().findByPassword(password));
    }

    public Account isCredentialValid(String username, String password) {
        return get(() -> dal().validateUser(username, password));
    }

    @Override
    public List<Account> search(String search) {
        return get(() -> dal().findContaining(search));
    }

    @Override
    public Account createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
        Account entity = new Account();
        for (Map.Entry<String, String[]> map : parameterMap.entrySet()) {
            try {
                switch (map.getKey()) {
                    case USERNAME:
                        String userName = parameterMap.get(USERNAME)[0];
                        ValidationUtil.validateString(userName, 45);
                        entity.setUsername(userName);
                        break;
                    case NICKNAME:
                        String name = parameterMap.get(NICKNAME)[0];
                        ValidationUtil.validateString(name, 45);
                        entity.setNickname(name);
                        break;
                    case ID:
                        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
                        break;
                    case PASSWORD:
                        String password = parameterMap.get(PASSWORD)[0];
                        ValidationUtil.validateString(password, 45);
                        entity.setPassword(password);
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                throw new ValidationException(ex);
            }
        }
        return entity;
    }

    /**
     * this method is used to send a list of all names to be used form table
     * column headers. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnCodes and
     * extractDataAsList
     *
     * @return list of all column names to be displayed.
     */
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList("ID", "Nickname", "Username", "Password");
    }

    /**
     * this method returns a list of column names that match the official column
     * names in the db. by having all names in one location there is less chance
     * of mistakes.
     *
     * this list must be in the same order as getColumnNames and
     * extractDataAsList
     *
     * @return list of all column names in DB.
     */
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, NICKNAME, USERNAME, PASSWORD);
    }

    /**
     * return the list of values of all columns (variables) in given entity.
     *
     * this list must be in the same order as getColumnNames and getColumnCodes
     *
     * @param e - given Entity to extract data from.
     *
     * @return list of extracted values
     */
    @Override
    public List<?> extractDataAsList(Account e) {
        return Arrays.asList(e.getId(), e.getNickname(), e.getUsername(), e.getPassword());
    }
}
