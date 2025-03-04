package jm.task.core.jdbc.service;
import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;
import java.util.List;

public class UserServiceImpl implements UserService {

    UserDao userDaoHib = new UserDaoHibernateImpl();

    @Override
    public void createUsersTable() {
        userDaoHib.createUsersTable();
    }
    @Override
    public void dropUsersTable() {
        userDaoHib.dropUsersTable();
    }
    @Override
    public void saveUser(String name, String lastName, byte age) {
        userDaoHib.saveUser(name, lastName, age);
    }
    @Override
    public void removeUserById(long id) { userDaoHib.removeUserById(id); }

    @Override
    public List<User> getAllUsers() {
        return userDaoHib.getAllUsers();
    }
    @Override
    public void cleanUsersTable() {
        userDaoHib.cleanUsersTable();
    }
}
