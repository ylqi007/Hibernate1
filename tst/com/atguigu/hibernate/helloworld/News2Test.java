package com.atguigu.hibernate.helloworld;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.hibernate.Session;
import org.hibernate.SessionException;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class News2Test {
    private SessionFactory sessionFactory;
    // 在实际开发中，Session和Transaction是不能作为成员变量的，因为可能存在并发的问题。
    private Session session;
    private Transaction transaction;

    @BeforeEach
    public void init() {
        System.out.println("init");
        Configuration configuration = new Configuration().configure();
        ServiceRegistry serviceRegistry =
                new ServiceRegistryBuilder().applySettings(configuration.getProperties())
                                            .buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @AfterEach
    public void destroy() {
        transaction.commit();
        session.close();
        sessionFactory.close();
        System.out.println("destroyed");
    }

    /**
     * session.contains(news)=false
     */
    @Test
    public void testTransientState() {
        News2 news = new News2("Java", "atguigu", new java.util.Date());
        System.out.println("session.contains(news)=" + session.contains(news)); // false

        assertFalse(session.contains(news));
    }

    /**
     * session.contains(news)=false
     * Hibernate:
     *     insert
     *     into
     *         NEWS2
     *         (TITLE, AUTHOR, DATE)
     *     values
     *         (?, ?, ?)
     * session.contains(news)=true
     */
    @Test void testPersistentStateSave() {
        News2 news = new News2("Java", "atguigu", new java.util.Date());
        System.out.println("session.contains(news)=" + session.contains(news)); // false
        session.save(news); // flush时，发送一条INSERT语句
        System.out.println("session.contains(news)=" + session.contains(news)); // true

        assertTrue(session.contains(news));
    }

    /**
     * session.contains(news)=false
     * Hibernate:
     *     insert
     *     into
     *         NEWS2
     *         (TITLE, AUTHOR, DATE)
     *     values
     *         (?, ?, ?)
     * session.contains(news)=true
     */
    @Test void testPersistentStatePersist() {
        News2 news = new News2("Java", "atguigu", new java.util.Date());
        System.out.println("session.contains(news)=" + session.contains(news)); // false
        session.persist(news); // flush时，发送一条INSERT语句
        System.out.println("session.contains(news)=" + session.contains(news)); // true

        assertTrue(session.contains(news));
    }

    /**
     * session.contains(news)=false
     * Hibernate:
     *     insert
     *     into
     *         NEWS2
     *         (TITLE, AUTHOR, DATE)
     *     values
     *         (?, ?, ?)
     * session.contains(news)=true
     * session.isOpen()=false
     */
    @Test
    public void testDetachedState() {
        News2 news = new News2("Java", "atguigu", new java.util.Date());
        System.out.println("session.contains(news)=" + session.contains(news)); // false
        session.persist(news); // flush时，发送一条INSERT语句
        System.out.println("session.contains(news)=" + session.contains(news)); // true

        session.close();
        assertFalse(session.isOpen());
        System.out.println("session.isOpen()=" + session.isOpen());     // false
        SessionException exception = assertThrows(SessionException.class, () -> session.contains(news)); //org.hibernate.SessionException: Session is closed!
        assertTrue(exception.getMessage().contains("Session is closed!"));
        session = sessionFactory.openSession();
    }

}