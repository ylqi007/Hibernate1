
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

## 6. Session的概述
* `Session`接口是Hibernate向应用程序提供的操纵数据库的最主要的接口,它提供了基本的保存,更新,删除和加载Java对象的方法(查询并不是由Session直接完成的，需要通过query)。
* `Session`具有一个缓存,位于缓存中的对象称为持久化对象,它和数据库中的相关记录对应. `Session`能够在某些时间点,按照缓存中对象的变化来执行相关的SQL语句, 来同步更新数据库, 这一过程被称为**刷新缓存(flush)**.
* 站在持久化的角度, Hibernate把对象分为4种状态: 持久化状态, 临时状态, 游离状态, 删除状态. `Session`的特定方法能使对象从一个状态转换到另一个状态.

### 6.1 Session缓存
* 在`Session`接口的实现中包含一系列的*Java集合*, 这些Java集合构成了Session缓存. 只要Session实例没有结束生命周期, 且没有清理缓存，则存放在它缓存中的对象也不会结束生命周期.
* Session缓存可减少Hibernate应用程序访问数据库的频率。

### 6.2 操作Session缓存
#### 6.2.1 flush()
* `flush()`: Session按照缓存中对象的属性变化来同步更新数据库
* 默认情况下Session在以下时间点刷新缓存：
  * 显式调用Session的`flush()`方法
  * 当应用程序调用Transaction的`commit()`方法的时, 该方法先flush，然后在向数据库提交事务
  * 当应用程序执行一些查询(HQL, Criteria)操作时， 如果缓存中持久化对象的属性已经发生了变化，会先flush缓存，以保证查询结果能够反映持久化对象的最新状态
* flush缓存的例外情况: 如果对象使用native生成器生成OID, 那么当调用Session的`save()`方法保存对象时, 会立即执行向数据库插入该实体的INSERT语句.
* `commit()`和`flush()`方法的区别： 
  * flush执行一系列SQL语句，但不提交事务； 
  * commit方法先调用flush()方法，然后提交事务. 提交事务意味着对数据库操作永久保存下来。

#### 6.2.2 refresh()
会强制发送SELECT语句，以使Session缓存中对象的状态和数据表中对应的记录保持一致
* 具体执行效果还与事务隔离级别有关 `<property name="connection.isolation">2</property>`

#### 6.2.3 clean()
* 清理缓存.

### 6.3 持久化对象的状态
站在持久化的角度，Hibernate把对象分为4种状态：
1. 临时状态(Transient)
   * 在使用代理主键的情况下，OID通常为null
   * 不处于Session的缓存中
   * 在数据库中没有对应的记录
2. 持久化状态(Persistent)
   * OID不为null
   * 位于Session缓存中
   * 若在数据库中已经有和其对应的记录，持久化对象和数据库中的相关记录对应
   * Session在flush缓存时，会根据持久化对象属性的变化，来同步更新数据库
   * 在同一个Session实例的缓存中，数据库表中的每条记录只对应唯一的持久化对象
3. 游离状态(Detached)
   * OID不为null
   * 不再处于Session缓存中
   * 一般情况下，游离对象是由持久化对象转变过来的，因此在数据库中可能还存在与它对应的记录
4. 删除状态(Removed)
   * 在数据库中没有和其OID对应的记录
   * 不再处于Session缓存中
   * 一般情况下，应用程序不该再使用被删除的对象

