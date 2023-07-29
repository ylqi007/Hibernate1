package com.atguigu.hibernate.unionsubclass;


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

class PersonPersonTest {
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
        System.out.println("Create tables");
    }

    /**
     * 插入操作：
     *  1. 对于子类对象，只需要把记录插入到一张数据表中。
     *
     * Hibernate:
     *     select
     *         next_hi
     *     from
     *         hibernate_unique_key for update
     *
     * Hibernate:
     *     update
     *         hibernate_unique_key
     *     set
     *         next_hi = ?
     *     where
     *         next_hi = ?
     * Hibernate:
     *     insert
     *     into
     *         PERSONS
     *         (NAME, AGE, ID)
     *     values
     *         (?, ?, ?)
     * Hibernate:
     *     insert
     *     into
     *         STUDENTS
     *         (NAME, AGE, SCHOOL, ID)
     *     values
     *         (?, ?, ?, ?)
     * destroyed
     */
    @Test
    public void testSave() {
        Person person = new Person();
        person.setName("AA");
        person.setAge(11);

        session.save(person);

        Student student = new Student();
        student.setName("BB");
        student.setAge(12);
        student.setSchool("School-atguigu");

        session.save(student);
    }

    /**
     * 优点：
     *  1. 无需使用辨别者列
     *  2. 子类独有的字段可以添加非空约束
     *
     * 缺点：
     *  1. 存在冗余的字段
     *  2. 若更新父表的字段，则更新的效率较低
     */

    /**
     * 查询：
     *  1. 查询父类记录，需把父表和子表记录汇总到一起再做查询，性能较差
     *  2. 对于子类记录，也只需要查询一张数据表
     *
     * Hibernate:
     *     select
     *         person0_.ID as ID1_13_,
     *         person0_.NAME as NAME2_13_,
     *         person0_.AGE as AGE3_13_,
     *         person0_.SCHOOL as SCHOOL1_14_,
     *         person0_.clazz_ as clazz_
     *     from
     *         ( select
     *             ID,
     *             NAME,
     *             AGE,
     *             null as SCHOOL,
     *             0 as clazz_
     *         from
     *             PERSONS
     *         union
     *         select
     *             ID,
     *             NAME,
     *             AGE,
     *             SCHOOL,
     *             1 as clazz_
     *         from
     *             STUDENTS
     *     ) person0_
     * 2
     * Hibernate:
     *     select
     *         student0_.ID as ID1_13_,
     *         student0_.NAME as NAME2_13_,
     *         student0_.AGE as AGE3_13_,
     *         student0_.SCHOOL as SCHOOL1_14_
     *     from
     *         STUDENTS student0_
     * 1
     * destroyed
     *
     *
     * <hibernate-mapping package="com.atguigu.hibernate.unionsubclass"> 不能加 auto-import=true
     */
    @Test
    public void testQuery() {
        List<Person> persons = session.createQuery("FROM Person").list();
        System.out.println(persons.size());

        List<Student> students = session.createQuery("FROM Student").list();
        System.out.println(students.size());
    }

    /**
     * Hibernate:
     *     insert
     *     into
     *         HT_PERSONS
     *         select
     *             person0_.ID as ID
     *         from
     *             ( select
     *                 ID,
     *                 NAME,
     *                 AGE,
     *                 null as SCHOOL,
     *                 0 as clazz_
     *             from
     *                 PERSONS
     *             union
     *             select
     *                 ID,
     *                 NAME,
     *                 AGE,
     *                 SCHOOL,
     *                 1 as clazz_
     *             from
     *                 STUDENTS
     *         ) person0_
     * Hibernate:
     *     update
     *         PERSONS
     *     set
     *         AGE=20
     *     where
     *         (
     *             ID
     *         ) IN (
     *             select
     *                 ID
     *             from
     *                 HT_PERSONS
     *         )
     * Hibernate:
     *     update
     *         PERSONS
     *     set
     *         AGE=20
     *     where
     *         (
     *             ID
     *         ) IN (
     *             select
     *                 ID
     *             from
     *                 HT_PERSONS
     *         )
     * Hibernate:
     *     update
     *         STUDENTS
     *     set
     *         AGE=20
     *     where
     *         (
     *             ID
     *         ) IN (
     *             select
     *                 ID
     *             from
     *                 HT_PERSONS
     *         )
     * destroyed
     */
    @Test
    public void testUpdate() {
        String hql = "UPDATE Person p SET p.age = 20";
        session.createQuery(hql).executeUpdate();
    }
}