package com.atguigu.hibernate.strategy.classlevel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


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

    @Test
    public void testInitialization() {

    }

    /**
     * classlevel/Customer.hbm.xml
     *  <class name="Customer" table="CUSTOMERS">
     *
     * class com.atguigu.hibernate.strategy.Customer_$$_jvst667_1
     * destroyed
     *
     * 当session.load()获取对象，但并没有使用任何属性时，使用的时代理对象，即class的lazy属性，默认为true
     */
    @Test
    public void testClassLevelStrategy() {
        Customer customer = (Customer) session.load(Customer.class, 1);
        System.out.println(customer.getClass());
    }

    /**
     * classlevel/Customer.hbm.xml
     *  <class name="Customer" table="CUSTOMERS" lazy="true">
     *
     * class com.atguigu.hibernate.strategy.Customer_$$_jvst617_1
     * 1
     * destroyed
     *
     * 当采用延迟加载策略(lazy="true")，当只用到OID属性时，并不会加载对象
     */
    @Test
    public void testClassLevelStrategy1() {
        Customer customer = (Customer) session.load(Customer.class, 1);
        System.out.println(customer.getClass());

        System.out.println(customer.getCustomerId());
    }

    /**
     * <class name="Customer" table="CUSTOMERS" lazy="true">
     *
     * class com.atguigu.hibernate.strategy.Customer_$$_jvstbc_1
     * 1        # System.out.println(customer.getCustomerId()); 因为是延迟加载，此时并没有加载Customer
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA       # System.out.println(customer.getCustomerName()); 此时需要访问Customer对象的属性，所以开始加载Customer对象
     * destroyed
     */
    @Test
    public void testClassLevelStrategy2() {
        Customer customer = (Customer) session.load(Customer.class, 1);
        System.out.println(customer.getClass());

        System.out.println(customer.getCustomerId());   // 只用到了OID
        System.out.println(customer.getCustomerName()); // SQL语句在打印OID之后发出
    }

    /**
     * <class name="Customer" table="CUSTOMERS" lazy="false">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * class com.atguigu.hibernate.strategy.Customer
     * destroyed
     *
     * 此时采用的是立即加载策略(lazy="false"), 即使System.out.println(customer.getClass())并没有
     * 访问Customer的具体属性，也会发出SELECT语句并加载Customer对象
     */
    @Test
    public void testClassLevelStrategyLazyFalse() {
        Customer customer = (Customer) session.load(Customer.class, 1);
        System.out.println(customer.getClass());
    }
}