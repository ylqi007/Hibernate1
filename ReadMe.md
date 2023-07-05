
## 1. 准备Hibernate环境
1. 导入Hibernate必须的jar包：这些jar包可以在`hibernate-release-4.2.4.Final/lib/required/`目录下找到。
2. 导入数据库驱动的jar包：`mysql-connector-java-5.1.44.jar`

## 2. Hibernate开发步骤
1. 创建Hibernate配置文件: `hibernate.cfg.xml`，这个配置文件中主要包含了三部分信息
   1. 连接数据库的基本信息，比如登录数据库的用户名和密码，连接数据库的驱动，以及数据库的URL信息。
      1. Property `connection.url`中的`localhost:3306`是default value，可以省略。
   2. 配置Hibernate的基本信息，比如用的方言(Dialect)，创建表的方式等等。
      1. 关于`Dialect`的信息，可以在`hibernate-release-4.2.4.Final/project/ect/hibernate.properties`中找到。
      2. 当使用`hibernate.dialect org.hibernate.dialect.MySQLInnoDBDialect`，会出现`TYPE=InnoDB`的问题。这时候应该使用`org.hibernate.dialect.MySQLDialect`。
   3. 指定关联的hbm.xml文件，也就是对象-关系映射文件。
      1. 指定关联的hbm.xml文件时，注意是要用目录结构(`com/atguigu/hibernate/helloworld/News.hbm.xml`)，而不是包结构(`com.atguigu.hibernate.helloworld.News.hbm.xml`)。
   4. [Hibernate Configuration](https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html_single/#tutorial-firstapp-configuration)
2. 创建持久化类: `News.java`
3. 创建对象-关系映射文件: `News.hbm.xml`
   1. [The mapping file](https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html_single/#tutorial-firstapp-mapping)
4. 通过Hibernate API编写访问数据库的代码

## 3. 创建持久化Java类
1. 提供一个无参构造器: 使Hibernate可以使用`Constructor.newInstance()`来实例化持久化类。比如获取数据库中的数据: `session.get(News.class, 1)`
2. 提供一个表示同意属性(identifier property): 通常映射为数据库表的主键字段。如果没有该属性，一些功能将不起作用，如: `Session.saveOrUpdate()`
3. 为类的持久化类字段声明访问方法(get/set): Hibernate对JavaBeans风格的属性实行持久化。
4. 使用非final类: 在运行时生成代理是Hibernate的一个重要的功能。如果持久化类没有实现任何接口，Hibernate使用CGLIB生成代理。如果使用的是final类，则无法生成CGLIB代理。
5. 重写equals和hashCode方法: 如果需要把持久化类实例放到Set中，则应该重写这两个方法。

## 4. 对象-关系映射文件
* 对象：类`News`～数据库的表`NEWS`，属性`News.title`～表中的列`NEWS.TITLE`

## 5. Hibernate配置文件
`hibernate.cfg.xml`

## Other Notes
1. [javax.net.ssl.SSLHandshakeException: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)](https://help.mulesoft.com/s/article/javax-net-ssl-SSLHandshakeException-No-appropriate-protocol-protocol-is-disabled-or-cipher-suites-are-inappropriate)
2. [How do I fix: "...error in your SQL syntax; check the manual for the right syntax"](https://stackoverflow.com/questions/16408334/how-do-i-fix-error-in-your-sql-syntax-check-the-manual-for-the-right-synta)
3. [HIBERNATE -- Community Documentation, 4.2](https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html_single/)

希望执行后，有表，且表中有记录
```sql
DROP TABLE hibernate5.news CASCADE;
```