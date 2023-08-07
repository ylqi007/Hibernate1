package com.atguigu.hibernate.dao;

import com.atguigu.hibernate.entities.Department;
import com.atguigu.hibernate.utils.HibernateUtils;
import org.hibernate.Session;

public class DepartmentDao {

    /**
     * 若需要出入一个Session对象，则意味着上一层(Service)需要获取Session对象，
     * 这说明上一层要和Hibernate的API紧密耦合，所以不推荐
     * @param session
     * @param department
     */
    public void save(Session session, Department department) {
        session.save(department);
    }

    /**
     * 内部获取Session对象。
     * 获取和当前线程绑定的Session对象
     *  1. 不需要从外部传入Session对象
     *  2. 多个DAO方法可以使用一个事务！
     * @param department
     */
    public void save(Department department) {
        Session session = HibernateUtils.getInstance().getSession();
        System.out.println("session.hashCode()=" + session.hashCode());
        session.save(department);
    }
}
