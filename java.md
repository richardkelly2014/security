##基础：
###==和 equals 的区别
	1. == : 它的作用是判断两个对象的地址是不是相等。即判断两个对象是不是同一个对象。(基本数据类型==比较的是值，引用数据类型==比较的是内存地址)
	2. equals() : 它的作用也是判断两个对象是否相等，它不能用于比较基本数据类型的变量。
	3.hashCode()与 equals()
		1. hashCode() 的作用是获取哈希码，也称为散列码；它实际上是返回一个 int 整数。这个哈希码的作用是确定该对象在哈希表中的索引位置
		2. equals 方法被覆盖过，则 hashCode 方法也必须被覆盖
###方法
	1.按值调用(call by value)表示方法接收的是调用者提供的值，而按引用调用（call by reference)表示方法接收的是调用者提供的变量地址。
	2.深拷贝 vs 浅拷贝
		1.浅拷贝：对基本数据类型进行值传递，对引用数据类型进行引用传递般的拷贝，此为浅拷贝。
		2.深拷贝：对基本数据类型进行值传递，对引用数据类型，创建一个新的对象，并复制其内容，此为深拷贝
###面向对象
	1. Java 程序在执行子类的构造方法之前，如果没有用 super()来调用父类特定的构造方法，则会调用父类中“没有参数的构造方法”。
	2. transient 关键字的作用是：阻止实例中那些用此关键字修饰的的变量序列化；当对象被反序列化时，被 transient 修饰的变量值不会被持久化和恢复。transient 只能修饰变量，不能修饰类和方法

###Arrays.asList
    1. Arrays.asList()是泛型方法，传入的对象必须是对象数组
    2. 不要在 foreach 循环里进行元素的 remove/add 操作

###枚举
    1. 使用 == 比较枚举类型
    2. 在 switch 语句中使用枚举类型
    3. 枚举类型的属性,方法和构造函数
    4. 单例设计与策略设计模式
    
###泛型

###final,static,this,super
    1. final 关键字
        1.final修饰的类不能被继承，final类中的所有成员方法都会被隐式的指定为final方法
        2.final修饰的方法不能被重写
        3.final修饰的变量是常量，如果是基本数据类型的变量，则其数值一旦在初始化之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能让其指向另一个对象
    2.static 关键字
        1.使用静态内部类，初始化单例设计模式    

###发射机制
###代理模式(静态代理和动态代理)
    1. jdk动态代理。在 Java 动态代理机制中 InvocationHandler 接口和 Proxy 类是核心
    2. CGLIB 动态代理机制 在 CGLIB 动态代理机制中 MethodInterceptor 接口和 Enhancer 类是核心.
       CGLIB 通过继承方式实现代理.
       
###BIO,NIO,AIO 总结
    1.同步/异步是从行为角度描述事物的，而阻塞和非阻塞描述的当前事物的状态

###集合(List,Set,Map)
    1.list: Arraylist(Object[]数组),LinkedList(双向链表)
    2.map : HashMap (数组和链表或红黑树组成)
    3. ArrayList 扩容机制
    4. 红黑树

### 树
    1. 二叉排序树,平衡二叉树
    2.平衡指所有叶子的深度趋于平衡，更广义的是指在树上所有可能查找的均摊复杂度偏低
    
    3. AVL树 ( 自平衡二叉查找树)
        1. 右旋：左结点转到根节点位置。
        2. 左旋：右节点转到根节点位置
    4. 红黑树 
        1. 节点是红色或黑色
        2. 根是黑色
        3. 所有叶子都是黑色
        4. 每个红色节点必须有两个黑色的子节点
        
### queue队列
    1.ArrayBlockingQueue 底层由数组存储的有界队列
        
### 跳跃表

### tcp
    1.三次握手 ，客户端发送SYN，服务端接受返回ACK确认 ，客户端接受SYN+ACK，并返回ACK给服务端
    2.四次挥手 ，客户端发送FIN，服务接受返回ACK，服务端发送FIN ，客户端返回ACK
### redis
    1.数据类型：String ,list set,hash,sortset,BitMap
    2.缓存穿透、缓存击穿、缓存雪崩
        1.缓存穿透.访问一个不存在的key，缓存不起作用，请求会穿透到 DB，流量大时 DB 会挂掉
        2.缓存雪崩,大量的 key 设置了相同的过期时间，导致在缓存在同一时刻全部失效，造成瞬时DB请求量大、压力骤增，引起雪崩
        3.缓存击穿 