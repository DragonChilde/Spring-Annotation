**Spirng Annotation **

# Bean注入 #
## Bean注入到容器@Bean ##

**Pom**

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.2.6.RELEASE</version>
    </dependency>
	
1. 用`XML`配置`Bean`

	**`Person`类**
	
		
		public class Person {
		
		    private String name;
		    private Integer age;
		
		    public Person(String name, Integer age) {
		        this.name = name;
		        this.age = age;
		    }
		
		    public Person() {
		    }
		
		    public String getName() {
		        return name;
		    }
		
		    public void setName(String name) {
		        this.name = name;
		    }
		
		    public Integer getAge() {
		        return age;
		    }
		
		    public void setAge(Integer age) {
		        this.age = age;
		    }
		
		    @Override
		    public String toString() {
		        return "Person{" +
		                "name='" + name + '\'' +
		                ", age=" + age +
		                '}';
		    }
		}
	
	**`Bean.xml`在配置文件里配置Person Bean**
	
		<?xml version="1.0" encoding="UTF-8"?>
		<beans xmlns="http://www.springframework.org/schema/beans"
		       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

		    <bean id="person" class="com.anno.bean.Person">
		        <property name="name" value="张三"/>
		        <property name="age" value="10"/>
		    </bean>
		</beans>
	
	**测试**
	
		public class MainTest {
		    public static void main(String[] args) {
				 /*使用XML配置获取Person Bean*/
		        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
		        Person bean = context.getBean(Person.class);
		        System.out.println(bean);/*Person{name='张三', age=10}*/
		    }
		}

2. 用注解配置`Bean`

	**自定义`Config`配置类**

		//配置类==配置文件
		@Configuration//告诉Spring这是一个配置类
		public class MyConfig {
		
			//给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
		    @Bean
		    public Person person(){
		        return new Person("李四",20);
		    }
		}

	**测试**

		public class MainTest {
		    public static void main(String[] args) {
		        /*使用注解配置获取Person Bean*/
		        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig.class);
		        Person bean = annotationConfigApplicationContext.getBean(Person.class);
		        System.out.println(bean);	/*Person{name='李四', age=20}*/
		    }
		}


	**注意:注解配置的`Bean ID`是根据方法的,如果把方法名改成`person01`时**

		 @Bean
	    public Person person01(){
	        return new Person("李四",20);
	    }

	----

	    public static void main(String[] args) {
	        /*获取注解配置的Bean Id*/
	        String[] beanNamesForType = annotationConfigApplicationContext.getBeanNamesForType(Person.class);
	        for (String s:
	        beanNamesForType) {
	            System.out.println(s);  //person01
	        }
	    }

	因此,为了不改变方法名而调整`Bean Id`,可以在`@Bean`里配其`ID`值

	    @Bean("person")
	    public Person person01(){
	        return new Person("李四",20);
	    }

	---

	    public static void main(String[] args) {
	        /*获取注解配置的Bean Id*/
	        String[] beanNamesForType = annotationConfigApplicationContext.getBeanNamesForType(Person.class);
	        for (String s:
	        beanNamesForType) {
	            System.out.println(s);  //person
	        }
	    }

## 关于包扫描@ComponentScan ##

1. XML配置包扫描

	    <!-- 包扫描、只要标注了@Controller、@Service、@Repository，@Component -->
	    <context:component-scan base-package="com.anno" use-default-filters="false">
			<!--把需要包含或者排除的包在这里进行配置-->
			 //<context:include-filter type="" expression=""/>
	       // <context:exclude-filter type="" expression=""/>
   		</context:component-scan>

2. 使用注解的方式包扫描

		@Configuration
		@ComponentScan(value = "com.anno")  //@ComponentScan  value:指定要扫描的包
		public class MyConfig {
			....
		}

	---
	
	    public void test01()
	    {
	        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig.class);
	        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
	        for (String s:
	        beanDefinitionNames) {
	            System.out.println(s);
	        }
	        /**
	         *org.springframework.context.event.internalEventListenerFactory
	         * myConfig
	         * bookController
	         * bookDao
	         * bookService
	         * person 
	         **/
	    }

	把`include-filter`和`exclude-filter`注解引入

		@Configuration
		//把Controller类型的包注入
		@ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})},useDefaultFilters = false)
		/**
		打印结果:
		myConfig
		bookController
		person
		**/
		//把BookService排除
		@ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {BookService.class})},useDefaultFilters = false)
		/**
		打印结果:
		myConfig
		person
		**/
		public class MyConfig {
			....
		}


	通过源码分析`@ComponentScan`是可以多重复注解的因此也可以使用`@ComponentScans`使有多个`@ComponentScan`

		@Configuration
		@ComponentScans(value = {
	        @ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})},useDefaultFilters = false)
		})
		public class MyConfig {
			.....
		}


	- **@ComponentScan**  value:指定要扫描的包
	 - **excludeFilters = Filter[]** ：指定扫描的时候按照什么规则排除那些组件
	 - **includeFilters = Filter[]** ：指定扫描的时候只需要包含哪些组件
		 - **FilterType.ANNOTATION**：按照注解
		 - **FilterType.ASSIGNABLE_TYPE**：按照给定的类型；
		 - **FilterType.ASPECTJ**：使用ASPECTJ表达式(基本不用)
		 - **FilterType.REGEX**：使用正则指定
		 - **FilterType.CUSTOM**：使用自定义规则


	**使用自定义规则扫描自定义加载的容器**

		public class MyTypeFilter implements TypeFilter {

		    /**
		     * metadataReader：读取到的当前正在扫描的类的信息
		     * metadataReaderFactory:可以获取到其他任何类信息的
		     */
		    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		
		        //获取当前类注解的信息
		        AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
		        //获取当前正在扫描的类的类信息
		        ClassMetadata classMetadata = metadataReader.getClassMetadata();
		        //获取当前类资源（类的路径）
		        Resource resource = metadataReader.getResource();
		
		        System.out.println("====>"+classMetadata.getClassName());
		        return false;
		    }
		}

	----

		@Configuration
		@ComponentScans(value = {
       		 @ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM,classes = {MyTypeFilter.class})},useDefaultFilters = false)
		})
		public class MyConfig {
		
		    //给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
		    @Bean("person")
		    public Person person01()
		    {
		        return new Person("李四",20);
		    }
		}

	---
		/**打印结果**/
		====>com.anno.MainTest
		====>com.anno.bean.Person
		====>com.anno.config.MyTypeFilter
		====>com.anno.controller.BookController
		====>com.anno.dao.BookDao
		====>com.anno.service.BookService


## 关于@Scope作用域 ##

	@Configuration
	public class MyConfig2 {
	
	    //默认是单实例的
	    @Bean("person")
	    public Person person()
	    {
	        System.out.println("给容器中添加Person....");
	        return new Person("李四",20);
	    }
	}


