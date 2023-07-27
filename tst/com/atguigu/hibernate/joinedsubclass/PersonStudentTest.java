package com.atguigu.hibernate.joinedsubclass;


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

class PersonStudentTest {
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
     *  1. 对于子类对象，至少需要插入到两张数据表中。
     *
     * Hibernate:
     *     insert
     *     into
     *         PERSONS2
     *         (NAME, AGE)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         PERSONS2
     *         (NAME, AGE)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         STUDENTS2
     *         (SCHOOL, STUDENT_ID)
     *     values
     *         (?, ?)
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
     * 查询：
     *  1. 查询父类记录，需要查多张表，做一个左外连接查询。
     *  2. 查询子类记录，做一个内连接查询。
     *
     * 优点：
     *  1. 不需要使用辨别者列。
     *  2. 子类独有的字段能添加非空约束。
     *  3. 没有冗余的字段。
     *
     * Hibernate:
     *     select
     *         person0_.ID as ID1_13_,
     *         person0_.NAME as NAME3_13_,
     *         person0_.AGE as AGE4_13_,
     *         person0_.SCHOOL as SCHOOL5_13_,
     *         person0_.TYPE as TYPE2_13_
     *     from
     *         PERSONS person0_
     * 2
     * Hibernate:
     *     select
     *         student0_.ID as ID1_13_,
     *         student0_.NAME as NAME3_13_,
     *         student0_.AGE as AGE4_13_,
     *         student0_.SCHOOL as SCHOOL5_13_
     *     from
     *         PERSONS student0_
     *     where
     *         student0_.TYPE='STUDENT'
     * 1
     */
    @Test
    public void testQuery() {
        List<Person> persons = session.createQuery("FROM Person").list();
        System.out.println(persons.size());

        List<Student> students = session.createQuery("FROM Student").list();
        System.out.println(students.size());
    }
}