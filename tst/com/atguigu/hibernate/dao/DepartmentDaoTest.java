package com.atguigu.hibernate.dao;

import com.atguigu.hibernate.entities.Department;
import com.atguigu.hibernate.utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentDaoTest {

    /**
     * session.hashCode()=1894978338
     * session.hashCode()=1894978338
     * session.hashCode()=1894978338
     */
    @Test
    public void testManageSession() {
        DepartmentDao departmentDao = new DepartmentDao();

        departmentDao.save(null);
        departmentDao.save(null);
        departmentDao.save(null);
    }

    /**
     * --> session.hashCode()=1894978338
     * session.hashCode()=1894978338
     * Hibernate:
     *     insert
     *     into
     *         AGG_DEPARTMENTS
     *         (NAME)
     *     values
     *         (?)
     * session.hashCode()=1894978338
     * session.hashCode()=1894978338
     * false
     */
    @Test
    public void testManageSession1() {
        // 获取Session，开启事务
        Session session = HibernateUtils.getInstance().getSession();
        System.out.println("--> session.hashCode()=" + session.hashCode());

        Transaction transaction = session.beginTransaction();
        DepartmentDao departmentDao = new DepartmentDao();

        Department department = new Department();
        department.setName("AtGuiGu");
        departmentDao.save(department);
        departmentDao.save(department);
        departmentDao.save(department);

        // 若Session是由thread管理的，则在提交或回滚事务时，已经关闭Session了
        transaction.commit();   // 说明调用transaction.commit()方法后，session已经关闭
        System.out.println(session.isOpen());
    }


    @Test
    public void testBatch() {
        Session session = HibernateUtils.getInstance().getSession();

        session.doWork(new Work() {
            @Override
            public void execute(Connection connection) throws SQLException {
                // 通过JDBC原生的API进行操作，效率最高，速度最快！

            }
        });
    }

}