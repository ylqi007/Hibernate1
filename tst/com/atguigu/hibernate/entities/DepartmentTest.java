package com.atguigu.hibernate.entities;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.script.ScriptEngine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;


class DepartmentTest {
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
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.SALARY>?
     *         and (
     *             employee0_.EMAIL like ?
     *         )
     *     order by
     *         employee0_.SALARY
     * [com.atguigu.hibernate.entities.Employee@2bc9a775, com.atguigu.hibernate.entities.Employee@2ba33e2c, com.atguigu.hibernate.entities.Employee@1f193686, com.atguigu.hibernate.entities.Employee@31e72cbc, com.atguigu.hibernate.entities.Employee@5fad41be, com.atguigu.hibernate.entities.Employee@6dcd5639, com.atguigu.hibernate.entities.Employee@3b36e000, com.atguigu.hibernate.entities.Employee@333cb916, com.atguigu.hibernate.entities.Employee@629ae7e, com.atguigu.hibernate.entities.Employee@1d25c1c, com.atguigu.hibernate.entities.Employee@de88ac6, com.atguigu.hibernate.entities.Employee@5bca7664, com.atguigu.hibernate.entities.Employee@105b693d, com.atguigu.hibernate.entities.Employee@3fae596, com.atguigu.hibernate.entities.Employee@9255c05, com.atguigu.hibernate.entities.Employee@5ce4369b, com.atguigu.hibernate.entities.Employee@7f829c76, com.atguigu.hibernate.entities.Employee@1cb19dba, com.atguigu.hibernate.entities.Employee@7c3ebc6b, com.atguigu.hibernate.entities.Employee@1931d99, com.atguigu.hibernate.entities.Employee@6a9950f1, com.atguigu.hibernate.entities.Employee@7ad54c55, com.atguigu.hibernate.entities.Employee@73017a80, com.atguigu.hibernate.entities.Employee@6ae7deac, com.atguigu.hibernate.entities.Employee@4a5905d9, com.atguigu.hibernate.entities.Employee@1a3e5f23, com.atguigu.hibernate.entities.Employee@6293e39e, com.atguigu.hibernate.entities.Employee@365553de, com.atguigu.hibernate.entities.Employee@34a0ef00, com.atguigu.hibernate.entities.Employee@5c0f79f0, com.atguigu.hibernate.entities.Employee@21fdfefc]
     * 31
     * destroyed
     */
    @Test
    public void testHQL() {
        // 1. 创建Query对象 -- 基于位置的参数
        String hql = "FROM Employee e WHERE e.salary > ? AND e.email LIKE ? ORDER BY e.salary";
        Query query = session.createQuery(hql);

        // 2. 绑定参数 -- Query对象支持setXxx方法职场方法链的编程风格
        query.setFloat(0, 6000)
                .setString(1, "%A%");

        // 3. 执行查询
        List<Employee> employees = query.list();
        System.out.println(employees);
        System.out.println(employees.size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.SALARY>?
     *         and (
     *             employee0_.EMAIL like ?
     *         )
     * [com.atguigu.hibernate.entities.Employee@2ba33e2c, com.atguigu.hibernate.entities.Employee@5bca7664, com.atguigu.hibernate.entities.Employee@105b693d, com.atguigu.hibernate.entities.Employee@3fae596, com.atguigu.hibernate.entities.Employee@4a0df195, com.atguigu.hibernate.entities.Employee@42fcc7e6, com.atguigu.hibernate.entities.Employee@9255c05, com.atguigu.hibernate.entities.Employee@5da7cee2, com.atguigu.hibernate.entities.Employee@78830d9a, com.atguigu.hibernate.entities.Employee@5ce4369b, com.atguigu.hibernate.entities.Employee@7f829c76, com.atguigu.hibernate.entities.Employee@1cb19dba, com.atguigu.hibernate.entities.Employee@7c3ebc6b, com.atguigu.hibernate.entities.Employee@1931d99, com.atguigu.hibernate.entities.Employee@73017a80, com.atguigu.hibernate.entities.Employee@1a3e5f23, com.atguigu.hibernate.entities.Employee@6293e39e, com.atguigu.hibernate.entities.Employee@365553de, com.atguigu.hibernate.entities.Employee@34a0ef00, com.atguigu.hibernate.entities.Employee@5c0f79f0, com.atguigu.hibernate.entities.Employee@21fdfefc, com.atguigu.hibernate.entities.Employee@3daa82be, com.atguigu.hibernate.entities.Employee@ec1b2e4]
     * 23
     * destroyed
     */
    @Test
    public void testHQLNamedParameter() {
        // 1. 创建Query对象 -- 基于命名的参数
        String hql = "FROM Employee e WHERE e.salary > :sal AND e.email LIKE :email";
        Query query = session.createQuery(hql);

        // 2. 绑定参数
        query.setFloat("sal", 7000)
                .setString("email", "%A%");

        // 3. 执行查询
        List<Employee> employees = query.list();
        System.out.println(employees);
        System.out.println(employees.size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.SALARY>?
     *         and (
     *             employee0_.EMAIL like ?
     *         )
     *         and employee0_.DEPT_ID=?
     * [com.atguigu.hibernate.entities.Employee@149f5761, com.atguigu.hibernate.entities.Employee@de88ac6, com.atguigu.hibernate.entities.Employee@5bca7664, com.atguigu.hibernate.entities.Employee@105b693d, com.atguigu.hibernate.entities.Employee@3fae596, com.atguigu.hibernate.entities.Employee@4a0df195, com.atguigu.hibernate.entities.Employee@42fcc7e6, com.atguigu.hibernate.entities.Employee@9255c05, com.atguigu.hibernate.entities.Employee@5da7cee2, com.atguigu.hibernate.entities.Employee@78830d9a, com.atguigu.hibernate.entities.Employee@5ce4369b, com.atguigu.hibernate.entities.Employee@7f829c76]
     * 12
     * destroyed
     */
    @Test
    public void testHQLInstanceParameter() {
        // 1. 创建Query对象 -- 基于命名的参数
        String hql = "FROM Employee e WHERE e.salary > ? AND e.email LIKE ? AND e.department = ?";
        Query query = session.createQuery(hql);

        // 2. 绑定参数
        Department department = new Department();
        department.setId(80);
        query.setFloat(0, 7000)
                .setString(1, "%A%")
                .setEntity(2, department);

        // 3. 执行查询
        List<Employee> employees = query.list();
        System.out.println(employees);
        System.out.println(employees.size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_ limit ?,
     *         ?
     * [Employee{id=110, name='Chen, Employee{id=111, name='Sciarra, Employee{id=112, name='Urman, Employee{id=113, name='Popp, Employee{id=114, name='Raphaely]
     * 5
     * destroyed
     */
    @Test
    public void testPageQuery() {
        String hql = "FROM Employee";
        Query query = session.createQuery(hql);

        int pageNo = 3;
        int pageSize = 5;

        List<Employee> employees = query.setFirstResult((pageNo - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();

        System.out.println(employees);
        System.out.println(employees.size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.SALARY>?
     *         and employee0_.SALARY<?
     * [Employee{id=103, name='Hunold, Employee{id=104, name='Ernst, Employee{id=109, name='Faviet, Employee{id=110, name='Chen, Employee{id=111, name='Sciarra, Employee{id=112, name='Urman, Employee{id=113, name='Popp, Employee{id=120, name='Weiss, Employee{id=121, name='Fripp, Employee{id=122, name='Kaufling, Employee{id=123, name='Vollman, Employee{id=124, name='Mourgos, Employee{id=151, name='Bernstein, Employee{id=152, name='Hall, Employee{id=153, name='Olsen, Employee{id=154, name='Cambrault, Employee{id=155, name='Tuvault, Employee{id=157, name='Sully, Employee{id=158, name='McEwen, Employee{id=159, name='Smith, Employee{id=160, name='Doran, Employee{id=161, name='Sewall, Employee{id=163, name='Greene, Employee{id=164, name='Marvins, Employee{id=165, name='Lee, Employee{id=166, name='Ande, Employee{id=167, name='Banda, Employee{id=170, name='Fox, Employee{id=171, name='Smith, Employee{id=172, name='Bates, Employee{id=173, name='Kumar, Employee{id=175, name='Hutton, Employee{id=176, name='Taylor, Employee{id=177, name='Livingston, Employee{id=178, name='Grant, Employee{id=179, name='Johnson, Employee{id=202, name='Fay, Employee{id=203, name='Mavris, Employee{id=206, name='Gietz]
     * 39
     * destroyed
     */
    @Test
    public void testNamedQuery() {
        Query query = session.getNamedQuery("salaryEmployees");

        List<Employee> employees = query.setFloat("minSal", 5000)
                .setFloat("maxSal", 10000)
                .list();
        System.out.println(employees);
        System.out.println(employees.size());
    }


    /**
     * Hibernate:
     *     select
     *         employee0_.EMAIL as col_0_0_,
     *         employee0_.SALARY as col_1_0_,
     *         employee0_.DEPT_ID as col_2_0_,
     *         department1_.ID as ID1_0_,
     *         department1_.NAME as NAME2_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     inner join
     *         AGG_DEPARTMENTS department1_
     *             on employee0_.DEPT_ID=department1_.ID
     *     where
     *         employee0_.DEPT_ID=?
     * [JRUSSEL, 14000.0, Department{id=80, name='Sales'}]
     * [KPARTNER, 13500.0, Department{id=80, name='Sales'}]
     * [AERRAZUR, 12000.0, Department{id=80, name='Sales'}]
     * [GCAMBRAU, 11000.0, Department{id=80, name='Sales'}]
     * [EZLOTKEY, 10500.0, Department{id=80, name='Sales'}]
     * [PTUCKER, 10000.0, Department{id=80, name='Sales'}]
     * [DBERNSTE, 9500.0, Department{id=80, name='Sales'}]
     * [PHALL, 9000.0, Department{id=80, name='Sales'}]
     * [COLSEN, 8000.0, Department{id=80, name='Sales'}]
     * [NCAMBRAU, 7500.0, Department{id=80, name='Sales'}]
     * [OTUVAULT, 7000.0, Department{id=80, name='Sales'}]
     * [JKING, 10000.0, Department{id=80, name='Sales'}]
     * [PSULLY, 9500.0, Department{id=80, name='Sales'}]
     * [AMCEWEN, 9000.0, Department{id=80, name='Sales'}]
     * [LSMITH, 8000.0, Department{id=80, name='Sales'}]
     * [LDORAN, 7500.0, Department{id=80, name='Sales'}]
     * [SSEWALL, 7000.0, Department{id=80, name='Sales'}]
     * [CVISHNEY, 10500.0, Department{id=80, name='Sales'}]
     * [DGREENE, 9500.0, Department{id=80, name='Sales'}]
     * [MMARVINS, 7200.0, Department{id=80, name='Sales'}]
     * [DLEE, 6800.0, Department{id=80, name='Sales'}]
     * [SANDE, 6400.0, Department{id=80, name='Sales'}]
     * [ABANDA, 6200.0, Department{id=80, name='Sales'}]
     * [LOZER, 11500.0, Department{id=80, name='Sales'}]
     * [HBLOOM, 10000.0, Department{id=80, name='Sales'}]
     * [TFOX, 9600.0, Department{id=80, name='Sales'}]
     * [WSMITH, 7400.0, Department{id=80, name='Sales'}]
     * [EBATES, 7300.0, Department{id=80, name='Sales'}]
     * [SKUMAR, 6100.0, Department{id=80, name='Sales'}]
     * [EABEL, 11000.0, Department{id=80, name='Sales'}]
     * [AHUTTON, 8800.0, Department{id=80, name='Sales'}]
     * [JTAYLOR, 8600.0, Department{id=80, name='Sales'}]
     * [JLIVINGS, 8400.0, Department{id=80, name='Sales'}]
     * [CJOHNSON, 6200.0, Department{id=80, name='Sales'}]
     * destroyed
     */
    @Test
    public void testFieldQuery() {
        String hql = "SELECT e.email, e.salary, e.department FROM Employee e WHERE e.department = :dept";
        Query query = session.createQuery(hql);

        Department department = new Department();
        department.setId(80);
        List<Object[]> result  = query.setEntity("dept", department).list();

        for(Object[] objects: result) {
            System.out.println(Arrays.asList(objects));
        }
    }

    /**
     * 投影查询
     * Hibernate:
     *     select
     *         employee0_.SALARY as col_0_0_,
     *         employee0_.EMAIL as col_1_0_,
     *         employee0_.DEPT_ID as col_2_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     inner join
     *         AGG_DEPARTMENTS department1_
     *             on employee0_.DEPT_ID=department1_.ID
     *     where
     *         employee0_.DEPT_ID=?
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * null, JRUSSEL, Department{id=80, name='Sales'}
     * null, KPARTNER, Department{id=80, name='Sales'}
     * null, AERRAZUR, Department{id=80, name='Sales'}
     * null, GCAMBRAU, Department{id=80, name='Sales'}
     * null, EZLOTKEY, Department{id=80, name='Sales'}
     * null, PTUCKER, Department{id=80, name='Sales'}
     * null, DBERNSTE, Department{id=80, name='Sales'}
     * null, PHALL, Department{id=80, name='Sales'}
     * null, COLSEN, Department{id=80, name='Sales'}
     * null, NCAMBRAU, Department{id=80, name='Sales'}
     * null, OTUVAULT, Department{id=80, name='Sales'}
     * null, JKING, Department{id=80, name='Sales'}
     * null, PSULLY, Department{id=80, name='Sales'}
     * null, AMCEWEN, Department{id=80, name='Sales'}
     * null, LSMITH, Department{id=80, name='Sales'}
     * null, LDORAN, Department{id=80, name='Sales'}
     * null, SSEWALL, Department{id=80, name='Sales'}
     * null, CVISHNEY, Department{id=80, name='Sales'}
     * null, DGREENE, Department{id=80, name='Sales'}
     * null, MMARVINS, Department{id=80, name='Sales'}
     * null, DLEE, Department{id=80, name='Sales'}
     * null, SANDE, Department{id=80, name='Sales'}
     * null, ABANDA, Department{id=80, name='Sales'}
     * null, LOZER, Department{id=80, name='Sales'}
     * null, HBLOOM, Department{id=80, name='Sales'}
     * null, TFOX, Department{id=80, name='Sales'}
     * null, WSMITH, Department{id=80, name='Sales'}
     * null, EBATES, Department{id=80, name='Sales'}
     * null, SKUMAR, Department{id=80, name='Sales'}
     * null, EABEL, Department{id=80, name='Sales'}
     * null, AHUTTON, Department{id=80, name='Sales'}
     * null, JTAYLOR, Department{id=80, name='Sales'}
     * null, JLIVINGS, Department{id=80, name='Sales'}
     * null, CJOHNSON, Department{id=80, name='Sales'}
     * destroyed
     */
    @Test
    public void testFieldQuery2() {
        String hql = "SELECT new Employee(e.salary, e.email, e.department) FROM Employee e WHERE e.department = :dept";
        Query query = session.createQuery(hql);

        Department department = new Department();
        department.setId(80);
        List<Employee> result  = query.setEntity("dept", department).list();

        for(Employee employee: result) {
            System.out.println(employee.getId() + ", " + employee.getEmail() + ", " + employee.getDepartment());
        }
    }

    /**
     * Hibernate:
     *     select
     *         min(employee0_.SALARY) as col_0_0_,
     *         max(employee0_.SALARY) as col_1_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     group by
     *         employee0_.DEPT_ID
     *     having
     *         min(employee0_.SALARY)>?
     * [7000.0, 7000.0]
     * [6000.0, 13000.0]
     * [6500.0, 6500.0]
     * [10000.0, 10000.0]
     * [6100.0, 14000.0]
     * [17000.0, 24000.0]
     * [6900.0, 12000.0]
     * [8300.0, 12000.0]
     * destroyed
     */
    @Test
    public void testGroupBy() {
        String hql = "SELECT min(e.salary), max(e.salary) FROM Employee e GROUP BY e.department HAVING min(salary) > :minSal";
        Query query = session.createQuery(hql)
                .setFloat("minSal", 5000);

        List<Object[]> result = query.list();
        for(Object[] objects: result) {
            System.out.println(Arrays.asList(objects));
        }

    }

    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         employees1_.ID as ID1_1_1_,
     *         department0_.NAME as NAME2_0_0_,
     *         employees1_.NAME as NAME2_1_1_,
     *         employees1_.SALARY as SALARY3_1_1_,
     *         employees1_.EMAIL as EMAIL4_1_1_,
     *         employees1_.DEPT_ID as DEPT_ID5_1_1_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * [[Ljava.lang.Object;@1ab6718, [Ljava.lang.Object;@3910fe11, ....]
     * 122
     * destroyed
     */
    @Test
    public void testLeftJoinFetch() {
        String hql = "FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);
        List<Department> departments = query.list();
        System.out.println(departments);
        System.out.println(departments.size());
    }

    /**
     * Hibernate:
     *     select
     *         distinct department0_.ID as ID1_0_,
     *         department0_.NAME as NAME2_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * [Department{id=10, name='Administration'}, Department{id=20, name='Marketing'}, Department{id=30, name='Purchasing'}, Department{id=40, name='Human Resources'}, Department{id=50, name='Shipping'}, Department{id=60, name='IT'}, Department{id=70, name='Public Relations'}, Department{id=80, name='Sales'}, Department{id=90, name='Executive'}, Department{id=100, name='Finance'}, Department{id=110, name='Accounting'}, Department{id=120, name='Treasury'}, Department{id=130, name='Corporate Tax'}, Department{id=140, name='Control And Credit'}, Department{id=150, name='Shareholder Services'}, Department{id=160, name='Benefits'}, Department{id=170, name='Manufacturing'}, Department{id=180, name='Construction'}, Department{id=190, name='Contracting'}, Department{id=200, name='Operations'}, Department{id=210, name='IT Support'}, Department{id=220, name='NOC'}, Department{id=230, name='IT Helpdesk'}, Department{id=240, name='Government Sales'}, Department{id=250, name='Retail Sales'}, Department{id=260, name='Recruiting'}, Department{id=270, name='Payroll'}]
     * 27
     * destroyed
     */
    @Test
    public void testLeftJoinFetch1() {
        String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);
        List<Department> departments = query.list();
        System.out.println(departments);
        System.out.println(departments.size());

        for(Department department: departments) {
            System.out.println(department.getName() + "--" + department.getEmployees().size());
        }
    }


    /**
     * 用HashSet去重复，但是测试失败
     */
    @Test
    public void testLeftJoinFetch2() {
        String hql = "FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);

        List<Department> departments = query.list();
        departments = new ArrayList<>(new LinkedHashSet<>(departments));
        System.out.println(departments);
        System.out.println(departments.size());
    }


    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         employees1_.ID as ID1_1_1_,
     *         department0_.NAME as NAME2_0_0_,
     *         employees1_.NAME as NAME2_1_1_,
     *         employees1_.SALARY as SALARY3_1_1_,
     *         employees1_.EMAIL as EMAIL4_1_1_,
     *         employees1_.DEPT_ID as DEPT_ID5_1_1_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * [[Ljava.lang.Object;@351e414e, [Ljava.lang.Object;@6fd77352, ...]
     * destroye
     */
    @Test
    public void testLeftJoin() {
        String hql = "FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);

        List result = query.list();
        System.out.println(result);
    }

    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         employees1_.ID as ID1_1_1_,
     *         department0_.NAME as NAME2_0_0_,
     *         employees1_.NAME as NAME2_1_1_,
     *         employees1_.SALARY as SALARY3_1_1_,
     *         employees1_.EMAIL as EMAIL4_1_1_,
     *         employees1_.DEPT_ID as DEPT_ID5_1_1_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * [[Ljava.lang.Object;@460510aa, [Ljava.lang.Object;@351e414e, [Ljava.lang.Object;@6fd77352, [Ljava.lang.Object;@5109e8cf, [Ljava.lang.Object;@3f672204, [Ljava.lang.Object;@78b41097, [Ljava.lang.Object;@2c2db130, [Ljava.lang.Object;@327c7bea, [Ljava.lang.Object;@348d18a3, [Ljava.lang.Object;@6c65860d, [Ljava.lang.Object;@2d000e80, [Ljava.lang.Object;@7cf283e1, [Ljava.lang.Object;@20e6c4dc, [Ljava.lang.Object;@60737b23, [Ljava.lang.Object;@4d2a1da3, [Ljava.lang.Object;@252f626c, [Ljava.lang.Object;@33f98231, [Ljava.lang.Object;@48284d0e, [Ljava.lang.Object;@557286ad, [Ljava.lang.Object;@b10a26d, [Ljava.lang.Object;@74075134, [Ljava.lang.Object;@7e4d2287, [Ljava.lang.Object;@3f4b840d, [Ljava.lang.Object;@31464a43, [Ljava.lang.Object;@7f8633ae, [Ljava.lang.Object;@68c87fc3, [Ljava.lang.Object;@bc0f53b, [Ljava.lang.Object;@8d7b252, [Ljava.lang.Object;@4682eba5, [Ljava.lang.Object;@6d9fb2d1, [Ljava.lang.Object;@61fafb74, [Ljava.lang.Object;@540a903b, [Ljava.lang.Object;@58496dc, [Ljava.lang.Object;@151db587, [Ljava.lang.Object;@238acd0b, [Ljava.lang.Object;@23811a09, [Ljava.lang.Object;@2125ad3, [Ljava.lang.Object;@7a5b769b, [Ljava.lang.Object;@f4c0e4e, [Ljava.lang.Object;@24361cfc, [Ljava.lang.Object;@34e20e6b, [Ljava.lang.Object;@15ac59c2, [Ljava.lang.Object;@7a7d1b47, [Ljava.lang.Object;@6eb82908, [Ljava.lang.Object;@4a8df3e2, [Ljava.lang.Object;@3d98d138, [Ljava.lang.Object;@2f2d52ef, [Ljava.lang.Object;@f2ce6b, [Ljava.lang.Object;@25c53f74, [Ljava.lang.Object;@5e2f3be5, [Ljava.lang.Object;@1bd0b0e5, [Ljava.lang.Object;@dc7b462, [Ljava.lang.Object;@1f51431, [Ljava.lang.Object;@26a2f7f9, [Ljava.lang.Object;@38eb2c50, [Ljava.lang.Object;@8bffb8b, [Ljava.lang.Object;@21422231, [Ljava.lang.Object;@10ee04df, [Ljava.lang.Object;@7186333e, [Ljava.lang.Object;@692fd26, [Ljava.lang.Object;@36f1046f, [Ljava.lang.Object;@56d93692, [Ljava.lang.Object;@1686f0b4, [Ljava.lang.Object;@76c548f, [Ljava.lang.Object;@3900fa71, [Ljava.lang.Object;@26722665, [Ljava.lang.Object;@7d0614f, [Ljava.lang.Object;@627d8516, [Ljava.lang.Object;@5c10285a, [Ljava.lang.Object;@6b667cb3, [Ljava.lang.Object;@6f38a289, [Ljava.lang.Object;@61e3cf4d, [Ljava.lang.Object;@3cec79d3, [Ljava.lang.Object;@64b70919, [Ljava.lang.Object;@4e31c3ec, [Ljava.lang.Object;@3157e4c0, [Ljava.lang.Object;@6eaa21d8, [Ljava.lang.Object;@328902d5, [Ljava.lang.Object;@72e789cb, [Ljava.lang.Object;@7c1812b3, [Ljava.lang.Object;@43034809, [Ljava.lang.Object;@39e67516, [Ljava.lang.Object;@77010a30, [Ljava.lang.Object;@4bb003e9, [Ljava.lang.Object;@12aa4996, [Ljava.lang.Object;@18eec010, [Ljava.lang.Object;@67c119b7, [Ljava.lang.Object;@2ca5f1ed, [Ljava.lang.Object;@6c03fb16, [Ljava.lang.Object;@28348c6, [Ljava.lang.Object;@6de0f580, [Ljava.lang.Object;@6e495b48, [Ljava.lang.Object;@1d61c6dc, [Ljava.lang.Object;@53c68ce, [Ljava.lang.Object;@6f9ad11c, [Ljava.lang.Object;@4b2d44bc, [Ljava.lang.Object;@58e92c23, [Ljava.lang.Object;@3e7545e8, [Ljava.lang.Object;@75e710b, [Ljava.lang.Object;@26f7cdf8, [Ljava.lang.Object;@376e7531, [Ljava.lang.Object;@23202c31, [Ljava.lang.Object;@5782d777, [Ljava.lang.Object;@4f824872, [Ljava.lang.Object;@b016b4e, [Ljava.lang.Object;@29629fbb, [Ljava.lang.Object;@681adc8f, [Ljava.lang.Object;@3506d826, [Ljava.lang.Object;@35dd9ed3, [Ljava.lang.Object;@8ff5094, [Ljava.lang.Object;@363f0ba0, [Ljava.lang.Object;@35fb22a9, [Ljava.lang.Object;@6c8909c3, [Ljava.lang.Object;@1e008f36, [Ljava.lang.Object;@50acf55d, [Ljava.lang.Object;@3cae7b8b, [Ljava.lang.Object;@184dbacc, [Ljava.lang.Object;@2aeefcc, [Ljava.lang.Object;@359ff4d9, [Ljava.lang.Object;@7a22a3c2, [Ljava.lang.Object;@f4cfd90, [Ljava.lang.Object;@7ae9a33a]
     * [Department{id=10, name='Administration'}, Employee{id=200, name='Whalen]
     * [Department{id=20, name='Marketing'}, Employee{id=201, name='Hartstein]
     * [Department{id=20, name='Marketing'}, Employee{id=202, name='Fay]
     * ...
     * [Department{id=110, name='Accounting'}, Employee{id=205, name='Higgins]
     * [Department{id=110, name='Accounting'}, Employee{id=206, name='Gietz]
     * [Department{id=120, name='Treasury'}, null]
     * ...
     * [Department{id=270, name='Payroll'}, null]
     * destroyed
     */
    @Test
    public void testLeftJoin1() {
        String hql = "FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);

        List<Object[]> result = query.list();
        System.out.println(result);

        for(Object[] objects: result) {
            System.out.println(Arrays.asList(objects));
        }
    }

    /**
     * Hibernate:
     *     select
     *         distinct department0_.ID as ID1_0_,
     *         department0_.NAME as NAME2_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * Administration
     * Marketing
     * Purchasing
     * Human Resources
     * Shipping
     * IT
     * Public Relations
     * Sales
     * Executive
     * Finance
     * Accounting
     * Treasury
     * Corporate Tax
     * Control And Credit
     * Shareholder Services
     * Benefits
     * Manufacturing
     * Construction
     * Contracting
     * Operations
     * IT Support
     * NOC
     * IT Helpdesk
     * Government Sales
     * Retail Sales
     * Recruiting
     * Payroll
     * 27
     * destroyed
     */
    @Test
    public void testLeftJoin2() {
        String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);

        List<Department> departments = query.list();
        for(Department department: departments) {
            System.out.println(department.getName());
        }
        System.out.println(departments.size());
    }


    /**
     * Hibernate:
     *     select
     *         distinct department0_.ID as ID1_0_,
     *         department0_.NAME as NAME2_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     left outer join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * Administration, 1
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * Marketing, 2
     * ...
     *
     * destroyed
     *
     * employees虽然查了，但是没有初始化
     */
    @Test
    public void testLeftJoin3() {
        String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN d.employees";
        Query query = session.createQuery(hql);

        List<Department> departments = query.list();
        for(Department department: departments) {
            System.out.println(department.getName() + ", " + department.getEmployees().size());
        }
        System.out.println(departments.size());
    }


    /**
     * Hibernate:
     *     select
     *         distinct department0_.ID as ID1_0_,
     *         department0_.NAME as NAME2_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     inner join
     *         AGG_EMPLOYEES employees1_
     *             on department0_.ID=employees1_.DEPT_ID
     * Administration
     * Marketing
     * Purchasing
     * Human Resources
     * Shipping
     * IT
     * Public Relations
     * Sales
     * Executive
     * Finance
     * Accounting
     * 11
     * destroyed
     */
    @Test
    public void testInnerJoin() {
        String hql = "SELECT DISTINCT d FROM Department d INNER JOIN d.employees";
        Query query = session.createQuery(hql);

        List<Department> departments = query.list();
        for(Department department: departments) {
            System.out.println(department.getName());
        }
        System.out.println(departments.size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         department1_.ID as ID1_0_1_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_,
     *         department1_.NAME as NAME2_0_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     left outer join
     *         AGG_DEPARTMENTS department1_
     *             on employee0_.DEPT_ID=department1_.ID
     * 107
     * destroyed
     */
    @Test
    public void testLeftJoinFetchMany2One() {
        String hql = "FROM Employee e LEFT JOIN FETCH e.department";
        Query query = session.createQuery(hql);

        List<Employee> employees = query.list();
        System.out.println(employees.size());
    }


    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         department1_.ID as ID1_0_1_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_,
     *         department1_.NAME as NAME2_0_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     inner join
     *         AGG_DEPARTMENTS department1_
     *             on employee0_.DEPT_ID=department1_.ID
     * 106
     * Whalen, Administration
     * Hartstein, Marketing
     * Fay, Marketing
     * Raphaely, Purchasing
     * Khoo, Purchasing
     * Baida, Purchasing
     * Tobias, Purchasing
     * Himuro, Purchasing
     * Colmenares, Purchasing
     * Mavris, Human Resources
     * Weiss, Shipping
     * Fripp, Shipping
     * Kaufling, Shipping
     * Vollman, Shipping
     * Mourgos, Shipping
     * Nayer, Shipping
     * Mikkilineni, Shipping
     * Landry, Shipping
     * Markle, Shipping
     * Bissot, Shipping
     * Atkinson, Shipping
     * Marlow, Shipping
     * Olson, Shipping
     * Mallin, Shipping
     * Rogers, Shipping
     * Gee, Shipping
     * Philtanker, Shipping
     * Ladwig, Shipping
     * Stiles, Shipping
     * Seo, Shipping
     * Patel, Shipping
     * Rajs, Shipping
     * Davies, Shipping
     * Matos, Shipping
     * Vargas, Shipping
     * Taylor, Shipping
     * Fleaur, Shipping
     * Sullivan, Shipping
     * Geoni, Shipping
     * Sarchand, Shipping
     * Bull, Shipping
     * Dellinger, Shipping
     * Cabrio, Shipping
     * Chung, Shipping
     * Dilly, Shipping
     * Gates, Shipping
     * Perkins, Shipping
     * Bell, Shipping
     * Everett, Shipping
     * McCain, Shipping
     * Jones, Shipping
     * Walsh, Shipping
     * Feeney, Shipping
     * OConnell, Shipping
     * Grant, Shipping
     * Hunold, IT
     * Ernst, IT
     * Austin, IT
     * Pataballa, IT
     * Lorentz, IT
     * Baer, Public Relations
     * Russell, Sales
     * Partners, Sales
     * Errazuriz, Sales
     * Cambrault, Sales
     * Zlotkey, Sales
     * Tucker, Sales
     * Bernstein, Sales
     * Hall, Sales
     * Olsen, Sales
     * Cambrault, Sales
     * Tuvault, Sales
     * King, Sales
     * Sully, Sales
     * McEwen, Sales
     * Smith, Sales
     * Doran, Sales
     * Sewall, Sales
     * Vishney, Sales
     * Greene, Sales
     * Marvins, Sales
     * Lee, Sales
     * Ande, Sales
     * Banda, Sales
     * Ozer, Sales
     * Bloom, Sales
     * Fox, Sales
     * Smith, Sales
     * Bates, Sales
     * Kumar, Sales
     * Abel, Sales
     * Hutton, Sales
     * Taylor, Sales
     * Livingston, Sales
     * Johnson, Sales
     * King, Executive
     * Kochhar, Executive
     * De Haan, Executive
     * Greenberg, Finance
     * Faviet, Finance
     * Chen, Finance
     * Sciarra, Finance
     * Urman, Finance
     * Popp, Finance
     * Higgins, Accounting
     * Gietz, Accounting
     * destroyed
     *
     * 连续打印，说明在检索Employee的时候，Department也已经被初始化了
     */
    @Test
    public void testLeftJoinFetchMany2One1() {
        String hql = "FROM Employee e INNER JOIN FETCH e.department";
        Query query = session.createQuery(hql);

        List<Employee> employees = query.list();
        System.out.println(employees.size());

        for(Employee employee: employees) {
            System.out.println(employee.getName() + ", " + employee.getDepartment().getName());
        }
    }


    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         department1_.ID as ID1_0_1_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_,
     *         department1_.NAME as NAME2_0_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     inner join
     *         AGG_DEPARTMENTS department1_
     *             on employee0_.DEPT_ID=department1_.ID
     * 106
     * destroyed
     */
    @Test
    public void testLeftJoinMany2One1() {
        String hql = "FROM Employee e INNER JOIN e.department";
        Query query = session.createQuery(hql);

        List<Employee> employees = query.list();
        System.out.println(employees.size());

        for(Employee employee: employees) {
            System.out.println(employee.getName() + ", " + employee.getDepartment().getName());
        }
    }


    /**
     * Hibernate:
     *     select
     *         this_.ID as ID1_1_0_,
     *         this_.NAME as NAME2_1_0_,
     *         this_.SALARY as SALARY3_1_0_,
     *         this_.EMAIL as EMAIL4_1_0_,
     *         this_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES this_
     *     where
     *         this_.EMAIL=?
     *         and this_.SALARY>?
     * Employee{id=173, name='Kumar
     * destroyed
     */
    @Test
    public void testQBC() {
        // 1. 创建Criteria对象
        Criteria criteria = session.createCriteria(Employee.class);

        // 2. 添加查询条件: 在QBC中，查询条件由Criterian表示
        // Criterion可以通过Restrictions的静态方法获得
        criteria.add(Restrictions.eq("email", "SKUMAR"));
        criteria.add(Restrictions.gt("salary", 5000F));

        // 3. 执行查询
        Employee employee = (Employee) criteria.uniqueResult();
        System.out.println(employee);
    }


    /**
     * (name like %a% and department=Department{id=80, name='null'})
     * (salary>6000.0 or email is null)
     * Hibernate:
     *     select
     *         this_.ID as ID1_1_0_,
     *         this_.NAME as NAME2_1_0_,
     *         this_.SALARY as SALARY3_1_0_,
     *         this_.EMAIL as EMAIL4_1_0_,
     *         this_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES this_
     *     where
     *         (
     *             this_.NAME like ?
     *             and this_.DEPT_ID=?
     *         )
     *         and (
     *             this_.SALARY>?
     *             or this_.EMAIL is null
     *         )
     * destroyed
     */
    @Test
    public void testQBCAndOr() {
        Criteria criteria = session.createCriteria(Employee.class);

        // 1. AND, 使用Conjunction表示, Conjunction本身就是一个Criterion对象，其其中还可以添加Criterion对象
        Conjunction conjunction = Restrictions.conjunction();
        conjunction.add(Restrictions.like("name", "a", MatchMode.ANYWHERE));
        Department department = new Department();
        department.setId(80);
        conjunction.add(Restrictions.eq("department", department));
        System.out.println(conjunction);    // (name like %a% and department=Department{id=80, name='null'})

        // 2. OR
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.gt("salary", 6000F));
        disjunction.add(Restrictions.isNull("email"));
        System.out.println(disjunction);    // (salary>6000.0 or email is null)

        criteria.add(conjunction);
        criteria.add(disjunction);
        criteria.list();
    }

    /**
     * Hibernate:
     *     select
     *         max(this_.SALARY) as y0_
     *     from
     *         AGG_EMPLOYEES this_
     * 24000.0
     * destroyed
     */
    @Test
    public void testQBC3() {
        Criteria criteria = session.createCriteria(Employee.class);

        // 统计查询：使用Projection来表示: 可以由Projections的静态方法得到
        criteria.setProjection(Projections.max("salary"));

        System.out.println(criteria.uniqueResult());
    }

    /**
     * Hibernate:
     *     select
     *         this_.ID as ID1_1_0_,
     *         this_.NAME as NAME2_1_0_,
     *         this_.SALARY as SALARY3_1_0_,
     *         this_.EMAIL as EMAIL4_1_0_,
     *         this_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES this_
     *     order by
     *         this_.SALARY asc,
     *         this_.EMAIL desc limit ?,
     *         ?
     * destroyed
     */
    @Test
    public void testQBC4() {
        Criteria criteria = session.createCriteria(Employee.class);

        // 1. 添加排除
        criteria.addOrder(Order.asc("salary"));
        criteria.addOrder(Order.desc("email"));

        // 2. 添加翻页方法
        int pageSize = 5;
        int pageNum = 3;
        criteria.setFirstResult((pageNum - 1) * pageSize)
                .setMaxResults(pageSize)
                .list();
    }


    /**
     * Hibernate:
     *     INSERT
     *     INTO
     *         AGG_DEPARTMENTS
     *
     *     VALUES
     *         (?, ?)
     * destroyed
     */
    @Test
    public void testNativeSQL() {
        String sql = "INSERT INTO AGG_DEPARTMENTS VALUES(?, ?)";
        Query query = session.createSQLQuery(sql);
        query.setInteger(0, 280)
                .setString(1, "ATGUITU")
                .executeUpdate();
    }


    /**
     * Hibernate:
     *     delete
     *     from
     *         AGG_DEPARTMENTS
     *     where
     *         ID=?
     * destroyed
     */
    @Test
    public void testHQLUpdate() {
        String hql = "DELETE FROM Department d WHERE d.id = :id";
        session.createQuery(hql)
                .setInteger("id", 280)
                .executeUpdate();
    }


    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.ID=?
     * Employee{id=100, name='King
     * Employee{id=100, name='King
     * destroyed
     *
     * 因为有session cache的存在，第二次查询时，cache中已经有目标对象了，就没有再次发送SELECT语句
     */
    @Test
    public void testHibernateSecondLevelCache() {
        Employee employee = (Employee) session.get(Employee.class, 100);
        System.out.println(employee);

        Employee employee1 = (Employee) session.get(Employee.class, 100);
        System.out.println(employee1);
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.ID=?
     * Employee{id=100, name='King
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.ID=?
     * Employee{id=100, name='King
     * destroyed
     *
     * 二级缓存的目的
     */
    @Test
    public void testHibernateSecondLevelCache1() {
        Employee employee = (Employee) session.get(Employee.class, 100);
        System.out.println(employee);

        transaction.commit();
        session.close();    // 关闭session后，会清理缓存

        session = sessionFactory.openSession(); // 开启新的session
        transaction = session.beginTransaction();

        Employee employee1 = (Employee) session.get(Employee.class, 100);
        System.out.println(employee1);
    }

    /**
     * Without Second Level Cache
     *
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_0_,
     *         employee0_.NAME as NAME2_1_0_,
     *         employee0_.SALARY as SALARY3_1_0_,
     *         employee0_.EMAIL as EMAIL4_1_0_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.ID=?
     * Employee{id=100, name='King
     * Employee{id=100, name='King
     * destroyed
     *
     * 即使关闭了session，也只发送了一条SELECT语句
     */
    @Test
    public void testHibernateSecondLevelCache2() {
        Employee employee = (Employee) session.get(Employee.class, 100);
        System.out.println(employee);

        transaction.commit();
        session.close();    // 关闭session后，会清理缓存

        session = sessionFactory.openSession(); // 开启新的session
        transaction = session.beginTransaction();

        Employee employee1 = (Employee) session.get(Employee.class, 100);
        System.out.println(employee1);
    }


    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * destroyed
     */
    @Test
    public void testCollectionSecondLevelCache() {
         Department department = (Department) session.get(Department.class, 80);

        System.out.println(department.getName());
        System.out.println(department.getEmployees().size());
    }


    /**
     * Without Second Level Cache,
     * 发送四条SQL语句
     */
    @Test
    public void testCollectionSecondLevelCache1() {
        Department department = (Department) session.get(Department.class, 80);

        System.out.println(department.getName());
        System.out.println(department.getEmployees().size());

        transaction.commit();
        session.close();    // 关闭session后，会清理缓存

        session = sessionFactory.openSession(); // 开启新的session
        transaction = session.beginTransaction();

        Department department1 = (Department) session.get(Department.class, 80);

        System.out.println(department1.getName());
        System.out.println(department1.getEmployees().size());
    }


    /**
     * 对Department类使用二级缓存，Employee没有二级缓存
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * destroyed
     *
     * 三条SQL语句
     */
    @Test
    public void testCollectionSecondLevelCache2() {
        Department department = (Department) session.get(Department.class, 80);

        System.out.println(department.getName());
        System.out.println(department.getEmployees().size());

        transaction.commit();
        session.close();    // 关闭session后，会清理缓存

        session = sessionFactory.openSession(); // 开启新的session
        transaction = session.beginTransaction();

        Department department1 = (Department) session.get(Department.class, 80);

        System.out.println(department1.getName());
        System.out.println(department1.getEmployees().size());
    }

    /**
     * <class-cache class="com.atguigu.hibernate.entities.Employee" usage="read-write"/>
     * <class-cache class="com.atguigu.hibernate.entities.Department" usage="read-write"/>
     * <collection-cache collection="com.atguigu.hibernate.entities.Department.employees" usage="read-write"/>
     *
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * Sales
     * 34
     * destroyed
     */
    @Test
    public void testCollectionSecondLevelCache3() {
        Department department = (Department) session.get(Department.class, 80);

        System.out.println(department.getName());
        System.out.println(department.getEmployees().size());

        transaction.commit();
        session.close();    // 关闭session后，会清理缓存

        session = sessionFactory.openSession(); // 开启新的session
        transaction = session.beginTransaction();

        Department department1 = (Department) session.get(Department.class, 80);

        System.out.println(department1.getName());
        System.out.println(department1.getEmployees().size());
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     * 107
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     * 107
     * destroyed
     *
     * cache并没有起作用
     */
    @Test
    public void testQueryCache() {
        Query query = session.createQuery("FROM Employee");

        List<Employee> employees = query.list();
        System.out.println(employees.size());

        employees = query.list();
        System.out.println(employees.size());
    }

    /**
     *
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     * 107
     * 107
     * destroyed
     */
    @Test
    public void testQueryCache1() {
        Query query = session.createQuery("FROM Employee");
        query.setCacheable(true);   // 还需要配置启用查询缓存

        List<Employee> employees = query.list();
        System.out.println(employees.size());

        employees = query.list();
        System.out.println(employees.size());
    }

    @Test
    public void testCriteriaCache() {
        Criteria criteria = session.createCriteria(Employee.class);
        criteria.setCacheable(true);
    }

    /**
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     * 107
     * Hibernate:
     *     update
     *         AGG_EMPLOYEES
     *     set
     *         NAME=?,
     *         SALARY=?,
     *         EMAIL=?,
     *         DEPT_ID=?
     *     where
     *         ID=?
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     * 107
     * destroyed
     */
    @Test
    public void testUpdateTimeStampCache() {
        Query query = session.createQuery("FROM Employee");
        query.setCacheable(true);   // 还需要配置启用查询缓存

        List<Employee> employees = query.list();
        System.out.println(employees.size());

        Employee employee = (Employee) session.get(Employee.class, 100);
        employee.setSalary(30000);

        employees = query.list();
        System.out.println(employees.size());
    }


    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * Hibernate:
     *     select
     *         employee0_.ID as ID1_1_,
     *         employee0_.NAME as NAME2_1_,
     *         employee0_.SALARY as SALARY3_1_,
     *         employee0_.EMAIL as EMAIL4_1_,
     *         employee0_.DEPT_ID as DEPT_ID5_1_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.DEPT_ID=80
     * 34
     * destroyed
     */
    @Test
    public void testQueryIterator() {
        Department department1 = (Department) session.get(Department.class, 80);

        System.out.println(department1.getName());
        System.out.println(department1.getEmployees().size());

        Query query = session.createQuery("FROM Employee e WHERE e.department.id = 80");
        List<Employee> employees = query.list();
        System.out.println(employees.size());
    }


    /**
     * Hibernate:
     *     select
     *         department0_.ID as ID1_0_0_,
     *         department0_.NAME as NAME2_0_0_
     *     from
     *         AGG_DEPARTMENTS department0_
     *     where
     *         department0_.ID=?
     * Sales
     * Hibernate:
     *     select
     *         employees0_.DEPT_ID as DEPT_ID5_0_1_,
     *         employees0_.ID as ID1_1_1_,
     *         employees0_.ID as ID1_1_0_,
     *         employees0_.NAME as NAME2_1_0_,
     *         employees0_.SALARY as SALARY3_1_0_,
     *         employees0_.EMAIL as EMAIL4_1_0_,
     *         employees0_.DEPT_ID as DEPT_ID5_1_0_
     *     from
     *         AGG_EMPLOYEES employees0_
     *     where
     *         employees0_.DEPT_ID=?
     * 34
     * Hibernate:
     *     select
     *         employee0_.ID as col_0_0_
     *     from
     *         AGG_EMPLOYEES employee0_
     *     where
     *         employee0_.DEPT_ID=80
     * Russell
     * Partners
     * Errazuriz
     * Cambrault
     * Zlotkey
     * Tucker
     * Bernstein
     * Hall
     * Olsen
     * Cambrault
     * Tuvault
     * King
     * Sully
     * McEwen
     * Smith
     * Doran
     * Sewall
     * Vishney
     * Greene
     * Marvins
     * Lee
     * Ande
     * Banda
     * Ozer
     * Bloom
     * Fox
     * Smith
     * Bates
     * Kumar
     * Abel
     * Hutton
     * Taylor
     * Livingston
     * Johnson
     * destroyed
     */
    @Test
    public void testQueryIterator1() {
        Department department1 = (Department) session.get(Department.class, 80);

        System.out.println(department1.getName());
        System.out.println(department1.getEmployees().size());

        Query query = session.createQuery("FROM Employee e WHERE e.department.id = 80");
        Iterator<Employee> employeeIterator = query.iterate();
        while ((employeeIterator.hasNext())) {
            System.out.println(employeeIterator.next().getName());
        }
    }
}