**测试**

    @Test
    public void test02()
    {

        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig2.class);

        Object person = annotationConfigApplicationContext.getBean("person");
        Object person2 = annotationConfigApplicationContext.getBean("person");

        System.out.println(person.equals(person2));

		/**
			给容器中添加Person....
			true
		**/
    }

---

	 /**
     * ConfigurableBeanFactory#SCOPE_PROTOTYPE
     * ConfigurableBeanFactory#SCOPE_SINGLETON
     * org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST  request
     * org.springframework.web.context.WebApplicationContext#SCOPE_SESSION	 sesssion
     * @return\
     * @Scope:调整作用域
     * prototype：多实例的：ioc容器启动并不会去调用方法创建对象放在容器中。
     * 					每次获取的时候才会调用方法创建对象；
     * singleton：单实例的（默认值）：ioc容器启动会调用方法创建对象放到ioc容器中。
     * 			以后每次获取就是直接从容器（map.get()）中拿，
     * request：同一次请求创建一个实例  (在实际开发中不会使用)
     * session：同一个session创建一个实例 (在实际开发中不会使用)
     */

使用作用域为`prototype`

	@Scope("prototype")
    @Bean("person")
    public Person person()
    {
        System.out.println("给容器中添加Person....");
        return new Person("李四",20);
    }

----

打印结果为:

	给容器中添加Person....
	给容器中添加Person....
	false

## 关于`@Lazy`懒加载 ##

- 懒加载：
	- 单实例bean：默认在容器启动的时候创建对象；
	- 懒加载：容器启动不创建对象。第一次使用(获取)Bean创建对象，并初始化；


单实例Bean默认是容器启动就全部加载

		给容器中添加Person....
		org.springframework.context.annotation.internalConfigurationAnnotationProcessor
		org.springframework.context.annotation.internalAutowiredAnnotationProcessor
		org.springframework.context.annotation.internalCommonAnnotationProcessor
		org.springframework.context.event.internalEventListenerProcessor
		org.springframework.context.event.internalEventListenerFactory
		myConfig2
		person
		true

----
		@Lazy
	    @Bean("person")
	    public Person person()
	    {
	        System.out.println("给容器中添加Person....");
	        return new Person("李四",20);
	    }


打印结果(当使用或者获取Bean时才会进行加载):

		org.springframework.context.annotation.internalConfigurationAnnotationProcessor
		org.springframework.context.annotation.internalAutowiredAnnotationProcessor
		org.springframework.context.annotation.internalCommonAnnotationProcessor
		org.springframework.context.event.internalEventListenerProcessor
		org.springframework.context.event.internalEventListenerFactory
		myConfig2
		person
		给容器中添加Person....
		true


## 关于@Conditional加载条件 ##

	@Conditional({Condition}):按照一定的条件进行判断,满足条件给容器中注册Bean,传入Condition数组,，使用时需自己创建类继承Condition然后重写match方法。

---

    @Bean("zhangsan")
    public Person person02()
    {
        return new Person("张三",10);
    }

    @Bean("lisi")
    public Person person03()
    {
        return new Person("李四",20);
    }

---

    @Test
    public void test03()
    {
        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
        for (String s:
                beanDefinitionNames) {
            System.out.println(s);
        }
        //动态获取环境变量的值
        ConfigurableEnvironment environment = annotationConfigApplicationContext.getEnvironment();
        String property = environment.getProperty("os.name");
        System.out.println(property);
    }


打印结果:


	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	myConfig2
	person
	zhangsan
	lisi
	Windows 7

可看出现在是可以获取所有的Bean容器(`zhangsan`和`lisi`是可以打印出来),同时也可以动态获取得到当前系统的操作环境,因此,现在可以增加一个条件,根据不同的操作系统加载特定的容器`Bean`


继承`Condition`,然后条件过滤

	//判断是否windows系统
	public class WindowCondition implements Condition {
	    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
	
			/**
			 * ConditionContext：判断条件能使用的上下文（环境）
			 * AnnotatedTypeMetadata：注释信息
			 */

	        //1、能获取到ioc使用的beanfactory
	        ConfigurableListableBeanFactory beanFactory = conditionContext.getBeanFactory();
	        //2、获取类加载器
	        ClassLoader classLoader = conditionContext.getClassLoader();
	        //3、获取当前环境信息
	        Environment environment = conditionContext.getEnvironment();
	        //4、获取到bean定义的注册类
	        BeanDefinitionRegistry registry = conditionContext.getRegistry();
	
	        //可以判断容器中的bean注册情况，也可以给容器中注册bean
	        BeanDefinition person = registry.getBeanDefinition("person");

	        Environment e = conditionContext.getEnvironment();
	        if( e.getProperty("os.name").equals("Windows 7"))
	        {
	            return true;
	        }
	        return false;
	    }
	}


---

	//判断是否linux系统
	public class LinuxCondition implements Condition {
	    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetdata) {
	        Environment e = conditionContext.getEnvironment();
	        if( e.getProperty("os.name").equals("linux"))
	        {
	            return true;
	        }
	        return false;
	    }
	}

---

在Config配置类里配置

    @Conditional(WindowCondition.class)
    @Bean("zhangsan")
    public Person person02()
    {
        return new Person("张三",10);
    }

    @Conditional(LinuxCondition.class)
    @Bean("lisi")
    public Person person03()
    {
        return new Person("李四",20);
    }

打印结果,现在`Bean lisi`没有加载进来

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	myConfig2
	person
	zhangsan
	Windows 7

**注意:当@Condition注释在类上时,是对整个类起作用,这个类中配置的所有bean注册才能生效**

## @Import ##

	/**
	 * 给容器中注册组件；
	 * 1）、包扫描+组件标注注解（@Controller/@Service/@Repository/@Component）[自己写的类]
	 * 2）、@Bean[导入的第三方包里面的组件]
	 * 3）、@Import[快速给容器中导入一个组件]
	 * 		1）、@Import(要导入到容器中的组件)；容器中就会自动注册这个组件，id默认是全类名
	 * 		2）、ImportSelector:返回需要导入的组件的全类名数组；
	 * 		3）、ImportBeanDefinitionRegistrar:手动注册bean到容器中
	 * 4）、使用Spring提供的 FactoryBean（工厂Bean）;
	 * 		1）、默认获取到的是工厂bean调用getObject创建的对象
	 * 		2）、要获取工厂Bean本身，我们需要给id前面加一个&
	 * 			&colorFactoryBean
	 */


**测试**

    @Test
    public void test04()
    {
        printBean(annotationConfigApplicationContext);
    }

    private void printBean(AnnotationConfigApplicationContext context)
    {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String s :
        beanDefinitionNames) {
            System.out.println(s);
        }
    }


----

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	myConfig2
	person
	zhangsan

使用@Import导入相关的类

	//@Import导入组件，id默认是组件的全类名
	@Import({Blue.class, Red.class})
	@Configuration
	public class MyConfig2 {
		....
	}

