package com.atguigu.hibernate.helloworld.entities.nto1;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;


class CustomerTest {
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
     * 生成两张表：CUSTOMERS and ORDERS
     * Table ORDERS有一列是 CUSTOMER_ID
     */
    @Test
    public void testManyToOne() {

    }

    /**
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     *
     * 发送了三条INSERT
     */
    @Test
    public void testManyToOneSave() {
        Customer customer = new Customer();
        customer.setCustomerName("AA");

        Order order1 = new Order();
        order1.setOrderName("Order-1");

        Order order2 = new Order();
        order2.setOrderName("Order-2");

        // 设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);

        // 执行save操作：先出入customer (一端)，再插入orders (多端)
        session.save(customer);
        session.save(order1);
        session.save(order2);
    }

    /**
     * Hibernate:
     *     insert
     *     into
     *         ORDERS
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     update
     *         ORDERS
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     *
     */
    @Test
    public void testManyToOneSave1() {
        Customer customer = new Customer();
        customer.setCustomerName("AA");

        Order order1 = new Order();
        order1.setOrderName("Order-1");

        Order order2 = new Order();
        order2.setOrderName("Order-2");

        // 设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);

        // 执行save操作：先插入orders (多端)，再插入customer (一端)：3条INSERT，2条UPDATE
        // 先插入N的一端，再插入1的一端，会多出UPDATE语句
        // 因为在插入N的一端时，无法确定1的一端的外键值，所以只能等1的一端插入后，再额外发送UPDATE语句。
        // 推荐先插入1的一端，后插入n的一端
        session.save(order1);
        session.save(order2);
        session.save(customer);
    }

    /**
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_3_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_3_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_3_0_
     *     from
     *         ORDERS order0_
     *     where
     *         order0_.ORDER_ID=?
     * 若查询多的一端的一个对象，则默认情况下，只查询了多的一端的对象，而没有查询关联的一的那一端的对象 ==> 延迟加载
     */
    @Test
    public void testManyToOneGet() {
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getOrderName());
    }

    /**
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_3_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_3_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_3_0_
     *     from
     *         ORDERS order0_
     *     where
     *         order0_.ORDER_ID=?
     * Order-1
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA
     *
     * 在需要使用到关联的对象时，才发送对应的SQL语句 ==> 懒加载
     */
    @Test
    public void testManyToOneGet1() {
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getOrderName());

        Customer customer = order.getCustomer();
        System.out.println(customer.getCustomerName());
    }

    /**
     * 在查询Customer对象时，由多的一端导航到1的一端时，若此时session已经关闭，默认情况下，则会发生LazyInitializationException
     * 抛出懒加载异常
     */
    @Test
    public void testManyToOneGet2() {
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getOrderName());

        session.close();

        Customer customer = order.getCustomer();
        System.out.println(customer.getCustomerName()); // org.hibernate.LazyInitializationException: could not initialize proxy - no Session

        session = sessionFactory.openSession();
    }

    /**
     * 获取Order对象时，默认情况下，其关联的Customer对象是一个代理对象！
     */
    @Test
    public void testManyToOneGet3() {
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getOrderName());
        System.out.println(order.getCustomer().getClass().getName());   // com.atguigu.hibernate.helloworld.entities.nto1.Customer_$$_jvstba1_2
    }

    /**
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_3_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_3_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_3_0_
     *     from
     *         ORDERS order0_
     *     where
     *         order0_.ORDER_ID=?
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * Hibernate:
     *     update
     *         CUSTOMERS
     *     set
     *         CUSTOMER_NAME=?
     *     where
     *         CUSTOMER_ID=?
     */
    @Test
    public void testManyToOneUpdate() {
        Order order = (Order) session.get(Order.class, 1);
        order.getCustomer().setCustomerName("AAAA");
    }

    /**
     * 在不设定级联关系的情况下，且1的一端的对象有n的对象在引用，则不能直接删除1的一端。
     * Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`hibernate5`.`orders`, CONSTRAINT `FK_57wwsm6wqqkcr1amp4dtsk5bs` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customers` (`CUSTOMER_ID`))
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
     * 	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
     * 	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:490)
     */
    @Test
    public void testManyToOneDelete() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        session.delete(customer);
    }
}