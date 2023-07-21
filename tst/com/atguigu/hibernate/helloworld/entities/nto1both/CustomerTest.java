package com.atguigu.hibernate.helloworld.entities.nto1both;

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

    /**
     * ## Without inverse="true"
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS2
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     *
     * 发送了三条INSERT + 两条UPDATE语句
     * 因为1端和n端都需要维护关联关系，所以会多出UPDATE语句
     * 可以在1端的Set节点设置inverse="true"，使1端放弃维护关联关系！
     * 建议设定Set的inverse="true"，并且先插入1端，再插入n端。优点是不会多出UPDATE语句
     *
     * ## With inverse="true"
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS2
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * 只有三条INSERT语句
     */
    @Test
    public void testBiDirectionManyToOneSave() {
        Customer customer = new Customer();
        customer.setCustomerName("CC");

        Order order1 = new Order();
        order1.setOrderName("Order-5");

        Order order2 = new Order();
        order2.setOrderName("Order-6");

        // 设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        // 执行save操作：先出入customer (一端)，再插入orders (多端)
        session.save(customer);
        session.save(order1);
        session.save(order2);
    }

    /**
     * ## Without inverse="true"
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS2
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     *
     * 发送了三条INSERT + 四条UPDATE语句
     *
     * ## Without inverse="true"
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS2
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     *
     * 三条INSERT语句 + 两条UPDATE语句
     */
    @Test
    public void testBiDirectionManyToOneSave1() {
        Customer customer = new Customer();
        customer.setCustomerName("DD");

        Order order1 = new Order();
        order1.setOrderName("Order-7");

        Order order2 = new Order();
        order2.setOrderName("Order-8");

        // 设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        // 执行save操作：先出入orders (多端)，再插入customer (一端)
        session.save(order1);
        session.save(order2);
        session.save(customer);
    }


    /**
     * 1. 只发送了一条SELECT语句，对多的一端使用延迟加载
     * 2. 返回的多端是Hibernate内置的集合类型，该类型具有延迟加载和存放代理对象的功能
     * 3. 如果close session，可能会发生LazyInitializationException
     * 4. 在需要使用集合中元素时，进行初始化
     */
    @Test
    public void testOne2ManyGet() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().getClass());    // class org.hibernate.collection.internal.PersistentSet
    }

    @Test
    public void testOne2ManyGetLazyInitializationException() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().getClass());    // class org.hibernate.collection.internal.PersistentSet

        session.close();
        System.out.println(customer.getOrders().size());    // org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.atguigu.hibernate.helloworld.entities.nto1both.Customer.orders, could not initialize proxy - no Session

        session = sessionFactory.openSession();
    }

    /**
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_1_0_
     *     from
     *         CUSTOMERS2 customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_5_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_5_0_
     *     from
     *         ORDERS2 orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     * Hibernate:
     *     update
     *         ORDERS2
     *     set
     *         ORDER_NAME=?,
     *         CUSTOMER_ID=?
     *     where
     *         ORDER_ID=?
     *
     * 发送了一条UPDATE语句，因为Set是无序的，所以无法保证update了哪个order
     */
    @Test
    public void testUpdate() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        customer.getOrders().iterator().next().setOrderName("GG~~");
    }

    /**
     * Without cascade="delete"
     * Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Cannot delete or update a parent row: a foreign key constraint fails (`hibernate5`.`orders2`, CONSTRAINT `FK_silhd0tqx04fmm7jxppy6hn27` FOREIGN KEY (`CUSTOMER_ID`) REFERENCES `customers2` (`CUSTOMER_ID`))
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
     * 	at java.base/jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
     * 	at java.base/jdk.internal.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
     * 	at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:490)
     *
     *
     * With cascade="delete"
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_1_0_
     *     from
     *         CUSTOMERS2 customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_5_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_5_0_
     *     from
     *         ORDERS2 orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     * Hibernate:
     *     delete
     *     from
     *         ORDERS2
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     delete
     *     from
     *         ORDERS2
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     delete
     *     from
     *         CUSTOMERS2
     *     where
     *         CUSTOMER_ID=?
     */
    @Test
    public void testDeleteWithoutCascade() {
        Customer customer = (Customer) session.get(Customer.class, 1);
        session.delete(customer);
    }

    /**
     * Without cascade="delete-orphan"
     *  Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_1_0_
     *     from
     *         CUSTOMERS2 customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     *
     * 只有SELECT操作
     *
     *
     * With cascade="delete-orphan"
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_1_0_
     *     from
     *         CUSTOMERS2 customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_5_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_5_0_
     *     from
     *         ORDERS2 orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     * Hibernate:
     *     delete
     *     from
     *         ORDERS2
     *     where
     *         ORDER_ID=?
     * Hibernate:
     *     delete
     *     from
     *         ORDERS2
     *     where
     *         ORDER_ID=?
     *
     * Customer没有删除，但是Orders被删除了
     */
    @Test
    public void testDeleteCascadeDeleteOrphan() {
        Customer customer = (Customer) session.get(Customer.class, 2);
        customer.getOrders().clear();
    }

    /**
     * With cascade="save-update"
     *
     * Hibernate:
     *     insert
     *     into
     *         CUSTOMERS2
     *         (CUSTOMER_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         ORDERS2
     *         (ORDER_NAME, CUSTOMER_ID)
     *     values
     *         (?, ?)
     */
    @Test
    public void testCascadeSaveUpdate() {
        Customer customer = new Customer();
        customer.setCustomerName("AA");

        Order order1 = new Order();
        order1.setOrderName("Order-AA1");

        Order order2 = new Order();
        order2.setOrderName("Order-AA2");

        // 设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);
        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        // 执行save操作：先出入customer (一端)，再插入orders (多端)
        session.save(customer);
        // 即使没有session.save(order)，orders也可以被保存到DB
    }

    /**
     * With order-by="ORDER_NAME DESC"
     *
     * Hibernate:
     *     select
     *         customer0_.CUSTOMER_ID as CUSTOMER1_1_0_,
     *         customer0_.CUSTOMER_NAME as CUSTOMER2_1_0_
     *     from
     *         CUSTOMERS2 customer0_
     *     where
     *         customer0_.CUSTOMER_ID=?
     * BB
     * Hibernate:
     *     select
     *         orders0_.CUSTOMER_ID as CUSTOMER3_1_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_1_,
     *         orders0_.ORDER_ID as ORDER_ID1_5_0_,
     *         orders0_.ORDER_NAME as ORDER_NA2_5_0_,
     *         orders0_.CUSTOMER_ID as CUSTOMER3_5_0_
     *     from
     *         ORDERS2 orders0_
     *     where
     *         orders0_.CUSTOMER_ID=?
     *     order by
     *         orders0_.ORDER_NAME desc
     */
    @Test
    public void testSetOrderBy() {
        Customer customer = (Customer) session.get(Customer.class, 2);
        System.out.println(customer.getCustomerName());

        System.out.println(customer.getOrders().size());
    }

}