---

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	myConfig2
	com.anno.bean.Blue
	com.anno.bean.Red
	person
	zhangsan
	/**Blue和Red可以加载进来**/

### ImportSelector ###

	//自定义逻辑返回需要导入的组件
	public class MyImportSelector implements ImportSelector {
	    //返回值，就是到导入到容器中的组件全类名
	    //AnnotationMetadata:当前标注@Import注解的类的所有注解信息
	    public String[] selectImports(AnnotationMetadata annotationMetadata) {
	
	        //方法不要返回null值
	        return new String[]{
	            "com.anno.bean.Yellow"
	        };
	    }
	}

---

	@Import({Blue.class, Red.class, MyImportSelector.class})
	@Configuration
	public class MyConfig2 {

		...
	}

注意:过来调试发现当如果反正NULL值,会出现异常,看源码可以发现,用的是字符串数组的长度,因此不能用NULL,必须返回空数组

![](http://120.77.237.175:9080/photos/springanno/01.jpg)


### ImportBeanDefinitionRegistrar ###

	public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	
	    /**
	     * AnnotationMetadata：当前类的注解信息
	     * BeanDefinitionRegistry:BeanDefinition注册类；
	     * 		把所有需要添加到容器中的bean；调用
	     * 		BeanDefinitionRegistry.registerBeanDefinition手工注册进来
	     */
	    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
	        boolean red = registry.containsBeanDefinition("com.anno.bean.Red");
	        boolean blue = registry.containsBeanDefinition("com.anno.bean.Blue");
	        if (red && blue)
	        {
	            //指定Bean定义信息；（Bean的类型，Bean。。。）
	            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Rainbow.class);
	            //注册一个Bean，指定bean名
	            registry.registerBeanDefinition("rainBow",rootBeanDefinition);
	        }
	    }
	}

---

	
	@Import({Blue.class, Red.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
	@Configuration
	public class MyConfig2 {
		....
	}

打印结果

	org.springframework.context.annotation.internalConfigurationAnnotationProcessor
	org.springframework.context.annotation.internalAutowiredAnnotationProcessor
	org.springframework.context.annotation.internalCommonAnnotationProcessor
	org.springframework.context.event.internalEventListenerProcessor
	org.springframework.context.event.internalEventListenerFactory
	myConfig2
	com.anno.bean.Blue
	com.anno.bean.Red
	com.anno.bean.Yellow
	person
	zhangsan
	rainBow
	com.anno.bean.Yellow@5bd03f44


## FactoryBean ##

创建一个工厂`Bean`

	//创建一个Spring定义的FactoryBean
	public class ColorFactoryBean implements FactoryBean<Color> {
	
	    //返回一个Color对象，这个对象会添加到容器中
	    public Color getObject() throws Exception {
	        System.out.println("ColorFactoryBean.....getObject()");
	        return new Color();
	    }
	
	    public Class<?> getObjectType() {
	        return Color.class;
	    }
	
	    //是单例？
	    //true：这个bean是单实例，在容器中保存一份
	    //false：多实例，每次获取都会创建一个新的bean；
	    public boolean isSingleton() {
	        return false;
	    }
	}

在`Myconfig2`配置类里配置工厂`Bean`

    @Bean
    public ColorFactoryBean colorFactoryBean()
    {

        return new ColorFactoryBean();
    }

测试

    @Test
    public void test05() throws Exception {
        printBean(annotationConfigApplicationContext);
        //工厂Bean获取的是调用getObject创建的对象
        Object bean1 = annotationConfigApplicationContext.getBean("colorFactoryBean");
        Object bean2 = annotationConfigApplicationContext.getBean("colorFactoryBean");
        System.out.println(bean1.getClass());

		//单例情况下两个相等,反之多例情况下不等
        System.out.println(bean1.equals(bean2));

		//如果要获取原生的工厂Bean,在前面在&
        Object bean = annotationConfigApplicationContext.getBean("&colorFactoryBean");
        System.out.println(bean.getClass());

		/**
			ColorFactoryBean.....getObject()
			ColorFactoryBean.....getObject()
			class com.anno.bean.Color
			false
			class com.anno.bean.ColorFactoryBean
		**/
    }

# `Bean`的生命周期 #

## Bean的初始化和销毁 `init-method`和`destroy-method` ##

	/**
	 * bean的生命周期：
	 * 		bean创建---初始化----销毁的过程
	 * 容器管理bean的生命周期；
	 * 我们可以自定义初始化和销毁方法；容器在bean进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法
	 * 
	 * 构造（对象创建）
	 * 		单实例：在容器启动的时候创建对象
	 * 		多实例：在每次获取的时候创建对象
	 * 
	 * BeanPostProcessor.postProcessBeforeInitialization
	 * 初始化：
	 * 		对象创建完成，并赋值好，调用初始化方法。。。
	 * BeanPostProcessor.postProcessAfterInitialization
	 * 销毁：
	 * 		单实例：容器关闭的时候
	 * 		多实例：容器不会管理这个bean；容器不会调用销毁方法；
	 * 
	 * 
	 * 1）、指定初始化和销毁方法；
	 * 		通过@Bean指定init-method和destroy-method；
	 * 2）、通过让Bean实现InitializingBean（定义初始化逻辑），
	 * 				DisposableBean（定义销毁逻辑）;
	 * 3）、可以使用JSR250；
	 * 		@PostConstruct：在bean创建完成并且属性赋值完成；来执行初始化方法
	 * 		@PreDestroy：在容器销毁bean之前通知我们进行清理工作
	 * 4）、BeanPostProcessor【interface】：bean的后置处理器；
	 * 		在bean初始化前后进行一些处理工作；
	 * 		postProcessBeforeInitialization:在初始化之前工作
	 * 		postProcessAfterInitialization:在初始化之后工作
	 * 
	 * Spring底层对 BeanPostProcessor 的使用；
	 * 		bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async,xxx BeanPostProcessor;
	 * 
	 *
	 */


**创建容器`Bean`**


	public class Car {

	    public Car() {
	        System.out.println("car construct .....");
	    }
	
	    public void init()
	    {
	        System.out.println("car init.....");
	    }
	
	    public void destroy()
	    {
	        System.out.println("car destory.....");
	    }
	}

**创建配置类**

	@ComponentScan("com.anno.bean")
	@Configuration
	public class MyConfigLifeCycle {
	
	    @Bean(initMethod = "init",destroyMethod = "destroy")
	    public Car car()
	    {
	        return new Car();
	    }
	}


测试

	 @Test
    public void test01()
    {
        //1、创建ioc容器
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfigLifeCycle.class);
        //System.out.println(car);

        //关闭容器
        annotationConfigApplicationContext.close();

		/**
			car construct .....
			car init.....
			car destory.....
		**/
    }

**注意:这里获取MyConfigLifeCycle配置类时并没有获取Car的容器Bean都会进行容器的初始化,构造和销毁,在多实例的情况下,不会理这个容器的初始化,构造和销毁,只有在对这个Bean进行赋值时才会进行初始化,多实例下是不会进行进行销毁**

## `InitializingBean`和 `DisposableBean`##

定义`Bean`

	@Component
	public class Cat implements InitializingBean, DisposableBean {
	    public Cat() {
	        System.out.println("cat .... construct ....");
	    }
	
	    public void destroy() throws Exception {
	        System.out.println("cat .... destroy");
	    }
	
	    public void afterPropertiesSet() throws Exception {
	        System.out.println("cat .... afterPropertiesSet");
	    }
	
	}


测试结果

	cat .... construct ....
	cat .... afterPropertiesSet
	car construct .....
	car init.....
	cat .... destroy


## `@PostConstruct`和`@PreDestroy` ##

	@Component
	public class Dog implements ApplicationContextAware {
	
	    public Dog() {
	        System.out.println("dog .... construct");
	    }
	
	    //对象创建并赋值之后调用
	    @PostConstruct
	    public void init()
	    {
	        System.out.println("dog....@PostConstruct");
	    }
	
	    //容器移除对象之前
	    @PreDestroy
	    public void destroy()
	    {
	        System.out.println("dog.... @PreDestroy");
	    }
	
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	
	    }
	}

