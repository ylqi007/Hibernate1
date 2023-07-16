package com.atguigu.hibernate.helloworld;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class WorkerTest {
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
     * Hibernate:
     *     insert
     *     into
     *         WORKER
     *         (NAME, MONTHLY_PAY, YEARLY_PAY, VOCATION_WITH_PAY)
     *     values
     *         (?, ?, ?, ?)
     *
     * 生成的WORKER表中不是只有三列，而是5列，其中包含pay的三个fields
     */
    @Test
    public void testComponent() {
        Worker worker = new Worker();
        Pay pay = new Pay();
        pay.setMonthlyPay(1000);
        pay.setYearlyPay(80000);
        pay.setVocationWithPay(5);

        worker.setName("ABCD");
        worker.setPay(pay);

        session.save(worker);
    }

    /**
     * Pay.class中有worker field
     * Worker.hbm.xml文件中设置 <parent name="worker"/>
     * 和testComponent()效果一致。
     */
    @Test
    public void testComponentWithParent() {
        Worker worker = new Worker();
        Pay pay = new Pay();
        pay.setMonthlyPay(1000);
        pay.setYearlyPay(80000);
        pay.setVocationWithPay(5);
        pay.setWorker(worker);

        worker.setName("ABCDE");
        worker.setPay(pay);

        session.save(worker);
    }

}