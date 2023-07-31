package com.atguigu.hibernate.strategy.set;

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
     * set的lazy属性
     * 1. 1-n或n-n的集合属性默认使用懒加载检索策略。
     * 2. 可以通过设置set的lazy属性来修改默认的检索策略。默认为true，并不建议设置为false
     * 3. lazy还可以设置为extra。增强的延迟检索。该取值会尽可能的延迟集合的初始化时机
     */

    /** lazy
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC">
     *  or
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA
     * destroyed
     * lazy为默认值true，即采用延迟加载策略。打印customer时只加载了Customer，而没有加载orders
     */
    @Test
    public void testOne2ManyLevelStrategy() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
    }

    /**
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC">
     *  or
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA   # System.out.println(customer.getCustomerName()); 此时并没有加载orders
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4    # System.out.println(customer.getOrders().size()); 此时需要orders的size，因此需要加载orders，所有发送了以上的SELECT语句
     * destroyed
     */
    @Test
    public void testOne2ManyLevelStrategy1() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().size());    // 使用Customer的orders属性时，才会检索
    }

    /**
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="false">
     *
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
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * AA
     *
     * 此时采用的立即加载策略(lazy="false"), 即使没有访问orders的属性，也会发送SELECT语句加载orders
     */
    @Test
    public void testOne2ManyLevelStrategy2() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
    }

    /**
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="extra">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA
     * Hibernate:
     *     select
     *         count(ORDER_ID)
     *     from
     *         ORDERS
     *     where
     *         CUSTOMER_ID =?
     * 4
     * destroyed
     *
     * 当执行 System.out.println(customer.getOrders().size()); 时，不需要访问具体的某个order的属性，所以只发送了SELECT count()
     * 查询orders的size，而不是加载所有的orders
     */
    @Test
    public void testOne2ManyLevelStrategy3() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().size());
    }

    /**
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="extra">
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_0_
     *     from
     *         CUSTOMERS customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * AA
     * Hibernate:
     *     select
     *         count(ORDER_ID)
     *     from
     *         ORDERS
     *     where
     *         CUSTOMER_ID =?
     * 4
     * Hibernate:
     *     select
     *         1
     *     from
     *         ORDERS
     *     where
     *         CUSTOMER_ID =?
     *         and ORDER_ID =?
     * true
     * destroyed
     *
     * 执行System.out.println(customer.getOrders().contains(order));时，并没有执行初始化，而是
     * where CUSTOMER_ID = ?
     *       and ORDER_ID = ?
     */
    @Test
    public void testOne2ManyLevelStrategy4() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().size());

        Order order = new Order();
        order.setOrderId(1);
        System.out.println(customer.getOrders().contains(order));
    }


    /**
     * set/Customer.hbm.xml (即1端)
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC">
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_
     *     from
     *         CUSTOMERS customer0_
     * 3
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 5
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 0
     * destroyed
     */
    @Test
    public void testBatchSize() {
        List<Customer> customers = session.createQuery("FROM Customer").list();
        System.out.println(customers.size());

        for(Customer customer: customers) {
            if(customer.getOrders() != null) {
                System.out.println(customer.getOrders().size());
            }
        }
    }

    /** batch-size
     * set元素的batch-size属性：设定一次初始化set集合的数量
     *
     * 能否对Orders进行批量初始化？
     * <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="5">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_
     *     from
     *         CUSTOMERS customer0_
     * 3
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID in (
     *             ?, ?, ?
     *         )
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4
     * 5
     * 0
     * destroyed
     */
    @Test
    public void testBatchSize1() {
        List<Customer> customers = session.createQuery("FROM Customer").list();
        System.out.println(customers.size());

        for(Customer customer: customers) {
            if(customer.getOrders() != null) {
                System.out.println(customer.getOrders().size());
            }
        }
    }

    /** fetch
     * set的fetch属性：决定初始化orders集合方式
     *  1. 默认值为select，通过正常的方式来初始化set元素
     *  2. 可以取值为subselect，通过子查询的方式来初始化所有的set集合。子查询作为where子句的in的条件出现，子查询所有1
     *     的一端的ID。此时lazy有效，但是batch-size失效
     *  3. 若取值join，则决定order集合被初始化的时机
     *    3.1 在加载1的一端的对象时，使用迫切左外连接(使用左外连接进行查询，且把集合属性进行初始化)的方式检索n的一端集合属性初始化
     *    3.2 忽略lazy属性
     *    3.3 HQL查询忽略fetch=join的取值
     */

    /**
     * <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="select">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_
     *     from
     *         CUSTOMERS customer0_
     * 3
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID in (
     *             ?, ?
     *         )
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4
     * 5
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 0
     * destroyed
     */
    @Test
    public void testSetFetch() {
        List<Customer> customers = session.createQuery("FROM Customer").list();
        System.out.println(customers.size());

        for(Customer customer: customers) {
            if(customer.getOrders() != null) {
                System.out.println(customer.getOrders().size());
            }
        }
    }

    /**
     * <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_
     *     from
     *         CUSTOMERS customer0_
     * 3
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID in (
     *             select
     *                 customer0_.CUSTOMER_ID
     *             from
     *                 CUSTOMERS customer0_
     *         )
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4
     * 5
     * 0
     * destroyed
     */
    @Test
    public void testSetFetch1() {
        List<Customer> customers = session.createQuery("FROM Customer").list();
        System.out.println(customers.size());

        for(Customer customer: customers) {
            if(customer.getOrders() != null) {
                System.out.println(customer.getOrders().size());
            }
        }
    }

    /**
     * set/Customer.hbm.xml
     *  <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="subselect">
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_
     *     from
     *         CUSTOMERS customer0_
     * 3
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID in (
     *             ?, ?
     *         )
     *     order by
     *         orders0_.ORDER_NAME desc
     * 4
     * 5
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_0_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         ORDERS orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     * 0
     * destroyed
     */
    @Test
    public void testSetFetchJoin() {
        List<Customer> customers = session.createQuery("FROM Customer").list();
        System.out.println(customers.size());

        for(Customer customer: customers) {
            if(customer.getOrders() != null) {
                System.out.println(customer.getOrders().size());
            }
        }
    }


    /**
     * <set name="orders" table="ORDERS" inverse="true" order-by="ORDER_NAME DESC" lazy="true" batch-size="2" fetch="join">
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_0_1_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_0_1_,
     *         orders1_.CUSTOMER_ID as CUSTOMER3_0_3_,
     *         orders1_.ORDER_ID as ORDER_ID1_1_3_,
     *         orders1_.ORDER_ID as ORDER_ID1_1_0_,
     *         orders1_.ORDER_NAME as ORDER_NA2_1_0_,
     *         orders1_.CUSTOMER_ID as CUSTOMER3_1_0_
     *     from
     *         CUSTOMERS customer0_
     *     left outer join
     *         ORDERS orders1_
     *             on customer0_.CUSTOMER_ID=orders1_.CUSTOMER_ID
     *     where
     *         customer0_.CUSTOMER_ID=?
     *     order by
     *         orders1_.ORDER_NAME desc
     * 4
     * destroyed
     */
    @Test
    public void testSetFetchJoin2() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getOrders().size());
    }
}