测试结果

	cat .... construct ....
	cat .... afterPropertiesSet
	dog .... construct
	dog....@PostConstruct
	car construct .....
	car init.....
	dog.... @PreDestroy
	cat .... destroy


## `BeanPostProcessor` ##

	/*
	 * 后置处理器：初始化前后进行处理工作
	 * 将后置处理器加入到容器中
	* */
	@Component
	public class MyBeanPostProcessor implements BeanPostProcessor {
	
	    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
	        System.out.println("postProcessBeforeInitialization...."+bean+"======>"+beanName);
	        return bean;
	    }
	
	    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	        System.out.println("postProcessAfterInitialization...."+bean+"======>"+beanName);
	        return bean;
	    }
	}

测试结果

	/**
	*		执行顺序:
	*		postProcessBeforeInitialization：在初始化之前进行一些工作
	*       对象初始化
	*       postProcessAfterInitialization：在初始化之后进行一些工作
	**/
	postProcessBeforeInitialization....com.anno.config.MyConfigLifeCycle$$EnhancerBySpringCGLIB$$c52377ad@1e802ef9======>myConfigLifeCycle
	postProcessAfterInitialization....com.anno.config.MyConfigLifeCycle$$EnhancerBySpringCGLIB$$c52377ad@1e802ef9======>myConfigLifeCycle
	cat .... construct ....
	postProcessBeforeInitialization....com.anno.bean.Cat@670002======>cat
	cat .... afterPropertiesSet
	postProcessAfterInitialization....com.anno.bean.Cat@670002======>cat
	dog .... construct
	postProcessBeforeInitialization....com.anno.bean.Dog@96def03======>dog
	dog....@PostConstruct
	postProcessAfterInitialization....com.anno.bean.Dog@96def03======>dog
	car construct .....
	postProcessBeforeInitialization....com.anno.bean.Car@48ae9b55======>car
	car init.....
	postProcessAfterInitialization....com.anno.bean.Car@48ae9b55======>car
	dog.... @PreDestroy
	cat .... destroy


## BeanPostProcessor原理 ##

以`Car`的执行结果为例:

	car construct .....
	postProcessBeforeInitialization....com.anno.bean.Car@3eb7fc54======>car
	car init.....
	postProcessAfterInitialization....com.anno.bean.Car@3eb7fc54======>car
	car destory.....

1. 首先执行`bean`的构造方法,
2. `BeanPostProcessor`的`postProcessBeforeInitialization`方法
3. `@Bean`注解的`initMethod`方法
5. `BeanPostProcessor`的`postProcessAfterInitialization`方法
7. `@Bean`注解的`destroyMethod`方法

		/*
		 * 
		 * 遍历得到容器中所有的BeanPostProcessor；挨个执行beforeInitialization，
		 * 一但返回null，跳出for循环，不会执行后面的BeanPostProcessor.postProcessorsBeforeInitialization
		 * 
		 * BeanPostProcessor原理
		 * populateBean(beanName, mbd, instanceWrapper);给bean进行属性赋值
		 * initializeBean
		 * {
		 * applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		 * invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化
		 * applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		 *}
		 *
		 */


接下来我们再来看看`spring`底层的实现，首先进入程序的启动类`AnnotationConfigApplicationContext`方法如下：

![](http://120.77.237.175:9080/photos/springanno/02.jpg)

里边有两个方法一个是`register`注册对应的`java`配置类和另一个是`refresh`方法，我们重点来看这个`refresh`方法如下：

	public void refresh() throws BeansException, IllegalStateException {
	        synchronized (this.startupShutdownMonitor) {
	            // Prepare this context for refreshing.
	            prepareRefresh();
	            // Tell the subclass to refresh the internal bean factory.
	            ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
	            // Prepare the bean factory for use in this context.
	            prepareBeanFactory(beanFactory);
	            try {
	                // 省略大部分代码
	                // 实例化所有的不是延迟加载（延迟加载的只有在使用的时候才会实例化）的bean实例
	                finishBeanFactoryInitialization(beanFactory);
	                // Last step: publish corresponding event.
	                finishRefresh();
	            }catch (BeansException ex) {
	                // 省略部分代码
	            }finally {
	                resetCommonCaches();
	            }
	        }
	}

接下来我们重点来看下`finishBeanFactoryInitialization`实例化`bean`的方法，进去之后我们发现最后有一个`preInstantiateSingletons`方法如下：

	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
	        // 省略大部分代码
	        // Instantiate all remaining (non-lazy-init) singletons.
	        beanFactory.preInstantiateSingletons();
	}

继续查看`preInstantiateSingletons`对应实现如下：

	@Override
    public void preInstantiateSingletons() throws BeansException {
	        // Iterate over a copy to allow for init methods which in turn register new bean definitions.
	        // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
	        List<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
	        // 循环所有的bean实例化
	        for (String beanName : beanNames) {
	            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
	            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
	                if (isFactoryBean(beanName)) {
	                    final FactoryBean<?> factory = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
	                    boolean isEagerInit;
	                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
	                        isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
	                            @Override
	                            public Boolean run() {
	                                return ((SmartFactoryBean<?>) factory).isEagerInit();
	                            }
	                        }, getAccessControlContext());
	                    }
	                    else {
	                        isEagerInit = (factory instanceof SmartFactoryBean &&
	                                ((SmartFactoryBean<?>) factory).isEagerInit());
	                    }
	                    if (isEagerInit) {
	                        // 获取bean方法
	                        getBean(beanName);
	                    }
	                }
	                else {
	                    getBean(beanName);
	                }
	            }
	        }
	        // 省略部分代码
	}

