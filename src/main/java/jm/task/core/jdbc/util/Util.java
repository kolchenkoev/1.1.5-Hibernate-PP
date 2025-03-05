package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Properties;

public class Util {
    private static volatile Util instance;
    public static SessionFactory sessionFactory;

    // private для реализации Singleton
    private Util() {
        try {
            // конфигурируем Hibernate внутри util
            Configuration configuration = new Configuration(); //объкт для настройки hibernate
            Properties properties = new Properties(); //объект для хранения настроек
            properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
            properties.put(Environment.URL, "jdbc:mysql://localhost:3306/mydbtest");
            properties.put(Environment.USER, "root");
            properties.put(Environment.PASS, "root");
            properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
            configuration.setProperties(properties); //устанавливаем свойства в конф hibernate
            configuration.addAnnotatedClass(User.class); //указывает Hibernate, что класс User является сущностью (entity), которую нужно отображать на таблицу в базе данных

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        } catch (HibernateException e) {
            System.err.println("Ошибка при создании SessionFactory: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    // реализуем шаблон Singleton
    public static Util getInstance() {
        if (instance == null) { //первая проверка выполняется без синхронизации, если экземпляр создан, то нет необходимости входить в синхронизированный блок
            synchronized (Util.class) { //обеспечиваем потокобезопасность, только один поток может войти в этот блок одновременно
                if (instance == null) { //вторая проверка необходима, потому что несколько потоков могли одновременно пройти первую проверку и оказаться в ожидании получения блокировки
                    instance = new Util(); //создает экземпляр класса Util, этот код будет выполнен только один раз при первом вызове getInstance()
                }
            }
        }
        return instance; //единственный экземпляр
    }

    // метод получения сессии
    public Session getSession() {
        return sessionFactory.openSession(); //Важно: Вызывающий код должен закрывать сессию после использования, чтобы избежать утечек ресурсов
    }

    // метод для закрытия сессии
    public void closeSession(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    // метод для закрытия SessionFactory
    public static void closeFactory() {  //Важно: Этот метод должен быть вызван при завершении работы приложения.
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
            } catch (HibernateException e) {
                System.err.println("Ошибка при закрытии SessionFactory: " + e.getMessage());
            }
        }
    }
}