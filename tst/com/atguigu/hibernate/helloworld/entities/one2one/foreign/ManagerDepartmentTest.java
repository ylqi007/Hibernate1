package com.atguigu.hibernate.helloworld.entities.one2one.foreign;

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
     * 生成的DEPARTMENTS表中有
     *  1. 主键PrimaryKey: DEPT_ID
     *  2. 外键ForeignKey: MANAGER_ID, 并且此外键有UNIQUE约束
     */
    @Test
    public void testGenerateInitialTable() {

    }

    /**
     * Hibernate:
     *     insert
     *     into
     *         MANAGERS
     *         (MGR_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     insert
     *     into
     *         DEPARTMENTS
     *         (DEPT_NAME, MANAGER_ID)
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
     *         DEPARTMENTS
     *         (DEPT_NAME, MANAGER_ID)
     *     values
     *         (?, ?)
     * Hibernate:
     *     insert
     *     into
     *         MANAGERS
     *         (MGR_NAME)
     *     values
     *         (?)
     * Hibernate:
     *     update
     *         DEPARTMENTS
     *     set
     *         DEPT_NAME=?,
     *         MANAGER_ID=?
     *     where
     *         DEPT_ID=?
     *
     * 多出一条UPDATE语句
     *
     * 建议：先保存没有外键列的对象，这样会减少UPDATE语句
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

        // 保存操作：建议没有
        session.save(department);   // 先存department时，还没有mgr_id。
        session.save(manager);      // 需要等manager存完之后，有了mgr_id,然后发一条UPDATE语句，更新关联关系
    }


    /**
     * 1. 默认情况下对关联属性使用懒加载
     * 2. 所以会出现懒加载异常的问题，比如关闭session之后，还要获取对象属性时就会抛出懒加载异常
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_2_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department0_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         DEPARTMENTS department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     */
    @Test
    public void testGet() {
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());
    }

    /**
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_2_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department0_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         DEPARTMENTS department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_3_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_3_1_,
     *         department1_.DEPT_ID as DEPT_ID1_2_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department1_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         MANAGERS manager0_
     *     left outer join
     *         DEPARTMENTS department1_
     *             on manager0_.MGR_ID=department1_.DEPT_ID     # 此处连接条件有问题，应该是managers.MGR_ID = departments.MGR_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     *
     * 从上述SQL语句可以看出，查询MANAGER时使用的是left outer join (左外连接)，且连接条件有问题！
     *
     * 3. 查询Manager对象的连接条件时，条件应该是departments.manager_id = managers.manager_id
     * 而不应该是 manager0_.MGR_ID=department1_.DEPT_ID
     * 这是在映射时，少映射了一个属性
     */
    @Test
    public void testGet1() {
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());

        Manager manager = department.getManager();
        System.out.println(manager.getMgrName());
    }

    /**
     * 在Manager.hbm.xml的<one-to-one>添加 property-ref="manager" 之后
     *
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_2_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department0_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         DEPARTMENTS department0_
     *     where
     *         department0_.DEPT_ID=?
     * Dept-AA
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_3_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_3_1_,
     *         department1_.DEPT_ID as DEPT_ID1_2_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department1_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         MANAGERS manager0_
     *     left outer join
     *         DEPARTMENTS department1_
     *             on manager0_.MGR_ID=department1_.MANAGER_ID      # 此时的连接条件才是正确的
     *     where
     *         manager0_.MGR_ID=?
     * Hibernate:
     *     select
     *         department0_.DEPT_ID as DEPT_ID1_2_0_,
     *         department0_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department0_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         DEPARTMENTS department0_
     *     where
     *         department0_.MANAGER_ID=?
     * Mgr-AA
     *
     * Manager.hbm.xml中的
     *  <one-to-one name="dept" class="Department" property-ref="manager"/>
     * 这是因为，在添加 property-ref="manager" 之后，使用Department.manager这个字段对应的列作为连接条件
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
     *         manager0_.MGR_ID as MGR_ID1_3_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_3_1_,
     *         department1_.DEPT_ID as DEPT_ID1_2_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department1_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         MANAGERS manager0_
     *     left outer join
     *         DEPARTMENTS department1_
     *             on manager0_.MGR_ID=department1_.MANAGER_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     *
     * 从上述的SELECT语句中可以看出，查询Manager时使用的是 left outer join (左外连接)
     */
    @Test
    public void testGet3() {
        Manager manager = (Manager) session.get(Manager.class, 1);
        System.out.println(manager.getMgrName());
    }

    /**
     * Hibernate:
     *     select
     *         manager0_.MGR_ID as MGR_ID1_3_1_,
     *         manager0_.MGR_NAME as MGR_NAME2_3_1_,
     *         department1_.DEPT_ID as DEPT_ID1_2_0_,
     *         department1_.DEPT_NAME as DEPT_NAM2_2_0_,
     *         department1_.MANAGER_ID as MANAGER_3_2_0_
     *     from
     *         MANAGERS manager0_
     *     left outer join
     *         DEPARTMENTS department1_
     *             on manager0_.MGR_ID=department1_.MANAGER_ID
     *     where
     *         manager0_.MGR_ID=?
     * Mgr-AA
     * Dept-AA
     *
     * testGet4() 中要打印manager.getDept().getDeptName()，但是发出的SELECT语句和 testGet3() 中是一致的
     *
     * 分析：
     * 在查询没有外键的实体对象(manager)时，使用的是左外连接查询，一并查询出其关联的对象，并已经进行初始化。
     *
     * 在查询有外键关联的实体对象(department)时，因为department已经有外键了，所以不需要左外连接查询
     */
    @Test
    public void testGet4() {
        Manager manager = (Manager) session.get(Manager.class, 1);
        System.out.println(manager.getMgrName());
        System.out.println(manager.getDept().getDeptName());
    }
}