我们发现里边的关键方法`getBean`如下：

![](http://120.77.237.175:9080/photos/springanno/03.png)

继续跟进去如下：

	protected <T> T doGetBean(
            final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly)
            throws BeansException {
        final String beanName = transformedBeanName(name);
        Object bean;
        // 检查缓存中是否已经存在了bean实例.
        Object sharedInstance = getSingleton(beanName);
        if (sharedInstance != null && args == null) {
            if (logger.isDebugEnabled()) {
                if (isSingletonCurrentlyInCreation(beanName)) {
                    logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                            "' that is not fully initialized yet - a consequence of a circular reference");
                }
                else {
                    logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
                }
            }
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
        }else {
            // 省略部分代码。。。。。
            try {
                // 省略部分代码。。。。。
                // 判断bean是否配置的是单实例
                if (mbd.isSingleton()) {
                    sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
                        @Override
                        public Object getObject() throws BeansException {
                            try {
                                return createBean(beanName, mbd, args);
                            }
                            catch (BeansException ex) {
                                destroySingleton(beanName);
                                throw ex;
                            }
                        }
                    });
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                }// bean配置的是多实例
                else if (mbd.isPrototype()) {
                    // It's a prototype -> create a new instance.
                    Object prototypeInstance = null;
                    try {
                        beforePrototypeCreation(beanName);
                        prototypeInstance = createBean(beanName, mbd, args);
                    }
                    finally {
                        afterPrototypeCreation(beanName);
                    }
                    bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
                }
                else {// 既不是单实例也不是多实例的逻辑
                    // 省略部分代码。。。。。
                }
            }
            catch (BeansException ex) {
                cleanupAfterBeanCreationFailure(beanName);
                throw ex;
            }
        }
        // 省略部分代码。。。。。
        return (T) bean;
    }


接下来重点看一下其中创建`bean`的方法`createBean`如下：

	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args)
            throws BeanCreationException {
	        // 省略部分代码
	        // Initialize the bean instance.
	        Object exposedObject = bean;
	        try {
	            populateBean(beanName, mbd, instanceWrapper);
	            if (exposedObject != null) {
	                exposedObject = initializeBean(beanName, exposedObject, mbd);
	            }
	        }
	        catch (Throwable ex) {
	            if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
	                throw (BeanCreationException) ex;
	            }
	            else {
	                throw new BeanCreationException(
	                        mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
	            }
	        }
	        // 省略部分代码......
	        // Register bean as disposable.
	        // 注意这个地方  下面讲销毁的时候说讲到
	        try {
	            registerDisposableBeanIfNecessary(beanName, bean, mbd);
	        }
	        catch (BeanDefinitionValidationException ex) {
	            throw new BeanCreationException(
	                    mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
	        }
	        return exposedObject;
	}


可以发现其中有一个`initializeBean`方法如下

	protected Object initializeBean(final String beanName, final Object bean, RootBeanDefinition mbd) {
	        // 省略部分代码。。。。
	        Object wrappedBean = bean;
	        if (mbd == null || !mbd.isSynthetic()) {
	            // 重点来了BeanPostProcessor的postProcessBeforeInitialization方法执行的地方
	            // 这也是为什么他执行所有的初始化之前的原因了
	            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
	        }
	        try {
	            // 初始化bean
	            invokeInitMethods(beanName, wrappedBean, mbd);
	        }
	        catch (Throwable ex) {
	            throw new BeanCreationException(
	                    (mbd != null ? mbd.getResourceDescription() : null),
	                    beanName, "Invocation of init method failed", ex);
	        }
	        if (mbd == null || !mbd.isSynthetic()) {
	            // BeanPostProcessor的PostProcessorsAfterInitialization方法执行的地方
	            // 初始化完成之后执行BeanPostProcessor的postProcessorsAfterInitialization
	            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
	        }
	        return wrappedBean;
	}


到这`BeanPostProcessor`的实现已经很清晰了吧，`BeanPostProcessor`的`postProcessBeforeInitialization`（方法位置2）和`BeanPostProcessor`的`postProcessAfterInitialization`（方法位置4）的执行位置我们搞清楚了，那上面的位置4又是怎么执行的呢，让我们继续到`invokeInitMethods`里边看看如下：

	protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd)
            throws Throwable {
	        boolean isInitializingBean = (bean instanceof InitializingBean);
	        if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
	            if (logger.isDebugEnabled()) {
	                logger.debug("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
	            }
	            if (System.getSecurityManager() != null) {
	                try {
	                    AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
	                        @Override
	                        public Object run() throws Exception {
	                            ((InitializingBean) bean).afterPropertiesSet();
	                            return null;
	                        }
	                    }, getAccessControlContext());
	                }
	                catch (PrivilegedActionException pae) {
	                    throw pae.getException();
	                }
	            }
	            else {
	              .....
	            }
	        }
	        if (mbd != null) {
	            // 位置4的 @Bean注解的initMethod方法
	            String initMethodName = mbd.getInitMethodName();
	            if (initMethodName != null && !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
	                    !mbd.isExternallyManagedInitMethod(initMethodName)) {
	                invokeCustomInitMethod(beanName, bean, mbd);
	            }
	        }
	}

点击进入`initializeBean.applyBeanPostProcessorsBeforeInitialization`方法，获取到`List<BeanPostProcessor>`，循环执行初始化前操作`postProcessBeforeInitialization`

