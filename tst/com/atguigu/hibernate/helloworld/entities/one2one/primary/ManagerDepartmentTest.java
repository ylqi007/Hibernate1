package com.atguigu.hibernate.helloworld.entities.one2one.primary;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ManagerDepartmentTest {
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
     * 生成的MANAGERS表中有MGR_ID
     *
     * 没有 constrained="true" 时，生成的DEPARTMENTS2表中有主键PrimaryKey: DEPT_ID, “没有”外键ForeignKey，因为没有指定约束 constraint
     * 在 Department.hbm.xml 中添加 constrained="true" 后，会生成外键约束
     */
    @Test
    public void testGenerateInitialTable() {

    }

    /**
     * Hibernate:
     *     insert
     *     into
     *         MANAGERS2
     *         (MGR_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         DEPARTMENTS2
     *         (DEPT_NAME, DEPT_ID)
     *     values
     *         (?, ?)
     */
    @Test
    public void testSave() {
        Department department = new Department();
        department.setDeptName("Dept-AA");

        Manager manager = new Manager();
        manager.setMgrName("Mgr-AA");

        // 设定关联关系
        department.setManager(manager);
        manager.setDept(department);

        // 保存操作
        session.save(manager);  // 先存manager
        session.save(department);
    }


    /**
     * Hibernate:
     *     insert
     *     into
     *         MANAGERS2
     *         (MGR_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         DEPARTMENTS2
     *         (DEPT_NAME, DEPT_ID)
     *     values
     *         (?, ?)
     *
     * 从 testSave() 和 testSave1() 发出的SQL语句可以看出，都是先插入Manager。
     * 因为如果先插入Department的话，department没有主键。因为主键非空且唯一，因此必须等到Manager插入后才会有外键。
     * 因此，无论先插入哪一个都不会有多余的UPDATE语句
     */
    @Test
    public void testSave1() {
        Department department = new Department();
        department.setDeptName("Dept-BB");

        Manager manager = new Manager();
        manager.setMgrName("Mgr-BB");

        // 设定关联关系
        department.setManager(manager);
        manager.setDept(department);

        // 保存操作
        session.save(department);   // 先存department
        session.save(manager);
    }

    /**
     * 在 Department.hbm.xml 中添加 constrained="true" 后，再执行 testSave2 会为Department的主键生成外键约束
     *
     * Hibernate:
     *     insert
     *     into
     *         MANAGERS2
     *         (MGR_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         DEPARTMENTS2
     *         (DEPT_NAME, DEPT_ID)
     *     values
     *         (?, ?)
     */
    @Test
    public void testSave2() {
        Department department = new Department();
        department.setDeptName("Dept-CC");

        Manager manager = new Manager();
        manager.setMgrName("Mgr-CC");

        // 设定关联关系
        department.setManager(manager);
        manager.setDept(department);

        // 保存操作：建议没有
        session.save(department);   // 先存department时，还没有mgr_id。所以还是会先插入manager
        session.save(manager);      // 要等manager存完之后，有了mgr_id，然后才插入department
    }

    /**
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_3_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         DEPARTMENTS2 department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     *
     * 也有懒加载
     */
    @Test
    public void testGet() {
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());
    }

    /**
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_3_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         DEPARTMENTS2 department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_5_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_5_1_,
     *         department1_.DEPT_ID as DEPT_ID1_3_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         MANAGERS2 manager0_
     *     left outer join
     *         DEPARTMENTS2 department1_
     *             on manager0_.MGR_ID=department1_.DEPT_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     *
     * 查Manager时，使用到了左外连接。因为Manager没有外键，不知道跟它关联的department对象是谁
     */
    @Test
    public void testGet1() {
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());

        Manager manager = department.getManager();
        System.out.println(manager.getMgrName());
    }

    /**
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_3_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         DEPARTMENTS2 department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_5_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_5_1_,
     *         department1_.DEPT_ID as DEPT_ID1_3_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         MANAGERS2 manager0_
     *     left outer join
     *         DEPARTMENTS2 department1_
     *             on manager0_.MGR_ID=department1_.DEPT_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     */
    @Test
    public void testGet2() {
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());

        Manager manager = department.getManager();
        System.out.println(manager.getMgrName());
    }

    /**
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_5_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_5_1_,
     *         department1_.DEPT_ID as DEPT_ID1_3_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         MANAGERS2 manager0_
     *     left outer join
     *         DEPARTMENTS2 department1_
     *             on manager0_.MGR_ID=department1_.DEPT_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     */
    @Test
    public void testGet3() {
        Manager manager = (Manager) session.get(Manager.class, 1);
        System.out.println(manager.getMgrName());
    }

    /**
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_5_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_5_1_,
     *         department1_.DEPT_ID as DEPT_ID1_3_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_3_0_
     *     from
     *         MANAGERS2 manager0_
     *     left outer join
     *         DEPARTMENTS2 department1_
     *             on manager0_.MGR_ID=department1_.DEPT_ID     # 此处不需要使用property-ref
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     * Dept-AA
     */
    @Test
    public void testGet4() {
        Manager manager = (Manager) session.get(Manager.class, 1);
        System.out.println(manager.getMgrName());
        System.out.println(manager.getDept().getDeptName());
    }
}