package com.atguigu.hibernate.strategy.many2one;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
     * 1. lazy取值为proxy和false分别代表对对应的属性采用延迟检索和立即检索
     * 2. fetch取值为join，表示使用迫切左外连接的方式初始化N关联的1的一端的属性。忽略lazy属性
     * 3. batch-size属性需要设置在1那一端的class元素中：<class name="Customer" table="CUSTOMERS" lazy="true" batch-size="3">
     *     作用：一次初始化1的这一端代理对象的个数。
     */

    /**
     * many2one/Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID"/>
     *
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS order0_
     *     where
     *         order0_.ORDER_ID=?
     * destroyed
     *
     * 只查询了order，而没有查询关联的Customer
     */
    @Test
    public void testMany2OneStrategy() {
        Order order = (Order) session.get(Order.class, 1);
    }

    /**
     * many2one/Customer.hbm.xml
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * many2one/Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID" lazy="proxy"/>
     *
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS order0_
     *     where
     *         order0_.ORDER_ID=?
     * destroyed
     * Order.hbm.xml中，lazy="proxy"，采用延迟加载策略
     *
     * many2one/Customer.hbm.xml
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * many2one/Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID" lazy="false"/>
     *
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_0_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_0_
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
     * destroyed
     *
     * 此时lazy="false"，不仅获取了order，也获取了customer
     */
    @Test
    public void testMany2OneStrategy1() {
        Order order = (Order) session.get(Order.class, 1);
    }

    /**
     * Customer.hbm.xml
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID" lazy="false" fetch="join"/>
     *
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_1_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_1_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_1_,
     *         customer1_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer1_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         ORDERS order0_
     *     left outer join
     *         CUSTOMERS customer1_
     *             on order0_.CUSTOMER_ID=customer1_.CUSTOMER_ID
     *     where
     *         order0_.ORDER_ID=?
     * AA
     * destroyed
     *
     * 2. fetch取值为join，表示使用迫切左外连接的方式初始化N关联的1的一端的属性
     */
    @Test
    public void testMany2OneStrategy2() {
        Order order = (Order) session.get(Order.class, 1);
        System.out.println(order.getCustomer().getCustomerName());
    }

    /**
     * Customer.hbm.xml
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID" lazy="false" fetch="join"/>
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_
     *     from
     *         ORDERS order0_
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA
     * AA
     * AA
     * AA
     * BB
     * BB
     * BB
     * BB
     * BB
     * destroyed
     *
     * 发送了两个SELECT语句初始化Customer
     */
    @Test
    public void testMany2OneStrategy3() {
        List<Order> orders = session.createQuery("FROM Order o").list();
        for(Order order: orders) {
            if(order.getCustomer() != null) {
                System.out.println(order.getCustomer().getCustomerName());
            }
        }
    }

    /**
     * Customer.hbm.xml
     *  <class name="Customer" table="CUSTOMERS" lazy="true" batch-size="3">
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * Order.hbm.xml
     *  <many-to-one name="customer" class="Customer" column="CUSTOMER_ID" lazy="false" fetch="join"/>
     *
     * Hibernate:
     *     select
     *         order0_.ORDER_ID as ORDER_ID1_1_,
     *         order0_.ORDER_NAME as ORDER_NA2_1_,
     *         order0_.CUSTOMER_ID as CUSTOMER3_1_
     *     from
     *         ORDERS order0_
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID in (
     *             ?, ?
     *         )
     * AA
     * AA
     * AA
     * AA
     * BB
     * BB
     * BB
     * BB
     * BB
     * destroyed
     *
     * 只有两个SELECT语句，一个是Order的，一个是Customer
     *
     * batch-size属性需要设置在1那一端的class元素中：<class name="Customer" table="CUSTOMERS" lazy="true" batch-size="3">
     *  作用：一次初始化1的这一端代理对象的个数。
     */
    @Test
    public void testMany2OneStrategy4() {
        List<Order> orders = session.createQuery("FROM Order o").list();
        for(Order order: orders) {
            if(order.getCustomer() != null) {
                System.out.println(order.getCustomer().getCustomerName());
            }
        }
    }
}