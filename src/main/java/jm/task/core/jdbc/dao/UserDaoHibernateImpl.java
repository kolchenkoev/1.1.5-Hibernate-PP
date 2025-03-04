package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.HibernateException;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static jm.task.core.jdbc.util.Util.sessionFactory;

public class UserDaoHibernateImpl implements UserDao {

    private final Util util = Util.getInstance();

    public UserDaoHibernateImpl() {
    }

    @Override
    public void createUsersTable() {
        Session session = util.getSession();
        Transaction transaction = session.beginTransaction(); //начинаем транзакцию
        try {
            session.createNativeQuery("CREATE TABLE IF NOT EXISTS users" +  //создаем объект NativeQuery, который может выполнять запросы непосредственно в базе
                    "(id BIGINT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), lastname VARCHAR(255), age INT(111))").executeUpdate(); //выполняет запрос
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
    }


    @Override
    public void dropUsersTable() {
        Session session = util.getSession(); // получаем сессию через экземпляр Util
        Transaction transaction = session.beginTransaction();
        try {
            session.createNativeQuery("DROP TABLE IF EXISTS users").executeUpdate();
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Session session = util.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(new User(name, lastName, age)); //используем метод save() интерфейса Session для сохранения объекта User в базе данных. Hibernate автоматически сгенерирует SQL-запрос INSERT и выполнит его
            transaction.commit();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
    }

    @Override
    public void removeUserById(long id) {
        Session session = util.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(session.get(User.class, id)); //используем метод delete() интерфейса Session для удаления объекта. Hibernate сгенерирует SQL-запрос DELETE
            transaction.commit();                        //Важно: чтобы объект, который мы удаляем, был прикреплен к текущей сессии
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        Session session = util.getSession();
        CriteriaQuery<User> criteriaQuery = session.getCriteriaBuilder().createQuery(User.class);  //CriteriaQuery - механизм Hib для создания запросов к базе данных, который позволяет нам формировать запросы через Java-код, а не SQL-строки напрямую (хотя он генерирует SQL под капотом)
        criteriaQuery.from(User.class); //извлекаем из
        Transaction transaction = session.beginTransaction();
        List<User> userList = session.createQuery(criteriaQuery).getResultList(); //создаем Query объект на основе созданного CriteriaQuery, выполняем запрос и возвращаем List<User>
        try {
            transaction.commit();
            return userList;
        } catch (HibernateException e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
        return userList;
    }

    @Override
    public void cleanUsersTable() {
        Session session = util.getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.createNativeQuery("TRUNCATE TABLE users;").executeUpdate(); //TRUNCATE TABLE обычно быстрее, чем DELETE FROM table (особенно для больших таблиц), так как он не ведет логи для каждой удаленной строки
            transaction.commit();
            System.out.println("Таблица очищена");
        } catch (HibernateException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                util.closeSession(session);
            }
        }
    }
}