![](http://120.77.237.175:9080/photos/springanno/04.jpg)

而之前创建`Bean`方法`doCreateBean`中初始化`initializeBean`之前，可以看到调用`populateBean`
，这个方法就是给`bean`赋值的，所以说在创建`bean`并且属性赋值完成后，进而执行初始化方法

![](http://120.77.237.175:9080/photos/springanno/05.jpg)

![](http://120.77.237.175:9080/photos/springanno/06.jpg)

**总结**：

**`BeanPostProcessor`原理**

`populateBean(beanName, mbd, instanceWrapper)`;给bean进行属性赋值

	initializeBean
	{
		applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化
		applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
	}

`applyBeanPostProcessorsBeforeInitialization`及`applyBeanPostProcessorsAfterInitialization`中
遍历得到容器中所有的`BeanPostProcessor`；挨个执行`beforeInitialization`，
一但返回`nul`l，跳出`for`循环，不会执行后面的`BeanPostProcessor.postProcessorsBeforeInitialization`


##  Spring底层对BeanPostProcessor的使用 ##


![](http://120.77.237.175:9080/photos/springanno/07.jpg)

### ApplicationContextAwareProcessor实现分析 ###

此类帮我们组建`IOC`容器,跟进`ApplicationContextAwareProcessor`我们发现, 这个后置处理器其实就是判断我们的`bean`有没有实现`ApplicationContextAware` 接口,并处理相应的逻辑,其实所有的后置处理器原理均如此.

那么怎么组建呢? 只需要实现 `ApplicationContextAware` 接口

![](http://120.77.237.175:9080/photos/springanno/08.jpg)

分析一下ApplicationContextAwareProcessor类的方法

![](http://120.77.237.175:9080/photos/springanno/09.jpg)

- 在创建`Dog`对象,还没初始化之前, 先判断是不是实现了`ApplicationContextAware`接口,如果是的话就调用`invokeAwareInterfaces`方法, 并给里面注入值;
- 进入`invokeAwareInterfaces()`方法,判断是哪个`aware`, 如果是`ApplicationContextAware`, 就将当前的bean转成`ApplicationContextAware`类型, 调用`setApplicationContext()`, 把`IOC`容器注入到`Dog`里去;
- 用`debug`调用; 测试用例打断点测试

![](http://120.77.237.175:9080/photos/springanno/10.jpg)

可见在调用`setApplicationContext`前已经调用`invokeAwareInterfaces(bean)`里判断当前`Bean`是否继承了`ApplicationContextAware`

![](http://120.77.237.175:9080/photos/springanno/11.jpg)

### BeanValidationPostProcess分析:数据校验 ###

![](http://120.77.237.175:9080/photos/springanno/12.jpg)

了解即可,处理器的原理和其它处理器一致.

**当对象创建完,给bean赋值后,在WEB用得特别多;把页面提交的值进行校验**

![](http://120.77.237.175:9080/photos/springanno/13.jpg)

### InitDestroyAnnotationBeanPostProcessor ###

此处理器用来处理`@PostConstruct`, `@PreDestroy`, 怎么知道这两注解是前后开始调用的呢, 就是 `InitDestroyAnnotationBeanPostProcessor`这个处理的

![](http://120.77.237.175:9080/photos/springanno/14.jpg)

以`@PostConstruct`为例, 为什么声明这个注解后就能找到初始化init方法呢?

![](http://120.77.237.175:9080/photos/springanno/15.jpg)

![](http://120.77.237.175:9080/photos/springanno/16.jpg)

**总结: Spring底层对BeanPostProcessor的使用, 包括bean的赋值, 注入其它组件, 生命周期注解功能,@Async, 等等**

# 属性赋值@Value和@PropertySource #

- 在配置文件里配置:

	   	<context:property-placeholder location="classpath:person.properties"/>
	    <bean id="person" class="com.anno.bean.Person">
	        <property name="name" value="张三"/>
	        <property name="age" value="10"/>
	    </bean>

- 使用注解方式配置:

	**Bean**
	
		public class Person {
	
		    //使用@Value赋值；
		    //1、基本数值
		    //2、可以写SpEL； #{}
		    //3、可以写${}；取出配置文件【properties】中的值（在运行环境变量里面的值）
		
		    @Value("李四")
		    private String name;
		    @Value("#{20-2}")
	
			@Value("${person.nickName}")
			private String nickName;
	
			.....
		}
	
	**配置类**
	
		//使用@PropertySource读取外部配置文件中的k/v保存到运行的环境变量中;加载完外部的配置文件以后使用${}取出配置文件的值
		@PropertySource(value = {"classpath:/person.properties"})
		@Configuration
		public class MainConfigOfPropertyValues {
		
		    @Bean
		    public Person person()
		    {
		        return new Person();
		    }
		
		}
	
	**测试**
	
		public class IOCTest_PropertyValue {
	
		    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);
		
		    @Test
		    public void test01()
		    {
		        Object person = annotationConfigApplicationContext.getBean("person");
		        System.out.println(person);
				/**
				Person{name='李四', age=18}
				**/
		
				//可以获取运行的环境变量指定的值
				ConfigurableEnvironment environment = annotationConfigApplicationContext.getEnvironment();
		        String property = environment.getProperty("person.nickName");
		        System.out.println(property);	//张三
	
		        //关闭容器
		        annotationConfigApplicationContext.close();
		    }
		}

# 自动注入 #

## @Autowired&@Qualifier&@Primary ##

### @Autowired ###

之前使用比较多的自动装配是`@Autowired`

**Service**

	@Service
	public class BookService {
	
	    @Autowired
	    private BookDao bookDao;
	
	    public void print()
	    {
	        System.out.println(bookDao);
	    }
	
	    @Override
	    public String toString() {
	        return "BookService{" +
	                "bookDao=" + bookDao +
	                '}';
	    }
	}

**Dao**

	@Repository
	public class BookDao {
	
	    private Integer label = 1;
	
	    public Integer getLabel() {
	        return label;
	    }
	
	    public void setLabel(Integer label) {
	        this.label = label;
	    }
	
	    @Override
	    public String toString() {
	        return "BookDao{" +
	                "label=" + label +
	                '}';
	    }
	}

**配置类**

	@Configuration
	@ComponentScan({"com.anno.service","com.anno.controller","com.anno.dao"})
	public class MainConifgOfAutowired {
		.....
	}

**测试**

	public class IOCTest_Autowired {
	    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConifgOfAutowired.class);
	
	    @Test
	    public void test01()
	    {
	        BookService bookService = annotationConfigApplicationContext.getBean(BookService.class);
	        System.out.println(bookService);
	
            BookDao bookDao = (BookDao) annotationConfigApplicationContext.getBean("bookDao");
        	System.out.println(bookDao);
	    }
	
	}

打印结果:

	BookService{bookDao=BookDao{label=1}}
	BookDao{label=1}

可以看到现在获取到的`BookDao`容器是同一个

在配置类里添加

	@Bean("bookDao2")
    public BookDao bookDao()
    {
        BookDao bookDao = new BookDao();
        bookDao.setLabel(2);
        return bookDao;
    }

打印结果:

	BookService{bookDao=BookDao{label=1}}
	BookDao{label=1}

**如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找**

调整测试里获取`BookDao2`

	 BookDao bookDao = (BookDao) annotationConfigApplicationContext.getBean("bookDao2");
     System.out.println(bookDao);

打印结果:

	BookService{bookDao=BookDao{label=1}}
	BookDao{label=2}

### @Qualifier ###

可以看到`BookService`里注入的`BookDao`类还是1的类型,使用`@Qualifier`指定需要装配的组件的id，而不是使用属性名

![](http://120.77.237.175:9080/photos/springanno/17.jpg)
	
打印结果:

	BookService{bookDao=BookDao{label=2}}
	BookDao{label=2}

**注意:自动装配默认一定要将属性赋值好，没有就会报错；可以使用`@Autowired(required=false)`**

![](http://120.77.237.175:9080/photos/springanno/18.jpg)

打印结果:
	
	BookService{bookDao=null}

### @Primary ###

让`Spring`进行自动装配的时候，默认使用首选的`bean`；也可以继续使用`@Qualifier`指定需要装配的`bean`的名字

![](http://120.77.237.175:9080/photos/springanno/19.jpg)

把@Qualifier注释掉

![](http://120.77.237.175:9080/photos/springanno/20.jpg)

打印结果:

	BookService{bookDao=BookDao{label=2}}

同时也可以使用`@Qualifier`指定注入的组件

![](http://120.77.237.175:9080/photos/springanno/21.jpg)

**当`@Qualifier`找不到指定的组件时,会优先把`@Primary`注入进来**

## `@Resource(JSR250)`和`@Inject(JSR330)` ##

Spring还支持使用@Resource(JSR250)和@Inject(JSR330)[java规范的注解]

### `@Resource` ###

![](http://120.77.237.175:9080/photos/springanno/22.jpg)

- 可以和`@Autowired`一样实现自动装配功能；默认是按照组件名称进行装配的；
- 没有能支持`@Primary`功能没有支持`@Autowired（reqiured=false）`;

要指定加载的`Bean`

![](http://120.77.237.175:9080/photos/springanno/23.jpg)

### `@Inject` ###

需要导入`javax.inject`的包，和`Autowired`的功能一样。没有`required=false`的功能；

**pom**

        <dependency>
            <groupId>javax.inject</groupId>
            <artifactId>javax.inject</artifactId>
            <version>1</version>
        </dependency>

![](http://120.77.237.175:9080/photos/springanno/24.jpg)

**`@Autowired`:`Spring`定义的； `@Resource`、`@Inject`都是`java`规范**

注意:上面的所有注入都是通过`AutowiredAnnotationBeanPostProcessor`解析完成自动装配功能

### `@Autowire`标注位置 ###

`@Autowired`:构造器，参数，方法，属性；都是从容器中获取参数组件的值

1. 放在属性位置

	**Bean**
	
		@Component
		public class Boss {
		
		    @Autowired
		    private Car car;
		
		    public Boss(Car car ) {
		        this.car =car;
		    }
		
		    public Car getCar() {
		        return car;
		    }
		
		    public void setCar(Car car) {
		        this.car = car;
		    }
		
		    @Override
		    public String toString() {
		        return "Boss{" +
		                "car=" + car +
		                '}';
		    }
		}

		@Component
		public class Car {
			....
		}

	打印结果:

		Boss boss = annotationConfigApplicationContext.getBean(Boss.class);
        System.out.println(boss);		//Boss{car=com.anno.bean.Car@57c758ac}
        Car car = annotationConfigApplicationContext.getBean(Car.class);
        System.out.println(car);		//com.anno.bean.Car@57c758ac

	可以看到加载进来的容器是一样的

2. 放在方法上

	![](http://120.77.237.175:9080/photos/springanno/25.jpg)


2. [标在构造器上]：如果组件只有一个有参构造器，这个有参构造器的`@Autowired`可以省略，参数位置的组件还是可以自动从容器中获取

	![](http://120.77.237.175:9080/photos/springanno/26.jpg)
	
3. [标注在方法位置]：`@Bean`+方法参数；参数从容器中获取;默认不写`@Autowired`效果是一样的；都能自动装配

	![](http://120.77.237.175:9080/photos/springanno/27.jpg)

## 自定义组件想要使用Spring容器底层的一些组件 ##

- 自定义组件实现`xxxAware`；在创建对象的时候，会调用接口规定的方法注入相关组件；`Aware`；
- 把`Spring`底层一些组件注入到自定义的`Bean`中；
- `xxxAware`：功能使用`xxxProcessor`；`ApplicationContextAware`==》`ApplicationContextAwareProcessor`；

![](http://120.77.237.175:9080/photos/springanno/28.jpg)



	@Component
	public class Red implements ApplicationContextAware, BeanNameAware, EmbeddedValueResolverAware {
	
	    private ApplicationContext applicationContext;
	
	    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
	        this.applicationContext = applicationContext;
	        System.out.println("传入的IOC: "+applicationContext);
	    }
	
	    public void setBeanName(String name) {
	        System.out.println("当前Bean的名字: "+name);
	    }
	
	    public void setEmbeddedValueResolver(StringValueResolver resolver) {
	        String resolveStringValue = resolver.resolveStringValue("你好 ${os.name} 我是 #{20*18}");
	        System.out.println(resolveStringValue);
	    }
	}

打印结果:

	当前Bean的名字: red
	你好 Windows 10 我是 360
	传入的IOC: org.springframework.context.annotation.AnnotationConfigApplicationContext@5cbc508c, started on Sun Jun 28 17:35:37 CST 2020

ApplicationContextAware的加载原理已在上面已分析过了,详细请看**3.6.1**

# @Profile #

`Spring`为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能；

`@Profile`：指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件

1. 加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境
2. 写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效
3. 没有标注环境标识的bean在任何环境下都是加载的；

**配置类**

		@PropertySource("classpath:/dbconfig.properties")
		@Configuration
		public class MainConfigOfProfile implements EmbeddedValueResolverAware {
		
		    @Value("${db.user}")
		    private String user;
		
		    private StringValueResolver valueResolver;
		
		    private String driverClass;
		
		    @Profile("test")
		    @Bean
		    public DataSource dataSource01(@Value("${db.password}") String password) throws Exception
		    {
		        ComboPooledDataSource dataSource = new ComboPooledDataSource();
		        dataSource.setUser(user);
		        dataSource.setPassword(password);
		        dataSource.setJdbcUrl("jdbc:mysql://120.77.237.175/test");
		        dataSource.setDriverClass(driverClass);
		        return dataSource;
		    }
		
		    @Profile("dev")
		    @Bean
		    public DataSource dataSource02(@Value("${db.password}") String password) throws Exception
		    {
		        ComboPooledDataSource dataSource = new ComboPooledDataSource();
		        dataSource.setUser(user);
		        dataSource.setPassword(password);
		        dataSource.setJdbcUrl("jdbc:mysql://120.77.237.175/test");
		        dataSource.setDriverClass(driverClass);
		        return dataSource;
		    }
		
		    @Profile("pro")
		    @Bean
		    public DataSource dataSource03(@Value("${db.password}") String password) throws Exception
		    {
		        ComboPooledDataSource dataSource = new ComboPooledDataSource();
		        dataSource.setUser(user);
		        dataSource.setPassword(password);
		        dataSource.setJdbcUrl("jdbc:mysql://120.77.237.175/test");
		        dataSource.setDriverClass(driverClass);
		        return dataSource;
		    }
		
		    public void setEmbeddedValueResolver(StringValueResolver resolver) {
		       valueResolver = resolver;
		        driverClass = valueResolver.resolveStringValue("${db.driverClass}");
		    }
		}

配置

	db.user=root
	db.password=123456
	db.driverClass=com.mysql.jdbc.Driver

测试

	public class IOCTest_Profile {
	    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
	
	    @Test
	    public void test01()
	    {
	        String[] beanNamesForType = annotationConfigApplicationContext.getBeanNamesForType(DataSource.class);
	
	        for (String s:beanNamesForType)
	        {
	            System.out.println(s);
	        }
	
	
	        annotationConfigApplicationContext.close();
	    }
	
	}

**打印结果为空,因为配置了@Profile,现在没有任何适用的环境**

1. 使用命令行动态参数: 在虚拟机参数位置加载 -Dspring.profiles.active=test
	![](http://120.77.237.175:9080/photos/springanno/29.jpg)

2. 代码的方式激活某种环境

	通过分析源码

	![](http://120.77.237.175:9080/photos/springanno/30.png)

	测试

		public class IOCTest_Profile {
		    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
		
		    @Test
		    public void test01()
		    {
		        //1、创建一个applicationContext
		        //2、设置需要激活的环境
		        annotationConfigApplicationContext.getEnvironment().setActiveProfiles("dev");
		        //3、注册主配置类
		        annotationConfigApplicationContext.register(MainConfigOfProfile.class);
		        //4、启动刷新容器
		        annotationConfigApplicationContext.refresh();
		
		        String[] beanNamesForType = annotationConfigApplicationContext.getBeanNamesForType(DataSource.class);
		
		        for (String s:beanNamesForType)
		        {
		            System.out.println(s);		//dataSource02
		        }
			
		        annotationConfigApplicationContext.close();
		    }
		
		}


当@Profile加在整个配置类上时,只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效

![](http://120.77.237.175:9080/photos/springanno/31.png)


# Aop动态代理 #

## 实现流程 ##

指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式；

- 将业务逻辑组件和切面类都加入到容器中；告诉`Spring`哪个是切面类（`@Aspect`）
- 在切面类上的每一个通知方法上标注通知注解，告诉`Spring`何时何地运行（切入点表达式）
- 开启基于注解的`aop`模式；`@EnableAspectJAutoProx`

流程:

1. **导入aop模块**

	 <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>5.2.6.RELEASE</version>
      </dependency>

2. **定义一个业务逻辑类（`MathCalculator`）；在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束、方法出现异常，xxx）**

		public class MathCalculator {
	
		    public int div(int i,int j)
		    {
		        System.out.println("MathCalculator.......div......");
		        return i/j;
		    }
		}

3. 定义一个日志切面类（`LogAspects`）：切面类里面的方法需要动态感知`MathCalculator.div`运行到哪里然后执行；

	通知方法：

	- 前置通知(`@Before`)：`logStart`：在目标方法(`div`)运行之前运行
	- 后置通知(`@After`)：`logEnd`：在目标方法(`div`)运行结束之后运行（无论方法正常结束还是异常结束）
	- 返回通知(`@AfterReturning`)：`logReturn`：在目标方法(`div`)正常返回之后运行
	- 异常通知(`@AfterThrowing`)：`logException`：在目标方法(`div`)出现异常以后运行
	- 环绕通知(`@Around`)：动态代理，手动推进目标方法运行（`joinPoint.procced()`）



4. 给切面类的目标方法标注何时何地运行（通知注解）；
5. 将切面类和业务逻辑类（目标方法所在类）都加入到容器中;
6. 必须告诉`Spring`哪个类是切面类(给切面类上加一个注解：`@Aspect`)
7. 给配置类中加 `@EnableAspectJAutoProxy` 【开启基于注解的`aop`模式】,在`Spring`中很多的 `@EnableXXX`;

	相当于以前在XML中配置

		<!-- 开启基于注解版的切面功能 -->
		<aop:aspectj-autoproxy></aop:aspectj-autoproxy>


**切面类**

		/**
		 * 
		 * @Aspect： 告诉Spring当前类是一个切面类
		 *
		 */
		@Aspect
		public class LogOfAspects {
		
		    //抽取公共的切入点表达式
		    //1、本类引用
		    //2、其他的切面引用
		    @Pointcut("execution(public int com.anno.aop.MathCalculator.*(..))")
		    public void pointCut()
		    {};
		
		    @Before("pointCut()")
		    public void logStart(JoinPoint joinPoint)
		    {
		        Object[] args = joinPoint.getArgs();
		        System.out.println(joinPoint.getSignature().getName()+"运行.......@Before:参数列表是"+ Arrays.asList(args));
		    }
		
		    @After("com.anno.aop.LogOfAspects.pointCut()")
		    public void logEnd(JoinPoint joinPoint)
		    {
		        System.out.println(joinPoint.getSignature().getName()+" 结束........@After");
		    }
		
		    //注意:JoinPoint一定要出现在参数表的第一位
		    @AfterReturning(value = "pointCut()",returning = "result")
		    public void logReturn(JoinPoint joinPoint,Object result)
		    {
		        System.out.println(joinPoint.getSignature().getName()+" 正常返回.......... @AfterReturning:运行结果:"+result);
		    }
		
		    @AfterThrowing(value = "pointCut()",throwing = "exception")
		    public void logException(JoinPoint joinPoint,Exception exception)
		    {
		        System.out.println(joinPoint.getSignature().getName()+" 异常.............异常信息: "+exception);
		    }
		
		}

**配置类**

	@EnableAspectJAutoProxy	
	@Configuration
	public class MainConfigOfAOP {
	
	    @Bean
	    public MathCalculator mathCalculator()
	    {
	        return new MathCalculator();
	    }
	
	    @Bean
	    public LogOfAspects logOfAspects()
	    {
	        return new LogOfAspects();
	    }
	}



**测试**

	public class IOCTest_Aop {
	    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
	
	    @Test
	    public void test01()
	    {
	        MathCalculator mathCalculator = annotationConfigApplicationContext.getBean(MathCalculator.class);
	        mathCalculator.div(1,1);
	        //System.out.println(mathCalculator);
	
	        annotationConfigApplicationContext.close();
	    }
	
	}

**打印结果**

	div运行.......@Before:参数列表是[1, 1]
	MathCalculator.......div......
	div 结束........@After
	div 正常返回.......... @AfterReturning:运行结果:1

## @EnableAspectJAutoProxy原理 ##

看给容器中注册了什么组件，这个组件什么时候工作，这个组件的功能是什么？

基于注解的方式实现AOP需要在配置类中添加注解@EnableAspectJAutoProxy。我们就先从这个注解看一下Spring实现AOP的过程：

![](http://120.77.237.175:9080/photos/springanno/32.jpg)

**发现`EnableAspectJAutoProxy`它就是给容器中注册了一个`AspectJAutoProxyRegistrar`,而`AspectJAutoProxyRegistrar`它实现了
`ImportBeanDefinitionRegistrar`接口,会向会编程式的向`IOC`容器中注册组件.**

![](http://120.77.237.175:9080/photos/springanno/33.jpg)

从上图打断点进入我看可以看到


![](http://120.77.237.175:9080/photos/springanno/34.jpg)