以上的解释来自尚硅谷佟刚的课件。以下英文解释来自Baeldung网站, [Object States in Hibernate’s Session](https://www.baeldung.com/hibernate-session-object-states)

**Object States**: In the context of Hibernates's _Session_, objects can be in one of three possible states: transient, persistent, or detached. 
* **Transient**: An object we haven't attached to any session is in the transient state. Since it was never persisted, it doesn't have any representation in the database. Because no session is aware of it, it won't be saved automatically.
* **Persistent**: An object that we've associated with a session is in the persistent state. We either saved it or read it from a persistence context, so it represents some row in the database.
* **Detached**: When we close the session, all objects inside it become detached. Although they still represent rows in the database, they're no longer managed by any session:

## 7. Session的核心方法
### 7.1 Session.save()
* Session的`save()`方法使一个临时对象转变为持久化对象
* Session的`save()`方法完成以下操作:
  * 把News对象加入到Session缓存中, 使它进入持久化状态。
  * 选用映射文件指定的标识符生成器(比如`<generator class="native"/>`), 为持久化对象分配唯一的OID。在使用代理主键的情况下,`setId()`方法为News对象设置OID使无效的。
  * 在flush缓存的时候，计划执行一条INSERT语句。
* Hibernate 通过持久化对象的OID来维持它和数据库相关记录的对应关系。**当News对象处于持久化状态时,不允许程序随意修改它的ID。**
* `persist()`和`save()`区别：
  * 当对一个OID不为Null的对象执行`save()`方法时, 会把该对象以一个新的OID保存到数据库中; 但执行`persist()`方法时会抛出一个异常。

### 7.2 Session.persist()
* 与`Session.save()`方法类似，`persist()`也会把一个临时对象转变为持久化对象，并发送INSERT语句。
* 和`save()`的区别:
  * 在调用`persist()`方法之前，若对象已经有ID了，则不会执行INSERT，而是抛出PersistentObjectException异常。

### 7.3 Session.get() & Session.load()
* 都可以根据跟定的OID从数据库中加载一个持久化对象
* 区别:
  * 当数据库中不存在与OID对应的记录时,`load()`方法抛出ObjectNotFoundException异常, 而`get()`方法返回null。
  * 两者采用不同的延迟检索策略: `load()`方法支持延迟加载策略,而`get()`不支持。
  * 若数据表中没有对应的记录，且Session没有关闭：
    * `get()`返回null
    * `load()`若不使用该对象的任何属性，没有问题；若需要初始化了，则抛出异常。
* `load()`方法可能会抛出：LazyInitializationException
  * 在需要初始化代理对象之前关闭Session，就会抛出LazyInitializationException

### 7.4 Session.update()
* Session的`update()`方法使一个游离对象转变为持久化对象,并且计划执行一条UPDATE语句.
* 若希望Session仅当修改了News对象的属性时, 才执行`update()`语句, 可以把映射文件(hbm.xml)中`<class>`元素的select-before-update设为true, 该属性的默认值为false
* 当`update()`方法关联一个游离对象时, 如果在Session的缓存中已经存在相同OID的持久化对象, 会抛出异常.
* 当`update()`方法关联一个游离对象时, 如果在数据库中不存在相应的记录, 也会抛出异常.

需要注意的是:
1. 无论需要更新的游离对象和数据表中的记录是否一致，都会发送UPDATE语句。
   * 如何能让`update()`方法不再盲目地发出UPDATE语句？在.hbm.xml文件的class节点设置`select-before-update=true` (default=false)。但通常不需要设置该属性
2. 若数据表中没有记录，但还调用了`update()`方法，会抛出异常。 see `News2Test.testUpdate5()`
3. 当`update()`方法关联一个游离对象时，如果在Session的缓存中已经有了相同的OID对象，会抛出异常。因为在Session缓存中不能有两个OID相同的对象。


## Other Notes
1. [javax.net.ssl.SSLHandshakeException: No appropriate protocol (protocol is disabled or cipher suites are inappropriate)](https://help.mulesoft.com/s/article/javax-net-ssl-SSLHandshakeException-No-appropriate-protocol-protocol-is-disabled-or-cipher-suites-are-inappropriate)
2. [How do I fix: "...error in your SQL syntax; check the manual for the right syntax"](https://stackoverflow.com/questions/16408334/how-do-i-fix-error-in-your-sql-syntax-check-the-manual-for-the-right-synta)
3. [HIBERNATE -- Community Documentation, 4.2](https://docs.jboss.org/hibernate/orm/4.2/manual/en-US/html_single/)

希望执行后，有表，且表中有记录
```sql
DROP TABLE hibernate5.news CASCADE;
```