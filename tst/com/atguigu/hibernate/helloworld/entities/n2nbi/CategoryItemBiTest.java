package com.atguigu.hibernate.helloworld.entities.n2nbi;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

class CategoryItemBiTest {
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

    @Test
    public void testCreateTables() {}

    /**
     * 如果没有设置 inverse="true", 会出现以下异常
     * Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Duplicate entry '2-1' for key 'categories_items.PRIMARY'
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
     * 	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
     * 	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:490)
     *
     * 设置 inverse="true" 之后
     * Hibernate:
     *     insert
     *     into
     *         Categories
     *         (NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         Categories
     *         (NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ITEMS
     *         (NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ITEMS
     *         (NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         CATEGORIES_ITEMS
     *         (C_ID, I_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CATEGORIES_ITEMS
     *         (C_ID, I_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CATEGORIES_ITEMS
     *         (C_ID, I_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CATEGORIES_ITEMS
     *         (C_ID, I_ID)
     *     values
     *         (?, ?)
     *
     * 插入CATEGORIES，两条
     * 插入ITEMS，两条
     * 插入CATREGORIES_ITEMS，四条
     */
    @Test
    public void testSave() {
        Category category1 = new Category();
        category1.setName("C-AA");

        Category category2 = new Category();
        category2.setName("C-BB");

        Item item1 = new Item();
        item1.setName("I-11");
        Item item2 = new Item();
        item2.setName("I-22");

        // 设定关联关系
        category1.getItems().add(item1);
        category1.getItems().add(item2);
        category2.getItems().add(item1);
        category2.getItems().add(item2);

        item1.getCategories().add(category1);
        item1.getCategories().add(category2);
        item2.getCategories().add(category1);
        item2.getCategories().add(category2);

        // 执行保存操作
        session.save(category1);
        session.save(category2);
        session.save(item1);
        session.save(item2);
    }

    /**
     * Hibernate:
     *     select
     *         category0_.ID as ID1_3_0_,
     *         category0_.NAME as NAME2_3_0_
     *     from
     *         Categories category0_
     *     where
     *         category0_.ID=?
     * C-AA
     * destroyed
     */
    @Test
    public void testGet() {
        Category category = (Category) session.get(Category.class, 1);
        System.out.println(category.getName());
    }

    /**
     * Hibernate:
     *     select
     *         category0_.ID as ID1_3_0_,
     *         category0_.NAME as NAME2_3_0_
     *     from
     *         Categories category0_
     *     where
     *         category0_.ID=?
     * C-AA
     * Hibernate:
     *     select
     *         items0_.C_ID as C_ID1_3_1_,
     *         items0_.I_ID as I_ID2_0_1_,
     *         item1_.ID as ID1_6_0_,
     *         item1_.NAME as NAME2_6_0_
     *     from
     *         CATEGORIES_ITEMS items0_
     *     inner join
     *         ITEMS item1_
     *             on items0_.I_ID=item1_.ID
     *     where
     *         items0_.C_ID=?
     * 2
     * destroyed
     */
    @Test
    public void testGet1() {
        Category category = (Category) session.get(Category.class, 1);
        System.out.println(category.getName());

        // 需要连接中间表
        Set<Item> items = category.getItems();
        System.out.println(items.size());
    }
}