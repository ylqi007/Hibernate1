package com.atguigu.hibernate.helloworld;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

import java.sql.Date;


class NewsTest {

    /* 目标：希望执行后，有表，且表中有记录 */
    @Test
    public void test() {
        // 1. 创建一个SessionFactory对象
        SessionFactory sessionFactory = null;

        // 1.1 创建一个Configuration对象：对应Hibernate的基本配置信息和对象关系映射信息
        Configuration configuration = new Configuration().configure();

        // 1.2 创建一个ServiceRegistry对象，Hibernte4.x新添加的对象
        ServiceRegistry serviceRegistry =
                new ServiceRegistryBuilder().applySettings(configuration.getProperties())
                                            .buildServiceRegistry();
        // 1.3 创建SessionFactory
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        // 2. 创建一个Session对象
        Session session = sessionFactory.openSession();

        // 3. 开启事务
        Transaction transaction = session.beginTransaction();

        // 4. 执行保存操作
        News news = new News("Java1", "atguigu", new Date(new java.util.Date().getTime()));
        session.save(news);

        // 5. 提交事务
        transaction.commit();

        // 6. 关闭Session
        session.close();

        // 7. 关闭SessionFactory
        sessionFactory.close();
    }

}