# Spirng Annotation #

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
BeanPostProcessor原理
populateBean(beanName, mbd, instanceWrapper);给bean进行属性赋值
initializeBean
{
applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化
applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}

applyBeanPostProcessorsBeforeInitialization及applyBeanPostProcessorsAfterInitialization中
遍历得到容器中所有的BeanPostProcessor；挨个执行beforeInitialization，
一但返回null，跳出for循环，不会执行后面的BeanPostProcessor.postProcessorsBeforeInitialization
