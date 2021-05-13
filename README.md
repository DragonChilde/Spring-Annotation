

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

```java
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
```

---

```java
@Import({Blue.class, Red.class, MyImportSelector.class})
@Configuration
public class MyConfig2 {

	...
}
```

注意:过来调试发现当如果反正NULL值,会出现异常,看源码可以发现,用的是字符串数组的长度,因此不能用NULL,必须返回空数组

![](http://120.77.237.175:9080/photos/springanno/01.jpg)


### ImportBeanDefinitionRegistrar ###

```java
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
```

---


```java
@Import({Blue.class, Red.class, MyImportSelector.class, MyImportBeanDefinitionRegistrar.class})
@Configuration
public class MyConfig2 {
	....
}
```

打印结果

```java
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
```


## FactoryBean ##

创建一个工厂`Bean`

```java
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
```

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

```java
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
```


可以发现其中有一个`initializeBean`方法如下

```java
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
```


到这`BeanPostProcessor`的实现已经很清晰了吧，`BeanPostProcessor`的`postProcessBeforeInitialization`（方法位置2）和`BeanPostProcessor`的`postProcessAfterInitialization`（方法位置4）的执行位置我们搞清楚了，那上面的位置4又是怎么执行的呢，让我们继续到`invokeInitMethods`里边看看如下：

```java
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
```

点击进入`initializeBean.applyBeanPostProcessorsBeforeInitialization`方法，获取到`List<BeanPostProcessor>`，循环执行初始化前操作`postProcessBeforeInitialization`

![](http://120.77.237.175:9080/photos/springanno/04.jpg)

而之前创建`Bean`方法`doCreateBean`中初始化`initializeBean`之前，可以看到调用`populateBean`
，这个方法就是给`bean`赋值的，所以说在创建`bean`并且属性赋值完成后，进而执行初始化方法

![](http://120.77.237.175:9080/photos/springanno/05.jpg)

![](http://120.77.237.175:9080/photos/springanno/06.jpg)

**总结**：

**`BeanPostProcessor`原理**

`populateBean(beanName, mbd, instanceWrapper)`;给bean进行属性赋值

```java
initializeBean
{
	applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
	invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化
	applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

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

     ```java
     <context:property-placeholder location="classpath:person.properties"/>
     <bean id="person" class="com.anno.bean.Person">
         <property name="name" value="张三"/>
         <property name="age" value="10"/>
     </bean>
     ```

- 使用注解方式配置:

	**Bean**
	
	```java
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
	```
	
	**配置类**
	
	```java
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
	```
	
	**测试**
	
	```java
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
	```

# 自动注入 #

## @Autowired&@Qualifier&@Primary ##

### @Autowired ###

之前使用比较多的自动装配是`@Autowired`

**Service**

```java
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
```

**Dao**

```java
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
```

**配置类**

```java
@Configuration
@ComponentScan({"com.anno.service","com.anno.controller","com.anno.dao"})
public class MainConifgOfAutowired {
	.....
}
```

**测试**

```java
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
```

打印结果:

```java
BookService{bookDao=BookDao{label=1}}
BookDao{label=1}
```

可以看到现在获取到的`BookDao`容器是同一个

在配置类里添加

```java
@Bean("bookDao2")
public BookDao bookDao()
{
    BookDao bookDao = new BookDao();
    bookDao.setLabel(2);
    return bookDao;
}
```

打印结果:

```java
BookService{bookDao=BookDao{label=1}}
BookDao{label=1}
```

**如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找**

调整测试里获取`BookDao2`

```java
 BookDao bookDao = (BookDao) annotationConfigApplicationContext.getBean("bookDao2");
 System.out.println(bookDao);
```

打印结果:

```java
BookService{bookDao=BookDao{label=1}}
BookDao{label=2}
```

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

```java
    <dependency>
        <groupId>javax.inject</groupId>
        <artifactId>javax.inject</artifactId>
        <version>1</version>
    </dependency>
```

![](http://120.77.237.175:9080/photos/springanno/24.jpg)

**`@Autowired`:`Spring`定义的； `@Resource`、`@Inject`都是`java`规范**

注意:上面的所有注入都是通过`AutowiredAnnotationBeanPostProcessor`解析完成自动装配功能

### `@Autowire`标注位置 ###

`@Autowired`:构造器，参数，方法，属性；都是从容器中获取参数组件的值

1. 放在属性位置

  **Bean**

  ```java
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
  ```

  打印结果:

  ```java
  Boss boss = annotationConfigApplicationContext.getBean(Boss.class);
   System.out.println(boss);		//Boss{car=com.anno.bean.Car@57c758ac}
   Car car = annotationConfigApplicationContext.getBean(Car.class);
   System.out.println(car);		//com.anno.bean.Car@57c758ac
  ```

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



```java
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
```

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

```java
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
```

配置

```java
db.user=root
db.password=123456
db.driverClass=com.mysql.jdbc.Driver
```

测试

```java
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
```

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

    ```java
    <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>5.2.6.RELEASE</version>
      </dependency>
    ```

    

2. **定义一个业务逻辑类（`MathCalculator`）；在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束、方法出现异常，xxx）**

   ```java
   public class MathCalculator {
   
       public int div(int i,int j)
       {
           System.out.println("MathCalculator.......div......");
           return i/j;
       }
   }
   ```

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

	```java
	<!-- 开启基于注解版的切面功能 -->
	<aop:aspectj-autoproxy></aop:aspectj-autoproxy>
	```


**切面类**

```java
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
```

**配置类**

```java
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
```



**测试**

```java
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
```

**打印结果**

```java
div运行.......@Before:参数列表是[1, 1]
MathCalculator.......div......
div 结束........@After
div 正常返回.......... @AfterReturning:运行结果:1
```

## @EnableAspectJAutoProxy原理 ##

看给容器中注册了什么组件，这个组件什么时候工作，这个组件的功能是什么？

基于注解的方式实现AOP需要在配置类中添加注解@EnableAspectJAutoProxy。我们就先从这个注解看一下Spring实现AOP的过程：

![](http://120.77.237.175:9080/photos/springanno/32.jpg)

**发现`EnableAspectJAutoProxy`它就是给容器中注册了一个`AspectJAutoProxyRegistrar`,而`AspectJAutoProxyRegistrar`它实现了
`ImportBeanDefinitionRegistrar`接口,会向会编程式的向`IOC`容器中注册组件.**

![](http://120.77.237.175:9080/photos/springanno/33.jpg)

从上图打断点进入我看可以看到返回的`org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator`


![](http://120.77.237.175:9080/photos/springanno/34.jpg)

接下来就是研究下给容器中注册的这个`AnnotationAwareAspectJAutoProxyCreator`
通过查看源码，可以发现`AnnotationAwareAspectJAutoProxyCreator`的继承树

## AnnotationAwareAspectJAutoProxCreator分析 ##

```java
/**
AnnotationAwareAspectJAutoProxyCreator
继承了AspectJAwareAdvisorAutoProxyCreator
	AspectJAwareAdvisorAutoProxyCreator
	继承自AbstractAdvisorAutoProxyCreator
		AbstractAdvisorAutoProxyCreator
		继承自AbstractAutoProxyCreator
			而AbstractAutoProxyCreator这个父类需要关注的是它实现了
			SmartInstantiationAwareBeanPostProcessor.BeanFactoryAware
	
	接下来我们需要在 AbstractAutoProxyCreator.setBeanFactory()打上【断点】		
	AbstractAutoProxyCreator.postProcessBeforeInstantiation()打上【断点】	
	AbstractAutoProxyCreator.postProcessAfterInitialization()打上【断点】	
	AbstractAdvisorAutoProxyCreator重点写了上级的setBeanFactory()打上【断点】,其方法里调用了initBeanFactory()下面重写了进入下面的
	AnnotationAwareAspectJAutoProxCreator又重写了上级的initBeanFactory()打上【断点】	
	mathCaculator()方法，logAspects()方法 两个方法都 打上【断点】
*/			
```

## 流程 ##
### AnnotationAwareAspectJAutoProxyCreator的创建和注册流程

1. 传入配置类，创建IOC容器

```java
public class IOCTest_Aop {
    //这里开始传入配置类,创建IOC容器,AnnotationConfigApplicationContext传入MainConfigOfAOP.class有参构造器,有参构造数如下
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);

    @Test
    public void test01()
    {
        MathCalculator mathCalculator = annotationConfigApplicationContext.getBean(MathCalculator.class);
        mathCalculator.div(1,0);
        //System.out.println(mathCalculator);

        annotationConfigApplicationContext.close();
    }

}
```

2. 注册配置类，调用refresh（）刷新容器

   ```java
   public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
       //1.首先使用无参构造器创建对象
       this();
       //2.再来把主配置类注册进来
       register(componentClasses);
       //3.后调用refresh()方法刷新容器，刷新容器就是要把容器中的所有bean都创建出来，也就是说这就像初始化容器一样
       refresh();	//点击进入看下一步如何刷新容器
   }
   ```

3. `registerBeanPostProcessors(beanFactory)`;注册bean的后置处理器来方便拦截bean的创建；

   ```java
   //进入AbstractApplicationContext.refresh()
   @Override
   public void refresh() throws BeansException, IllegalStateException {
      synchronized (this.startupShutdownMonitor) {
         // Prepare this context for refreshing.
         prepareRefresh();
   
         // Tell the subclass to refresh the internal bean factory.
         ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
   
         // Prepare the bean factory for use in this context.
         prepareBeanFactory(beanFactory);
   
         try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);
   
            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);
   
            //即注册bean的后置处理器。它的作用是什么呢？它就是用来方便拦截bean的创建的，那么这个后置处理器的注册逻辑又是什么样的呢？
            registerBeanPostProcessors(beanFactory);
   
            // Initialize message source for this context.
            initMessageSource();
   
            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();
   
            // Initialize other special beans in specific context subclasses.
            onRefresh();
   
            // Check for listener beans and register them.
            registerListeners();
   
            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);
   
            // Last step: publish corresponding event.
            finishRefresh();
         }
   
         catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
               logger.warn("Exception encountered during context initialization - " +
                     "cancelling refresh attempt: " + ex);
            }
   
            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();
   
            // Reset 'active' flag.
            cancelRefresh(ex);
   
            // Propagate exception to caller.
            throw ex;
         }
   
         finally {
            // Reset common introspection caches in Spring's core, since we
            // might not ever need metadata for singleton beans anymore...
            resetCommonCaches();
         }
      }
   }
   ```

   ```java
   //AbstractApplicationContext	
   protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
       //继续进入
       PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
   }
   ```

   ```java
   //PostProcessorRegistrationDelegate	
   public static void registerBeanPostProcessors(
   			ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
   		//1.先获取ioc容器已经定义了的需要创建对象的所有BeanPostProcessor
       	/**
    你可能要问了，为什么IOC容器中会有一些已定义的BeanPostProcessor呢？这是因为在前面创建IOC容器时，需要先传入配置类，而我们在解析配置类的时候，由于这个配置类里面有一个@EnableAspectJAutoProxy注解，对于该注解，我们之前也说过，它会为我们容器中注册一个AnnotationAwareAspectJAutoProxyCreator（后置处理器），这还仅仅是这个@EnableAspectJAutoProxy注解做的事，除此之外，容器中还有一些默认的后置处理器的定义。
   所以，程序运行到这，容器中已经有一些我们将要用的后置处理器了，只不过现在还没创建对象，都只是一些定义，也就是说容器中有哪些后置处理器。
       	**/
       	///获取到的postProcessorNames如下图1.0显示,有三个
   		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);
   
   		// Register BeanPostProcessorChecker that logs an info message when
   		// a bean is created during BeanPostProcessor instantiation, i.e. when
   		// a bean is not eligible for getting processed by all BeanPostProcessors.
   		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
       	//2. 给容器中加别的BeanPostProcessor
   		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));
   
       	//3.继续往下看这个registerBeanPostProcessors()方法，发现它里面还有这样的注释，如下所示：
   		// Separate between BeanPostProcessors that implement PriorityOrdered,
   		// Ordered, and the rest.
       	//说的是分离这些BeanPostProcessor，看哪些是实现了PriorityOrdered接口的，哪些又是实现了Ordered接口的，包括哪些是原生的没有实现什么接口的。所以，在这儿，对这些BeanPostProcessor还做了一些处理，所做的处理看以下代码便一目了然。
   		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
   		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
   		List<String> orderedPostProcessorNames = new ArrayList<>();
   		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
       //拿到IOC容器中所有这些BeanPostProcessor之后，是怎么处理的呢？它是来看我们这个BeanPostProcessor是不是实现了PriorityOrdered接口，我们不妨看一下PriorityOrdered接口的源码，如1.1所示
   		for (String ppName : postProcessorNames) {
              
   			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                   //上面Debug定位到这里
                   //可以看到，是先拿到要注册的BeanPostProcessor的名字，然后再从beanFactory中来获取。
                   //进入这里看如是如何获取的,进入1.2
   				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   				priorityOrderedPostProcessors.add(pp);
   				if (pp instanceof MergedBeanDefinitionPostProcessor) {
   					internalPostProcessors.add(pp);
   				}
   			}
               	
   			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
   				orderedPostProcessorNames.add(ppName);
   			}
   			else {
   				nonOrderedPostProcessorNames.add(ppName);
   			}
   		}
   
       	//4.继续往下看这个registerBeanPostProcessors()方法，主要是看其中的注释，不难发现有以下三步：
   		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
       	//4.1 优先注册实现了PriorityOrdered接口的BeanPostProcessor；
   		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);
   
   		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
   		for (String ppName : orderedPostProcessorNames) {
   			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   			orderedPostProcessors.add(pp);
   			if (pp instanceof MergedBeanDefinitionPostProcessor) {
   				internalPostProcessors.add(pp);
   			}
   		}
   		sortPostProcessors(orderedPostProcessors, beanFactory);
       	//4.2 再给容器中注册实现了Ordered接口的BeanPostProcessor；
   		registerBeanPostProcessors(beanFactory, orderedPostProcessors);
   
   		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
   		for (String ppName : nonOrderedPostProcessorNames) {
   			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
   			nonOrderedPostProcessors.add(pp);
   			if (pp instanceof MergedBeanDefinitionPostProcessor) {
   				internalPostProcessors.add(pp);
   			}
   		}
       	//4.3注册没实现优先级接口的BeanPostProcessor
   		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);
   
   		// Finally, re-register all internal BeanPostProcessors.
   		sortPostProcessors(internalPostProcessors, beanFactory);
   		registerBeanPostProcessors(beanFactory, internalPostProcessors);
   
   		// Re-register post-processor for detecting inner beans as ApplicationListeners,
   		// moving it to the end of the processor chain (for picking up proxies etc).
   		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
   	}
   ```

   ​	1.0

   ​	![](http://120.77.237.175:9080/photos/springanno/35.jpg)

   ​	1.1

   ```java
   /**
   可以看到该接口其实是Ordered接口旗下的，也就是说它继承了Ordered接口。进一步说明，IOC容器中的那些BeanPostProcessor是有优先级排序的。
   
   好了，现在我们知道了这样一个结论，那就是：IOC容器中的那些BeanPostProcessor可以实现PriorityOrdered以及Ordered这些接口来定义它们工作的优先级，即谁先前谁先后。
   
   回到代码中，就不难看到，它是在这儿将这些BeanPostProcessor做了一下划分，如果BeanPostProcessor实现了PriorityOrdered接口，那么就将其保存在名为priorityOrderedPostProcessors的List集合中，并且要是该BeanPostProcessor还是MergedBeanDefinitionPostProcessor这种类型的，则还得将其保存在名为internalPostProcessors的List集合中。
   **/
   public interface PriorityOrdered extends Ordered {
   }
   ```

   那么，所谓的注册`BeanPostProcessor`又是什么呢？我们还是来到程序停留的地方，为啥子程序会停留在这儿呢？因为咱们现在即将要创建的名称为`internalAutoProxyCreator`的组件（其实它就是我们之前经常讲的`AnnotationAwareAspectJAutoProxyCreator`）实现了`Ordered`接口，这只要查看`AnnotationAwareAspectJAutoProxyCreator`类的源码便知，一级一级地往上查。

   ![](http://120.77.237.175:9080/photos/springanno/36.jpg)

   ​	1.2

```java
@Override
public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
    //继续进入
	return doGetBean(name, requiredType, null, false);
}
```

![](http://120.77.237.175:9080/photos/springanno/37.jpg)

```java
//进入了AbstractBeanFactory.doGetBean()方法
//这个方法特别特别的长，这儿我就不再详细分析它了，只须关注程序停留的这行代码即可。这行代码的意思是调用getSingleton()方法来获取单实例的bean，但是呢，IOC容器中第一次并不会有这个bean，所以第一次获取它肯定是会有问题的。
//我们继续跟进方法调用栈，如下图所示，可以看到现在是定位到了DefaultSingletonBeanRegistry类的getSingleton()方法中。
```

![](http://120.77.237.175:9080/photos/springanno/38.jpg)

也就是说如果从IOC容器中第一次获取单实例的bean出现问题，也即获取不到时，那么就会调用singletonFactory的getObject()方法。

我们继续跟进方法调用栈，如下图所示，可以看到现在又定位到了AbstractBeanFactory抽象类的doGetBean()方法中。

![](http://120.77.237.175:9080/photos/springanno/39.jpg)

可以发现，现在就是来创建bean的，也就是说如果获取不到那么就创建bean。**咱们现在就是需要注册BeanPostProcessor，说白了，实际上就是创建BeanPostProcessor对象，然后保存在容器中。**

那么接下来，我们就来看看是如何创建出名称为`internalAutoProxyCreator`的`BeanPostProcesso`的，它的类型其实就是我们之前经常说的`AnnotationAwareAspectJAutoProxyCreator`。我们就以它为例，来看看它这个对象是怎么创建出来的。

我们继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`createBean()`方法中

![](http://120.77.237.175:9080/photos/springanno/40.jpg)

接着再跟进方法调用栈，如下图所示，可以看到现在是定位到了AbstractAutowireCapableBeanFactory抽象类的doCreateBean()方法中

![](http://120.77.237.175:9080/photos/springanno/41.jpg)

程序停留在这儿，就是在初始化bean实例，说明bean实例已经创建好了，如果你要不信的话，那么可以往前翻阅该doCreateBean()方法，这时你应该会看到一个`createBeanInstance()`方法，说的就是bean实例的创建。创建的是哪个bean实例呢？就是名称为`internalAutoProxyCreator`的实例，该实例的类型就是我们之前经常说的`AnnotationAwareAspectJAutoProxyCreator`，即创建这个类型的实例。创建好了之后，就在程序停留的地方进行初始化。

所以，整个的过程就应该是下面这个样子的：

1. 首先创建bean的实例
2. 然后给bean的各种属性赋值（即调用populateBean()方法）
3. 接着初始化bean（即调用initializeBean()方法），这个初始化bean其实特别地重要，因为我们这个后置处理器就是在bean初始化的前后进行工作的。

接下来，我们就来看看这个bean的实例是如何初始化的。继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`initializeBean()`方法中。

![](http://120.77.237.175:9080/photos/springanno/42.jpg)

**这里分析下初始化bean的流程**。

1. 进入`invokeAwareMethods()`

   ```java
   //其实，这个方法是来判断我们这个bean对象是不是Aware接口的，如果是，并且它还是BeanNameAware、BeanClassLoaderAware以及BeanFactoryAware这几个Aware接口中的其中一个，那么就调用相关的Aware接口方法，即处理Aware接口的方法回调。	
   private void invokeAwareMethods(final String beanName, final Object bean) {
   		if (bean instanceof Aware) {
   			if (bean instanceof BeanNameAware) {
   				((BeanNameAware) bean).setBeanName(beanName);
   			}
   			if (bean instanceof BeanClassLoaderAware) {
   				ClassLoader bcl = getBeanClassLoader();
   				if (bcl != null) {
   					((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
   				}
   			}
   			if (bean instanceof BeanFactoryAware) {
   				((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
   			}
   		}
   	}
   ```

   现在当前的这个bean叫`internalAutoProxyCreator`，并且这个`bean`对象已经被创建出来了，创建出来的这个`bean`对象之前我们也分析过，它是有实现`BeanFactoryAware`接口的，故而会调用相关的`Aware`接口方法，这也是程序为什么会停留在`invokeAwareMethods()`这个方法的原因

2. 还是回到上图的`AbstractAutowireCapableBeanFactory`抽象类的`initializeBean()`方法中，即程序停留的地方。如果`invokeAwareMethods()`这个方法执行完了以后，那么后续又会发生什么呢?

   往下翻阅`initializeBean()`方法，会发现有一个叫`applyBeanPostProcessorsBeforeInitialization`的方法，如下图所示。

   ![](http://120.77.237.175:9080/photos/springanno/43.jpg)

   这个方法调用完以后，会返回一个被包装的bean。

   该方法的意思其实就是应用后置处理器的`postProcessBeforeInitialization()`方法。我们可以进入该方法中去看一看，到底是怎么应用后置处理器的postProcessBeforeInitialization()方法的？

   ```java
   	@Override
   	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
   			throws BeansException {
   
   		Object result = existingBean;
           //可以看到，它是拿到所有的后置处理器，然后再调用后置处理器的postProcessBeforeInitialization()方法，也就是说bean初始化之前后置处理器的调用在这儿。
   		for (BeanPostProcessor processor : getBeanPostProcessors()) {
   			Object current = processor.postProcessBeforeInitialization(result, beanName);
   			if (current == null) {
   				return result;
   			}
   			result = current;
   		}
   		return result;
   	}
   ```

3. 还是回到程序停留的地方，继续往下翻阅`initializeBean()`方法，你会发现还有一个叫`invokeInitMethods`的方法，即执行自定义的初始化方法。

   ![](http://120.77.237.175:9080/photos/springanno/44.jpg)

   这个自定义的初始化方法呢，你可以用`@bean`注解来定义，指定一下初始化方法是什么，销毁方法又是什么，这个我们之前都说过了。

4. 自定义的初始化方法执行完以后，又有一个叫`applyBeanPostProcessorsAfterInitialization`的方法，该方法的意思其实就是应用后置处理器的`postProcessAfterInitialization()`方法。我们可以进入该方法中去看一看，到底是怎么应用后置处理器的`postProcessAfterInitialization()`方法的？

   ![](http://120.77.237.175:9080/photos/springanno/45.jpg)

   ```java
   	@Override
   	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
   			throws BeansException {
   
   		Object result = existingBean;
           //依旧是拿到所有的后置处理器，然后再调用后置处理器的postProcessAfterInitialization()方法。
   		for (BeanPostProcessor processor : getBeanPostProcessors()) {
   			Object current = processor.postProcessAfterInitialization(result, beanName);
   			if (current == null) {
   				return result;
   			}
   			result = current;
   		}
   		return result;
   	}
   ```

   所以，后置处理器的这两个`postProcessBeforeInitialization()`与`postProcessAfterInitialization()`方法前后的执行，就是在这块体现的。我们在这儿也清楚地看到了。

接下来，我们还是回到程序停留的地方，即下面这行代码处。

```java
invokeAwareMethods(beanName, bean);
```

调用`initializeBean()`方法初始化`bean`的时候，还得执行那些`Aware`接口的方法，那到底怎么执行呢？正好我们知道，当前的这个bean它确实是实现了`BeanFactoryAware`接口。因此我们继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`invokeAwareMethods()`方法中。

![](http://120.77.237.175:9080/photos/springanno/46.jpg)

再继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAdvisorAutoProxyCreator`抽象类的`setBeanFactory()`方法中。

![](http://120.77.237.175:9080/photos/springanno/47.jpg)

可以看到现在调用的是`AbstractAdvisorAutoProxyCreator`抽象类中的`setBeanFactory()`方法。我们要创建的是`AnnotationAwareAspectJAutoProxyCreator`对象，但是调用的却是它父类的`setBeanFactory()`方法。

可以看到父类的setBeanFactory()方法被调用完了。这时会运行到如下这行代码处

```java
initBeanFactory((ConfigurableListableBeanFactory) beanFactory);
```

该`initBeanFactory()`方法就是用来初始化`BeanFactory`的。进入到当前方法内部，如下图所示，可以看到调用到了`AnnotationAwareAspectJAutoProxyCreator`这个类的`initBeanFactory()`方法中了，即调到了我们要给容器中创建的`AspectJ`自动代理创建器的`initBeanFactory()`方法中

![](http://120.77.237.175:9080/photos/springanno/48.jpg)

可以看到这个`initBeanFactory()`方法创建了两个东西，一个叫`ReflectiveAspectJAdvisorFactory`，还有一个叫`BeanFactoryAspectJAdvisorsBuilderAdapter`，它相当于把之前创建的`aspectJAdvisorFactory`以及`beanFactory`重新包装了一下，就只是这样。

至此，整个这么一个流程下来以后，咱们的这个`BeanPostProcessor`，是以`AnnotationAwareAspectJAutoProxyCreator`（就是`@EnableAspectJAutoProxy`这个注解核心导入的`BeanPostProcessor`）为例来讲解的，就创建成功了。并且还调用了它的`initBeanFactory()`方法得到了一些什么`aspectJAdvisorFactory`和`aspectJAdvisorsBuilder`，这两个东东大家知道一下就行了。至此，整个`initBeanFactory()`方法就说完了，也就是说我们整个的后置处理器的注册以及创建过程就说完了。

一开始的这行代码是用来注册后置处理器

```java
registerBeanPostProcessors(beanFactory);
```

此刻，后置处理器已经在容器中注册进来了。所谓的注册又是什么呢？接下来，我们可以再来看一下，按下`F6`快捷键继续让程序往下运行，一直让程序运行到`AbstractAutowireCapableBeanFactory`抽象类的`initializeBean()`方法中的如下这行代码处。

```java
Object wrappedBean = bean;
```

紧接着就是应用各种什么`applyBeanPostProcessorsBeforeInitialization()`方法或者`applyBeanPostProcessorsAfterInitialization()`方法了，我们继续按下`F6`快捷键让程序往下运行，一直让程序运行到如下图所示的这行代码处。

![](http://120.77.237.175:9080/photos/springanno/49.jpg)

可以看到咱们要创建的后置处理器（即`AnnotationAwareAspectJAutoProxyCreator`）总算是创建完了。继续往下`F6`

![](http://120.77.237.175:9080/photos/springanno/50.jpg)

上图,把后置处理器创建完以后会添加到我们已创建的那个bean集合里面,继续按下`F6`快捷键让程序往下运行，一直让程序运行到如下图

![](http://120.77.237.175:9080/photos/springanno/51.jpg)

可以看到，咱们这个`BeanPostProcessor`（即`AnnotationAwareAspectJAutoProxyCreator`）创建完了以后，会放进了一个internalPostProcessors的集合里面。

继续按下`F6`快捷键让程序往下运行，会调用`sortPostProcessors()`方法按照优先级给这些后置处理器们排一个序，程序再往下运行，就会调用到`registerBeanPostProcessors()`方法了，进到该方法中去看一下。

```java
	private static void registerBeanPostProcessors(
			ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {

		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}
```

就是**拿到所有的`BeanPostProcessor`，然后调用`beanFactory`的`addBeanPostProcessor()`方法将`BeanPostProcessor`注册到`BeanFactory`中。**

IOC容器在创建对象的时候，会注册这一些后置处理器，而在上一讲中，就已经把`AnnotationAwareAspectJAutoProxyCreator`这个后置处理器创建出来了，它呢，就是`@EnableAspectJAutoProxy`注解利用`AspectJAutoProxyRegistrar`给容器中创建出的一个`bean`的配置信息。

当然了，在注册后置处理器的时候，这个`bean`肯定就已经提前创建出来了。而且，它呢，我们也都知道是一个后置处理器，只要这个后置处理器已经创建出来并且放在容器中了，那么以后在创建其他组件的时候，它就可以拦截到这些组件的创建过程了。因为我们知道，任何组件在创建bean的实例时，都会经历给bean中的各种属性赋值、初始化bean（并且在初始化bean前后都会有后置处理器的作用）等过程。

这下面,我们就来看一下`AnnotationAwareAspectJAutoProxyCreator`作为后置处理器，被注册完之后，接下来就得完成BeanFactory的初始化工作了。

### BeanFactory初始化工作

我们还是以debug模式来运行`IOCTest_Aop`测试类，这时，应该还是会来到`AbstractAdvisorAutoProxyCreator`类的`setBeanFactory()`方法中，如下图所示

![](http://120.77.237.175:9080/photos/springanno/52.jpg)

在上一讲中，我们是从`test01()`方法开始一步一步研究慢慢分析到这儿的，我们按下`F8`快捷键直接运行到下一个断点，如下图所示，可以看到现在是定位到了`AbstractAutoProxyCreator`抽象类的`setBeanFactory()`方法中。

![](http://120.77.237.175:9080/photos/springanno/53.jpg)

然后继续按下`F8`快捷键运行直到下一个断点，一直运行到如下图所示的这行代码处。

![](http://120.77.237.175:9080/photos/springanno/54.jpg)

可以看到程序现在是停留在了`AbstractAutoProxyCreator`类的`postProcessBeforeInstantiation()`方法中，不过从方法调用栈中我们可以清楚地看到现在其实调用的是`AnnotationAwareAspectJAutoProxyCreator`的`postProcessBeforeInstantiation()`方法。

这个方法大家一定要引起注意，它跟我们之前经常讲到的后置处理器中的方法是有区别的。你不妨看一下`BeanPostProcessor`接口的源码，如所示，它里面有一个`postProcessbeforeInitialization()`方法。

```java
public interface BeanPostProcessor {

	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```

而现在这个方法是叫`postProcessBeforeInstantiation`，大家可一定要分清楚哟！

你可能要问了，`AnnotationAwareAspectJAutoProxyCreator`它本身就是一个后置处理器，为何其中的方法叫`postProcessBeforeInstantiation`，而不是叫`postProcessbeforeInitialization`呢？因为后置处理器跟为后置处理器是不一样的，当前我们要用到的这个后置处理器（即`AnnotationAwareAspectJAutoProxyCreator`）实现的是一个叫`SmartInstantiationAwareBeanPostProcessor`的接口，而该接口继承的是`InstantiationAwareBeanPostProcessor`接口（它又继承了`BeanPostProcessor`接口），也就是说，**`AnnotationAwareAspectJAutoProxyCreator`虽然是一个`BeanPostProcessor`，但是它却是`InstantiationAwareBeanPostProcessor`这种类型的**，而`InstantiationAwareBeanPostProcessor`接口中声明的方法就叫`postProcessBeforeInstantiation`。

```java
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	@Nullable
	default PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName)
			throws BeansException {

		return null;
	}

	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		return pvs;
	}

}
```

故而程序就停留到了`AbstractAutoProxyCreator`类的`postProcessBeforeInstantiation()`方法中。

为什么会来到这儿呢？我们同样可以仿照前面来大致地来探究一下，在左上角的方法调用栈中，仔细查找，就会在前面找到一个`test01()`方法，它其实就是`IOCTest_AOP`测试类中的测试方法，我们就从该方法开始分析。

鼠标单击方法调用栈中的那个`test01()`方法，此时，我们会进入到`IOCTest_AOP`测试类中的`test01()`方法中，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/55.jpg)

可以看到这一步还是传入主配置类来创建IOC容器，依旧会调用refresh()方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/56.jpg)

继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractApplicationContext`抽象类的`refresh()`方法中。

![](http://120.77.237.175:9080/photos/springanno/57.jpg)可以看到，在这儿会调用`finishBeanFactoryInitialization()`方法，这是用来初始化剩下的单实例`bean`的。而在该方法前面，有一个叫`registerBeanPostProcessors`的方法，它是用来注册后置处理器的，在上面已经有讲解过了。

注册完后置处理器之后，接下来就来到了`finishBeanFactoryInitialization()`方法处，以完成`BeanFactory`的初始化工作。所谓的完成`BeanFactory`的初始化工作，其实就是来创建剩下的单实例bean。为什么叫剩下的呢？因为IOC容器中的这些组件，比如一些`BeanPostProcessor`，早都已经在注册的时候就被创建了，所以会留一下没被创建的组件，让它们在这儿进行创建。

我们继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractApplicationContext`抽象类的`finishBeanFactoryInitialization()`方法中。

![](http://120.77.237.175:9080/photos/springanno/58.jpg)

在这儿会调用`getBean()`方法来获取一个`bean`,名为`mainConfigOfAOP`，它跟我们目前的研究没什么关系。

既然没有关系，那为何还要获取这个bean呢？往前翻阅`preInstantiateSingletons()`方法，可以看到有一个`for`循环，它是来遍历一个`beanNames`的List集合的，这个`beanNames`又是什么呢？很明显它是一个`List<String>`集合，它里面保存的是容器中所有bean定义的名称，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/59.jpg)

所以，接下来，我们就可以讲讲完成`BeanFactory`的初始化工作的第一步了

### BeanFactory的初始化工作的第一步

遍历获取容器中所有的`bean`，并依次创建对象，注意是依次调用`getBean()`方法来创建对象的。

我们继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractBeanFactory`抽象类的`getBean()`方法中

![](http://120.77.237.175:9080/photos/springanno/60.jpg)

再继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractBeanFactory`抽象类的`doGetBean()`方法中。

![](http://120.77.237.175:9080/photos/springanno/61.jpg)

可以看到，获取单实例`bean`调用的是`getSingleton()`方法，并且会返回一个`sharedInstance`对象。其实，从该方法上面的注释中也能看出，这儿是来创建bean实例的。

其实呢，在这儿创建之前，`sharedInstance`变量已经提前声明过了，我们往前翻阅`doGetBean()`方法，就能看到已声明的`sharedInstance`变量了。

![](http://120.77.237.175:9080/photos/springanno/62.jpg)

可以清楚地看到，在如下这行代码处是来第一次获取单实例bean。

那到底是怎么获取的呢？其实从注释中可以知道，它会提前先检查单实例的缓存中是不是已经人工注册了一些单实例的bean，若是则获取。

### 完成BeanFactory的初始化工作的第二步

也就是说，这个bean的创建不是说一下就创建好了的，它得**先从缓存中获取当前bean，如果能获取到，说明当前bean之前是被创建过的，那么就直接使用，否则的话再创建。**

往上翻阅AbstractBeanFactory抽象类的doGetBean()方法，可以看到有这样的逻辑：

![](http://120.77.237.175:9080/photos/springanno/63.jpg)

![](http://120.77.237.175:9080/photos/springanno/64.jpg)

上面,否则的话,才会进入这个`getSingleton()`方法中,也就是说,它是能获取就获取,不能获取才创建可以看到，单实例bean是能获取就获取，不能获取才创建。**Spring就是利用这个机制来保证我们这些单实例bean只会被创建一次，也就是说只要创建好的bean都会被缓存起来。**

继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`DefaultSingletonBeanRegistry`类的`getSingleton()`方法中。

![](http://120.77.237.175:9080/photos/springanno/65.jpg)

这儿是调用单实例工厂来进行创建单实例bean。

继续跟进方法调用栈，如下图所示，可以看到现在又定位到了`AbstractBeanFactory`抽象类的`doGetBean()`方法中。

![](http://120.77.237.175:9080/photos/springanno/66.jpg)

可以看到又会调用`createBean()`方法来进行创建单实例`bean`。而在该方法前面是`bean`能获取到就不会再创建了。

继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`createBean()`方法中。

![](http://120.77.237.175:9080/photos/springanno/67.jpg)

往上翻阅`createBean()`方法，发现可以拿到要创建的`bean`的定义信息，包括要创建的`bean`的类型是什么，它是否是单实例等等，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/68.jpg)

好，现在回到`resolveBeforeInstantiation()`方法上，当前程序也是停在了这一行

该方法是来解析`BeforeInstantiation`的，可以看一下该方法上的注释，它是说给后置处理器一个机会，来返回一个代理对象，替代我们创建的目标的bean实例。也就是说，我们希望后置处理器在此能返回一个代理对象，如果能返回代理对象那当然就很好了，直接使用就得了，如果不能那么就得调用`doCreateBean()`方法来创建一个`bean`实例了。

![](http://120.77.237.175:9080/photos/springanno/69.jpg)

什么要说这个方法呢？进入该方法里面看看你自然就懂了

```java
	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
			throws BeanCreationException {

		// Instantiate the bean.
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
            //1. 创建bean的实例
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		final Object bean = instanceWrapper.getWrappedInstance();
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}

		// Allow post-processors to modify the merged bean definition.
		synchronized (mbd.postProcessingLock) {
			if (!mbd.postProcessed) {
				try {
					applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Post-processing of merged bean definition failed", ex);
				}
				mbd.postProcessed = true;
			}
		}

		// Eagerly cache singletons to be able to resolve circular references
		// even when triggered by lifecycle interfaces like BeanFactoryAware.
		boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
				isSingletonCurrentlyInCreation(beanName));
		if (earlySingletonExposure) {
			if (logger.isTraceEnabled()) {
				logger.trace("Eagerly caching bean '" + beanName +
						"' to allow for resolving potential circular references");
			}
			addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
		}

		// Initialize the bean instance.
		Object exposedObject = bean;
		try {
            //2. 给bean的各种属性赋值
			populateBean(beanName, mbd, instanceWrapper);
            //3. 初始化bean
            //3.1 先执行Aware接口的方法
            //3.2 应用后置处理器的postProcessBeforeInitialization()方法
            //3.3 执行自定义的初始化方法
            //3.4 应用后置处理器的postProcessAfterInitialization()方法
			exposedObject = initializeBean(beanName, exposedObject, mbd);
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

		if (earlySingletonExposure) {
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				if (exposedObject == bean) {
					exposedObject = earlySingletonReference;
				}
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" +
								StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
								"] in its raw version as part of a circular reference, but has eventually been " +
								"wrapped. This means that said other beans do not use the final version of the " +
								"bean. This is often the result of over-eager type matching - consider using " +
								"'getBeanNamesForType' with the 'allowEagerInit' flag turned off, for example.");
					}
				}
			}
		}

		// Register bean as disposable.
		try {
			registerDisposableBeanIfNecessary(beanName, bean, mbd);
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanCreationException(
					mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
		}

		return exposedObject;
	}
```

其实，这个`doCreateBean()`方法我们之前已经介绍了，所做的事情无非就是：

1. 首先创建bean的实例
2. 然后给bean的各种属性赋值
3. 接着初始化bean
   - 1）先执行Aware接口的方法
   - 2）应用后置处理器的postProcessBeforeInitialization()方法
   - 3）执行自定义的初始化方法
   - 4）应用后置处理器的postProcessAfterInitialization()方法

调用doCreateBean()方法才是真正的去创建一个bean实例。

回到`AbstractAutowireCapableBeanFactory`看看`resolveBeforeInstantiation()`方法里面具体是怎么做的了。

继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`resolveBeforeInstantiation()`方法中，既然程序是停留在了此处，那说明并没有走后面调用`doCreateBean()`方法创建`bean`实例的流程，而是先来到这儿，希望后置处理器能返回一个代理对象。

```java
	@Nullable
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
		Object bean = null;
        //看创建的这个bean是不是已经提前被解析过了
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
                    //重点是下面这两个方法
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			mbd.beforeInstantiationResolved = (bean != null);
		}
		return bean;
	}
//可以看到，在该方法中，首先会拿到要创建的bean的定义信息，包括要创建的bean的类型是什么，它是否是单实例等等，然后看它是不是已经提前被解析过了什么什么，这儿都不算太重要
```

![](http://120.77.237.175:9080/photos/springanno/70.jpg)

这一块会调用两个方法，一个叫方法叫`applyBeanPostProcessorsBeforeInstantiation`，另一个方法叫`applyBeanPostProcessorsAfterInitialization`。

后置处理器会先尝试返回对象，怎么尝试返回呢？可以看到，是调用`applyBeanPostProcessorsBeforeInstantiation()`方法返回一个对象的，继续跟进方法调用栈，如下图所示，可以看到现在是定位到了`AbstractAutowireCapableBeanFactory`抽象类的`applyBeanPostProcessorsBeforeInstantiation()`方法中。

![](http://120.77.237.175:9080/photos/springanno/71.jpg)

可以看到,它是拿到所有的后置处理器，如果后置处理器是`InstantiationAwareBeanPostProcessor`这种类型的，那么就执行该后置处理器的`postProcessBeforeInstantiation()`方法。我为什么要说这个方法呢？因为现在遍历拿到的后置处理器是`AnnotationAwareAspectJAutoProxyCreator`这种类型的，如下图所示

![](http://120.77.237.175:9080/photos/springanno/72.jpg)

并且前面我也说了，它就是`InstantiationAwareBeanPostProcessor`这种类型的后置处理器，这种类型的后置处理器中声明的方法就叫`postProcessBeforeInstantiation`，而不是我们以前学的后置处理器中的叫`postProcessbeforeInitialization`的方法，也就是说后置处理器跟后置处理器是不一样的。

我们以前就知道，`BeanPostProcessor`是在bean对象创建完成初始化前后调用的。而在这儿我们也看到了，首先是会有一个判断，即判断后置处理器是不是`InstantiationAwareBeanPostProcessor`这种类型的，然后再尝试用后置处理器返回对象（当然了，是在创建bean实例之前）。

总之，我们可以得出一个结论：**`AnnotationAwareAspectJAutoProxyCreator`会在所有bean创建之前会有一个拦截，InstantiationAwareBeanPostProcessor，会调用postProcessBeforeInstantiation(),先尝试返回bean的实例**

最后，我们继续跟进方法调用栈，如下图所示，可以看到终于又定位到了`AbstractAutoProxyCreator`抽象类的`postProcessBeforeInstantiation()`方法中

![](http://120.77.237.175:9080/photos/springanno/73.jpg)

为什么程序会来到这个方法中呢？想必你也非常清楚了，因为判断后置处理器是不是`InstantiationAwareBeanPostProcessor`这种类型时，轮到了`AnnotationAwareAspectJAutoProxyCreator`这个后置处理器，而它正好是`InstantiationAwareBeanPostProcessor`这种类型的，所以程序自然就会来到它的`postProcessBeforeInstantiation()`方法中。

呼应前面，终于分析到了`AnnotationAwareAspectJAutoProxyCreator`这个后置处理器的`postProcessBeforeInstantiation()`方法中，也就是知道了程序是怎么到这儿来的。

最终，我们得出这样一个结论：**`AnnotationAwareAspectJAutoProxyCreator`在所有`bean`创建之前，会有一个拦截，因为它是`InstantiationAwareBeanPostProcessor`这种类型的后置处理器，然后会调用它的`postProcessBeforeInstantiation()`方法。**

### AnnotationAwareAspectJAutoProxyCreator作为后置处理器

1. **在每一个bean创建之前，调用postProcessBeforeInstantiation()方法**

`AnnotationAwareAspectJAutoProxyCreator`作为后置处理器，它其中的一个作用就是在每一个`bean`创建之前，调用其`postProcessBeforeInstantiation()`方法。

接下来，我们来看看这个方法都做了哪些事情。此刻，是来创建容器中的第一个bean的，即class com.anno.config.MainConfigOfAOP，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/74.jpg)

在上面已经知道，这块是一个循环创建，会循环创建每一个bean。像`MainConfigOfAOP`这样的`bean`，跟我们要研究的`AOP`原理没什么关系，所以我们并不关心这个`bean`的创建。我们主要关心`MathCalculator`（业务逻辑类）和`LogAspects`（切面类）这两个bean的创建。

2. **先来判断当前bean是否在advisedBeans中**

`advisedBeans`是个什么东西呢？它是一个`Map`集合，里面保存了所有需要增强的`bean`的名称。那什么又叫需要增强的bean呢？就是那些业务逻辑类，例如`MathCalculator`，因为它里面的那些方法是需要切面来切的，所以我们要执行它里面的方法，不能再像以前那么简单地执行了，得需要增强，这就是所谓的需要增强的bean。

当程序运行到如下这行代码时，我们来看一下，名为`advisedBeans`的`Map`集合里面是不包含`MathCalculator`这个bean的名称的，因为我们是第一次来处理这个bean。

也就是说，在这儿会判断当前的`MathCalculator`这个`bean`有没有在`advisedBeans`集合里面

3. **再来判断当前bean是否是基础类型，或者是否是切面（标注了@Aspect注解的）**

继续按下`F6`快捷键让程序往下运行，可以看到又会做一个判断，即判断当前bean是否是基础类型，或者是否是标注了`@Aspect`注解的切面

```java
//isInfrastructureClass判断当前Bean是否是基础类,或者是否是标注了@Aspect注解的切面
if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
    this.advisedBeans.put(cacheKey, Boolean.FALSE);
    return null;
}
```

什么叫基础类型呢？所谓的基础类型就是当前`bean`是否是实现了`Advice`、`Pointcut`、`Advisor`以及`AopInfrastructureBean`这些接口。我们可以点进去`isInfrastructureClass()`方法里面大概看一看，如下所示，你现在该知道所谓的基础类型是什么了吧

```java
	protected boolean isInfrastructureClass(Class<?> beanClass) {
		boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
				Pointcut.class.isAssignableFrom(beanClass) ||
				Advisor.class.isAssignableFrom(beanClass) ||
				AopInfrastructureBean.class.isAssignableFrom(beanClass);
		if (retVal && logger.isTraceEnabled()) {
			logger.trace("Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
		}
		return retVal;
	}
```

其实，除了判断当前`bean`是否是基础类型之外，还有一个判断，那怎么看到这个判断呢？选中isInfrastructureClass()方法，按下`F5`快捷键进入该方法里面，就能看到这个判断了，即判断当前`bean`是否是标注了`@Aspect`注解的切面。

```java
	@Override
	protected boolean isInfrastructureClass(Class<?> beanClass) {
        //isInfrastructureClass()判断当前Bean是否基码类型,isAspect()判断当前Bean是否标注了@Aspect注解的切面
		return (super.isInfrastructureClass(beanClass) ||
				(this.aspectJAdvisorFactory != null && this.aspectJAdvisorFactory.isAspect(beanClass)));
	}
```

从上面可以清楚地看到，还有一个叫`isAspect`的方法，它就是来判断当前`bean`是否是标注了`@Aspect`注解的切面的。那么它是怎么来判断的呢？我们可以进入该方法里面去看一看（选中该方法，然后按下`F5`快捷键即可进入），如下图所示，可以看到它是用`hasAspectAnnotation()`方法来判断当前`bean`有没有标注`@Aspect`注解的。

```java
	@Override
	public boolean isAspect(Class<?> clazz) {
		return (hasAspectAnnotation(clazz) && !compiledByAjc(clazz));
	}
```

很显然，当前的这个`bean`（即MathCalculator）既不是基础类型，也不是标注了`@Aspect`注解的切面。所以，按下`F6`快捷键让程序继续往下运行，运行回`postProcessBeforeInstantiation()`方法中之后，`isInfrastructureClass(beanClass)`表达式的值就是`false`了

![](http://120.77.237.175:9080/photos/springanno/75.jpg)

4. **最后判断是否需要跳过**

所谓的跳过，就是说不要再处理这个`bean`了。那跳过又是怎么判断的呢？我们可以按下`F5`快捷键进入`shouldSkip()`方法里面去看一看，如下图所示。

```java
	protected boolean shouldSkip(Class<?> beanClass, String beanName) {
		// TODO: Consider optimization by caching the list of the aspect names
		List<Advisor> candidateAdvisors = findCandidateAdvisors();
		for (Advisor advisor : candidateAdvisors) {
			if (advisor instanceof AspectJPointcutAdvisor &&
					((AspectJPointcutAdvisor) advisor).getAspectName().equals(beanName)) {
				return true;
			}
		}
		return super.shouldSkip(beanClass, beanName);
	}
```

可以看到，首先是调用`findCandidateAdvisors()`方法找到候选的增强器的集合。

继续按下`F6`快捷键让程序往下运行，检查`candidateAdvisors`变量，可以看到现在有4个增强器，什么叫增强器啊？**增强器就是切面里面的那些通知方法。** 而且第一个增强器就是logStart()方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/76.jpg)

第二个增强器是logEnd()方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/77.jpg)

第三个增强器是logReturn()方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/78.jpg)

第四个增强器是logException()方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/79.jpg)

总结：在`shouldSkip()`方法里面，首先会获取到以上这4个通知方法。也就是说，先来获取候选的增强器。所谓的增强器其实就是切面里面的那些通知方法，只不过，在这儿是把通知方法的详细信息包装成了一个`Advisor`，并将其存放在了一个`List<Advisor>`集合中，即增强器的集合，即是说，每一个通知方法都会被认为是一个增强器。

那么，每一个增强器的类型又是什么呢？检查一下`candidateAdvisors`变量便知，每一个封装通知方法的增强器都是`InstantiationModelAwarePointcutAdvisor`这种类型的。

![](http://120.77.237.175:9080/photos/springanno/80.jpg)

获取到4个增强器之后，然后会来判断每一个增强器是不是`AspectJPointcutAdvisor`这种类型，如果是，那么返回true。很显然，每一个增强器并不是这种类型的，而是`InstantiationModelAwarePointcutAdvisor`这种类型的，因此程序并不会进入到那个if判断语句中。

继续按下`F6`快捷键让程序往下运行，一直运行到`shouldSkip()`方法中的最后一行代码处，可以看到，在`shouldSkip()`方法里面，最终会调用父类的`shouldSkip()`方法，如下。我们可以按下`F5`快捷键进入父类的`shouldSkip()`方法里面去看一看，如下图所示，发现它在这儿直接就返回`false`了

```java
	protected boolean shouldSkip(Class<?> beanClass, String beanName) {
		return AutoProxyUtils.isOriginalInstance(beanName, beanClass);
	}
==============================
    //判断直接返回false
	static boolean isOriginalInstance(String beanName, Class<?> beanClass) {
		if (!StringUtils.hasLength(beanName) || beanName.length() !=
				beanClass.getName().length() + AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX.length()) {
			return false;
		}
		return (beanName.startsWith(beanClass.getName()) &&
				beanName.endsWith(AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX));
	}
```

继续按下`F6`快捷键让程序往下运行，一直运行回`postProcessBeforeInstantiation()`方法中，这时，我们可以知道，if判断语句中的第二个表达式的值就是false。

![](http://120.77.237.175:9080/photos/springanno/81.jpg)

也就是说，shouldSkip()方法的返回值永远是`false`，而它就是用来判断是否需要跳过的，所以相当于就是说要跳过了。

好吧，跳过那就跳过吧！那就继续按下`F6`快捷键让程序往下运行，我们还是能看到当前这个`bean`的名字是叫calculator，而且还会拿到什么自定义的`TargetSource`，但这跟我们目前的研究没有关系，程序往下运行，最后将会直接返回`null`。

然后，按下`F8`快捷键运行到下一个断点，发现这时会来到主配置类的`calculator()`方法中。此刻，是要调用`mathCalculator()`方法来创建`MathCalculator`对象了。

![](http://120.77.237.175:9080/photos/springanno/82.jpg)

继续按下`F8`快捷键运行到下一个断点，可以发现当我们把`MathCalculator`对象创建完了以后，在这儿又会调用`postProcessAfterInitialization()`方法。

![](http://120.77.237.175:9080/photos/springanno/83.jpg)

其实，上面我也已经说过了，**在每次创建`bean`的时候，都会先调用`postProcessBeforeInstantiation()`方法，然后再调用`postProcessAfterInitialization()`方法。**

**5. 创建完对象以后，调用postProcessAfterInitialization()方法**

前面我就已说过，`AnnotationAwareAspectJAutoProxyCreator`作为后置处理器，它的第一个作用。现在，我就来说说它的第二个作用，即在创建完对象以后，会调用其`postProcessAfterInitialization()`方法。

我们调用刚才的`mathCalculator()`方法创建完`MathCalculator`对象以后，发现又会调用`AnnotationAwareAspectJAutoProxyCreator`（后置处理器）的`postProcessAfterInitialization()`方法。那么该方法又做了些什么事呢？

继续按下`F6`快捷键让程序往下运行，我们可以看到当前创建好的`MathCalculator`对象，并且这个`bean`的名字就叫`mathCalculator`，也可以看到在这儿还做了一个判断，即判断名为`earlyProxyReferences`的Set集合里面是否包含当前`bean`，在该Set集合里面我们可以看到之前已经代理过了什么，目前该Set集合是一个空集合。这都不是我们要关注的内容，我们重点要关注的内容其实是那个叫`wrapIfNecessary`的方法

什么情况是需要包装的呢？我们可以按下`F5`快捷键进入该方法里面去看一看，如下

```java
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
		if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
			return bean;
		}
		if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
			return bean;
		}
		if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
			this.advisedBeans.put(cacheKey, Boolean.FALSE);
			return bean;
		}

		// 重点在这
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
		if (specificInterceptors != DO_NOT_PROXY) {
			this.advisedBeans.put(cacheKey, Boolean.TRUE);
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			this.proxyTypes.put(cacheKey, proxy.getClass());
			return proxy;
		}

		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}
```

我们继续按下`F6`快捷键让程序往下运行，可以看到：

- 首先是拿到`MathCalculator`这个bean的名称（即`mathCalculator`），然后再来判断名为`targetSourcedBeans`的`Set`集合里面是否包含有这个`bean`的名称，只不过此时该Set集合是一个空集合。

- 接着再来判断名为`advisedBeans`的`Map`集合里面是否包含有当前`bean`的名称。我在前面也说过了`advisedBeans`这个东东，它就是一个`Map`集合，里面保存了所有需要增强的`bean`的名称。

  由于这儿是第一次来处理当前`bean`，所以名为`advisedBeans`的`Map`集合里面是不包含`MathCalculator`这个`bean`的名称的。

- 紧接着再来判断当前`bean`是否是基础类型，或者是否是切面（即标注了`@Aspect`注解的）。这儿是怎样来判断的，之前我已经详细地讲过了，故略过。

上面这些东东都不是我们要关注的内容，我们重点要关注的内容其实是下面这个叫`getAdvicesAndAdvisorsForBean`的方法。

从该方法上面的注释中可以得知，它是用于创建代理对象的，从该方法的名称上（见名知义），我们也可以知道它是来获取当前bean的通知方法以及那些增强器的。

6. ### **获取当前bean的所有增强器**

调用`getAdvicesAndAdvisorsForBean()`方法获取当前`bean`的所有增强器，也就是那些通知方法，最终封装成这样一个`Object[] specificInterceptors`数组。

到底是怎么来获取当前bean的所有增强器的呢？我们可以按下`F5`快捷键进入`getAdvicesAndAdvisorsForBean()`方法里面去看一看，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/84.jpg)

可以看到，又会调用`findEligibleAdvisors()`方法来获取`MathCalculator`这个类型的所有增强器，也可以说成是可用的增强器。它又是怎么获取的呢？我们可以按下`F5`快捷键进入`findEligibleAdvisors()`方法里面去看一看，如下图所示，可以看到会先调用一个`findCandidateAdvisors()`方法来获取候选的所有增强器。

```java
	protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		List<Advisor> candidateAdvisors = findCandidateAdvisors();
		List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
		extendAdvisors(eligibleAdvisors);
		if (!eligibleAdvisors.isEmpty()) {
			eligibleAdvisors = sortAdvisors(eligibleAdvisors);
		}
		return eligibleAdvisors;
	}
```

候选的所有增强器，前面我也说过了，有4个，就是切面里面定义的那4个通知方法。

按下`F6`快捷键让程序往下运行，可以看到会调用一个`findAdvisorsThatCanApply()`方法，见名知义，该方法是来找到那些可用的增强器的，以便可以应用到目标对象里面的目标方法中。

现在所要做的事情就是，**找到候选的所有增强器，也就是说是来找哪些通知方法是需要切入到当前bean的目标方法中的。**

怎么找呢？我们继续按下`F5`快捷键进入`findAdvisorsThatCanApply()`方法里面去看一看，如下图所示，可以看到它是用`AopUtils`工具类来找到所有能用的增强器（通知方法）的。

```java
	protected List<Advisor> findAdvisorsThatCanApply(
			List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {

		ProxyCreationContext.setCurrentProxiedBeanName(beanName);
		try {
			return AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
		}
		finally {
			ProxyCreationContext.setCurrentProxiedBeanName(null);
		}
	}
```

又是怎么找的呢？我们继续按下`F5`快捷键进入`AopUtils`工具类的`findAdvisorsThatCanApply()`方法里面去看一看，如下图所示。

```java
	public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
		if (candidateAdvisors.isEmpty()) {
			return candidateAdvisors;
		}
		List<Advisor> eligibleAdvisors = new ArrayList<>();
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
				eligibleAdvisors.add(candidate);
			}
		}
		boolean hasIntroductions = !eligibleAdvisors.isEmpty();
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor) {
				// already processed
				continue;
			}
			if (canApply(candidate, clazz, hasIntroductions)) {
				eligibleAdvisors.add(candidate);
			}
		}
		return eligibleAdvisors;
	}
```

在按下`F6`快捷键让程序往下运行的过程中，我们可以看到，先是定义了一个保存可用增强器的`LinkedList`集合，即`eligibleAdvisors`。然后通过下面的一个`for`循环来遍历每一个增强器，在遍历的过程中，可以看到有两个`&&`判断条件，前面的那个是来判断每一个增强器是不是`IntroductionAdvisor`这种类型的，很明显，每一个增强器并不是这种类型的，它是`InstantiationModelAwarePointcutAdvisor`这种类型的，前面我也说过了。所以，程序压根就不会进入到这个if判断语句中。

程序继续往下运行，这时我们会看到还有一个`for`循环，它同样是来遍历每一个增强器的，在遍历的过程中，可以看到先是来判断每一个增强器是不是`IntroductionAdvisor`这种类型的，但很显然，并不是，然后再来利用`canApply()`方法判断每一个增强器是不是可用的，那什么是叫可用的呢？

我们可以按下`F5`快捷键进入canApply()方法里面去看一看，如下图所示。

```java
public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
   if (advisor instanceof IntroductionAdvisor) {
      return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
   }
   else if (advisor instanceof PointcutAdvisor) {
      PointcutAdvisor pca = (PointcutAdvisor) advisor;
      return canApply(pca.getPointcut(), targetClass, hasIntroductions);
   }
   else {
      // It doesn't have a pointcut so we assume it applies.
      return true;
   }
}
```

在按下`F6`快捷键让程序往下运行的过程中，我们可以看到，这一块的逻辑就是用`PointcutAdvisor`（切入点表达式）开始来算一下每一个通知方法能不能匹配上，现在每一个增强器（通知方法）都是能匹配上的哟

继续按下`F6`快捷键让程序往下运行，可以看到，现在程序是回到了`findAdvisorsThatCanApply()`方法的第二个for循环中

由于现在第一个增强器（`logStart()`方法）是能匹配上的，即它肯定是能切入到目标对象的目标方法中的，也就是说这个增强器是可用的，所以它会被添加到名为`eligibleAdvisors的LinkedList`集合里面。

继续按下`F6`快捷键让程序往下运行，就会循环判断接下来的每一个增强器能不能用，若能用则添加到名为`eligibleAdvisors`的`LinkedList`集合中。

接着，继续按下`F6`快捷键让程序往下运行，一直运行回`findEligibleAdvisors()`方法中

```java
	protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
		List<Advisor> candidateAdvisors = findCandidateAdvisors();
		List<Advisor> eligibleAdvisors = findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
		extendAdvisors(eligibleAdvisors);
		if (!eligibleAdvisors.isEmpty()) {
			eligibleAdvisors = sortAdvisors(eligibleAdvisors);
		}
		return eligibleAdvisors;
	}
```

至此，终于**获取到能在当前bean中使用的增强器**了。

继续按下`F6`快捷键让程序往下运行，可以看到，在该方法中还对增强器做了一些排序，也就是说调用哪些通知方法，它们都是有顺序的。

继续按下`F6`快捷键让程序往下运行，这时程序会运行回`getAdvicesAndAdvisorsForBean()`方法中，最终能在当前`bean`中使用的增强器就获取到了，要是没获取到呢（即advisors集合为空），那么就会返回一个`DO_NOT_PROXY`，这个`DO_NOT_PROXY`其实就是`null`。很显然，这儿是获取到了，`advisors`集合并不会为空，所以程序最终会运行到下面这行代码处。

```java
	protected Object[] getAdvicesAndAdvisorsForBean(
			Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {

		List<Advisor> advisors = findEligibleAdvisors(beanClass, beanName);
		if (advisors.isEmpty()) {
			return DO_NOT_PROXY;
		}
		return advisors.toArray();
	}
```

继续按下`F6`快捷键让程序往下运行，这时程序会运行回`wrapIfNecessary()`方法中，如下图所示。

现在这个叫`specificInterceptors`的`Object[]`数组里面已经具有了那些指定好的增强器，这些增强器其实就是要拦截目标方法执行的

**小结**

以上这一小节的流程，我们可以归纳为：

1. 找到候选的所有增强器，也就是说是来找哪些通知方法是需要切入到当前`bean`的目标方法中的
2. 获取到能在当前`bean`中使用的增强器
3. 给增强器排序

7. ### 保存当前bean在advisedBeans中，表示这个当前bean已经被增强处理了

接下来，继续按下`F6`快捷键让程序往下运行，当程序运行到下面这一行代码时，就会将当前bean添加到名为advisedBeans的Map集合中，表示这个当前bean已经被增强处理了。

```java
	protected Object wrapIfNecessary(Object bean, String beanName, Object cacheKey) {
		if (StringUtils.hasLength(beanName) && this.targetSourcedBeans.contains(beanName)) {
			return bean;
		}
		if (Boolean.FALSE.equals(this.advisedBeans.get(cacheKey))) {
			return bean;
		}
		if (isInfrastructureClass(bean.getClass()) || shouldSkip(bean.getClass(), beanName)) {
			this.advisedBeans.put(cacheKey, Boolean.FALSE);
			return bean;
		}

		// Create proxy if we have advice.
		Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName, null);
        //如果当前Bean是需要增强的,那么就会进入到if判断语句中,因为在这儿已经获取到了这些可用的增强器,这就表明specificInterceptors!=null,specificInterceptors!=null程序自然就会进入到if判断语句中
		if (specificInterceptors != DO_NOT_PROXY) {
			this.advisedBeans.put(cacheKey, Boolean.TRUE);
			Object proxy = createProxy(
					bean.getClass(), beanName, specificInterceptors, new SingletonTargetSource(bean));
			this.proxyTypes.put(cacheKey, proxy.getClass());
			return proxy;
		}

		this.advisedBeans.put(cacheKey, Boolean.FALSE);
		return bean;
	}
```

当程序继续往下运行时，会发现有一个`createProxy()`方法，这个方法非常重要，它是来创建代理对象的。

下面我们主要来研究一下createProxy()方法。

8. #### **若当前bean需要增强，则创建当前bean的代理对象**

当程序运行到`createProxy()`方法处时，就会创建当前`bean`的代理对象，那么这个代理对象怎么创建的呢？如下图所示

```java
	protected Object createProxy(Class<?> beanClass, @Nullable String beanName,
			@Nullable Object[] specificInterceptors, TargetSource targetSource) {

		if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
			AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
		}

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.copyFrom(this);

		if (!proxyFactory.isProxyTargetClass()) {
			if (shouldProxyTargetClass(beanClass, beanName)) {
				proxyFactory.setProxyTargetClass(true);
			}
			else {
				evaluateProxyInterfaces(beanClass, proxyFactory);
			}
		}
		//先拿到所有增强器
		Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
        //增强保存到代理工厂
		proxyFactory.addAdvisors(advisors);
		proxyFactory.setTargetSource(targetSource);
		customizeProxyFactory(proxyFactory);

		proxyFactory.setFrozen(this.freezeProxy);
		if (advisorsPreFiltered()) {
			proxyFactory.setPreFiltered(true);
		}
		//利用代理工厂帮我们创建一个代理对象
		return proxyFactory.getProxy(getProxyClassLoader());
	}
```

继续按下`F6`快捷键让程序往下运行，可以看到是先拿到所有增强器，然后再把这些增强器保存到代理工厂（即`proxyFactory`）中。

那它是怎么帮我们创建代理对象的呢？这得进入到代理工厂的getProxy()方法里面去看一看了，就能真正进入到代理工厂的getProxy()方法里面了，如下图所示。

```java
	public Object getProxy(@Nullable ClassLoader classLoader) {
		return createAopProxy().getProxy(classLoader);
	}
```

可以看到，会先调用`createAopProxy()`方法来创建AOP代理。我们按下`F5`快捷键进入该方法中去看一看，如下图所示，可以看到是先得到AOP代理的创建工厂，然后再来创建AOP代理的。

```java
	protected final synchronized AopProxy createAopProxy() {
		if (!this.active) {
			activate();
		}
		return getAopProxyFactory().createAopProxy(this);
	}
```

`getAopProxyFactory()`方法就调用完了，也即`AOP`代理的创建工厂就获取到了。接下来，就是调用`createAopProxy()`方法为this对象创建`AOP`代理了。

那到底是怎么来创建创建`AOP`代理的呢？我们可以按下`F5`快捷键进入`createAopProxy()`方法中去看一看，如下图所示，这时Spring会自动决定，是为组件创建jdk的动态代理呢，还是为组件创建cglib的动态代理？

```java
	public AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException {
		if (config.isOptimize() || config.isProxyTargetClass() || hasNoUserSuppliedProxyInterfaces(config)) {
			Class<?> targetClass = config.getTargetClass();
			if (targetClass == null) {
				throw new AopConfigException("TargetSource cannot determine target class: " +
						"Either an interface or a target is required for proxy creation.");
			}
			if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) {
				return new JdkDynamicAopProxy(config);
			}
			return new ObjenesisCglibAopProxy(config);
		}
		else {
			return new JdkDynamicAopProxy(config);
		}
	}
```

也就是说，会在这儿为组件创建代理对象，并且有两种形式的代理对象，它们分别是：

- 一种是`JdkDynamicAopProxy`这种形式的，即jdk的动态代理
- 一种是`ObjenesisCglibAopProxy`这种形式的，即cglib的动态代理

那么`Spring`是怎么自动决定是要创建`jdk`的动态代理，还是要创建`cglib`的动态代理呢？如果当前类是有实现接口的，那么就使用`jdk`来创建动态代理，如果当前类没有实现接口，例如`MathCalculator`类，此时jdk是没法创建动态代理的，那么自然就得使用cglib来创建动态代理了。而且，咱们可以让Spring强制使用`cglib`

也就是说，不管怎么样，`Spring`都会在这儿为我们创建一个代理对象。很显然，现在是使用`cglib`来创建动态代理的。

我们继续按下`F6`快捷键让程序往下运行，一直让程序运行回`wrapIfNecessary()`方法中，如下图所示，这时`createProxy()`方法返回的`proxy`对象是一个通过`Spring cglib`增强了的代理对象。

![](http://120.77.237.175:9080/photos/springanno/86.jpg)

继续按下`F6`快捷键让程序往下运行，一直让程序运行到`applyBeanPostProcessorsAfterInitialization()`方法中，如下

```java
	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

至此，刚才的那个`wrapIfNecessary()`方法就完全算是调用完了。

经过上面的分析，我们知道，**`wrapIfNecessary()`方法调用完之后，最终会给容器中返回当前组件使用`cglib`增强了的代理对象。**

对于`MathCalculator`这个组件来说，以后从容器中获取到的就是该组件的代理对象，然后在执行其目标方法时，这个代理对象就会执行切面里面的通知方法。

**小结**

1. 获取所有增强器，所谓的增强器就是切面里面的那些通知方法。
2. 然后再把这些增强器保存到代理工厂（即`proxyFactory`）中。
3. 为当前组件创建代理对象，并且会有两种形式的代理对象，它们分别如下，最终Spring会自动决定，是为当前组件创建`jdk`的动态代理，还是创建`cglib`的动态代理。
   - 一种是`JdkDynamicAopProxy`这种形式的，即`jdk`的动态代理
   - 一种是`ObjenesisCglibAopProxy`这种形式的，即`cglib`的动态代理



### 目标方法的拦截逻辑

打开IOCTest_AOP测试类的代码，并在目标方法运行的地方打上一个断点

![](http://120.77.237.175:9080/photos/springanno/87.jpg)

当程序运行到目标方法处之后，我们就得进入该方法中来看一看其执行流程了。不过在此之前，我们来看一下从容器中得到的MathCalculator对象，可以看到它确实是使用cglib增强了的代理对象，它里面还封装了好多的数据，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/88.png)

也就是说**容器中存放的这个增强后的代理对象里面保存了所有通知方法的详细信息，以及还包括要切入的目标对象。**

接下来，我们按下`F5`快捷键进入目标方法中去看一看，进入到了`CglibAopProxy`类的`intercept()`方法中，如下图所示。

```java
		@Override
		@Nullable
		public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
			Object oldProxy = null;
			boolean setProxyContext = false;
			Object target = null;
			TargetSource targetSource = this.advised.getTargetSource();
			try {
				if (this.advised.exposeProxy) {
					// Make invocation available if necessary.
					oldProxy = AopContext.setCurrentProxy(proxy);
					setProxyContext = true;
				}
				// Get as late as possible to minimize the time we "own" the target, in case it comes from a pool...
				target = targetSource.getTarget();
				Class<?> targetClass = (target != null ? target.getClass() : null);
				List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
				Object retVal;
				// Check whether we only have one InvokerInterceptor: that is,
				// no real advice, but just reflective invocation of the target.
				if (chain.isEmpty() && Modifier.isPublic(method.getModifiers())) {
					// We can skip creating a MethodInvocation: just invoke the target directly.
					// Note that the final invoker must be an InvokerInterceptor, so we know
					// it does nothing but a reflective operation on the target, and no hot
					// swapping or fancy proxying.
					Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
					retVal = methodProxy.invoke(target, argsToUse);
				}
				else {
					// We need to create a method invocation...
					retVal = new CglibMethodInvocation(proxy, target, method, args, targetClass, chain, methodProxy).proceed();
				}
				retVal = processReturnType(proxy, target, method, retVal);
				return retVal;
			}
			finally {
				if (target != null && !targetSource.isStatic()) {
					targetSource.releaseTarget(target);
				}
				if (setProxyContext) {
					// Restore old proxy.
					AopContext.setCurrentProxy(oldProxy);
				}
			}
		}
```

见名知义，这个方法就是来拦截目标方法的执行的。也就是说，在执行目标方法之前，先让这个`AOP`代理来拦截一下。接下来，我们就来看看它的拦截逻辑。

1. #### **根据ProxyFactory对象获取将要执行的目标方法的拦截器链**

在按下`F6`快捷键让程序往下运行的过程中，可以看到前面都是一些变量的声明，直至程序运行到下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/89.jpg)

这时，就拿到了我们要切的目标对象，即`MathCalculator`对象。接下来，我们就得仔细研究一下`getInterceptorsAndDynamicInterceptionAdvice()`方法了。

它的意思是说要根据`ProxyFactory`对象获取将要执行的目标方法的拦截器链（chain，chain翻译过来就是链的意思），其中，advised变量代表的是`ProxyFactory`对象，`method`参数代表的是即将要执行的目标方法（即`div()`方法）。

那么，目标方法的拦截器链到底是怎么获取的呢？这才是我们关注的核心。听起来，它是来拦截目标方法前后进行执行的，而在目标方法前后要执行的，其实就是切面里面的通知方法。所以，我们可以大胆猜测，这个拦截器链应该是来说先是怎么执行通知方法，然后再来怎么执行目标方法的。

回到主题，如果有拦截器链，那么这个拦截器链是怎么获取的呢？我们可以按下`F5`快捷键进入`getInterceptorsAndDynamicInterceptionAdvice()`方法中去看一看，进来以后可以看到有一些缓存，缓存就是要把这些获取到的东西保存起来，方便下一次直接使用。

```java
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, @Nullable Class<?> targetClass) {
		MethodCacheKey cacheKey = new MethodCacheKey(method);
		List<Object> cached = this.methodCache.get(cacheKey);
		if (cached == null) {
            //可以看到，它是利用advisorChainFactory来获取目标方法的拦截器链的。那又是怎么获取的呢?按下F5快捷键进入getInterceptorsAndDynamicInterceptionAdvice()方法中看一看便知道了
			cached = this.advisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice(
					this, method, targetClass);
			this.methodCache.put(cacheKey, cached);
		}
		return cached;
	}
```

```java
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(
			Advised config, Method method, @Nullable Class<?> targetClass) {

		// This is somewhat tricky... We have to process introductions first,
		// but we need to preserve order in the ultimate list.
		AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
		Advisor[] advisors = config.getAdvisors();
        //1. 先是在这里创建List<Object> interceptorList
		List<Object> interceptorList = new ArrayList<>(advisors.length);
		Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
		Boolean hasIntroductions = null;
		//2. 在这儿遍历所有的增强器
		for (Advisor advisor : advisors) {
			if (advisor instanceof PointcutAdvisor) {
				// Add it conditionally.
				PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
				if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
					MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
					boolean match;
					if (mm instanceof IntroductionAwareMethodMatcher) {
						if (hasIntroductions == null) {
							hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
						}
						match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
					}
					else {
						match = mm.matches(method, actualClass);
					}
					if (match) {
						MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
						if (mm.isRuntime()) {
							// Creating a new object instance in the getInterceptors() method
							// isn't a problem as we normally cache created chains.
							for (MethodInterceptor interceptor : interceptors) {
								interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
							}
						}
						else {
                            //3. 接着在这里为该集合添加值
							interceptorList.addAll(Arrays.asList(interceptors));
						}
					}
				}
			}
			else if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
					Interceptor[] interceptors = registry.getInterceptors(advisor);
                    //3. 接着在这里为该集合添加值
					interceptorList.addAll(Arrays.asList(interceptors));
				}
			}
			else {
				Interceptor[] interceptors = registry.getInterceptors(advisor);
                //3. 接着在这里为该集合添加值
				interceptorList.addAll(Arrays.asList(interceptors));
			}
		}
		//4. 最后返回该集合
		return interceptorList;
	}
```

可以看到，先是在开头创建一个`List<Object> interceptorList`集合，然后在后面遍历所有增强器，并为该集合添加值，最后返回该集合。最终，整个拦截器链就会被封装到List集合中。接下来，我就来详细讲讲`getInterceptorsAndDynamicInterceptionAdvice()`方法，看这个方法都做了些什么？

2. #### **先创建一个List集合，来保存所有拦截器**

注意，在开头创建`List`集合时，其实已经为该集合赋好了长度，长度到底是多少呢？如下图所示

![](http://120.77.237.175:9080/photos/springanno/90.jpg)

很显然，该集合的长度是5，1个默认的`ExposeInvocationInterceptor`和4个增强器，这个List集合虽然有长度，但是现在是空的。另外，我们也知道，第一个增强器其实是一个异常通知，即`AspectJAfterThrowingAdvice`，因为我已在前面分析过了。

按下`F6`快捷键让程序往下运行，这时会有一个for循环，它是来遍历所有的Advisor的（一共有5个），每遍历出一个Advisor，便来判断它是不是`PointcutAdvisor`（和切入点有关的Advisor），若是则把这个Advisor传过来，然后包装成一个``MethodInterceptor[]`类型的`interceptors`，接着再把它添加到一开始创建的List集合中。

![](http://120.77.237.175:9080/photos/springanno/91.jpg)

如果遍历出的`Advisor`不是`PointcutAdvisor`，而是`IntroductionAdvisor`，那么怎么办呢？同样是将这个`Advisor`传过来，然后包装成一个`Interceptor[]`类型的`interceptors`，最后再把它添加到一开始创建的List集合中。

```java
else if (advisor instanceof IntroductionAdvisor) {
    IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
    if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
        Interceptor[] interceptors = registry.getInterceptors(advisor);
        interceptorList.addAll(Arrays.asList(interceptors));
    }
}
```

或者，直接将遍历出的Advisor传进来，然后包装成一个`Interceptor[]`类型的`interceptors`，最后再把它添加到一开始创建的List集合中

```java
else {
    Interceptor[] interceptors = registry.getInterceptors(advisor);
    interceptorList.addAll(Arrays.asList(interceptors));
}
```

也就是说，遍历所有的增强器，并将这些增强器封装成一个`Interceptor`。

3. ### **遍历所有的增强器，将其转为Interceptor**

我们按下`F6`快捷键让程序往下运行，即进入for循环中去遍历所有的Advisor。此时，inspect一下advisor变量的值，便能知道第一个增强器是ExposeInvocationInterceptor，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/92.jpg)

然后来判断这个`Advisor`是不是`PointcutAdvisor`，按下`F6`快捷键让程序继续往下运行，发现能进入到if判读语句中，说明这个`Advisor`确实是`PointcutAdvisor`。继续让程序往下运行，即：

```java
MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
```

4. **转换第一个增强器**

我们按下`F5`快捷键进入`getInterceptors()`方法里面去一探究竟

```java
	@Override
	public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
		List<MethodInterceptor> interceptors = new ArrayList<>(3);
		Advice advice = advisor.getAdvice();
		if (advice instanceof MethodInterceptor) {
			interceptors.add((MethodInterceptor) advice);
		}
		for (AdvisorAdapter adapter : this.adapters) {
			if (adapter.supportsAdvice(advice)) {
				interceptors.add(adapter.getInterceptor(advisor));
			}
		}
		if (interceptors.isEmpty()) {
			throw new UnknownAdviceTypeException(advisor.getAdvice());
		}
		return interceptors.toArray(new MethodInterceptor[0]);
	}
```

该方法的逻辑其实蛮简单的，就是**先拿到增强器，然后判断这个增强器是不是`MethodInterceptor`这种类型的，若是则直接添加进名为`interceptors`的`List`集合里面，若不是则使用`AdvisorAdapter`（增强器的适配器）将这个增强器转为`MethodInterceptor`这种类型，然后再添加进`List`集合里面，反正不管如何，最后都会将该`List`集合转换成`MethodInterceptor`数组返回出去。**

按下`F6`快捷键让程序往下运行，发现程序会进入到第一个if判断语句中，说明拿到的第一个增强器（即ExposeInvocationInterceptor）是`MethodInterceptor`这种类型的，那么自然就会将其添加进List集合中。

![](http://120.77.237.175:9080/photos/springanno/93.jpg)

继续按下`F6`快捷键让程序往下运行，inspect一下adapters变量的值，发现它里面有3个增强器的适配器，它们分别是：

1. `MethodBeforeAdviceAdapter`：专门来转前置通知的
2. `AfterReturningAdviceAdapter`：专门来返回置通知的
3. `ThrowsAdviceAdapter`：专门来异常置通知的

此时，会使用到以上这3个增强器的适配器吗？并不会，因为程序继续往下运行的过程中，并不会进入到for循环里面的if判断语句中。

接着，让程序继续往下运行，直至`getInterceptors()`方法执行完毕，并且该方法运行完会返回一个`MethodInterceptor`数组，该数组只有一个元素，即拿到的第一个增强器（即`ExposeInvocationInterceptor`）。

![](http://120.77.237.175:9080/photos/springanno/94.jpg)

让程序继续往下运行，这时程序就运行回`getInterceptorsAndDynamicInterceptionAdvice()`方法中了，如下图

![](http://120.77.237.175:9080/photos/springanno/95.jpg)

```
传递过去的是第一个advisor,它里面特有的增强器是ExposeInvocationInterceptor,而返回的是一个ExposeInvocationInterceptor数组,该数组只有一个元素,并且
```

接着进入`getInterceptors()`打上断点,看其他的增强器是怎么转成`Interceptor`的。

```java
@Override
public MethodInterceptor[] getInterceptors(Advisor advisor) throws UnknownAdviceTypeException {
   List<MethodInterceptor> interceptors = new ArrayList<>(3);
   Advice advice = advisor.getAdvice();
   if (advice instanceof MethodInterceptor) {
      interceptors.add((MethodInterceptor) advice);
   }
   for (AdvisorAdapter adapter : this.adapters) {
      if (adapter.supportsAdvice(advice)) {
         interceptors.add(adapter.getInterceptor(advisor));
      }
   }
   if (interceptors.isEmpty()) {
      throw new UnknownAdviceTypeException(advisor.getAdvice());
   }
   return interceptors.toArray(new MethodInterceptor[0]);
}
```

5. #### **转换第二个增强器**

此时，按下`F8`快捷键让程序运行到下一个断点，可以看到现在传递过来的是第二个`Advisor`，该`Advisor`持有的增强器是`AspectJAfterThrowingAdvice`，即异常通知。

![](http://120.77.237.175:9080/photos/springanno/96.jpg)

在按下`F6`快捷键让程序往下运行的过程中，可以看到，先是判断拿到的第二个增强器是不是`MethodInterceptor`这种类型的。但很显然，它正好就是这种类型，你只要查看一下`AspectJAfterThrowingAdvice`类的源码便知道了，如下图所示，该类实现了MethodInterceptor接口。

```java
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
		implements MethodInterceptor, AfterAdvice, Serializable {
	...		
}
```

既然是，那么自然就会将其添加进List集合中

继续按下`F6`快捷键让程序往下运行，此时，会使用到那3个增强器的适配器吗？并不会，因为程序继续往下运行的过程中，并不会进入到for循环里面的if判断语句中。

当程序运行至`getInterceptors()`方法的最后一行代码时，该方法会返回一个`MethodInterceptor`数组，并且该数组只有一个元素，即拿到的第二个增强器（即`AspectJAfterThrowingAdvice`）。

![](http://120.77.237.175:9080/photos/springanno/97.jpg)

6. #### **转换第三个增强器**

按下`F8`快捷键让程序运行到下一个断点，可以看到现在传递过来的是第三个`Advisor`，该Advisor持有的增强器是`AspectJAfterReturningAdvice`，即返回通知。

![](http://120.77.237.175:9080/photos/springanno/98.jpg)

按下`F6`快捷键让程序往下运行，发现程序并不会进入到第一个if判断语句中，说明拿到的第三个增强器（即`AspectJAfterReturningAdvice`）并不是`MethodInterceptor`这种类型。也就是说**有些通知方法是实现了`MethodInterceptor`接口的，也有些不是。** 如果不是的话，那么该怎么办呢？这时，就要使用到增强器的适配器了。

让程序继续往下运行，可以看到现在使用会遍历`this.adapters`的里的三个增强器的适配器,当遍历到到第二个是AfterReturningAdviceAdapter，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/99.jpg)

该适配器是专门来转返回通知的，很显然它肯定是支持转换这个`AspectJAfterReturningAdvice`（返回通知）的。那么，问题来了，该适配器是怎么将`AspectJAfterReturningAdvice`（返回通知）转换为`Interceptor`的呢？进入`getInterceptor()`方法里面一看便知，如下图所示，实际上就是拿到`Advice`（增强器），并将其包装成一个`Interceptor`而已。

```java
class AfterReturningAdviceAdapter implements AdvisorAdapter, Serializable {

	@Override
	public boolean supportsAdvice(Advice advice) {
		return (advice instanceof AfterReturningAdvice);
	}

	@Override
	public MethodInterceptor getInterceptor(Advisor advisor) {
		AfterReturningAdvice advice = (AfterReturningAdvice) advisor.getAdvice();
		return new AfterReturningAdviceInterceptor(advice);
	}

}
```

当程序运行至`getInterceptors()`方法的最后一行代码时，该方法会返回一个`MethodInterceptor`数组，并且该数组只有一个元素，即拿到的第三个增强器（即`AfterReturningAdviceInterceptor`）。

![](http://120.77.237.175:9080/photos/springanno/100.jpg)

7. #### **转换第四个增强器**

按下`F8`快捷键让程序运行到下一个断点，可以看到现在传递过来的是第四个`Advisor`，该Advisor持有的增强器是`AspectJAfterAdvice`，即后置通知。

![](http://120.77.237.175:9080/photos/springanno/101.jpg)

然后，按下`F6`快捷键让程序往下运行，发现程序会进入到第一个if判断语句中，说明拿到的第四个增强器（即`AspectJAfterAdvice`）是`MethodInterceptor`这种类型的，那么自然就会将其添加进List集合中。

继续按下`F6`快捷键让程序往下运行，此时，会使用到那3个增强器的适配器吗？并不会，因为程序继续往下运行的过程中，并不会进入到for循环里面的if判断语句中。

当程序运行至`getInterceptors()`方法的最后一行代码时，该方法会返回一个`MethodInterceptor`数组，并且该数组只有一个元素，即拿到的第四个增强器（即`AspectJAfterAdvice`）

![](http://120.77.237.175:9080/photos/springanno/102.jpg)

8. #### **转换第五个增强器**

按下`F8`快捷键让程序运行到下一个断点，可以看到现在传递过来的是第五个`Advisor`，该`Advisor`持有的增强器是`AspectJMethodBeforeAdvice`，即前置通知

![](http://120.77.237.175:9080/photos/springanno/103.jpg)

然后，按下`F6`快捷键让程序往下运行，发现程序并不会进入到第一个if判断语句中，说明拿到的第五个增强器（即`AspectJMethodBeforeAdvice`）并不是`MethodInterceptor`这种类型。如果不是的话，那么该怎么办呢？这时，就要使用到增强器的适配器了。

让程序继续往下运行，可以看到现在使用的增强器的适配器是`MethodBeforeAdviceAdapter`，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/104.jpg)

该适配器是专门来转前置通知的，很显然它肯定是支持转换这个`AspectJMethodBeforeAdvice`（前置通知）的。该适配器是怎么转换的呢？其实很简单，就是拿到`Advice`（增强器），然后将其包装成一个`Interceptor`而已，这前面我也讲过了。

```java
class MethodBeforeAdviceAdapter implements AdvisorAdapter, Serializable {

	@Override
	public boolean supportsAdvice(Advice advice) {
		return (advice instanceof MethodBeforeAdvice);
	}

	@Override
	public MethodInterceptor getInterceptor(Advisor advisor) {
		MethodBeforeAdvice advice = (MethodBeforeAdvice) advisor.getAdvice();
		return new MethodBeforeAdviceInterceptor(advice);
	}

}
```

当程序运行至`getInterceptors()`方法的最后一行代码时，该方法会返回一个`MethodInterceptor`数组，并且该数组只有一个元素，即拿到的第五个增强器（即`MethodBeforeAdviceInterceptor`）

![](http://120.77.237.175:9080/photos/springanno/105.jpg)

接着，让程序继续往下运行，将整个转换流程走完，直至程序运行回`DefaultAdvisorChainFactory.getInterceptorsAndDynamicInterceptionAdvice()`方法中

紧接着，继续让程序往下运行，此时会返回一开始就创建的List集合

![](http://120.77.237.175:9080/photos/springanno/106.jpg)

可以看到，该List集合里面有5个拦截器，其中`AspectJAfterThrowingAdvice`和`AspectJAfterAdvice`这俩人家本来就是拦截器，而`AfterReturningAdviceInterceptor`和`MethodBeforeAdviceInterceptor`这俩是使用适配器重新转换之后的拦截器。

最后，继续让程序往下运行，直至运行回`CglibAopProxy`类的`intercept()`方法中，如下图所示

![](http://120.77.237.175:9080/photos/springanno/107.jpg)

此时，将要执行的目标方法的拦截器链就获取到了，**拦截器链里面保存的其实就是每一个通知方法**

9. #### **如果有拦截器链，那么怎么做呢**

**什么叫拦截器链呢？所谓的拦截器链其实就是每一个通知方法又被包装为了方法拦截器。** 之后，目标方法的执行，就要使用到这个拦截器链机制。

如果真的获取到了拦截器链，那么接下来会怎么做呢？很明显，这时程序会进入到`else`分支语句中，然后将需要执行的目标对象、目标方法以及拦截器链等所有相关信息传入`CglibMethodInvocation`类的有参构造器中，来创建一个`CglibMethodInvocation`对象，接着会调用其`proceed()`方法，并且该方法会有一个返回值

![](http://120.77.237.175:9080/photos/springanno/108.jpg)

接下来，我们就来看看到底是怎么来new这个CglibMethodInvocation对象的。先按下`F5`快捷键进入当前方法中，再按下`F7`快捷键从当前方法里面退出来，然后再按下`F5`快捷键进入当前方法中，这时程序会进入到CglibMethodInvocation匿名内部类的有参构造器中，如下

```java
public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method,
                             Object[] arguments, @Nullable Class<?> targetClass,
                             List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {

    super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);

    // Only use method proxy for public methods not derived from java.lang.Object
    this.methodProxy = (Modifier.isPublic(method.getModifiers()) &&
                        method.getDeclaringClass() != Object.class && !AopUtils.isEqualsMethod(method) &&
                        !AopUtils.isHashCodeMethod(method) && !AopUtils.isToStringMethod(method) ?
                        methodProxy : null);
}
```

`new`出来了这个`CglibMethodInvocation`对象。

`new`出来该对象以后，接下来就是来执行其`proceed()`方法了，相当于是来执行获取到的拦截器链，因为在`new`这个`CglibMethodInvocation对象的时候，会把拦截器链传过来，传过来以后，势必就要执行该拦截器链了，而整个的执行过程其实就是触发拦截器链的调用过程。

10. #### **如果没有拦截器链，那么直接执行目标方法**

获取完拦截器链之后，如果这个链是空的，也就是说并没有获取到拦截器链，那么程序就会进入到if判断语句中执行如下这行代码

```java
Object[] argsToUse = AopProxyUtils.adaptArgumentsIfNecessary(method, args);
retVal = methodProxy.invoke(target, argsToUse);
```

这行代码的意思是什么呢？仔细看一下这行代码上面的注释，我们就能知道，它的意思是说将会跳过创建一个`MethodInvocation`对象，然后直接就来执行目标对象中的目标方法

### 拦截器链的执行过程

同在去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，

```java
	private static class CglibMethodInvocation extends ReflectiveMethodInvocation {

		@Nullable
		private final MethodProxy methodProxy;

		public CglibMethodInvocation(Object proxy, @Nullable Object target, Method method,
				Object[] arguments, @Nullable Class<?> targetClass,
				List<Object> interceptorsAndDynamicMethodMatchers, MethodProxy methodProxy) {

			super(proxy, target, method, arguments, targetClass, interceptorsAndDynamicMethodMatchers);

			// Only use method proxy for public methods not derived from java.lang.Object
			this.methodProxy = (Modifier.isPublic(method.getModifiers()) &&
					method.getDeclaringClass() != Object.class && !AopUtils.isEqualsMethod(method) &&
					!AopUtils.isHashCodeMethod(method) && !AopUtils.isToStringMethod(method) ?
					methodProxy : null);
		}

		@Override
		@Nullable
		public Object proceed() throws Throwable {
			try {
				return super.proceed();
			}
			catch (RuntimeException ex) {
				throw ex;
			}
			catch (Exception ex) {
				if (ReflectionUtils.declaresException(getMethod(), ex.getClass())) {
					throw ex;
				}
				else {
					throw new UndeclaredThrowableException(ex);
				}
			}
		}

		/**
		 * Gives a marginal performance improvement versus using reflection to
		 * invoke the target when invoking public methods.
		 */
		@Override
		protected Object invokeJoinpoint() throws Throwable {
			if (this.methodProxy != null) {
				return this.methodProxy.invoke(this.target, this.arguments);
			}
			else {
				return super.invokeJoinpoint();
			}
		}
	}
```

可以看到，首先调用父类的proceed(),点击进去

```java
	@Override
	@Nullable
	public Object proceed() throws Throwable {
		// We start with an index of -1 and increment early.
		if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return invokeJoinpoint();
		}

		Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
			// Evaluate dynamic method matcher here: static part will already have
			// been evaluated and found to match.
			InterceptorAndDynamicMethodMatcher dm =
					(InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
			Class<?> targetClass = (this.targetClass != null ? this.targetClass : this.method.getDeclaringClass());
			if (dm.methodMatcher.matches(this.method, targetClass, this.arguments)) {
				return dm.interceptor.invoke(this);
			}
			else {
				// Dynamic matching failed.
				// Skip this interceptor and invoke the next in the chain.
				return proceed();
			}
		}
		else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}
```

这儿首先是有一个成员变量，即`currentInterceptorIndex`，翻译过来应该是当前拦截器的索引。索引的默认值是-1，点进该成员变量里面一看便知。

```java
private int currentInterceptorIndex = -1;
```

然后，会在这儿做一个判断，即判断`currentInterceptorIndex`成员变量的值（也即索引的值）是否等于`this.interceptorsAndDynamicMethodMatchers.size() - 1`。你可能要问了，这个`interceptorsAndDynamicMethodMatchers`到底是什么啊？`inspect`一下它便知道了，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/109.jpg)

可以看到，`interceptorsAndDynamicMethodMatchers`其实就是一个`ArrayList`集合，它里面保存有5个拦截器。

也就是说，在这儿是来判断-1是否等于5-1的，很显然，并不相等，那什么时候相等呢？这得分两种情况来看：

1. 如果我们没有获取到拦截器链，那么该`ArrayList`集合必然就是空的，此时就相当于是在判断-1是否等于0-1，你说等不等呢？
2. `currentInterceptorIndex`成员变量是来记录我们当前拦截器的索引的（从-1开始），有可能正好当前拦截器的索引为4，此时就相当于是在判断4是否等于5（拦截器总数）-1，你说等不等呢？

不管是哪种情况，程序都会进入到`i`f判断语句中。就以第一种情况来说，此时并没有拦截器链，那么必然就会调用`invokeJoinpoint()`方法。我们可以点进该方法里面去一探究竟，发现进入到了`ReflectiveMethodInvocation`类的`invokeJoinpoint()`方法中，如下图所示。

```java
	@Nullable
	protected Object invokeJoinpoint() throws Throwable {
		return AopUtils.invokeJoinpointUsingReflection(this.target, this.method, this.arguments);
	}
```

再点进`invokeJoinpointUsingReflection()`方法里面，发现其实就是利用反射来执行目标方法，如下图所示。

```java
	@Nullable
	public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args)
			throws Throwable {

		// Use reflection to invoke the method.
		try {
			ReflectionUtils.makeAccessible(method);
            //其实就是利用反射来执行目标方法
			return method.invoke(target, args);
		}
		catch (InvocationTargetException ex) {
			// Invoked method threw a checked exception.
			// We must rethrow it. The client won't see the interceptor.
			throw ex.getTargetException();
		}
		catch (IllegalArgumentException ex) {
			throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" +
					method + "] on target [" + target + "]", ex);
		}
		catch (IllegalAccessException ex) {
			throw new AopInvocationException("Could not access method [" + method + "]", ex);
		}
	}
```

所以，我们可以得出这样一个结论：**如果没有拦截器链，或者当前拦截器的索引和拦截器总数-1的大小一样，那么便直接执行目标方法。** 我们先分析到这，因为过一会就可以看到这个过程了。

好，我们还是回到`proceed()`方法里面，此时是来判断`currentInterceptorInde`成员变量的值（即-1）是否等于拦截器总数（5）-1的，很显然并不相等，所以程序并不会进入到if判断语句中。

按下`F6`快捷键让程序往下运行，运行至第162行代码处时，可以看到会先让`currentInterceptorIndex`成员变量自增，即由-1自增为0，然后再从拦截器链里面获取第0号拦截器，即`ExposeInvocationInterceptor`。

```java
//这时,会从拦截器链里面获取第0号拦截器,即ExposeInvocationInterceptor
Object interceptorOrInterceptionAdvice =
				this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
```

也就是说，在`proceed()`方法里面，我们会先来获取到第0号拦截器。第0号拦截器我们拿到以后，那么接下来该怎么办呢？继续按下`F6`快捷键让程序往下运行，如下

```java
return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
```

可以看到，会调用当前拦截器的`invoke()`方法，而且会将`this`指代的对象传入该方法中。那么`this`指代的又是哪一个对象呢？`inspect`一下`this`，我们便知道它指代的就是之前创建的`CglibMethodInvocation`对象

![](http://120.77.237.175:9080/photos/springanno/110.jpg)

接下来，我们就进去`invoke()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，可以看到，这儿是先从`invocation`里面get到一个`MethodInvocation`实例。

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		MethodInvocation oldInvocation = invocation.get();
		invocation.set(mi);
		try {
			return mi.proceed();
		}
		finally {
			invocation.set(oldInvocation);
		}
	}
```

你不禁就要问了，这个`invocation`是啥？这个`MethodInvocation`又是啥？点击`invocation`成员变量进去看一下，可以看到它是一个`ThreadLocal`，`ThreadLocal`就是同一个线程来共享数据的，它要共享的数据就是`MethodInvocation`实例，而这个`MethodInvocation`实例其实就是之前创建的`CglibMethodInvocation`对象。

```java
	private static final ThreadLocal<MethodInvocation> invocation =
			new NamedThreadLocal<>("Current AOP method invocation");
```

由于这是第一次，`ThreadLocal`里面还没有共享数据，因此接下来便会在当前线程中保存`CglibMethodInvocation`对象。然后就会来执行`CglibMethodInvocation`对象的`proceed()`方法

说白了，**执行拦截器的`invoke()`方法其实就是执行`CglibMethodInvocation`对象的`proceed()`方法。**

接下来，我们就再进去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，你会发现这又是同样熟悉的那套,又直接调用父类的`super.proceed()`，只不过现在是来判断`currentInterceptorIndex`成员变量的值（即0，因为自增过一次，所以已经由之前的-1变成了0）是否等于拦截器总数（5）-1的，很显然并不相等，所以程序并不会进入到if判断语句中

```
		@Override
		@Nullable
		public Object proceed() throws Throwable {
			try {
				return super.proceed();
			}
			catch (RuntimeException ex) {
				throw ex;
			}
			catch (Exception ex) {
				if (ReflectionUtils.declaresException(getMethod(), ex.getClass())) {
					throw ex;
				}
				else {
					throw new UndeclaredThrowableException(ex);
				}
			}
		}
```

继续按下`F6`快捷键让程序往下运行，运行至第162行代码处时，可以看到会先让`currentInterceptorIndex`成员变量自增，即由0自增为1，然后再从拦截器链里面获取第1号拦截器，即`AspectJAfterThrowingAdvice`

![](http://120.77.237.175:9080/photos/springanno/111.jpg)

也就是说，**每执行一次proceed()方法，当前拦截器的索引（即currentInterceptorIndex成员变量）都会自增一次。**

第1号拦截器我们拿到以后，那么接下来该怎么办呢？继续按下`F6`快捷键让程序往下运行，发现运行到如下

```java
return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
```

可以看到，又会调用当前拦截器的`invoke()`方法，并且会将`CglibMethodInvocation`对象传入该方法中。

这时，你会发现，现在是从第一个拦截器（即`ExposeInvocationInterceptor`）锁定到了下一个拦截器（即`AspectJAfterThrowingAdvice`），而且我们也看到了，所有拦截器都会调用`invoke()`方法。

接着，我们就再进去`invoke()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，可以看到，当前拦截器（即`AspectJAfterThrowingAdvice`）的`invoke()`方法的执行逻辑是下面这样子的。

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}
		catch (Throwable ex) {
			if (shouldInvokeOnThrowing(ex)) {
				invokeAdviceMethod(getJoinPointMatch(), null, ex);
			}
			throw ex;
		}
	}
```

可以看到又是来调用`CglibMethodInvocation`对象的`proceed()`方法，其中`mi`变量指代的就是`CglibMethodInvocation`对象。

至此，我们可以得出这样一个结论：**每执行一次proceed()方法，当前拦截器的索引（即`currentInterceptorIndex`成员变量）都会自增一次，并且还会拿到下一个拦截器。这个流程会不断地循环往复，直至拿到最后一个拦截器为止。**

接下来，我们就再进去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，你会发现又是同样熟悉的那套，只不过现在是来判断`currentInterceptorIndex`成员变量的值（即1，因为自增过一次，所以已经由之前的0变成了1）是否等于拦截器总数（5）-1的，很显然并不相等，所以程序并不会进入到if判断语句中

继续按下`F6`快捷键让程序往下运行，可以看到会先让`currentInterceptorIndex`成员变量自增，即由1自增为2，然后再从拦截器链里面获取第2号拦截器，即`AfterReturningAdviceInterceptor`。

![](http://120.77.237.175:9080/photos/springanno/112.jpg)

第2号拦截器我们拿到以后，那么接下来该怎么办呢？继续按下`F6`快捷键让程序往下运行

```java
return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
```

可以看到，又会调用当前拦截器的`invoke()`方法，并且会将`CglibMethodInvocation`对象传入该方法中。

这时，你会发现，现在是从上一个拦截器（即`AspectJAfterThrowingAdvice`）锁定到了当前这个拦截器（即`AfterReturningAdviceInterceptor`），可想而知，当前这个拦截器又该要锁定到下一个拦截器了。

接着，我们就再进去`invoke()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，可以看到，又是来调用`CglibMethodInvocation`对象的`proceed()`方法，其中`mi`变量指代的就是`CglibMethodInvocation`对象

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		Object retVal = mi.proceed();
		this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
		return retVal;
	}
```

接下来，我们就再进去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，你会发现又是同样熟悉的那套，只不过现在是来判断`currentInterceptorIndex`成员变量的值（即2，因为自增过一次，所以已经由之前的1变成了2）是否等于拦截器总数（5）-1的，很显然并不相等，所以程序并不会进入到if判断语句中

继续按下`F6`快捷键让程序往下运行，可以看到会先让`currentInterceptorIndex`成员变量自增，即由2自增为3，然后再从拦截器链里面获取第3号拦截器，即`AspectJAfterAdvice`。

![](http://120.77.237.175:9080/photos/springanno/113.jpg)

第3号拦截器我们拿到以后，那么接下来该怎么办呢？继续按下`F6`快捷键让程序往下运行，如下图所示。

```java
return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
```

可以看到，又会调用当前拦截器的`invoke()`方法，并且会将`CglibMethodInvocation`对象传入该方法中。

这时，你会发现，现在是从上一个拦截器（即`AfterReturningAdviceInterceptor`）锁定到了当前这个拦截器（即`AspectJAfterAdvice`），可想而知，当前这个拦截器又该要锁定到下一个拦截器了。

接着，我们就再进去`nvoke()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，可以看到，又是来调用`CglibMethodInvocation`对象的`proceed()`方法，其中`mi`变量指代的就是`CglibMethodInvocation`对象

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}
		finally {
			invokeAdviceMethod(getJoinPointMatch(), null, null);
		}
	}
```

现在，你是否明白了这样一个道理，就是**`invoke()`方法里面会调用`proceed()`方法，而这个`proceed()`方法又是寻找下一个拦截器**？

接下来，我们就再进去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，你会发现又是同样熟悉的那套，只不过现在是来判断`currentInterceptorIndex成`员变量的值（即3，因为自增过一次，所以已经由之前的2变成了3）是否等于拦截器总数（5）-1的，很显然并不相等，所以程序并不会进入到if判断语句中。

继续按下`F6`快捷键让程序往下运行，可以看到会先让`currentInterceptorIndex`成员变量自增，即由3自增为4，然后再从拦截器链里面获取第4号拦截器，即`MethodBeforeAdviceInterceptor`，它已是最后一个拦截器了

![](http://120.77.237.175:9080/photos/springanno/114.jpg)

最后一个拦截器我们拿到以后，那么接下来该怎么办呢？继续按下`F6`快捷键让程序往下运行，如下所示。

```java
return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
```

可以看到，又会调用当前拦截器的`invoke()`方法，并且会将`CglibMethodInvocation`对象传入该方法中。

这时，你会发现，现在是从上一个拦截器（即`AspectJAfterAdvice`）锁定到了当前最后这个拦截器（即`MethodBeforeAdviceInterceptor`）。

接着，我们就再进去`invoke()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，可以看到，现在这个`invoke()`方法里面的逻辑有点变化，它会先调用前置通知，再来调用`CglibMethodInvocation`对象的`proceed()`方法，其中mi变量指代的就是`CglibMethodInvocation`对象。

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		this.advice.before(mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}
```

#### 调用前置通知

继续按下`F6`快捷键让程序往下运行，会发现`MethodBeforeAdviceInterceptor.invoke()`这个拦截器总算是做一点事了，即调用前置通知,并且控制台也打印出了前置通知要输出的内容

![](http://120.77.237.175:9080/photos/springanno/115.jpg)

前置通知调用完之后，会再来调用`CglibMethodInvocation`对象的`proceed()`方法。

接下来，我们就再进去`proceed()`方法里面去看一看，它到底是怎么执行的。按下`F5`快捷键进入该方法中，你会发现又是同样熟悉的那套，只不过现在是来判断`currentInterceptorIndex`成员变量的值（即4，因为自增过一次，所以已经由之前的3变成了4）是否等于拦截器总数（5）-1的，很显然是相等的，所以程序会进入到if判断语句中。

![](http://120.77.237.175:9080/photos/springanno/116.jpg)

这时，会直接利用反射来执行目标方法。继续按下`F6`快捷键让程序往下运行，你会看到控制台打印出了目标方法中要输出的内容，这表明目标方法已执行完了

```java
		@Override
		protected Object invokeJoinpoint() throws Throwable {
			if (this.methodProxy != null) {
                	//利用返射执行目标方法
				return this.methodProxy.invoke(this.target, this.arguments);
			}
			else {
				return super.invokeJoinpoint();
			}
		}
```

![](http://120.77.237.175:9080/photos/springanno/117.jpg)

也就是说，**前置通知调用完之后，接着是来调用目标方法的，并且目标方法调用完之后会返回到上一个拦截器（即AspectJAfterAdvice）中。**

#### 调用后置通知

执行完目标方法并返回到上一个拦截器（即`AspectJAfterAdvice`）中之后，可以看到会在`finally`代码块中执行后置通知，因为`AspectJAfterAdvice`是一个后置通知的拦截器。`

继续按下`F6`快捷键让程序往下运行,看到控制台打印出了后置通知要输出的内容,这表明当前拦截器（即AspectJAfterAdvice）的invoke()方法就调用完了

![](http://120.77.237.175:9080/photos/springanno/118.jpg)

从上图中可以知道，调用完后置通知之后，再次返回到了第二个拦截器（即`AspectJAfterThrowingAdvice`）中

#### 如果目标方法运行时没有抛异常，那么调用返回通知

如果目标方法运行时没有抛异常，那么后置通知调用完之后，就应该返回到第三个拦截器（即`AfterReturningAdviceInterceptor`）中。

![](http://120.77.237.175:9080/photos/springanno/122.jpg)

这个拦截器并没有对异常进行处理，如果有异常而是直接抛给了上一个拦截器（即`AspectJAfterThrowingAdvice`）

`AspectJAfterThrowingAdvice`,在这个拦截器的`invoke()`方法中才有`catch`语句捕获到异常。

先来看一下`AfterReturningAdviceInterceptor`这个拦截器的invoke()方法，看看它到底是怎么执行的，如下图所示。

```java
	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		//只有这里执行没有问题,才会调用返回通知,而且在invoke()方法中,我们并没有看到try catch代码块,所以这儿一旦抛了异常,那么便会直接抛给上一个拦截器
		Object retVal = mi.proceed();
        //这里调用返回通知
		this.advice.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
		return retVal;
	}
```

可以看到，只有`Object retVal = mi.proceed();`执行时没有问题，才会调用返回通知。

也就是说，**返回通知只有在目标方法运行没抛异常的时候才会被调用**

#### 如果目标方法运行时抛异常，那么调用异常通知

但是，现在目标方法运行时抛了异常，所以在后置通知调用完之后，返回到了第三个拦截器（即`AfterReturningAdviceInterceptor`）中。

![](http://120.77.237.175:9080/photos/springanno/121.jpg)

该拦截器捕获到异常之后，便会调用异常通知。按下`F6`快捷键让程序往下运行，你会看到控制台打印出了异常通知要输出的内容，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/119.jpg)

异常通知调用完之后，如果有异常，整个的这个异常还会被抛出去，而且是一层一层地往上抛，有人处理就处理，没人处理就抛给虚拟机。

至此，整个拦截器链的执行过程，我们就知道的非常清楚了。更具体一点的说，我们知道了目标方法的整个执行流程，即先执行前置通知，然后再来执行目标方法，接着再来执行后置通知，这三步是固定的。最后，如果目标方法运行时没有抛异常，那么调用返回通知，如果目标方法运行时抛了异常，那么调用异常通知。

![](http://120.77.237.175:9080/photos/springanno/120.png)

**小结**

整个拦截器链的执行过程，我们总结一下，其实就是链式获取每一个拦截器，然后执行拦截器的invoke()方法，每一个拦截器等待下一个拦截器执行完成并返回以后，再来执行其invoke()方法。通过拦截器链这种机制，保证了通知方法与目标方法的执行顺序。

## AOP原理总结

最后，我们还需要对AOP原理做一个简单的总结，完美结束对其研究的旅程。

1. 利用`@EnableAspectJAutoProxy`注解来开启AOP功能

2. 这个AOP功能是怎么开启的呢？主要是通过`@EnableAspectJAutoProxy`注解向IOC容器中注册一个`AnnotationAwareAspectJAutoProxyCreator`组件来做到这点的

3. `AnnotationAwareAspectJAutoProxyCreator`组件是一个后置处理器

4. 该后置处理器是怎么工作的呢？在IOC容器创建的过程中，我们就能清楚地看到这个后置处理器是如何创建以及注册的，以及它的工作流程。

   1. 首先，在创建IOC容器的过程中，会调用`refresh()`方法来刷新容器，而在刷新容器的过程中有一步是来注册后置处理器的，如下所示：

      ```java
      registerBeanPostProcessors(beanFactory); // 注册后置处理器，在这一步会创建AnnotationAwareAspectJAutoProxyCreator对象
      ```

      其实，这一步会为所有后置处理器都创建对象 

   2. 在刷新容器的过程中还有一步是来完成`BeanFactory`的初始化工作的，如下所示：

      ```java
      finishBeanFactoryInitialization(beanFactory); // 完成BeanFactory的初始化工作。所谓的完成BeanFactory的初始化工作，其实就是来创建剩下的单实例bean的。
      ```

      很显然，剩下的单实例`bean`自然就包括`MathCalculator`（业务逻辑类）和`LogAspects`（切面类）这两个`bean`，因此这两个bean就是在这儿被创建的。

      1. 创建业务逻辑组件和切面组件

      2. 在这两个组件创建的过程中，最核心的一点就是`AnnotationAwareAspectJAutoProxyCreator`（后置处理器）会来拦截这俩组件的创建过程

      3. 怎么拦截呢？主要就是在组件创建完成之后，判断组件是否需要增强。如需要，则会把切面里面的通知方法包装成增强器，然后再为业务逻辑组件创建一个代理对象。我们也认真仔细探究过了，在为业务逻辑组件创建代理对象的时候，使用的是`cglib`来创建动态代理的。当然了，如果业务逻辑类有实现接口，那么就使用`jdk`来创建动态代理。一旦这个代理对象创建出来了，那么它里面就会有所有的增强器。

         这个代理对象创建完以后，`IOC`容器也就创建完了。接下来，便要来执行目标方法了。

5. 执行目标方法

      1. 此时，其实是代理对象来执行目标方法
      2. 使用`CglibAopProxy`类的`intercept()`方法来拦截目标方法的执行，拦截的过程如下：
         1. 得到目标方法的拦截器链，所谓的拦截器链其实就是每一个通知方法又被包装为了方法拦截器，即`MethodInterceptor`
         2. 利用拦截器的链式机制，依次进入每一个拦截器中进行执行
         3. 最终，整个的执行效果就会有两套：
            - 目标方法正常执行：前置通知→目标方法→后置通知→返回通知
            - 目标方法出现异常：前置通知→目标方法→后置通知→异常通知

# 声明式事务

pom

```java
<!--添加spring-jdbc模块的依赖--> 
<dependency>
     <groupId>org.springframework</groupId>
     <artifactId>spring-jdbc</artifactId>
     <version>5.2.6.RELEASE</version>
 </dependency>
     <!--添加MySQL数据库驱动的依赖-->
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
         <version>6.0.6</version>
     </dependency>
```

## 配置数据源以及JdbcTemplate

```java
@EnableTransactionManagement	//开启事务管理
@ComponentScan("com.anno")
@Configuration
public class TxConfig {
    // 注册c3p0数据源
    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://120.77.237.175:9306/test?serverTimezone=Asia/Shanghai");
        dataSource.setDriverClass("com.mysql.jdbc.Driver");
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        return dataSource;

    }

    @Bean
    public JdbcTemplate jdbcTemplate() throws Exception {
        //Spring对@Configuration类会特殊处理；给容器中加组件的方法，多次调用都只是从容器中找组件
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        return jdbcTemplate;
    }

    //注册事务管理器在容器中
    @Bean
    public PlatformTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }
}
```



```java
@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insert() {
        String sql = "INSERT INTO `tbl_user`(username,age) VALUES(?,?)";
        String username = UUID.randomUUID().toString().substring(0, 5);
        jdbcTemplate.update(sql, username, 19);
    }
}
```

```java
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Transactional	//事务,当有异常时回滚
    public void insertUser() {
        userDao.insert();
        System.out.println("插入完成...");
        int i = 10 / 0;
    }
}
```

测试

```java
public class IOCTest_Tx {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TxConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
        userService.insertUser();
        applicationContext.close();
    }
}
```

## 源码分析

### @EnableTransactionManagement

在配置类上添加`@EnableTransactionManagement`注解，便能够开启基于注解的事务管理功能。那下面我们就来看一看它的源码，如下图所示。

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransactionManagementConfigurationSelector.class)
public @interface EnableTransactionManagement {

	boolean proxyTargetClass() default false;

	AdviceMode mode() default AdviceMode.PROXY;

	int order() default Ordered.LOWEST_PRECEDENCE;

}
```

从源码中可以看出，`@EnableTransactionManagement`注解使用@Import注解给容器中引入了`TransactionManagementConfigurationSelector`组件。那这个`TransactionManagementConfigurationSelector`又是啥呢？它其实是一个`ImportSelector`。

这是怎么得出来的呢？我们可以点到`TransactionManagementConfigurationSelector`类中一看究竟，如下图所示，发现它继承了一个类，叫`AdviceModeImportSelector`。

```java
public class TransactionManagementConfigurationSelector extends AdviceModeImportSelector<EnableTransactionManagement> {

	/**
	 * Returns {@link ProxyTransactionManagementConfiguration} or
	 * {@code AspectJ(Jta)TransactionManagementConfiguration} for {@code PROXY}
	 * and {@code ASPECTJ} values of {@link EnableTransactionManagement#mode()},
	 * respectively.
	 */
	@Override
	protected String[] selectImports(AdviceMode adviceMode) {
		switch (adviceMode) {
			case PROXY:
				return new String[] {AutoProxyRegistrar.class.getName(),
						ProxyTransactionManagementConfiguration.class.getName()};
			case ASPECTJ:
				return new String[] {determineTransactionAspectClass()};
			default:
				return null;
		}
	}

	private String determineTransactionAspectClass() {
		return (ClassUtils.isPresent("javax.transaction.Transactional", getClass().getClassLoader()) ?
				TransactionManagementConfigUtils.JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME :
				TransactionManagementConfigUtils.TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME);
	}

}
```

然后再次点到`AdviceModeImportSelector`类中，如下图所示，发现它实现了一个接口，叫`ImportSelector`,具体可看上面的介绍。

```java
public abstract class AdviceModeImportSelector<A extends Annotation> implements ImportSelector {
	...
}
```

说到底，其实它是用于给容器中快速导入一些组件的，到底要导入哪些组件，就看它会返回哪些要导入到容器中的组件的全类名。

我们可以看一下`TransactionManagementConfigurationSelector`类的源码，看看它里面到底是怎么写的。其实在上面我们就看清楚该类的源码了，在它里面会做一个`switch`判断，如果`adviceMode`是`PROXY`，那么就会返回一个`String[]`，该`String`数组如下所示

```java
new String[] {AutoProxyRegistrar.class.getName(),
						ProxyTransactionManagementConfiguration.class.getName()}
```

这说明会向容器中导入`AutoProxyRegistrar`和`ProxyTransactionManagementConfiguration`这两个组件。

如果`adviceMode`是`ASPECTJ`，那么便会返回如下这样一个`String[]`。

```java
new String[] {determineTransactionAspectClass()}
```

点`TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME,JTA_TRANSACTION_ASPECT_CONFIGURATION_CLASS_NAME`一下，可以看到，它其实就是`AspectJTransactionManagementConfiguration`类的全类名，如下图所示

![](http://120.77.237.175:9080/photos/springanno/123.jpg)

也就是说，如果`adviceMode`是`ASPECTJ`，那么就会向容器中导入一个`AspectJTransactionManagementConfiguration`组件。只可惜，它和我们研究声明式事务的原理没有半毛钱的关系。

那么问题来了，`AdviceMode`又是个啥呢？点它，发现它是一个枚举，如下

```java
public enum AdviceMode {

   /**
    * JDK proxy-based advice.
    */
   PROXY,

   /**
    * AspectJ weaving-based advice.
    */
   ASPECTJ

}
```

这个枚举有啥子用呢？我们可以再来看一下`@EnableTransactionManagement`注解的源码，发现它里面会定义一个`mode`属性，且其默认值就是`AdviceMode.PROXY`。既然如此，那么便会进入到`TransactionManagementConfigurationSelector`类的`switch`语句的`case PROXY`选项中，这时，就会向容器中快速导入两个组件，一个叫`AutoProxyRegistrar`，一个叫`ProxyTransactionManagementConfiguration`。

接下来，我们便要来分析这两个组件的功能了，只要分析清楚了，声明式事务的原理就呼之欲出了。

### AutoProxyRegistrar

```java
public class AutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

   private final Log logger = LogFactory.getLog(getClass());

   @Override
   public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      boolean candidateFound = false;
      Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
      for (String annType : annTypes) {
         AnnotationAttributes candidate = AnnotationConfigUtils.attributesFor(importingClassMetadata, annType);
         if (candidate == null) {
            continue;
         }
         Object mode = candidate.get("mode");
         Object proxyTargetClass = candidate.get("proxyTargetClass");
         if (mode != null && proxyTargetClass != null && AdviceMode.class == mode.getClass() &&
               Boolean.class == proxyTargetClass.getClass()) {
            candidateFound = true;
            if (mode == AdviceMode.PROXY) {
               AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
               if ((Boolean) proxyTargetClass) {
                  AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
                  return;
               }
            }
         }
      }
      if (!candidateFound && logger.isInfoEnabled()) {
         String name = getClass().getSimpleName();
         logger.info(String.format("%s was imported but no annotations were found " +
               "having both 'mode' and 'proxyTargetClass' attributes of type " +
               "AdviceMode and boolean respectively. This means that auto proxy " +
               "creator registration and configuration may not have occurred as " +
               "intended, and components may not be proxied as expected. Check to " +
               "ensure that %s has been @Import'ed on the same class where these " +
               "annotations are declared; otherwise remove the import of %s " +
               "altogether.", name, name, name));
      }
   }

}
```

它实现了一个接口，叫`ImportBeanDefinitionRegistrar`具体可看上面的`ImportBeanDefinitionRegistrar`介绍,这个`AutoProxyRegistrar`组件其实就是用来向容器中注册`bean`的，那你就应该清楚，最终会调用该组件的`registerBeanDefinitions()`方法来向容器中注册`bean`

么会向容器中注册什么`bean`呢？我们仔细地看一下`AutoProxyRegistrar`类中的`registerBeanDefinitions()`方法,如上

在该方法中先是通过如下一行代码来获取各种注解类型，这儿需要特别注意的是，这里是拿到所有的注解类型，而不是只拿`@EnableAspectJAutoProxy`这个类型的。因为`mode`、`proxyTargetClass`等属性会直接影响到代理的方式，而拥有这些属性的注解至少有`@EnableTransactionManagement`、`@EnableAsync`以及`@EnableCaching`等等，甚至还有启用`AOP`的注解，即`@EnableAspectJAutoProxy`，它也能设置`proxyTargetClass`这个属性的值，因此也会产生关联影响。

```java
Set<String> annTypes = importingClassMetadata.getAnnotationTypes();
```

然后是拿到注解里的`mode`、`proxyTargetClass`这两个属性的值，如下图所示。

```java
Object mode = candidate.get("mode");
Object proxyTargetClass = candidate.get("proxyTargetClass");
```

注意，如果这儿的注解是`@Configuration`或者别的其他注解的话，那么获取到的这俩属性的值就是null了。

接着做一个判断，如果存在`mode`、`proxyTargetClass`这两个属性，并且这两个属性的`class`类型也都是对的，那么便会进入到if判断语句中，这样，其余注解就相当于都被挡在外面了。

要是真进入到了if判断语句中，是不是意味着找到了候选的注解（例如`@EnableTransactionManagement`）呢？你仔细想一下，是不是这回事。找到了候选的注解之后，就将`candidateFound`标识置为`true`。

紧接着会再做一个判断，即判断找到的候选注解中的`mode`属性的值是否为`AdviceMode.PROXY`，若是则会调用我们熟悉的`AopConfigUtils`工具类的`registerAutoProxyCreatorIfNecessary`方法。相信大家也很熟悉这个方法了，它主要是来向容器中注册一个`InfrastructureAdvisorAutoProxyCreator`组件的。

继续往下看`AutoProxyRegistrar`类的`registerBeanDefinitions()`方法。这时，又会做一个判断，要是找到的候选注解设置了`proxyTargetClass`这个属性的值，并且值为`true`，那么便会进入到下面的if判断语句中，**看要不要强制使用CGLIB的方式**。

如果此时找到的候选注解是`@EnableTransactionManagement`，想一想会发生什么事情？查看该注解的源码，你会发现它里面就拥有一个`proxyTargetClass`属性，并且其默认值是`false`。所以此时压根就不会进入到if判断语句中，而只会调用我们熟悉的`AopConfigUtils`工具类的`registerAutoProxyCreatorIfNecessary`方法。

这个咱们再熟悉不过的`registerAutoProxyCreatorIfNecessary`方法会向容器中注册什么呢？上面我也说到了，它会向容器中注册一个`InfrastructureAdvisorAutoProxyCreator`组件，即自动代理创建器。点进去`registerAutoProxyCreatorIfNecessary`方法中，如下图所示，可以看到这个方法又调用了一个同名的重载方法。

```java
public abstract class AopConfigUtils {

	/**
	 * The bean name of the internally managed auto-proxy creator.
	 */
	public static final String AUTO_PROXY_CREATOR_BEAN_NAME =
			"org.springframework.aop.config.internalAutoProxyCreator";

	/**
	 * Stores the auto proxy creator classes in escalation order.
	 */
	private static final List<Class<?>> APC_PRIORITY_LIST = new ArrayList<>(3);

	static {
		// Set up the escalation list...
		APC_PRIORITY_LIST.add(InfrastructureAdvisorAutoProxyCreator.class);
		APC_PRIORITY_LIST.add(AspectJAwareAdvisorAutoProxyCreator.class);
		APC_PRIORITY_LIST.add(AnnotationAwareAspectJAutoProxyCreator.class);
	}


	@Nullable
	public static BeanDefinition registerAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
		return registerAutoProxyCreatorIfNecessary(registry, null);
	}

	@Nullable
	public static BeanDefinition registerAutoProxyCreatorIfNecessary(
			BeanDefinitionRegistry registry, @Nullable Object source) {

		return registerOrEscalateApcAsRequired(InfrastructureAdvisorAutoProxyCreator.class, registry, source);
	}

	@Nullable
	public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(BeanDefinitionRegistry registry) {
		return registerAspectJAutoProxyCreatorIfNecessary(registry, null);
	}

	@Nullable
	public static BeanDefinition registerAspectJAutoProxyCreatorIfNecessary(
			BeanDefinitionRegistry registry, @Nullable Object source) {

		return registerOrEscalateApcAsRequired(AspectJAwareAdvisorAutoProxyCreator.class, registry, source);
	}

	...
}
```

你现在该知道调用`AopConfigUtils`工具类的`registerAutoProxyCreatorIfNecessary`方法会向容器中注册什么组件了吧！

现在我们可以得出这样一个结论：**导入的第一个组件（即`AutoProxyRegistrar`）向容器中注入了一个自动代理创建器，即`InfrastructureAdvisorAutoProxyCreator`。**

其实，大家可以好好看一下`AopConfigUtils`工具类的源码，`AnnotationAwareAspectJAutoProxyCreator`

当初咱们在研究AOP的原理时，不是得出了这样一个结论吗？**即`@EnableAspectJAutoProxy`注解会利用`AspectJAutoProxyRegistrar`向容器中注入一个`AnnotationAwareAspectJAutoProxyCreator`组件。**

声明式事务的原理跟`AOP`的原理很相似，只不过对于声明式事务原理而言，它注入的是`InfrastructureAdvisorAutoProxyCreator`组件而已。我们都知道，在研究`AOP`原理时，`AnnotationAwareAspectJAutoProxyCreator`实质上是一个后置处理器，那么`InfrastructureAdvisorAutoProxyCreator`实质上又是一个什么呢？也会是一个后置处理器吗？

点进去`InfrastructureAdvisorAutoProxyCreator`类里面去看一看，直到`SmartInstantiationAwareBeanPostProcessor`,看下图的继承关系

![](http://120.77.237.175:9080/photos/springanno/124.jpg)

说明注入的`InfrastructureAdvisorAutoProxyCreator`组件同样也是一个后置处理器。

它做的事情也很简单，和之前研究AOP原理时向容器中注入的`AnnotationAwareAspectJAutoProxyCreator`组件所做的事情基本上没差别，只是利用后置处理器机制在对象创建以后进行包装，然后返回一个代理对象，并且该代理对象里面会存有所有的增强器。最后，代理对象执行目标方法，在此过程中会利用拦截器的链式机制，依次进入每一个拦截器中进行执行。

### ProxyTransactionManagementConfiguration

#### 向容器中注册事务增强器

很快你就会发现它是一个配置类，它会利用`@Bean`注解向容器中注册各种组件，而且注册的第一个组件就是`BeanFactoryTransactionAttributeSourceAdvisor`，这个`Advisor`可是事务的核心内容，可以暂时称之为事务增强器。

```java
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyTransactionManagementConfiguration extends AbstractTransactionManagementConfiguration {

	@Bean(name = TransactionManagementConfigUtils.TRANSACTION_ADVISOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public BeanFactoryTransactionAttributeSourceAdvisor transactionAdvisor(
			TransactionAttributeSource transactionAttributeSource, TransactionInterceptor transactionInterceptor) {

		BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
		advisor.setTransactionAttributeSource(transactionAttributeSource);
		advisor.setAdvice(transactionInterceptor);
		if (this.enableTx != null) {
			advisor.setOrder(this.enableTx.<Integer>getNumber("order"));
		}
		return advisor;
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionAttributeSource transactionAttributeSource() {
		return new AnnotationTransactionAttributeSource();
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource) {
		TransactionInterceptor interceptor = new TransactionInterceptor();
		interceptor.setTransactionAttributeSource(transactionAttributeSource);
		if (this.txManager != null) {
			interceptor.setTransactionManager(this.txManager);
		}
		return interceptor;
	}

}
```

总之一句话，以上配置类会利用@Bean注解向容器中注册一个事务增强器。

#### 在向容器中注册事务增强器时，需要用到事务属性源

那么这个所谓的事务增强器又是什么呢？从上面的配置类中可以看出，在注册事务源属性时,会把bean属性的事务源注册进来

```java
advisor.setTransactionAttributeSource(transactionAttributeSource);
```

TransactionAttributeSource`,翻译过来应该是事务属性源

很快，你就会发现所需的`TransactionAttributeSource`又是容器中的一个`bean`，而且从`transactionAttributeSource`方法中可以看出，它是new出来了一个`AnnotationTransactionAttributeSource`对象。这个是重点，它是基于注解驱动的事务管理的事务属性源，和`@Transactional`注解相关，也是现在使用得最多的方式，其基本作用是遇上比如`@Transactional`注解标注的方法时，此类会分析此事务注解。

然后，点进`AnnotationTransactionAttributeSource`类的无参构造方法中去看一看，发现该方法又调用了如下一个this(true)方法，即本类的另一个重载的有参构造方法。

```java
	public AnnotationTransactionAttributeSource() {
		this(true);
	}
```

接着，点击一下this(true)方法，这时会跳到如下的一个有参构造方法处。

```java
	public AnnotationTransactionAttributeSource(boolean publicMethodsOnly) {
		this.publicMethodsOnly = publicMethodsOnly;
		if (jta12Present || ejb3Present) {
            //事务注解的解析器集合
			this.annotationParsers = new LinkedHashSet<>(4);
			this.annotationParsers.add(new SpringTransactionAnnotationParser());
			if (jta12Present) {
				this.annotationParsers.add(new JtaTransactionAnnotationParser());
			}
			if (ejb3Present) {
				this.annotationParsers.add(new Ejb3TransactionAnnotationParser());
			}
		}
		else {
			this.annotationParsers = Collections.singleton(new SpringTransactionAnnotationParser());
		}
	}
```

在该方法中，你会看到三方法类,三个方法都继承了`TransactionAnnotationParser`接口，源码如下图所示

```java
public interface TransactionAnnotationParser {

	default boolean isCandidateClass(Class<?> targetClass) {
		return true;
	}

	@Nullable
	TransactionAttribute parseTransactionAnnotation(AnnotatedElement element);

}
```

顾名思义，它是解析方法/类上事务注解的，当然了，你也可以称它为事务注解的解析器。

这里我要说明的一点是，`Spring`支持三个不同的事务注解，它们分别是：

1. `Spring`事务注解，即`org.springframework.transaction.annotation.Transactional`（纯正血统，官方推荐）
2. `JTA`事务注解，即`javax.transaction.Transactional`
3. `EJB 3`事务注解，即`javax.ejb.TransactionAttribute`

因一般都会使用`Spring`事务注解。另外，上面三个注解虽然语义上一样，但是使用方式上不完全一样

上面说到了Spring支持三个不同的事务注解，这里很显然，它们都对应了三个不同的注解解析器，即`SpringTransactionAnnotationParser`、`JtaTransactionAnnotationParser`以及`Ejb3TransactionAnnotationParse`r。

`SpringTransactionAnnotationParser.parseTransactionAnnotation`方法，你会发现它就是来解析`@Transactional`注解里面的每一个信息的，包括它里面的每一个属性，例如`rollbackFor`、`noRollbackFor`、···

```java
	protected TransactionAttribute parseTransactionAnnotation(AnnotationAttributes attributes) {
		RuleBasedTransactionAttribute rbta = new RuleBasedTransactionAttribute();

		Propagation propagation = attributes.getEnum("propagation");
		rbta.setPropagationBehavior(propagation.value());
		Isolation isolation = attributes.getEnum("isolation");
		rbta.setIsolationLevel(isolation.value());
		rbta.setTimeout(attributes.getNumber("timeout").intValue());
		rbta.setReadOnly(attributes.getBoolean("readOnly"));
		rbta.setQualifier(attributes.getString("value"));

		List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
		for (Class<?> rbRule : attributes.getClassArray("rollbackFor")) {
			rollbackRules.add(new RollbackRuleAttribute(rbRule));
		}
		for (String rbRule : attributes.getStringArray("rollbackForClassName")) {
			rollbackRules.add(new RollbackRuleAttribute(rbRule));
		}
		for (Class<?> rbRule : attributes.getClassArray("noRollbackFor")) {
			rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
		}
		for (String rbRule : attributes.getStringArray("noRollbackForClassName")) {
			rollbackRules.add(new NoRollbackRuleAttribute(rbRule));
		}
		rbta.setRollbackRules(rollbackRules);

		return rbta;
	}
```

`rollbackFor`、`noRollbackFor`等等这些属性就是我们可以在`@Transactional`注解里面能写的。

![](http://120.77.237.175:9080/photos/springanno/125.jpg)

事务增强器要用到事务注解的信息，会使用到一个叫`AnnotationTransactionAttributeSource`的类，用它来解析事务注解。

#### 在向容器中注册事务增强器时，还需要用到事务的拦截器

接下来，我们再来看看向容器中注册事务增强器时，还得做些什么。回到上面`ProxyTransactionManagementConfiguration`类中，发现在向容器中注册事务增强器时，除了需要事务注解信息，还需要一个事务的拦截器，看到那个`transactionInterceptor`方法没，它就是表示事务增强器还要用到一个事务的拦截器。上面已经有提及过

```java
advisor.setAdvice(transactionInterceptor);
```

仔细查看上面的`transactionInterceptor`方法，你会看到在里面创建了一个`TransactionInterceptor`对象，创建完毕之后，不但会将事务属性源设置进去，而且还会将事务管理器（`txManager`）设置进去。也就是说，事务拦截器里面不仅保存了事务属性信息，还保存了事务管理器。

我们点进去`TransactionIntercepto`r类里面去看一下，发现该类实现了一个`MethodInterceptor`接口，如下图所示

```java
public class TransactionInterceptor extends TransactionAspectSupport implements MethodInterceptor, Serializable {
	...
}
```

看到它，你是不是倍感亲切，因为咱们在研究AOP的原理时，就已经认识它了。相信你应该还记得这样一个知识点，**切面类里面的通知方法最终都会被整成增强器，而增强器又会被转换成`MethodInterceptor`**。所以，这样看来，这个事务拦截器实质上还是一个`MethodInterceptor`（方法拦截器）。

啥叫方法拦截器呢？简单来说就是，现在会向容器中放一个代理对象，代理对象要执行目标方法，那么方法拦截器就会进行工作。

其实，跟我们以前研究AOP的原理一模一样，在代理对象执行目标方法的时候，它便会来执行拦截器链，而现在这个拦截器链，只有一个`TransactionInterceptor`，它正是这个事务拦截器。接下来，我们就来看看这个事务拦截器是怎样工作的，即它的作用是什么。

仔细翻阅`TransactionInterceptor`类的源码，你会发现它里面有一个`invoke`方法，而且还会看到在该方法里面又调用了一个`invokeWithinTransaction`方法，如下图所示。

```java
	@Override
	@Nullable
	public Object invoke(MethodInvocation invocation) throws Throwable {
		// Work out the target class: may be {@code null}.
		// The TransactionAttributeSource should be passed the target class
		// as well as the method, which may be from an interface.
		Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);

		// Adapt to TransactionAspectSupport's invokeWithinTransaction...
		return invokeWithinTransaction(invocation.getMethod(), targetClass, invocation::proceed);
	}
```

点进去`invokeWithinTransaction`方法里面看一下，你就能知道这个事务拦截器是怎样工作的了

```java
	protected Object invokeWithinTransaction(Method method, @Nullable Class<?> targetClass,
			final InvocationCallback invocation) throws Throwable {

		// If the transaction attribute is null, the method is non-transactional.
		TransactionAttributeSource tas = getTransactionAttributeSource();
		final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
		final TransactionManager tm = determineTransactionManager(txAttr);

		if (this.reactiveAdapterRegistry != null && tm instanceof ReactiveTransactionManager) {
			ReactiveTransactionSupport txSupport = this.transactionSupportCache.computeIfAbsent(method, key -> {
				if (KotlinDetector.isKotlinType(method.getDeclaringClass()) && KotlinDelegate.isSuspend(method)) {
					throw new TransactionUsageException(
							"Unsupported annotated transaction on suspending function detected: " + method +
							". Use TransactionalOperator.transactional extensions instead.");
				}
				ReactiveAdapter adapter = this.reactiveAdapterRegistry.getAdapter(method.getReturnType());
				if (adapter == null) {
					throw new IllegalStateException("Cannot apply reactive transaction to non-reactive return type: " +
							method.getReturnType());
				}
				return new ReactiveTransactionSupport(adapter);
			});
			return txSupport.invokeWithinTransaction(
					method, targetClass, invocation, txAttr, (ReactiveTransactionManager) tm);
		}

		PlatformTransactionManager ptm = asPlatformTransactionManager(tm);
		final String joinpointIdentification = methodIdentification(method, targetClass, txAttr);

		if (txAttr == null || !(ptm instanceof CallbackPreferringPlatformTransactionManager)) {
			// Standard transaction demarcation with getTransaction and commit/rollback calls.
			TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);

			Object retVal;
			try {
				// This is an around advice: Invoke the next interceptor in the chain.
				// This will normally result in a target object being invoked.
				retVal = invocation.proceedWithInvocation();
			}
			catch (Throwable ex) {
				// target invocation exception
				completeTransactionAfterThrowing(txInfo, ex);
				throw ex;
			}
			finally {
				cleanupTransactionInfo(txInfo);
			}

			if (vavrPresent && VavrDelegate.isVavrTry(retVal)) {
				// Set rollback-only in case of Vavr failure matching our rollback rules...
				TransactionStatus status = txInfo.getTransactionStatus();
				if (status != null && txAttr != null) {
					retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
				}
			}

			commitTransactionAfterReturning(txInfo);
			return retVal;
		}

		else {
			final ThrowableHolder throwableHolder = new ThrowableHolder();

			// It's a CallbackPreferringPlatformTransactionManager: pass a TransactionCallback in.
			try {
				Object result = ((CallbackPreferringPlatformTransactionManager) ptm).execute(txAttr, status -> {
					TransactionInfo txInfo = prepareTransactionInfo(ptm, txAttr, joinpointIdentification, status);
					try {
						Object retVal = invocation.proceedWithInvocation();
						if (vavrPresent && VavrDelegate.isVavrTry(retVal)) {
							// Set rollback-only in case of Vavr failure matching our rollback rules...
							retVal = VavrDelegate.evaluateTryFailure(retVal, txAttr, status);
						}
						return retVal;
					}
					catch (Throwable ex) {
						if (txAttr.rollbackOn(ex)) {
							// A RuntimeException: will lead to a rollback.
							if (ex instanceof RuntimeException) {
								throw (RuntimeException) ex;
							}
							else {
								throw new ThrowableHolderException(ex);
							}
						}
						else {
							// A normal return value: will lead to a commit.
							throwableHolder.throwable = ex;
							return null;
						}
					}
					finally {
						cleanupTransactionInfo(txInfo);
					}
				});

				// Check result state: It might indicate a Throwable to rethrow.
				if (throwableHolder.throwable != null) {
					throw throwableHolder.throwable;
				}
				return result;
			}
			catch (ThrowableHolderException ex) {
				throw ex.getCause();
			}
			catch (TransactionSystemException ex2) {
				if (throwableHolder.throwable != null) {
					logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
					ex2.initApplicationException(throwableHolder.throwable);
				}
				throw ex2;
			}
			catch (Throwable ex2) {
				if (throwableHolder.throwable != null) {
					logger.error("Application exception overridden by commit exception", throwableHolder.throwable);
				}
				throw ex2;
			}
		}
	}
```

**先来获取事务相关的一些属性信息**

```java
TransactionAttributeSource tas = getTransactionAttributeSource();
final TransactionAttribute txAttr = (tas != null ? tas.getTransactionAttribute(method, targetClass) : null);
```

我们便可以知道，这儿是来获取事务相关的一些属性信息的。

**再来获取PlatformTransactionManager**

接着往下看`invokeWithinTransaction`方法，可以看到它的第二行代码是这样写的：

```java
final TransactionManager tm = determineTransactionManager(txAttr);
```

这就是来获取`PlatformTransactionManager`的，还记得我们之前就已经向容器中注册了一个吗，现在就是来获取它的。那到底又是怎么来获取的呢？我们不妨点进去`determineTransactionManager`方法里面去看一下。

```java
	protected TransactionManager determineTransactionManager(@Nullable TransactionAttribute txAttr) {
		// Do not attempt to lookup tx manager if no tx attributes are set
		if (txAttr == null || this.beanFactory == null) {
			return getTransactionManager();
		}
	
        //如果事务属性里面有Qualifier这个注解，并且这个注解还有值，那么就会直接从容器中按照这个指定的值来获取PlatformTransactionManager。
        //其实我们在为某个业务方法标注@Transactional注解的时候，是可以明确地指定事务管理器的名字的，下图：
        //指定事务管理器的名字，其实就等同于Qualifier这个注解。虽说是可以明确指定事务管理器的名字，但我们一般都不这么做，即不指定
		String qualifier = txAttr.getQualifier();
		if (StringUtils.hasText(qualifier)) {
			return determineQualifiedTransactionManager(this.beanFactory, qualifier);
		}
		else if (StringUtils.hasText(this.transactionManagerBeanName)) {
			return determineQualifiedTransactionManager(this.beanFactory, this.transactionManagerBeanName);
		}
		else {
            //如果没指定的话，那么就是来获取默认的了，这时很显然会进入到最下面的else判断中
            //可以看到，会先调用getTransactionManager方法，获取的是默认向容器中自动装配进去的PlatformTransactionManager。
			TransactionManager defaultTransactionManager = getTransactionManager();
			if (defaultTransactionManager == null) {
                //首次获取肯定就为null，但没关系，因为最终会从容器中按照类型来获取，这可以从下面这行代码中看出来。
				defaultTransactionManager = this.transactionManagerCache.get(DEFAULT_TRANSACTION_MANAGER_KEY);
				if (defaultTransactionManager == null) {
					defaultTransactionManager = this.beanFactory.getBean(TransactionManager.class);
					this.transactionManagerCache.putIfAbsent(
							DEFAULT_TRANSACTION_MANAGER_KEY, defaultTransactionManager);
				}
			}
            //所以，我们只需要给容器中注入一个PlatformTransactionManager,就能获取到PlatformTransactionManager了
			return defaultTransactionManager;
		}
	}
```

![](http://120.77.237.175:9080/photos/springanno/126.jpg)

**总结：如果事先没有添加指定任何`TransactionManager`，那么最终会从容器中按照类型来获取一个`PlatformTransactionManager`。**

**执行目标方法**

接下来，继续往下看`invokeWithinTransaction`方法，来看它接下去又做了些什么。其实，很容易就能看出来，获取到事务管理器之后，然后便要来执行目标方法了，而且如果目标方法执行时一切正常，那么还能拿到一个返回值，如下图所示

![](http://120.77.237.175:9080/photos/springanno/127.jpg)

在执行上面这句代码之前，还有这样一句代码

```java
TransactionInfo txInfo = createTransactionIfNecessary(ptm, txAttr, joinpointIdentification);
```

上面这个方法翻译成中文，就是如果是必须的话，那么得先创建一个`Transaction`。说人话，就是如果目标方法是一个事务，那么便开启事务。

如果目标方法执行时一切正常，那么接下来该怎么办呢？这时，会调用一个叫`commitTransactionAfterReturning`的方法，如上面图所示

我们可以点进去`commitTransactionAfterReturning`方法里面去看一看，发现它是先获取到事务管理器，然后再利用事务管理器提交事务，如下图所示。

```java
	protected void commitTransactionAfterReturning(@Nullable TransactionInfo txInfo) {
		if (txInfo != null && txInfo.getTransactionStatus() != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() + "]");
			}
            //可以看到,事务是在这儿被提交的
			txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
		}
	}
```

如果执行目标方法时出现异常，那么又该怎么办呢？这时，会调用一个叫`completeTransactionAfterThrowing`的方法，上图所示。

点进去`completeTransactionAfterThrowing`方法里面去看一看，发现它是先获取到事务管理器，然后再利用事务管理器回滚这次操作，如下图所示。

```java
	protected void completeTransactionAfterThrowing(@Nullable TransactionInfo txInfo, Throwable ex) {
		if (txInfo != null && txInfo.getTransactionStatus() != null) {
			if (logger.isTraceEnabled()) {
				logger.trace("Completing transaction for [" + txInfo.getJoinpointIdentification() +
						"] after exception: " + ex);
			}
			if (txInfo.transactionAttribute != null && txInfo.transactionAttribute.rollbackOn(ex)) {
				try {
                    //拿到事务管理器之后进行回滚
					txInfo.getTransactionManager().rollback(txInfo.getTransactionStatus());
				}
				catch (TransactionSystemException ex2) {
					logger.error("Application exception overridden by rollback exception", ex);
					ex2.initApplicationException(ex);
					throw ex2;
				}
				catch (RuntimeException | Error ex2) {
					logger.error("Application exception overridden by rollback exception", ex);
					throw ex2;
				}
			}
			else {
				// We don't roll back on this exception.
				// Will still roll back if TransactionStatus.isRollbackOnly() is true.
				try {
					txInfo.getTransactionManager().commit(txInfo.getTransactionStatus());
				}
				catch (TransactionSystemException ex2) {
					logger.error("Application exception overridden by commit exception", ex);
					ex2.initApplicationException(ex);
					throw ex2;
				}
				catch (RuntimeException | Error ex2) {
					logger.error("Application exception overridden by commit exception", ex);
					throw ex2;
				}
			}
		}
	}
```

也就是说，真正的回滚与提交事务的操作都是由事务管理器来做的，而`TransactionInterceptor`只是用来拦截目标方法的。

## 总结

首先，使用`AutoProxyRegistrar`向`Spring`容器里面注册一个后置处理器，这个后置处理器会负责给我们包装代理对象。然后，使用`ProxyTransactionManagementConfiguration`（配置类）再向`Spring`容器里面注册一个事务增强器，此时，需要用到事务拦截器。最后，代理对象执行目标方法，在这一过程中，便会执行到当前`Spring`容器里面的拦截器链，而且每次在执行目标方法时，如果出现了异常，那么便会利用事务管理器进行回滚事务，如果执行过程中一切正常，那么则会利用事务管理器提交事务。

# BeanFactoryPostProcessor

## 调用时机

`BeanFactoryPostProcessor`其实就是`BeanFactory`（创建bean的工厂）的后置处理器。

我们点进去BeanFactoryPostProcessor的源码里面去看一看，发现它是一个接口，如下图所示。

```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```

仔细看一下其内部`postProcessBeanFactory`方法上的描述，这很重要，因为从这段描述中我们就可以知道`BeanFactoryPostProcessor`的调用时机。描述中说，我们可以在`IOC`容器里面的`BeanFactory`的标准初始化完成之后，修改`IOC`容器里面的这个`BeanFactory`。

也就是说，`BeanFactoryPostProcessor`的调用时机是在`BeanFactory`标准初始化之后，这样一来，我们就可以来定制和修改`BeanFactory`里面的一些内容了。那什么叫标准初始化呢？接着看描述，它说的是所有的bean定义已经被加载了，但是还没有bean被初始化。

就是**`BeanFactoryPostProcessor`的调用时机是在`BeanFactory`标准初始化之后，这样一来，我们就可以来定制和修改`BeanFactory`里面的一些内容了，此时，所有的bean定义已经保存加载到`BeanFactory`中了，但是`bean的实例还未创建**

## 测试

```java
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("MyBeanFactoryPostProcessor...postProcessBeanFactory()...");
        int count = beanFactory.getBeanDefinitionCount();   //获取容器bean的数量
        String[] names = beanFactory.getBeanDefinitionNames();  //获取所有容器bean的名
        System.out.println("当前BeanFactory中有" + count + " 个Bean");
        System.out.println(Arrays.asList(names));
    }
}
```

```java
@ComponentScan("com.anno.ext")
@Configuration
public class ExtConfig {

    @Bean
    public Car car() {
        return new Car();
    }
}
```

```java
public class IOCTest_Ext {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);
        applicationContext.close();
    }
}
/**
MyBeanFactoryPostProcessor...postProcessBeanFactory()...
当前BeanFactory中有8 个Bean
[org.springframework.context.annotation.internalConfigurationAnnotationProcessor, org.springframework.context.annotation.internalAutowiredAnnotationProcessor, org.springframework.context.annotation.internalCommonAnnotationProcessor, org.springframework.context.event.internalEventListenerProcessor, org.springframework.context.event.internalEventListenerFactory, extConfig, myBeanFactoryPostProcessor, car]
car construct .....
**/
```

从上面结果可以看到在构造器这前已经打印了`bean`的个数和`bean`容器各个名字,因此可以**确定`BeanFactoryPostProcessor`的执行时机就是在bean没实例创建之前**

## 原理

在`postProcessBeanFactory`打上断点,如下图,由`test01`开始分析

![](http://120.77.237.175:9080/photos/springanno/128.jpg)

1. 首先进入`IOCTest_Ext.test01()`方法

2. 进入容器进行刷新

   ![](http://120.77.237.175:9080/photos/springanno/129.jpg)

3. 进入刷新容器方法里，可以看到断点停留在如下图，此方法执行`BeanFactoryPostProcessors`

   ![](http://120.77.237.175:9080/photos/springanno/130.jpg)

   此方法是如何执行的，可以点击进入查看

   ```java
   //根据DEBUG进入这个方法	
   protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
       	//根据DEBG继续进入这里
   		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
   		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
   		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
   		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
   			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
   			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
   		}
   	}
   ```

   ```java
   	public static void invokeBeanFactoryPostProcessors(
   			ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
   
   		// Invoke BeanDefinitionRegistryPostProcessors first, if any.
   		Set<String> processedBeans = new HashSet<>();
   
   		if (beanFactory instanceof BeanDefinitionRegistry) {
   			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
   			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
   			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
   
   			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
   				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
   					BeanDefinitionRegistryPostProcessor registryProcessor =
   							(BeanDefinitionRegistryPostProcessor) postProcessor;
   					registryProcessor.postProcessBeanDefinitionRegistry(registry);
   					registryProcessors.add(registryProcessor);
   				}
   				else {
   					regularPostProcessors.add(postProcessor);
   				}
   			}
   
   			// Do not initialize FactoryBeans here: We need to leave all regular beans
   			// uninitialized to let the bean factory post-processors apply to them!
   			// Separate between BeanDefinitionRegistryPostProcessors that implement
   			// PriorityOrdered, Ordered, and the rest.
   			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
   
   			// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
   			String[] postProcessorNames =
   					beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   			for (String ppName : postProcessorNames) {
   				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
   					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
   					processedBeans.add(ppName);
   				}
   			}
   			sortPostProcessors(currentRegistryProcessors, beanFactory);
   			registryProcessors.addAll(currentRegistryProcessors);
   			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   			currentRegistryProcessors.clear();
   
   			// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
   			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   			for (String ppName : postProcessorNames) {
   				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
   					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
   					processedBeans.add(ppName);
   				}
   			}
   			sortPostProcessors(currentRegistryProcessors, beanFactory);
   			registryProcessors.addAll(currentRegistryProcessors);
   			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   			currentRegistryProcessors.clear();
   
   			// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
   			boolean reiterate = true;
   			while (reiterate) {
   				reiterate = false;
   				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   				for (String ppName : postProcessorNames) {
   					if (!processedBeans.contains(ppName)) {
   						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
   						processedBeans.add(ppName);
   						reiterate = true;
   					}
   				}
   				sortPostProcessors(currentRegistryProcessors, beanFactory);
   				registryProcessors.addAll(currentRegistryProcessors);
   				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   				currentRegistryProcessors.clear();
   			}
   
   			// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
   			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
   			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
   		}
   
   		else {
   			// Invoke factory processors registered with the context instance.
   			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
   		}
   
   		// Do not initialize FactoryBeans here: We need to leave all regular beans
   		// uninitialized to let the bean factory post-processors apply to them!
   		String[] postProcessorNames =
   				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
   
   		// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
   		// Ordered, and the rest.
   		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
   		List<String> orderedPostProcessorNames = new ArrayList<>();
   		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
   		for (String ppName : postProcessorNames) {
   			if (processedBeans.contains(ppName)) {
   				// skip - already processed in first phase above
   			}
   			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
   				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
   			}
   			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
   				orderedPostProcessorNames.add(ppName);
   			}
   			else {
   				nonOrderedPostProcessorNames.add(ppName);
   			}
   		}
   
   		// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
   		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
   		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
   
   		// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
   		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
   		for (String postProcessorName : orderedPostProcessorNames) {
   			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   		}
   		sortPostProcessors(orderedPostProcessors, beanFactory);
   		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
   
   		// Finally, invoke all other BeanFactoryPostProcessors.
   		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
   		for (String postProcessorName : nonOrderedPostProcessorNames) {
   			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   		}
   
   		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
   
   		// Clear cached merged bean definitions since the post-processors might have
   		// modified the original metadata, e.g. replacing placeholders in values...
   		beanFactory.clearMetadataCache();
   	}
   ```

   `debug`断点停留在

   ```java
   invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory)
   ```

   究竟是如何找到所有的`BeanFactoryPostProcessor`并执行它们的方法?

   可以看到这里有个`nonOrderedPostProcessors`参数,可以看到这个`nonOrderedPostProcessors`是从下面的`bean`工厂获取到的,根据每个`beanPostProcessors`的名字,去获取`BeanFactoryPostProcessor.class`

   ```java
   for (String postProcessorName : nonOrderedPostProcessorNames) {
   	nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   }
   ```

   可以看到上面的方法也有优先级的排序,优先执行实现了`PriorityOrdered`接口的

   ```java
   // First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
   sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
   //执行实现了优先级接口的
   invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);
   ```

   其次再执行实现了`Ordered`接口的

   ```java
   // Next, invoke the BeanFactoryPostProcessors that implement Ordered.
   List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
   for (String postProcessorName : orderedPostProcessorNames) {
       orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   }
   sortPostProcessors(orderedPostProcessors, beanFactory);
   //执行实现了Ordered接口的
   invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);
   ```

   最后再执行普通的

   ```java
   // Finally, invoke all other BeanFactoryPostProcessors.
   List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
   for (String postProcessorName : nonOrderedPostProcessorNames) {
   nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   }
   //执行普通的
   invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
   ```

   取到所有的 `BeanFactoryPostProcessor`后,进入`invokeBeanFactoryPostProcessors()`看是如何执行的,点击进去,如下

   ```java
   private static void invokeBeanFactoryPostProcessors(
       Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
   	//遍历所有的postProcessors,然后进行回调,这里的参数beanFactory是如何找到的,继续看上面的invokeBeanFactoryPostProcessors()方法研究
       for (BeanFactoryPostProcessor postProcessor : postProcessors) {
           postProcessor.postProcessBeanFactory(beanFactory);
       }
   }
   ```

    首先,从`beanFactory`工厂获取所有类型是`BeanFactoryPostProcessor`组件

   ```java
   String[] postProcessorNames =
   				beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
   ```

   获取到所有的组件放入数组后,逐个遍历,检查哪些是有排序的,哪些是没有排序的

   ```java
   for (String ppName : postProcessorNames) {
       if (processedBeans.contains(ppName)) {
           // skip - already processed in first phase above
       }
       else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
           //如果有优先级接口的放进这
           priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
       }
       else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
           //有排序接口的添加进这里
           orderedPostProcessorNames.add(ppName);
       }
       else {
           //普通的添加进这里
           nonOrderedPostProcessorNames.add(ppName);
       }
   }
   ```

   最后再一个个`invokeBeanFactoryPostProcessors()`执行它们的方法

   **那为什么BeanFactoryPostProcessor会在初始化其它组件前执行了?**

   如果之前在AOP源理有印象的话,应该清楚,在刷新容器时

   **`bean`对象创建在finishBeanFactoryInitialization(beanFactory);`此方法是实例化所有的单实例`bean`,而此方法上面已经执行了`invokeBeanFactoryPostProcessors(beanFactory);`方法了,所以`BeanFactoryPostProcessor`在初始化其它组件前执行**

   ![](http://120.77.237.175:9080/photos/springanno/131.jpg)


# BeanDefinitionRegistryPostProcessor

`BeanDefinitionRegistryPostProcessor`是`BeanFactoryPostProcessor`的子接口,如下

```java
//可以看到其实现增加了一个postProcessBeanDefinitionRegistry()方法.根据下面的意思,其加载的时机是在所有Bean定义信息将要被加载,但bean实例还未创建
//可以看出其加载的时机是在BeanFactoryPostProcessor之前,因为BeanFactoryPostProcessor是在所有的bean的定义已经加载到beanFactory之后执行的
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean definition registry after its
	 * standard initialization. All regular bean definitions will have been loaded,
	 * but no beans will have been instantiated yet. This allows for adding further
	 * bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}

```

## 测试

```java
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

  //BeanDefinitionRegistry bean定义信息的保存中心,以后BeanFactory就是按照BeanDefinitionRegistry里面保存的每一个bean定义信息创建bean实例
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
       {
    System.out.println(
        "MyBeanDefinitionRegistryPostProcessor...postProcessBeanDefinitionRegistry...bean的数量:"
            + registry.getBeanDefinitionCount());

    // RootBeanDefinition beanDefinition = new RootBeanDefinition(Blue.class);
    // 与上面的 初始化方法一样,两个都作用都是一样的
    AbstractBeanDefinition beanDefinition =
        BeanDefinitionBuilder.rootBeanDefinition(Blue.class).getBeanDefinition();
    registry.registerBeanDefinition("hello", beanDefinition);
  }

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    System.out.println(
        "MyBeanDefinitionRegistryPostProcessor...postProcessBeanFactory...bean的数量:"
            + beanFactory.getBeanDefinitionCount());
  }
}
```

继续使用`BeanFactoryPostProcessor`上面的测试代码执行,结果如下

```java
MyBeanDefinitionRegistryPostProcessor...postProcessBeanDefinitionRegistry...bean的数量:9
MyBeanDefinitionRegistryPostProcessor...postProcessBeanFactory...bean的数量:10
MyBeanFactoryPostProcessor...postProcessBeanFactory()...
当前BeanFactory中有10 个Bean
[org.springframework.context.annotation.internalConfigurationAnnotationProcessor, org.springframework.context.annotation.internalAutowiredAnnotationProcessor, org.springframework.context.annotation.internalCommonAnnotationProcessor, org.springframework.context.event.internalEventListenerProcessor, org.springframework.context.event.internalEventListenerFactory, extConfig, myBeanDefinitionRegistryPostProcessor, myBeanFactoryPostProcessor, car, hello]
car construct .....

```

1. 从其结果可以看到先执行`MyBeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry()`,先取到`IOC`的`bean`数量有9个
2. 然后在其里面注册了一个hello
3. 再执行``MyBeanDefinitionRegistryPostProcessor.postProcessBeanFactory()`,获取的`bean`的数量是10个
4. `BeanDefinitionRegistryPostProcessor`里面的方法执行完以后,再到`BeanFactoryPostProcessor`的方法执行

**因此可知`BeanDefinitionRegistryPostProcessor`优先于`BeanFactoryPostProcessor`执行,可利用`BeanDefinitionRegistryPostProcessor`给容器中再额外添加一些组件**

## 原理

在`postProcessBeanDefinitionRegistry()`和`postProcessBeanFactory()`方法上打上断点,执行

1. 首先IOC创建对象

   ![](http://120.77.237.175:9080/photos/springanno/132.jpg)

2. `refresh()`方法,跟上面的`BeanFactoryPostProcessor`原理一样

   ![](http://120.77.237.175:9080/photos/springanno/129.jpg)

3. 进入还是跟上面的`BeanFactoryPostProcessor`一样,调用了`invokeBeanFactoryPostProcessors(beanFactory);`方法,继续点击进入

   ![](http://120.77.237.175:9080/photos/springanno/133.jpg)

4. 还是跟上面的`BeanFactoryPostProcessor`一样,调用了`PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());`方法,继续根据`DEBUG`流程进入一个方法

   ![](http://120.77.237.175:9080/photos/springanno/134.jpg)

5. 这时断点停留在如下图

   ![](http://120.77.237.175:9080/photos/springanno/135.jpg)

6. `currentRegistryProcessors`是如何获取的呢?在方法上面继续查找,如下图

   ![](http://120.77.237.175:9080/photos/springanno/136.jpg)
   
   发现原来整 个处理逻辑和上面的`BeanFactoryPostProcessor`的处理逻辑一样,从容器中获取到所有的`BeanDefinitionRegistryPostProcessor`组件,如下图,现在获取到的只有一个,就是我们自定义的组件,继续进入`invokeBeanDefinitionRegistryPostProcessors`看是如何执行的
   
   ![](http://120.77.237.175:9080/photos/springanno/137.jpg)
   
7. 其执行过程就是获取到所有有的`BeanDefinitionRegistryPostProcessor`,依次触发所有的`postProcessBeanDefinitionRegistry()`方法
   
      ```java
      private static void invokeBeanDefinitionRegistryPostProcessors(
          Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
      
          for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
              postProcessor.postProcessBeanDefinitionRegistry(registry);
          }
      }
      ```
   
8. 那为什么`postProcessBeanDefinitionRegistry()`会优先在`postProcessBeanFactory()`方法前面执行呢,是因为如下图

   ![](http://120.77.237.175:9080/photos/springanno/138.jpg)
   
   点击进`invokeBeanFactoryPostProcessors`可看如下
   
   ```java
   	private static void invokeBeanFactoryPostProcessors(
   			Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
   //重点是这,调用所有的BeanFactoryPostProcessor.postProcessBeanFactory()方法
   		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
   			postProcessor.postProcessBeanFactory(beanFactory);
   		}
   	}
   ```
   
   `invokeBeanDefinitionRegistryPostProcessors()`方法在是优先在`invokeBeanFactoryPostProcessors`方法前执行的,因此可知
   
   **当触发完所有的`postProcessBeanDefinitionRegistry()`方法,再来触发`postProcessBeanFactory()`方法**
   
9. 那为什么我们自定义的`MyBeanFactoryPostProcessor`会在后面执行呢?,继续根据当前断点往下查看源码,如下图
   
   ![](http://120.77.237.175:9080/photos/springanno/139.jpg)
   
   这段代码就是上面`BeanFactoryPostProcessor`的源码优先级加载过程,里面所有的`invokeBeanFactoryPostProcessors()`方法里面都是`postProcessBeanFactory()`的执行时机过程
   
   **再来从容器中找到`BeanFactoryPostProcessor`组件,然后依次触发`postProcessBeanFactory()`方法**
   
   **因此`MyBeanDefinitionRegistryPostProcessor`定义的方法优先于`BeanFactoryPostProcessor`定义的方法执行**

# ApplicationListener

监听容器中发布的事件。事件驱动模型开发,源码如下:

```java
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
```

如果要写一个监听器，那么我们要写的监听器就得实现这个接口，而该接口中带的泛型就是我们要监听的事件。也就是说，我们应该要监听`ApplicationEvent`及其下面的子事件，因此，如果我们要发布事件，那么所发布的事件应该是`ApplicationEvent`的子类。

## ApplicationListener的用法

```java
@Component
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {

    //当容器中发布此事件以后，方法触发
    public void onApplicationEvent(ApplicationEvent event) {

        System.out.println("收到的事件:" + event);
    }
}
```

测试一下以上监听器的功能了。试着运行`IOCTest_Ext`测试类中的`test01`方法，看能不能收到事件？

```java
public class IOCTest_Ext {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);
        applicationContext.close();
    }
}
```

```java
收到的事件:org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Tue Feb 23 14:25:26 CST 2021]
收到的事件:org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Tue Feb 23 14:25:26 CST 2021]
```

可以看到我们收到了两个事件，这两个事件分别是`org.springframework.context.event.ContextRefreshedEvent`和`org.springframework.context.event.ContextClosedEvent`，其中第一个是容器已经刷新完成事件，第二个是容器关闭事件。而且，从下图中可以看到，这两个事件都是ApplicationEvent下面的事件。

![](http://120.77.237.175:9080/photos/springanno/140.jpg)

- `ContextClosedEvent` 容器关闭事件
- `ContextRefreshedEvent` 容器刷新事件
- `ContextStoppedEvent` 容器开始事件
- `ContextStartedEvent` 容器停止事件

只不过现在暂时还没用到容器开始和容器停止这两个事件而已。其实，想必你也已经猜到了，`IOC`容器在刷新完成之后便会发布`ContextRefreshedEvent`事件，一旦容器关闭了便会发布`ContextClosedEvent`事件。

这时，你不禁要问了，我们可不可以自己发布事件呢？当然可以了，只不过此时我们应该遵循如下的步骤来进行开发。

第一步，写一个监听器来监听某个事件。当然了，监听的这个事件必须是ApplicationEvent及其子类。

第二步，把监听器加入到容器中，这样Spring才能知道有这样一个监听器。

第三步，只要容器中有相关事件发布，那么我们就能监听到这个事件。举个例子，就拿我们上面监听的两个事件来说，你要搞清楚的一个问题是谁发布了这两个事件，猜都能猜得到，这两个事件都是由Spring发布的。

- ContextRefreshedEvent：容器刷新完成事件。即容器刷新完成（此时，所有bean都已完全创建），便会发布该事件。
- ContextClosedEvent：容器关闭事件。即容器关闭时，便会发布该事件。

其实，在上面我们也看到了，Spring还默认定义了一些其他事件。除此之外，我们自己也可以编写一些自定义事件。但是，问题的关键是我们能不能自己发布事件呢？答案是可以。

第四步，我们自己来发布一个事件。而发布一个事件，我们需要像下面这么来做

```java
public class IOCTest_Ext {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);
        //发布事件；
        applicationContext.publishEvent(new ApplicationEvent(new String("我发布的事件")) {
        });
        //关闭容器
        applicationContext.close();
    }
}
```

```java
收到的事件:org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Tue Feb 23 14:42:19 CST 2021]
收到的事件:com.anno.IOCTest_Ext$1[source=我发布的事件]
收到的事件:org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Tue Feb 23 14:42:19 CST 2021]
```

 除了能收到容器刷新完成和容器关闭这俩事件之外，还能收到我们调用`applicationContext`发布出去的事件。只要把这个事件发布出去，那么我们自己编写的监听器就能监听到这个事件。

## ApplicationListener的原理

### 创建容器并且刷新

在自己编写的监听器（例如`MyApplicationListener`）内的`onApplicationEvent`方法处打上一个断点

![](http://120.77.237.175:9080/photos/springanno/141.jpg)

首先来到了IOCTest_Ext测试类的test01方法中，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/142.jpg)

可以看到第一步是要来创建`IOC`容器的。继续跟进代码，可以看到在创建容器的过程中，还会调用一个`refresh`方法来刷新容器，刷新容器其实就是创建容器里面的所有`bean`

![](http://120.77.237.175:9080/photos/springanno/143.jpg)

继续跟进代码，看这个`refresh`方法里面具体都做了些啥，如下图所示，可以看到它里面调用了如下一个finishRefresh方法，顾名思义，该方法就是来完成容器的刷新工作的

![](http://120.77.237.175:9080/photos/springanno/144.jpg)

对于这个`refresh`方法而言，想必你是再熟悉不过了，它里面做了很多的事情，也就是说，在容器刷新这一步中做了很多的事情，比如执行`BeanFactoryPostProcessor`组件的方法、给容器中注册后置处理器等等，这些之前我就已经详细讲解过了

### 容器刷新完成，发布ContextRefreshedEvent事件

当容器刷新完成时，就会调用`finishRefresh`方法，那么该方法里面又做了哪些事呢？我们继续跟进代码，如下图所示，发现容器刷新完成时调用的`finishRefresh`方法里面又调用了一个叫`publishEvent`的方法，而且传递进该方法的参数是`new`出来的一个`ContextRefreshedEvent`对象。这一切都在说明着，**容器在刷新完成以后，便会发布一个`ContextRefreshedEvent`事件**。

![](http://120.77.237.175:9080/photos/springanno/145.jpg)

接下来，我们就来看看`ContextRefreshedEvent`事件的发布流程

### 事件发布流程

当容器刷新完成时，就会来调用一个叫`publishEvent`的方法，而且会向该方法中传递一个`ContextRefreshedEvent`对象。这即是发布了一个事件，这个事件呢，正是我们第一个感知到的事件，即容器刷新完成事件。接下来，我们就来看看这个事件到底是怎么发布的。

继续跟进代码，可以看到程序来到了如下图所示的地方

![](http://120.77.237.175:9080/photos/springanno/146.jpg)

继续跟进代码，可以看到程序来到了如下图所示的这行代码处。

![](http://120.77.237.175:9080/photos/springanno/147.jpg)

```java
//1. getApplicationEventMulticaster() 获取事件多播器
//2. multicastEvent(applicationEvent, eventType) 向各个监听器派发事件
getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
```

可以看到先是调用一个`getApplicationEventMulticaster`方法，从该方法的名字中就可以看出，它是来获取事件多播器的，不过也有人叫事件派发器。接下来，我们就可以说说`ContextRefreshedEvent`事件的发布流程了。

首先，调用`getApplicationEventMulticaster`方法来获取到事件多播器，或者，你叫事件派发器也行。所谓的事件多播器就是指我们要把一个事件发送给多个监听器，让它们同时感知。

然后，调用事件多播器的`multicastEvent`方法，这个方法就是用来向各个监听器派发事件的。那么，它到底是怎么来派发事件的呢？

继续跟进代码，来好好看看`multicastEvent`方法是怎么写的，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/148.jpg)

可以看到，一开始就有一个`for`循环，在这个for循环中，有一个`getApplicationListeners`方法，它是来拿到所有的`ApplicationListener`的，拿到之后就会来挨个遍历再来拿到每一个`ApplicationListener`。

很快，你会看到有一个`if`判断，它会判断`getTaskExecutor`方法能不能够返回一个`Executor`对象，如果能够，那么会利用`Executor`的异步执行功能来使用多线程的方式异步地派发事件；如果不能够，那么就使用同步的方式直接执行`ApplicationListener`的方法。

可以点进去`Executor`里面去看一看，你会发现它是一个接口，并且`Spring`提供了一个叫`TaskExecutor`的子接口来继承它。在该子接口下，`Spring`又提供了一个`SyncTaskExecutor`类来实现它，以及一个`AsyncTaskExecutor`接口来继承它，

![](http://120.77.237.175:9080/photos/springanno/149.jpg)

`SyncTaskExecutor`支持以同步的方式来执行某一任务，`AsyncTaskExecutor`支持以异步的方式来执行某一任务。也就是说，我们可以在自定义事件派发器的时候（这个后面就会讲到），给它传递这两种类型的`TaskExecutor`，让它支持以同步或者异步的方式来派发事件。

现在程序很显然是进入到了`else`判断语句中，也就是说，现在是使用同步的方式来直接执行`ApplicationListener`的方法的，相应地，这时是调用了一个叫`invokeListener`的方法，而且在该方法中传入了当前遍历出来的`ApplicationListener`。那么问题来了，这个方法的内部又做了哪些事呢？

我们继续跟进代码，可以看到程序来到了如下图所示的地方。这时，`invokeListener`方法里面调用了一个叫`doInvokeListener`的方法

![](http://120.77.237.175:9080/photos/springanno/150.jpg)

继续跟进代码，可以看到程序来到了如下图所示的这行代码处。看到这儿，你差不多应该知道了这样一个结论，即**遍历拿到每一个`ApplicationListener`之后，会回调它的`onApplicationEvent`方法**

![](http://120.77.237.175:9080/photos/springanno/151.jpg)

继续跟进代码，这时，程序就会来到我们自己编写的监听器（例如`MyApplicationListener`）中，继而来回调它其中的`onApplicationEvent`方法

![](http://120.77.237.175:9080/photos/springanno/152.jpg)

以上就是`ContextRefreshedEvent`事件的发布流程

写到这里，我来做一下总结，即总结一下一个事件怎么发布的。首先调用一个`publishEvent`方法，然后获取到事件多播器，接着为我们派发事件。你看，就是这么简单！

可以知道收到的第一个事件就是`ContextRefreshedEvent`事件。为了让大家能够更加清晰地看到这一点，我按下`F6`快捷键让程序继续往下运行，如下图所示，这时控制台打印出了收到的第一个事件，即`ContextRefreshedEvent`事件。

![](http://120.77.237.175:9080/photos/springanno/153.jpg)

### 自定义发布的事件

按下`F8`快捷键让程序运行到下一个断点，如下图所示，这时是来到了我们自己编写的监听器（例如`MyApplicationListener`）里面的onApplicationEvent`方法中

这里，我们要明白一点，这儿是我们自己发布的事件，就是调用容器的`publishEvent`方法发布出去的事件，这可以从`test01`方法的如下这行代码处看出。

![](http://120.77.237.175:9080/photos/springanno/155.jpg)

接下来，我们就要来看一下咱们自己发布的事件的发布流程了。

**其实，咱们自己发布的事件的发布流程与上面所讲述的`ContextRefreshedEvent`事件的发布流程是一模一样的**，为什么会这么说呢，这得看接下来的源码分析了。

继续跟进代码，可以看到程序来到了如下图所示的地方，这不是还是再调用`publishEvent`方法吗？

![](http://120.77.237.175:9080/photos/springanno/154.jpg)

继续跟进代码，可以看到程序来到了如下图所示的这行代码处

![](http://120.77.237.175:9080/photos/springanno/156.jpg)

可以看到，还是先获取到事件多播器，然后再调用事件多播器的`multicastEvent`方法向各个监听器派发事件。

继续跟进代码，可以看到`multicastEvent`方法是像下面这样写的。

![](http://120.77.237.175:9080/photos/springanno/157.jpg)

依然还是拿到所有的`ApplicationListener`，然后再遍历拿到每一个`ApplicationListener`，接着来挨个执行每一个`ApplicationListener`的方法。怎么来执行呢？如果是异步模式，那么就使用异步的方式来执行，否则便使用同步的方式直接执行。

继续跟进代码，可以看到程序来到了如下图所示的地方。这时，`invokeListener`方法里面调用了一个叫`doInvokeListener`的方法

![](http://120.77.237.175:9080/photos/springanno/158.jpg)

继续跟进代码，可以看到程序来到了如下图所示的这行代码处。依旧能看到，这是遍历拿到每一个`ApplicationListener`之后，再来回调它的`onApplicationEvent`方法。

![](http://120.77.237.175:9080/photos/springanno/159.jpg)

以上就是咱们自己发布的事件的发布流程。

**不管是容器发布的事件，还是咱们自己发布的事件，都会走以上这个事件发布流程，即先拿到事件多播器，然后再拿到所有的监听器，接着再挨个回调它的方法**。

### 容器关闭，发布ContextClosedEvent事件

接下来，可想而知，就应该是要轮到最后一个事件了，即容器关闭事件。我们按下`F8`快捷键让程序运行到下一个断点，如下图所示，可以看到控制台打印出了收到的第二个事件，即我们自己发布的事件。

![](http://120.77.237.175:9080/photos/springanno/160.jpg)

而且，从上图中也能看到，这时程序来到了我们自己编写的监听器（例如`MyApplicationListener`）里面的`onApplicationEvent`方法中。

下面，我们就来看看容器关闭事件的发布流程。继续按`F8`这时程序来到了`IOCTest_Ext`测试类的`test01`方法中的最后一行代码处，如下图所示

![](http://120.77.237.175:9080/photos/springanno/161.jpg)

以上这行代码说的就是来关闭容器，那么容器是怎么关闭的呢？我们继续跟进代码，发现关闭容器的`close`方法里面又调用了一个`doClose`方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/162.jpg)

继续跟进代码，如下图所示，可以看到`doClose`方法里面又调用了一个`publishEvent`方法，而且传递进该方法的参数是`new`出来的一个`ContextClosedEvent`对象。这一切都在说明着，**关闭容器，会发布一个`ContextClosedEvent`事件**

![](http://120.77.237.175:9080/photos/springanno/163.jpg)

当然不管怎么发布，ContextClosedEvent事件所遵循的发布流程和上面讲述的一模一样

### 事件多播器

**事件多播器是怎么拿到的吗？**

你是不是注意到了这一点，在上面我们讲述事件发布的流程时，会通过一个`getApplicationEventMulticaster`方法来获取事件多播器，我们不妨看一下该方法是怎么写的，如下

```java
/**
	 * Return the internal ApplicationEventMulticaster used by the context.
	 * @return the internal ApplicationEventMulticaster (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (this.applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
					"call 'refresh' before multicasting events via the context: " + this);
		}
		return this.applicationEventMulticaster;
	}
```

通过该方法可以获取到事件多播器，很显然，`applicationEventMulticaster`这么一个玩意代表的就是事件多播器。那么问题来了，我们是从哪获取到的事件多播器的呢？

首先，创建`IOC`容器。我们知道，在创建容器的过程中，还会调用一个`refresh`方法来刷新容器，如下图所示

```java
	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given component classes and automatically refreshing the context.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 */
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
		this();
		register(componentClasses);
		refresh();
	}
```

然后，我们就要来看看这个`refresh`方法具体都做了哪些事。该方法我们已经很熟悉了，如下图所示，可以看到在该方法中会调非常多的方法，其中就有一个叫`initApplicationEventMulticaster`的方法，顾名思义，它就是来初始化`ApplicationEventMulticaster`的。而且，它还是在初始化创建其他组件之前调用的

![](http://120.77.237.175:9080/photos/springanno/164.jpg)

那么，初始化`ApplicationEventMulticaster`的逻辑又是怎样的呢？我们也可以来看一看，进入`initApplicationEventMulticaster`方法里面，如下。

```java
	/**
	 * Initialize the ApplicationEventMulticaster.
	 * Uses SimpleApplicationEventMulticaster if none defined in the context.
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
			}
		}
	}
```

它就是先判断`IOC`容器（也就是`BeanFactory`）中是否有`id`等于`applicationEventMulticaster`的组件APPLICATION_EVENT_MULTICASTER_BEAN_NAME`这个常量就是字符串`applicationEventMulticaster`，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/165.jpg)

如果`IOC`容器中有`id`等于`applicationEventMulticaster`的组件，那么就会通过`getBean`方法直接拿到这个组件；如果没有，那么就重新new一个`SimpleApplicationEventMulticaster`类型的事件多播器，然后再把这个事件多播器注册到容器中，也就是说，这相当于我们自己给容器中注册了一个事件多播器，这样，以后我们就可以在其他组件要派发事件的时候，自动注入这个事件多播器就行了。**其实说白了，在整个事件派发的过程中，我们可以自定义事件多播器**。

以上就是我们这个事件多播器它是怎么拿到的。

### 容器是怎么将容器中的监听器注册到事件多播器中

还记得我们在分析事件发布流程时，有一个叫`getApplicationListeners`的方法吗？

![](http://120.77.237.175:9080/photos/springanno/166.jpg)

通过该方法就能知道容器中有哪些监听器。

那么问题来了，容器中到底有哪些监听器呢？其实，这个问题的答案很简单，因为我们把监听器早就已经添加到了容器中，所以，容器只需要判断一下哪些组件是监听器就行了。

首先，依旧还是创建`IOC`容器。我们也知道，在创建容器的过程中，还会调用一个`refresh`方法来刷新容器，如下图所示。

```java
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
		this();
		register(componentClasses);
		refresh();
	}
```

然后，我们就要来看看这个`refresh`方法具体都做了哪些事。该方法我们已经超熟悉了，如下图所示，可以看到在该方法中会调非常多的方法，其中就有一个叫`registerListeners`的方法，顾名思义，它就是来注册监听器的

![](http://120.77.237.175:9080/photos/springanno/167.jpg)

那到底是怎么来注册监听器的呢？我们可以点进去该方法里面看一看，如下图所示，可以看到它是先从容器中拿到所有的监听器，然后再把它们注册到`applicationEventMulticaster`当中。

```java
	/**
	 * Add beans that implement ApplicationListener as listeners.
	 * Doesn't affect other listeners, which can be added without being beans.
	 */
	protected void registerListeners() {
		// Register statically specified listeners first.
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let post-processors apply to them!
        //1. 从容器中获取到所有的监听器
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
            //2. 然后把这些监听器注册到事件派发器中
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// Publish early application events now that we finally have a multicaster...
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (earlyEventsToProcess != null) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}
```

当然了，第一次调用该方法时，`getApplicationListeners`方法是获取不到容器中所有的监听器的，因为这些监听器还没注册到容器中

所以，第一次调用该方法时，它会调用`getBeanNamesForType`方法从容器中拿到所有`ApplicationListener`类型的组件（即监听器），然后再把这些组件注册到事件派发器中。

## @EventListener注解的用法

```java
@Service
public class UserServiceListener {

    @EventListener(classes={ApplicationEvent.class})
    public void listen(ApplicationEvent event) {
        System.out.println("UserService...监听到的事件：" + event);
    }
}
```

执行发布事件的测试方法,结果如下

```
UserService...监听到的事件：org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Wed Feb 24 16:05:51 CST 2021]
收到的事件:org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Wed Feb 24 16:05:51 CST 2021]
UserService...监听到的事件：com.anno.IOCTest_Ext$1[source=我发布的事件]
收到的事件:com.anno.IOCTest_Ext$1[source=我发布的事件]
UserService...监听到的事件：org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Wed Feb 24 16:05:51 CST 2021]
收到的事件:org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@f30e0a, started on Wed Feb 24 16:05:51 CST 2021]
```

 `@EventListener`这个注解的使用会比较多，因为它使用起来非常方便。

## @EventListener注解的原理

可以点进去`@EventListener`这个注解里面去看一看，如下图所示

![](http://120.77.237.175:9080/photos/springanno/168.jpg)

描述中有一个醒目的字眼，即参考`EventListenerMethodProcessor`。意思可能是说，如果你想搞清楚`@EventListener`注解的内部工作原理，那么可以参考`EventListenerMethodProcessor`这个类。

`EventListenerMethodProcessor`是啥呢？它就是一个处理器，其作用是来解析方法上的`@EventListener`注解的。这也就是说，**Spring会使用`EventListenerMethodProcessor`这个处理器来解析方法上的`@EventListener`注解**。因此，接下来，我们就要将关注点放在这个处理器上，搞清楚这个处理器是怎样工作的。搞清楚了这个，自然地我们就搞清楚了`@EventListener`注解的内部工作原理。

我们点进去`EventListenerMethodProcessor`这个类里面去看一看，如下图所示，发现它实现了一个接口，叫`SmartInitializingSingleton`。这时，要想搞清楚`EventListenerMethodProcessor`这个处理器是怎样工作的，那就得先搞清楚`SmartInitializingSingleton`这个接口的原理了。

![](http://120.77.237.175:9080/photos/springanno/169.jpg)

不妨点进去`SmartInitializingSingleton`这个接口里面去看一看，你会发现它里面定义了一个叫`afterSingletonsInstantiated`的方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/170.jpg)

接下来，我们就要搞清楚到底是什么时候开始触发执行`afterSingletonsInstantiated`方法的。

仔细看一下`SmartInitializingSingleton`接口中`afterSingletonsInstantiated`方法上面的描述信息，不难看出该方法是在所有的单实例`bean`已经全部被创建完了以后才会被执行。

其实，在介绍`SmartInitializingSingleton`接口的时候，我们也能从描述信息中知道，在所有的单实例`bean`已经全部被创建完成以后才会触发该接口。紧接着下面一段的描述还说了，该接口的调用时机有点类似于`ContextRefreshedEvent`事件，即在容器刷新完成以后，便会回调该接口。也就是说，这个时候容器已经创建完了。

好吧，回到主题，我们来看看`afterSingletonsInstantiated`方法的触发时机。首先，我们得在`EventListenerMethodProcessor`类里面的`afterSingletonsInstantiated`方法处打上一个断点，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/171.jpg)

后，以`debug`的方式运行`IOCTest_Ext`测试类中的`test01`方法，这时程序停留在了`EventListenerMethodProcessor`类里面的`afterSingletonsInstantiated`方法中

不妨从`IOCTest_Ext`测试类中的test01方法开始，来梳理一遍整个流程。

![](http://120.77.237.175:9080/photos/springanno/172.jpg)

可以看到第一步是要来创建`IOC`容器的。继续跟进代码，可以看到在创建容器的过程中，还会调用一个`refresh`方法来刷新容器，刷新容器其实就是创建容器里面的所有`bean`。

![](http://120.77.237.175:9080/photos/springanno/173.jpg)

继续跟进代码，看这个`refresh`方法里面具体都做了些啥，如下图所示，可以看到它里面调用了如下一个`finishBeanFactoryInitialization`方法，顾名思义，该方法就是来完成`BeanFactory`的初始化工作的

![](http://120.77.237.175:9080/photos/springanno/174.jpg)

对于以上这个方法，我相信大家都不会陌生，因为我们之前就看过好多遍了，它其实就是来初始化所有剩下的那些单实例`bean`的。也就是说，如果还有一些单实例`bean`还没被初始化，即还没创建对象，那么便会在这一步进行（初始化）。

继续跟进代码，如下图所示，可以看到在`finishBeanFactoryInitialization`方法里面执行了如下一行代码，依旧还是来初始化所有剩下的单实例 `bean`。

![](http://120.77.237.175:9080/photos/springanno/175.jpg)

继续跟进代码，如下图所示，可以看到现在程序停留在了如下这行代码处。

![](http://120.77.237.175:9080/photos/springanno/176.jpg)

这不就是我们要讲的`afterSingletonsInstantiated`方法吗？它原来是在这儿调用的啊！接下来，咱们就得好好看看在调用该方法之前，具体都做了哪些事。

由于`afterSingletonsInstantiated`方法位于`DefaultListableBeanFactory`类的`preInstantiateSingletons`方法里面，所以我们就得来仔细看看`preInstantiateSingletons`方法里面具体都做了些啥了。

进入眼帘的首先是一个for循环，在该for循环里面，`beanNames`里面存储的都是即将要创建的所有`bean`的名字，紧接着会做一个判断，即判断`bean`是不是抽象的，是不是单实例的，等等等等。最后，不管怎样，都会调用getBean方法来创建对象。

```java
	public void preInstantiateSingletons() throws BeansException {
		if (logger.isTraceEnabled()) {
			logger.trace("Pre-instantiating singletons in " + this);
		}

		// Iterate over a copy to allow for init methods which in turn register new bean definitions.
		// While this may not be part of the regular factory bootstrap, it does otherwise work fine.
		List<String> beanNames = new ArrayList<>(this.beanDefinitionNames);

		// Trigger initialization of all non-lazy singleton beans...
        //循环获取所有的bean
		for (String beanName : beanNames) {
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
				if (isFactoryBean(beanName)) {
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					if (bean instanceof FactoryBean) {
						final FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
											((SmartFactoryBean<?>) factory)::isEagerInit,
									getAccessControlContext());
						}
						else {
							isEagerInit = (factory instanceof SmartFactoryBean &&
									((SmartFactoryBean<?>) factory).isEagerInit());
						}
						if (isEagerInit) {
							getBean(beanName);
						}
					}
				}
				else {
					getBean(beanName);
				}
			}
		}

		// Trigger post-initialization callback for all applicable beans...
		for (String beanName : beanNames) {
			Object singletonInstance = getSingleton(beanName);
            //判断是否继承了SmartInitializingSingleton
			if (singletonInstance instanceof SmartInitializingSingleton) {
				final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
				if (System.getSecurityManager() != null) {
					AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                        	//调用afterSingletonsInstantiated()方法
						smartSingleton.afterSingletonsInstantiated();
						return null;
					}, getAccessControlContext());
				}
				else {
                    	//调用afterSingletonsInstantiated()方法
					smartSingleton.afterSingletonsInstantiated();
				}
			}
		}
	}
```

总结一下就是，**先利用一个`for`循环拿到所有我们要创建的单实例bean，然后挨个调用`getBean`方法来创建对象。也即，创建所有的单实例bean。**

再来往下翻阅`preInstantiateSingletons`方法，发现它下面还有一个`for`循环，在该`for`循环里面，`beanNames`里面依旧存储的是即将要创建的所有`bean`的名字。那么，在该for循环中所做的事情又是什么呢？很显然，在最上面的那个for循环中，所有的单实例`bean`都已经全部创建完了。因此，在下面这个for循环中，咱们所要做的事就是**获取所有创建好的单实例`bean`，然后判断每一个`bean`对象是否是`SmartInitializingSingleton`这个接口类型的，如果是，那么便调用它里面的`afterSingletonsInstantiated`方法，而该方法就是`SmartInitializingSingleton`接口中定义的方法。**

至此，`afterSingletonsInstantiated`就是在**所有单实例bean全部创建完成以后执行的**。

如果所有的单实例bean都已经创建完了，也就是说下面这一步都执行完了，那么说明IOC容器已经创建完成

![](http://120.77.237.175:9080/photos/springanno/177.jpg)

那么，紧接着便会来调用`finishRefresh`方法，容器已经创建完了，此时就会来发布容器已经刷新完成的事件。这就呼应了开头的那句话，即`SmartInitializingSingleton`接口的调用时机有点类似于`ContextRefreshedEvent`事件，即在容器刷新完成以后，便会回调该接口

# Spring IOC容器创建源码解析

## BeanFactory的创建以及预准备工作

先来看一下如下的一个单元测试类（例如IOCTest_Ext）

```java
public class IOCTest_Ext {
    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);
        //发布事件；
        applicationContext.publishEvent(new ApplicationEvent(new String("我发布的事件")) {
        });
        //关闭容器
        applicationContext.close();
    }
}
```

我们知道如下这样一行代码是来new一个IOC容器的，而且还可以看到传入了一个配置类。

```java
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ExtConfig.class);
```

点进去`AnnotationConfigApplicationContext`类的有参构造方法里面去看一看，如下图所示，相信大家对该有参构造方法是再熟悉不过了。

```java
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
		this();						//前面这些都是做一些预处理以及解析工作,由于现在是来分析
		register(componentClasses);		//Spring容器的创建以及初始化过程,所以略过
		refresh();
	}
```

由于我们现在是来分析Spring容器的创建以及初始化过程，所以我们将核心的关注点放在`refresh`方法上，也即刷新容器。该方法运行完以后，容器就创建完成了，包括所有的`bean`对象也都创建和初始化完成了。

接下来，我们在刷新容器的`refresh`方法上打上一个断点，重点分析一下刷新容器这个方法里面到底做了些什么事
![](http://120.77.237.175:9080/photos/springanno/178.jpg)

我们以`debug`的方式运行`IOCTest_Ext`测试类中的`test01`方法，如下图所示，程序现在停到了标注断点的refresh方法处。

![](http://120.77.237.175:9080/photos/springanno/179.jpg)

按下`F5`快捷键进入`refresh`方法里面，如下图所示，可以看到映入眼帘的是一个线程安全的锁机制，除此之外，你还能看到第一个方法，即prepareRefresh方法，顾名思义，它是来执行刷新容器前的预处理工作的

![](http://120.77.237.175:9080/photos/springanno/180.jpg)

那么问题来了，刷新容器前的这个预处理工作它到底都做了哪些事呢？下面我们就来详细说说。

### prepareRefresh()：刷新容器前的预处理工作

按下`F6`快捷键让程序往下运行，运行到`prepareRefresh`方法处时，按下`F5`快捷键进入该方法里面，如下图所示，可以看到会先清理一些缓存，我们的关注点不在这儿，所以略过。

继续按下`F6`快捷键让程序往下运行，运行到`super.prepareRefresh()`这行代码处，这儿也是来执行刷新容器前的预处理工作的。按下`F5`快捷键进入该方法里面，如下图所示，我们可以看到它里面都做了些什么预处理工作。

![](http://120.77.237.175:9080/photos/springanno/181.jpg)

发现就是先记录下当前时间，然后设置下当前容器是否是关闭、是否是活跃的等状态，除此之外，还会打印当前容器的刷新日志。如果你要是不信的话，那么可以按下F6快捷键让程序往下运行，直至运行到`initPropertySources`方法处，你便能看到控制台打印出了一些当前容器的刷新日志

这时，我们看到了第一个方法，即initPropertySources方法。那么，它里面做了些啥事呢？

#### initPropertySources()：子类自定义个性化的属性设置的方法

顾名思义，该方法是来初始化一些属性设置的。那么，该方法里面究竟做了些啥事呢？我们不妨进去一探究竟，按下`F5`快捷键进入该方法中，如下图所示，发现它是空的，没有做任何事情。

```java
	protected void initPropertySources() {
		// For subclasses: do nothing by default.
	}
```

但是，我们要注意该方法是`protected`类型的，这意味着它是留给子类自定义个性化的属性设置的。例如，我们可以自己来写一个`AnnotationConfigApplicationContext`的子类，在容器刷新的时候，重写这个方法，这样，我们就可以在子类（也叫子容器）的该方法中自定义一些个性化的属性设置了。

这个方法只有在子类自定义的时候有用，只不过现在它还是空的，里面啥也没做。

#### getEnvironment().validateRequiredProperties()：获取其环境变量，然后校验属性的合法性

继续按下`F6`快捷键让程序往下运行，直至运行到以下这行代码处。

![](http://120.77.237.175:9080/photos/springanno/182.jpg)

这行代码的意思很容易知道，前面不是自定义了一些个性化的属性吗？这儿就是来校验这些属性的合法性的。

那么是怎么来进行属性校验的呢？首先是要来获取其环境变量，你可以按下`F5`快捷键进入getEnvironment方法中去看看，如下图所示，可以看到该方法就是用来获取其环境变量的。

```java
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}
```

继续按下F6快捷键让程序往下运行，让程序再次运行到`getEnvironment().validateRequiredProperties()`这行代码处。然后，再次按下F5快捷键进入`validateRequiredProperties`方法中去看看，如下图所示，可以看到就是使用属性解析器来进行属性校验的。

```java
    public void validateRequiredProperties() throws MissingRequiredPropertiesException {
    	//使用属性解析器来进行属性校验
        this.propertyResolver.validateRequiredProperties();
    }
```

只不过，我们现在没有自定义什么属性，所以，此时并没有做任何属性校验工作。

#### 保存容器中早期的事件

继续按下`F6`快捷键让程序往下运行，直至运行到以下这行代码处。

![](http://120.77.237.175:9080/photos/springanno/183.jpg)

这儿是`new`了一个`LinkedHashSet`，它主要是来临时保存一些容器中早期的事件的。如果有事件发生，那么就存放在这个`LinkedHashSet`里面，这样，当事件派发器好了以后，直接用事件派发器把这些事件都派发出去。

总结一下就是一句话，即允许收集早期的容器事件，等待事件派发器可用之后，即可进行发布。

至此，我们就分析完了`prepareRefresh`方法，以上就是该方法所做的事情。我们发现这个方法和`BeanFactory`并没有太大关系，因此，接下来我们还得来看下一个方法，即`obtainFreshBeanFactory`方法。

#### obtainFreshBeanFactory()：获取BeanFactory对象

继续按下`F6`快捷键让程序往下运行，直至运行至以下这行代码处。

![](http://120.77.237.175:9080/photos/springanno/184.jpg)

可以看到一个叫`obtainFreshBeanFactory`的方法，顾名思义，它是来获取`BeanFactory`的实例的。接下来，我们就来看看该方法里面究竟做了哪些事。

#### refreshBeanFactory()：创建BeanFactory对象，并为其设置一个序列化id

按下`F5`快捷键进入该方法中，如下图所示，可以看到其获取`BeanFactory`实例的过程是下面这样子的。

```java
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		refreshBeanFactory();
		return getBeanFactory();
	}
```

发现首先调用了一个叫`refreshBeanFactory`的方法，该方法见名思义，应该是来刷新`BeanFactory`的。那么，该方法里面又做了哪些事呢？

我们可以按下`F5`快捷键进入该方法中去看看，如下图所示，发现程序来到了`GenericApplicationContext`类里面。

![](http://120.77.237.175:9080/photos/springanno/185.jpg)

而且，我们还可以看到在以上`refreshBeanFactory`方法中，会先判断是不是重复刷新了。于是，我们继续按下`F6`快捷键让程序往下运行，发现程序并没有进入到if判断语句中，而是来到了下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/186.jpg)

程序运行到这里，你会不会有一个大大的疑问，那就是我们的`beanFactory`不是还没创建么，怎么在这儿又开始调用方法了呢，难道是已经创建了吗？

我们向上翻阅`GenericApplicationContext`类的代码，发现原来是在这个类的无参构造方法里面，就已经实例化了`beanFactory`这个对象。也就是说，在创建GenericApplicationContext`对象时，无参构造器里面就`new`出来了`beanFactory`这个对象。
![](http://120.77.237.175:9080/photos/springanno/187.jpg)

相当于我们做了非常核心的一步，即创建了一个`beanFactory`对象，而且该对象还是`DefaultListableBeanFactory`类型的。

现在，我们已经知道了在`GenericApplicationContext`这个类的无参构造方法里面，就已经实例化了`beanFactory`这个对象。那么，你可能会有疑问，究竟是在什么地方调用`GenericApplicationContext`类的无参构造方法的呢？

这时，我们可以去看一下我们的单元测试类（例如IOCTest_Ext），如下图所示
![](http://120.77.237.175:9080/photos/springanno/188.jpg)

只要点进去`AnnotationConfigApplicationContext`类里面去看一看，你就知道大概了，如下图所示，原来`AnnotationConfigApplicationContext`类继承了`GenericApplicationContext`这个类，所以，当我们实例化`AnnotationConfigApplicationContext`时就会调用其父类的构造方法，相应地这时就会对我们的`BeanFactory`进行实例化了。

![](http://120.77.237.175:9080/photos/springanno/189.jpg)

`BeanFactory`对象创建好了之后，接下来就是要给其设置一个序列化`id`，相当于打了一个`id`标识。我们不妨`Inspect`一下`getId`方法的值，发现它是`org.springframework.context.annotation.AnnotationConfigApplicationContext@1f23557`这么一长串的字符串，原来这个序列化id就是它啊！

![](http://120.77.237.175:9080/photos/springanno/190.jpg)

按下`F6`快捷键让程序往下运行，直至程序运行到下面这行代码处，`refreshBeanFactory`方法就执行完了。

该方法所做的事情很简单，无非就是**创建了一个`BeanFactory`对象（`DefaultListableBeanFactory`类型的），并为其设置好了一个序列化`id`**。

#### getBeanFactory()：返回设置了序列化id后的BeanFactory对象

接下来，我们就要看看`getBeanFactory`方法了。按下`F5`快捷键进入该方法里面，如下图所示，发现它里面就只是做了一件事，即返回设置了序列化id后的`BeanFactory`对象。

```java
	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		return this.beanFactory;	//返回设置了序列化id后的BeanFactory对象
	}
```

继续按下`F6`快捷键让程序往下运行，一直让程序运行到下面这行代码处。程序运行至此，就返回了我们刚刚创建好的那个BeanFactory对象，只不过这个BeanFactory对象，由于我们刚创建，所以它里面的什么东西都是默认的一些设置。

![](http://120.77.237.175:9080/photos/springanno/191.jpg)

至此，我们就分析完了`obtainFreshBeanFactory`方法，以上就是该方法所做的事情，即获取BeanFactory对象

#### prepareBeanFactory(beanFactory)：BeanFactory的预准备工作，即对BeanFactory进性一些预处理

接下来，我们就得来说说`prepareBeanFactory`方法了。顾名思义，该方法就是对`BeanFactory`做一些预处理，即`BeanFactory`的预准备工作。

为什么要在这儿对`BeanFactory`做一些预处理啊？因为我们前面刚刚创建好的`BeanFactory`还没有做任何设置呢，所以就得在这儿对`BeanFactory`做一些设置了。

按下F5快捷键进入该方法中，如下图所示，我们发现会对`BeanFactory`进行一系列的赋值（即设置一些属性）。比方说，设置`BeanFactory`的类加载器，就得像下面这样。

```java
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// Tell the internal bean factory to use the context's class loader etc.
		beanFactory.setBeanClassLoader(getClassLoader());	//设置BeanFactory的类加载器
        //设置支持相关表达式语言的解析器
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
        //添加属性的编辑注册器
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

		// Configure the bean factory with context callbacks.
        //发现还向BeanFactory中添加了一个BeanPostProcessor，即ApplicationContextAwareProcessor。
        //温馨提示：这儿只是向BeanFactory中添加了部分的BeanPostProcessor，而不是添加所有的，比如我们现在只是向BeanFactory中添加了一个叫ApplicationContextAwareProcessor的BeanPostProcessor。它的作用，我们之前也看过了，就是在bean初始化以后来判断这个bean是不是实现了ApplicationContextAware接口。
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
        //可以看到现在是来为BeanFactory设置忽略的自动装配的接口，比如说像EnvironmentAware、EmbeddedValueResolverAware等等这些接口。
        //那么，设置忽略的自动装配的这些接口有什么作用呢？作用就是，这些接口的实现类不能通过接口类型来自动注入。
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

		// BeanFactory interface not registered as resolvable type in a plain factory.
		// MessageSource registered (and found for autowiring) as a bean.
        //可以看到现在是来为BeanFactory注册可以解析的自动装配。
        //所谓的可以解析的自动装配，就是说，我们可以直接在任何组件里面自动注入像BeanFactory、ResourceLoader、ApplicationEventPublisher（它就是上一讲我们讲述的事件派发器）以及ApplicationContext（也就是我们的IOC容器）这些东西。
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		beanFactory.registerResolvableDependency(ApplicationContext.class, this);

		// Register early post-processor for detecting inner beans as ApplicationListeners.
        //可以看到现在又向BeanFactory中添加了一个BeanPostProcessor，只不过现在添加的是一个叫ApplicationListenerDetector的BeanPostProcessor。
        //也就是说，会向BeanFactory中添加很多的后置处理器，后置处理器的作用就是在bean初始化前后做一些工作。
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

		// Detect a LoadTimeWeaver and prepare for weaving, if found.
        //继续向下看prepareBeanFactory方法，可以看到有一个if判断语句，它这是向BeanFactory中添加编译时与AspectJ支持相关的东西。
        //这里的if判断是添加编译时与AspectJ支持相关的东西
		if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			// Set a temporary ClassLoader for type matching.
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}

        //而我们现在默认的这些都是运行时的动态代理，所以你会看到这样一个现象，按下F6快捷键让程序往下运行，程序并不会进入到以上if判断语句中，而是来到了下面这个if判断语句处。
        
		// Register default environment beans.
        //这儿是在向BeanFactory中注册一些与环境变量相关的bean，比如注册了一个名字是environment，值是当前环境对象（其类型是ConfigurableEnvironment）的bean。
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
            //注册当前的环境对象
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}
        //除此之外，还注册了一个名字为systemProperties的bean，也即系统属性，它是通过当前环境对象的getSystemProperties方法获得的。我们来看一下系统属性是个什么东西，进入getSystemProperties方法里面，如下图所示，可以看到系统属性就是一个Map<String, Object>，该Map里面的key/value就是环境变量里面的key/value。
		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}
        //最后，还会注册一个名字为systemEnvironment的bean，即系统的整个环境信息。我们也不妨点进去getSystemEnvironment方法里面去看一下，如下图所示，发现系统的整个环境信息也是一个Map<String, Object>。
		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}
```

```java
    public Map<String, Object> getSystemProperties() {
        try {
            return System.getProperties();
        } catch (AccessControlException var2) {
            return new ReadOnlySystemAttributesMap() {
                @Nullable
                protected String getSystemAttribute(String attributeName) {
                    try {
                        return System.getProperty(attributeName);
                    } catch (AccessControlException var3) {
                        if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                            AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system property '" + attributeName + "'; its value will be returned [null]. Reason: " + var3.getMessage());
                        }

                        return null;
                    }
                }
            };
        }
    }
```

```java
    public Map<String, Object> getSystemEnvironment() {
        if (this.suppressGetenvAccess()) {
            return Collections.emptyMap();
        } else {
            try {
                return System.getenv();
            } catch (AccessControlException var2) {
                return new ReadOnlySystemAttributesMap() {
                    @Nullable
                    protected String getSystemAttribute(String attributeName) {
                        try {
                            return System.getenv(attributeName);
                        } catch (AccessControlException var3) {
                            if (AbstractEnvironment.this.logger.isInfoEnabled()) {
                                AbstractEnvironment.this.logger.info("Caught AccessControlException when accessing system environment variable '" + attributeName + "'; its value will be returned [null]. Reason: " + var3.getMessage());
                            }

                            return null;
                        }
                    }
                };
            }
        }
    }
```

也就是说，我们向`BeanFactory`中注册了以上三个与环境变量相关的`bean`。以后，如果我们想用的话，只须将它们自动注入即可。

继续按下F6快捷键让程序往下运行，一直让程序运行到下面这行代码处。程序运行至此，说明`prepareBeanFactory`方法就执行完了，相应地，`BeanFactory`就已经创建好了，里面该设置的属性也都设置了。
![](http://120.77.237.175:9080/photos/springanno/192.png)

#### postProcessBeanFactory(beanFactory)：BeanFactory准备工作完成后进行的后置处理工作

接下来，就得来说说`postProcessBeanFactory`方法了。它说的就是在`BeanFactory`准备工作完成之后进行的后置处理工作。我们不妨点进去该方法里面看看，它究竟做了哪些事，如下图所示，发现它里面是空的。

```java
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
	}
```

这不是和我们刷新容器前的预处理工作中的`initPropertySources`方法一样吗？方法里面都是空的，默认都是不进行任何处理的，但是方法都是`protected`类型的，这也就是说子类可以通过重写这个方法，在`BeanFactory`创建并预处理完成以后做进一步的设置。

这个方法只有在子类重写的时候有用，只不过现在它还是空的，里面啥也没做。

继续按下F6快捷键让程序往下运行，一直让程序运行到下面这行代码处。程序运行到这里之后，我们先让它停一停。

![](http://120.77.237.175:9080/photos/springanno/193.jpg)

至此，`BeanFactory`的创建以及预准备工作就已经完成啦

既然有了`BeanFactory`对象，那么接下来我们就要利用`BeanFactory`来创建各种组件了。

## 执行BeanFactoryPostProcessor

接着上面,当执行到可以看到上图,执行一个叫invokeBeanFactoryPostProcessors的方法，这个方法我们之前也看过，它就是来执行BeanFactoryPostProcessor的,它就是BeanFactory的后置处理器。那么，它是什么时候来执行的呢？我们不妨看一下它的源码，如下图所示。

```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for overriding or adding
	 * properties even to eager-initializing beans.
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
    //以上描述说:BeanFactoryPostProcessor的方法是在BeanFactory标准初始化之后执行的.而BeanFactory标准初始化就是我们上一讲所阐述的内容
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```

BeanFactoryPostProcessor接口的继承树，如下图所示。
![](http://120.77.237.175:9080/photos/springanno/194.jpg)

可以看到，`BeanFactoryPostProcessor`接口下还有一个子接口，即`BeanDefinitionRegistryPostProcessor`。以前，我们还用过BeanDefinitionRegistryPostProcessor`这个接口给IOC容器中额外添加过组件

接下来，我们就来看看`invokeBeanFactoryPostProcessors`这个方法里面到底做了哪些事，也就是看一下`BeanFactoryPostProcessor`的整个执行过程。

### BeanFactoryPostProcessor的执行过程

在invokeBeanFactoryPostProcessors`方法里面主要就是执行了`BeanDefinitionRegistryPostProcessor`的`postProcessBeanDefinitionRegistry`和`postProcessBeanFactory`这俩方法，以及`BeanFactoryPostProcessors`的`postProcessBeanFactory`方法

#### 先执行BeanDefinitionRegistryPostProcessor的方法

我们可以按下`F5`快捷键进入`invokeBeanFactoryPostProcessors`方法里面去瞧一瞧，如下图所示，可以看到现在程序来到了如下这行代码处。

```java
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}
```

以上这个`invokeBeanFactoryPostProcessors`方法，看名字就知道了，同样是来执行`BeanFactoryPostProcessor`的方法的，那怎么来执行呢？我们可以按下F5快捷键来跟踪源码看看，此时你会发现进入到了`getBeanFactoryPostProcessors`方法中，如下图所示，该方法仅仅只是返回了一个空的`List<BeanFactoryPostProcessor>`集合，该集合是用于存放所有的`BeanFactoryPostProcessor`的，只不过它现在默认是空的而已，也就是说该集合里面还没存储任何`BeanFactoryPostProcessor`。

![](http://120.77.237.175:9080/photos/springanno/195.jpg)

不过，可以通过以下`addBeanFactoryPostProcessor`方法向该集合中添加`BeanFactoryPostProcessor`。

![](http://120.77.237.175:9080/photos/springanno/196.jpg)

返回到调用层，然后按下`F5`快捷键进入`invokeBeanFactoryPostProcessors`方法里面去一探究竟，如下图所示

![](http://120.77.237.175:9080/photos/springanno/197.jpg)

其中，一开始的注释就告诉了我们，无论什么时候都会先调用实现了`BeanDefinitionRegistryPostProcessor`接口的类。

一定要注意哟！紧接着会先来判断我们这个`beanFactory`是不是`BeanDefinitionRegistry`。之前我们在上一讲中就已经说过了，生成的`BeanFactory`对象是`DefaultListableBeanFactory`类型的，而且还使用了`ConfigurableListableBeanFactory`接口进行接收。这里我们就来看下`DefaultListableBeanFactory`类是不是实现了`BeanDefinitionRegistry`接口，看下图，很显然是实现了。

![](http://120.77.237.175:9080/photos/springanno/198.jpg)

自然地，程序就会进入到if判断语句中，进来以后呢，我们来大致地分析一下下面的流程。首先，映入眼帘的是一个for循环，它是来循环遍历`invokeBeanFactoryPostProcessors`方法中的第二个参数的，即`beanFactoryPostProcessors`。其实呢，就是拿到所有的`BeanFactoryPostProcessor`，再挨个遍历出来。然后，再来以遍历出来的每一个`BeanFactoryPostProcessor`是否实现了`BeanDefinitionRegistryPostProcessor`接口为依据将其分别存放于以下两个箭头所指向的`LinkedList`中，其中实现了`BeanDefinitionRegistryPostProcessor`接口的还会被直接调用。

![](http://120.77.237.175:9080/photos/springanno/199.jpg)

##### 根据优先级，分别执行BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处，可以看到现在是会拿到所有`BeanDefinitionRegistryPostProcessor`的这些`bean`的名字。

![](http://120.77.237.175:9080/photos/springanno/200.jpg)

有意思的是，会发现每次执行前，都会运行完这么一行代码：

```java
beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
```

这行代码的意思，就是来获取容器中所有实现了`BeanDefinitionRegistryPostProcessor`接口的组件。**那么，为什么每次执行前，都会运行这样一行代码呢？这是因为每次执行可能会加载进来新的`BeanDefinition`，所以每次都要重新获取。**

###### 执行实现了PriorityOrdered优先级接口的BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法

继续按下F6快捷键让程序往下运行，往下运行一步即可，`Inspect`一下`postProcessorNames`变量的值，你会发现从IOC容器中拿到的只有一个名字为`org.springframework.context.annotation.internalConfigurationAnnotationProcessor`的组件，即默认拿到的是`ConfigurationClassPostProcessor`这样一个`BeanDefinitionRegistryPostProcessor`。

![](http://120.77.237.175:9080/photos/springanno/201.jpg)

第一次获取容器中所有实现了`BeanDefinitionRegistryPostProcessor`接口的组件时，其实只能获取到`ConfigurationClassPostProcessor`，因为手工加的只是`BeanDefinition`，等`ConfigurationClassPostProcessor`把对应的`Definition`加载后，下面才能获取到我们手工加载的BeanDefinition。

获取到容器中所有`BeanDefinitionRegistryPostProcessor`组件之后，接下来，就得遍历所有这些`BeanDefinitionRegistryPostProcessor`组件了，挨个遍历出来之后，会判断每一个`BeanDefinitionRegistryPostProcessor`组件是不是实现了`PriorityOrdered`这个优先级接口，若是，则会先按照优先级排个序，然后再调用该组件的`postProcessBeanDefinitionRegistry`方法。

![](http://120.77.237.175:9080/photos/springanno/202.jpg)

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处，这儿就是来执行每一个实现了`PriorityOrdered`优先级接口的`BeanDefinitionRegistryPostProcessor`组件的方法的。

```java
invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
```

不妨按下`F5`快捷键进入该方法中去看一看，如下图所示，可以看到这儿是来执行 `BeanDefinitionRegistryPostProcessor`组件的`postProcessBeanDefinitionRegistry`方法的。

```java
	private static void invokeBeanDefinitionRegistryPostProcessors(
			Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}
```

###### 执行实现了Ordered顺序接口的BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法

继续按下F6快捷键让程序往下运行，直至运行到下面这行代码处，可以看到在每次执行前都会执行下面一行代码，这是因为每次执行可能会加载进来新的`BeanDefinition`，所以每次都要重新获取所有实现了`BeanDefinitionRegistryPostProcessor`接口的组件
![](http://120.77.237.175:9080/photos/springanno/203.jpg)

很明显，这儿是来执行实现了`Ordered`顺序接口的`BeanDefinitionRegistryPostProcessor`组件的方法的。

原理同上面都是一模一样的，都是获取到容器中所有`BeanDefinitionRegistryPostProcessor`组件，紧接着再来遍历所有这些`BeanDefinitionRegistryPostProcessor`组件，挨个遍历出来之后，会判断每一个`BeanDefinitionRegistryPostProcessor`组件是不是实现了`Ordered`这个顺序接口，若是，则会先按照指定顺序来排个序，然后再调用该组件的`postProcessBeanDefinitionRegistry`方法。
![](http://120.77.237.175:9080/photos/springanno/204.jpg)

###### 执行没有实现任何优先级或者是顺序接口的BeanDefinitionRegistryPostProcessor的postProcessBeanDefinitionRegistry方法

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/205.jpg)

很明显，这块是来执行没有实现任何优先级或者是顺序接口的`BeanDefinitionRegistryPostProcessor`组件的方法的。

原理基本同上，首先获取到容器中所有`BeanDefinitionRegistryPostProcessor`组件，然后遍历所有这些`BeanDefinitionRegistryPostProcessor`组件，挨个遍历出来之后，接着再调用该组件的`postProcessBeanDefinitionRegistry`方法。

```java
invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
```

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处，这时，你会发现控制台有内容输出。

![](http://120.77.237.175:9080/photos/springanno/206.jpg)

很明显，这是咱们自己编写的`MyBeanDefinitionRegistryPostProcessor`类中的`postProcessBeanDefinitionRegistry`方法执行之后所输出的信息。

##### 执行BeanDefinitionRegistryPostProcessor的postProcessBeanFactory方法

因为`BeanDefinitionRegistryPostProcessor`是`BeanFactoryPostProcessor`的子接口，所以，接下来还得执行`BeanDefinitionRegistryPostProcessor`组件里面的`postProcessBeanFactory`方法。

按下F6快捷键让程序往下运行，往下运行一步即可，这时，你同样会发现控制台有内容输出。

![](http://120.77.237.175:9080/photos/springanno/207.jpg)

很明显，这是咱们自己编写的`MyBeanDefinitionRegistryPostProcessor`类中的`postProcessBeanFactory`方法执行之后所输出的信息。

也就是说，对于`BeanDefinitionRegistryPostProcessor`组件来说，它里面`postProcessBeanDefinitionRegistry`方法会先被调用，`postProcessBeanFactory`方法会后被调用。

#### 再执行BeanFactoryPostProcessor的方法

`BeanDefinitionRegistryPostProcessor`是要优先于`BeanFactoryPostProcessor`执行的。在上面已经执行完了`BeanDefinitionRegistryPostProcessor`的方法，接下来就得来执行`BeanFactoryPostProcessor`的方法了。

执行的流程是怎样的呢？按下`F6`快捷键让程序往下运行，直至程序运行到以下这行代码处，可以看到现在是来从`beanFactory`中按照类型获取所有`BeanFactoryPostProcessor`组件的名字。
![](http://120.77.237.175:9080/photos/springanno/208.jpg)

获取到所有`BeanFactoryPostProcessor`组件之后，接下来，就得遍历所有这些`BeanFactoryPostProcessor`组件了，挨个遍历出来之后，按照是否实现了PriorityOrdered`接口、`Ordered`接口以及没有实现这两个接口这三种情况进行分类，将其分别存储于三个`ArrayList`中
![](http://120.77.237.175:9080/photos/springanno/209.jpg)

紧接着，按照顺序依次执行`BeanFactoryPostProcessors`组件对应的`postProcessBeanFactory`方法。

![](http://120.77.237.175:9080/photos/springanno/210.jpg)

也就是说，先来执行实现了`PriorityOrdered`优先级接口的`BeanFactoryPostProcessor`组件的`postProcessBeanFactory`方法，再来执行实现了`Ordered`顺序接口的`BeanFactoryPostProcessor`组件的`postProcessBeanFactory方法，最后再来执行没有实现任何优先级或者是顺序接口的`BeanFactoryPostProcessor`组件的`postProcessBeanFactory`方法。

程序直至到这儿，才是来执行所有`BeanFactoryPostProcessor`组件的`postProcessBeanFactory`方法的呢？

继续按下F6快捷键让程序往下运行，直至程序运行到以下这行代码处，这时，发现控制台有内容输出。
![](http://120.77.237.175:9080/photos/springanno/211.jpg)

### 小结

继续按下`F6`快捷键让程序往下运行，直至程序运行到以下这行代码处，这时，`invokeBeanFactoryPostProcessors`方法才总算是执行完了。

![](http://120.77.237.175:9080/photos/springanno/212.jpg)

至此，`invokeBeanFactoryPostProcessors`方法最主要的核心作用就是执行了`BeanDefinitionRegistryPostProcessor`的`postProcessBeanDefinitionRegistry`和`postProcessBeanFactory`这俩方法，以及`BeanFactoryPostProcessors`的`postProcessBeanFactory`方法。

`BeanDefinitionRegistryPostProcessor`是要优先于`BeanFactoryPostProcessor`执行的。

## 注册BeanPostProcessor

`invokeBeanFactoryPostProcessors`方法，该方法所做的事情无非就是在`BeanFactory`准备好以后，执行`BeanFactoryPostProcessor`的方法。

接下来，就是`registerBeanPostProcessors`方法了。该方法就是来注册`BeanPostProcessor`的，即注册`bean`的后置处理器。其实，从该方法的描述上，也能知道其作用就是注册`bean`的后置处理器，拦截`bean`的创建过程。

### 获取所有的BeanPostProcessor

按下`F5`快捷键进入`registerBeanPostProcessors`方法里面，如下图所示，可以看到在该方法里面会调用`PostProcessorRegistrationDelegate`类的`registerBeanPostProcessors`方法。

```java
protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
	}
```

于是，我们再次按下`F5`快捷键进入以上方法中，如下图所示，可以看到一开始就会获取所有`BeanPostProcessor`组件的名字。

![](http://120.77.237.175:9080/photos/springanno/213.jpg)

`BeanPostProcessor`接口旗下有非常多的子接口，查看一下`BeanPostProcessor`接口的继承树就知道了，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/214.jpg)

`BeanPostProcessor`接口有很多子接口，而且每一个子接口，还有点不一样,不同接口类型的`BeanPostProcessor`在bean创建前后的执行时机是不一样的，虽然都是后置处理器。下面只介绍几个接口

- `DestructionAwareBeanPostProcessor`:它是销毁bean的后置处理器
- `InstantiationAwareBeanPostProcessor`
- `SmartInstantiationAwareBeanPostProcessor`
- `MergedBeanDefinitionPostProcessor`

获取到所有的`BeanPostProcessor`组件之后，按下F6快捷键让程序往下运行，直至程序运行到下面这行代码处，可以看到现在向`beanFactory`中添加了一个BeanPostProcessorChecker`类型的后置处理器，它是来检查所有`BeanPostProcessor`组件的。

![](http://120.77.237.175:9080/photos/springanno/215.jpg)

### 按分好类的优先级顺序来注册BeanPostProcessor

继续按下`F6`快捷键让程序往下运行，在这一过程中，可以看到后置处理器也可以按照是否实现了`PriorityOrdered`接口、`Ordered`接口以及没有实现这两个接口这三种情况进行分类。

![](http://120.77.237.175:9080/photos/springanno/216.jpg)

将所有的`BeanPostProcessor`组件分门别类之后，依次存储在不同的`ArrayLis`t集合中。

其实，会发现不止有三个`ArrayList`集合，还有一个名字为`internalPostProcessors的ArrayList`集合。如果后置处理器是`MergedBeanDefinitionPostProcessor`这种类型的，那么它就会被存放在名字为`internalPostProcessors`的`ArrayList`集合中。

由于`BeanPostProcessor`还是挺多的（除了IOC容器自己拥有的以外，还有咱们自己编写的），因此你得不停地按下F6快捷键让程序往下运行，直至程序把整个`for`循环执行完

当程序运行完整个`for`循环后，可以看到这是来注册实现了`PriorityOrdered`优先级接口的`BeanPostProcessor`的。因为这儿调用了一个叫`registerBeanPostProcessors`的方法，该方法就是来注册`bean`的后置处理器的，而所谓的注册就是向`beanFactory`中添加进去这些`BeanPostProcessor`。

按下F5快捷键进入到`registerBeanPostProcessors`方法中，如下

![](http://120.77.237.175:9080/photos/springanno/217.jpg)

然后，注册实现了`Ordered接口的BeanPostProcessor`，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/218.jpg)

接着，再来注册既没有实现`PriorityOrdered`接口又没有实现`Ordered`接口的`BeanPostProcessor`，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/219.jpg)

最后，再来注册`MergedBeanDefinitionPostProcessor`这种类型的`BeanPostProcessor`，因为名字为`internalPostProcessors`的`ArrayList`集合中存放的就是这种类型的`BeanPostProcessor`。

![](http://120.77.237.175:9080/photos/springanno/220.jpg)

除此之外，还会向`beanFactory`中添加一个`ApplicationListenerDetector`类型的`BeanPostProcessor`。不妨点进`ApplicationListenerDetector`类里面去看一看，如下图所示，它里面有一个`postProcessAfterInitialization`方法，该方法是在`bean`创建初始化之后，探测该`bean`是不是`ApplicationListener`的。
![](http://120.77.237.175:9080/photos/springanno/221.jpg)

也就是说，该方法的作用是检查哪些`bean`是监听器的。如果是，那么会将该`bean`放在容器中保存起来。

> 注意:**以上只是来注册`bean`的后置处理器，即只是向`beanFactory`中添加了所有这些`bean`的后置处理器，而并不会执行它们。**

## 初始化`MessageSource`组件

让程序继续执行到代码（即`initMessageSource`方法）处。顾名思义，该方法是来初始化`MessageSource`组件的。对于`Spring MVC`而言，该方法主要是来做国际化功能的，如消息绑定、消息解析等。

### 获取BeanFactory

按下`F5`快捷键进入到`initMessageSource`方法里面，如下图所示，可以看到一开始是先来获取`BeanFactory`的。

![](http://120.77.237.175:9080/photos/springanno/222.jpg)

而这个`BeanFactory`，之前早就准备好了。

### 看容器中是否有`id`为`messageSource`，类型是`MessageSource`的组件

按下F6快捷键让程序继续往下运行，会发现有一个判断，即判断`BeanFactory`中是否有一个`id`为`messageSource`的组件。为什么会这么说呢，只要看一下常量`MESSAGE_SOURCE_BEAN_NAME`的值就知道了，如下图所示，该常量的值就是`messageSource`。

![](http://120.77.237.175:9080/photos/springanno/223.jpg)

### 若有，则赋值给`this.messageSource`

如果有的话，那么会从`BeanFactory`中获取到`id`为`messageSource`，类型是`MessageSource`的组件，并将其赋值给`this.messageSource`。从下面这行代码看出。

```java
this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
```

很显然，容器刚开始创建的时候，肯定是还没有的，所以程序会来到下面的else语句中。

### 若没有，则创建一个`DelegatingMessageSource`类型的组件，并把创建好的组件注册在容器中

如果没有的话，那么`Spring`自己会创建一个`DelegatingMessageSource`类型的对象，即`MessageSource`类型的组件。

那么问题来了，这种`MessageSource`类型的组件有啥作用呢？不妨查看一下`MessageSource`接口的源码，如下图所示，它里面定义了很多重载的`getMessage`方法，该方法可以从配置文件（特别是国际化配置文件）中取出某一个key所对应的值。

![](http://120.77.237.175:9080/photos/springanno/224.jpg)

也就是说，这种`MessageSource`类型的组件的作用一般是取出国际化配置文件中某个`key`所对应的值，而且还能按照区域信息获取

紧接着，把创建好的`MessageSource`类型的组件注册到容器中，所执行的是下面这行代码。

![](http://120.77.237.175:9080/photos/springanno/225.jpg)

那么，以后想获取国际化配置文件中的值的时候，就可以直接自动注入这个`MessageSource`类型的组件了，然后调用它的`getMessage`方法就行了，并且还能按照区域信息获取

## 初始化事件派发器

代码执行到（即`initApplicationEventMulticaster`方法）处。顾名思义，该方法是来初始化事件派发器的。

按下`F5`快捷键进入到`initApplicationEventMulticaster`方法里面，如下图所示，可以看到一开始是先来获取BeanFactory的

```java
	protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();	//获取BeanFactory
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
						"[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
			}
		}
	}
```

### 看容器中是否有`id`为`applicationEventMulticaster`，类型是`ApplicationEventMulticaster`的组件

按下F6快捷键让程序继续往下运行，会发现有一个判断，即判断`BeanFactory`中是否有一个`id`为`applicationEventMulticaster`的组件。只要看一下常量`APPLICATION_EVENT_MULTICASTER_BEAN_NAME`的值，如下图所示，该常量的值就是`applicationEventMulticaster`。

![](http://120.77.237.175:9080/photos/springanno/226.jpg)

### 若有，则赋值给`this.applicationEventMulticaster`

如果有的话，那么会从`BeanFactory`中获取到`id`为`applicationEventMulticaster`，类型是`ApplicationEventMulticaster`的组件，并将其赋值给`this.applicationEventMulticaster`。这可以从下面这行代码看出。

```java
this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
```

也就是说，如果我们之前已经在容器中配置了一个事件派发器，那么此刻就能从`BeanFactory`中获取到该事件派发器了。

很显然，容器刚开始创建的时候，肯定是还没有的，所以程序会来到下面的`else`语句中。

### 若没有，则创建一个`SimpleApplicationEventMulticaster`类型的组件，并把创建好的组件注册在容器中

如果没有的话，那么`Spring`自己会创建一个`SimpleApplicationEventMulticaster`类型的对象，即一个简单的事件派发器。

然后，把创建好的事件派发器组件注册到容器中，即添加到`BeanFactory`中，所执行的是下面这行代码。

```java
beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
```

这样，我们以后其他组件要使用事件派发器，直接自动注入这个事件派发器组件即可。

## `onRefresh()`

按下`F6`快捷键让程序继续往下运行，直至运行到下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/227.jpg)

于是，我们按下`F5`快捷键进入到以上`onRefresh`方法里面去看一看，如下图所示，发现它里面是空的。

```java
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}
```

是不是觉得很熟悉，因为之前就见到过两次类似这样的空方法，一次是在做容器刷新前的预处理工作时，可以让子类自定义个性化的属性设置，另一次是在BeanFactory创建并预处理完成以后，可以让子类做进一步的设置。

同理，以上`onRefresh`方法就是留给子类来重写的，这样是为了给我们留下一定的弹性，当子类（也可以说是子容器）重写该方法后，在容器刷新的时候就可以再自定义一些逻辑了，比如给容器中多注册一些组件之类的。

## `registerListeners()`

继续按下`F6`快捷键让程序继续往下运行，直至运行到下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/228.jpg)

按照`registerListeners`方法上面的注释来说，该方法是来检查监听器并注册它们的。也就是说，该方法会将我们项目里面的监听器（也即咱们自己编写的`ApplicationListener`）注册进来。

```java
	protected void registerListeners() {
		// Register statically specified listeners first.
        //遍历从容器中获取到的所有的ApplicationListener.然后将遍历出的每一个监听器添加到事件派发器中
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let post-processors apply to them!
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// Publish early application events now that we finally have a multicaster...
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (earlyEventsToProcess != null) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}
```

可以看到一开始会有一个`for`循环，该`for`循环是来遍历从容器中获取到的所有的`ApplicationListener`的，然后将遍历出的每一个监听器添加到事件派发器中。

当我们按下`F6`快捷键让程序继续往下运行时，发现并没有进入`for`循环中，而是来到了下面这行代码处。

```java
String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
```

这是调用`getBeanNamesForType`方法从容器中拿到`ApplicationListener`类型的所有`bean`的名字的。也就是说，首先会从容器中拿到所有的`ApplicationListener`组件。

按下F6快捷键让程序继续往下运行，运行一步即可，这时`Inspect`一下`listenerBeanNames`变量的值，你就能看到确实是获取到了自己编写的`ApplicationListener`了，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/229.jpg)

然后，将获取到的每一个监听器添加到事件派发器中。

当早期容器中有一些事件时，会将这些事件保存在名为`earlyApplicationEvents的Set`集合中。这时，会先获取到事件派发器，再利用事件派发器将这些事件派发出去。也就是说，派发之前步骤产生的事件。

而现在呢，容器中默认还没有什么事件，所以，程序压根就不会进入到下面的`for`循环中去派发事件。当程序运行至下面这行代码处时，`registerListeners`方法就执行完了，它所做的事情很简单，无非就是从容器中拿到所有的`ApplicationListener`组件，然后将每一个监听器添加到事件派发器中。
![](http://120.77.237.175:9080/photos/springanno/230.jpg)

以上`finishBeanFactoryInitialization`方法是非常非常重要的，顾名思义，它是来初始化所有剩下的单实例`bean`的。执行完该方法之后，就完成`BeanFactory`的初始化了。

## 初始化所有剩下的单实例`bean`

### `finishBeanFactoryInitialization(beanFactory)`：初始化所有剩下的单实例`bean`

按下`F5`快捷键进入`finishBeanFactoryInitialization`方法里面

```java
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		// Initialize conversion service for this context.
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		// Register a default embedded value resolver if no bean post-processor
		// (such as a PropertyPlaceholderConfigurer bean) registered any before:
		// at this point, primarily for resolution in annotation attribute values.
		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}

		// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		// Stop using the temporary ClassLoader for type matching.
		beanFactory.setTempClassLoader(null);

		// Allow for caching all bean definition metadata, not expecting further changes.
		beanFactory.freezeConfiguration();

		// Instantiate all remaining (non-lazy-init) singletons.
		beanFactory.preInstantiateSingletons();
	}
```

上面所有的代码中,重点放在`beanFactory.preInstantiateSingletons()`,可以很清楚地从该行代码上的注释看出，这儿是来初始化所有剩下的单实例`bean`的。

### `beanFactory.preInstantiateSingletons()`：初始化所有剩下的单实例`bean`

#### 获取容器中所有的bean，然后依次进行初始化和创建对象

按下F5快捷键进入`preInstantiateSingletons`方法里面，如下图所示，可以看到一开始会先获取容器中所有`bean`的名字。当程序运行至如下这行代码处时，我们不妨`Inspect`一下`beanNames`变量的值，可以看到容器中现在有好多`bean`，有自定义编写的组件，有`Spring`默认内置的一些组件。
![](http://120.77.237.175:9080/photos/springanno/231.jpg)

对于容器中现在所有的这些`bean`来说，有些`bean`可能已经在之前的步骤中创建以及初始化完成了。因此，`preInstantiateSingletons`方法就是来初始化所有剩下的`bean`的。你很明显地看到，这就有一个`for`循环，该`for`循环是来遍历容器中所有的`bean`，然后依次触发它们的整个初始化逻辑的。

#### 获取bean的定义注册信息

进入`for`循环中之后，会获取到每一个遍历出来的`bean`的定义注册信息。我们要知道`bean`的定义注册信息是需要用`RootBeanDefinition`这种类型来进行封装的。

```java
RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
```

#### 根据bean的定义注册信息判断bean是否是抽象的、单实例的、懒加载的

接下来，会根据`bean`的定义注册信息来判断`bean`是否是抽象的、单实例的、懒加载的。如果该`bean`既不是抽象的也不是懒加载的（之前就说过懒加载，它就是用到的时候再创建对象，与@Lazy注解有关），并且还是单实例的，那么这个时候程序就会进入到最外面的if判断语句中,即以下语句中

按下`F6`快捷键让程序继续往下运行，你会发现还有一个判断，它是来判断当前`bean`是不是`FactoryBean`的，若是则进入到if判断语句中，若不是则进入到`else`分支语句中。

```java
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                //若当前bean是FactoryBean接口的,则进入到这里面来
				if (isFactoryBean(beanName)) {
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					if (bean instanceof FactoryBean) {
						final FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)
											((SmartFactoryBean<?>) factory)::isEagerInit,
									getAccessControlContext());
						}
						else {
							isEagerInit = (factory instanceof SmartFactoryBean &&
									((SmartFactoryBean<?>) factory).isEagerInit());
						}
						if (isEagerInit) {
							getBean(beanName);
						}
					}
				}
                //若不是,则走这个逻辑
				else {
					getBean(beanName);
				}
			}
		}
```

点进`isFactoryBean`方法里面去看一看，如下图所示，可以很清楚地看到该方法就是来判断当前`bean`是不是属于`FactoryBean`接口的。

```java
	@Override
	public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
		String beanName = transformedBeanName(name);
		Object beanInstance = getSingleton(beanName, false);
		if (beanInstance != null) {
			return (beanInstance instanceof FactoryBean);
		}
		// No singleton instance found -> check bean definition.
		if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
			// No bean definition found in this factory -> delegate to parent.
			return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
		}
		return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
	}
```

经过判断，如果我们的`bean`确实实现了`FactoryBean`接口，那么`Spring`就会调用`FactoryBean`接口里面的`getObject`方法来帮我们创建对象，查看`FactoryBean`接口的源码，会发现它里面定义了一个`getObject`方法，之前是不是已经说过了

![](http://120.77.237.175:9080/photos/springanno/232.jpg)

来看看第一个`bean`究竟是不是属于`FactoryBean`接口的，如下图。

![](http://120.77.237.175:9080/photos/springanno/233.jpg)

那么它是不是实现了`FactoryBean`接口呢？按下`F6`快捷键让程序继续往下运行，发现并没有，因为此时程序来到了下面的`else`分支语句中，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/234.jpg)

为了能够继续跟踪`Spring`源码的执行过程，可以在`getBean(beanName)`方法处打上一个断点

然后，需要给程序不断地放行了，一直放行到自定义编写的`bean`中，例如，之前在讲解`Spring`其他的扩展原理时，编写了一个如下的配置类。

![](http://120.77.237.175:9080/photos/springanno/235.jpg)

从该配置类的代码中，可以看到还会向容器中注册一个自定义编写的`Car`组件。同样地，为了方便继续跟踪``Spring``源码的执行过程，也可以在`public Car car()`方法上打上一个断点。

打上以上两个断点之后，按下`F8`快捷键让程序运行直到执行到自定义的Car对象，

![](http://120.77.237.175:9080/photos/springanno/236.jpg)

程序运行至此，可以知道`Car`对象是得通过`getBean`方法来创建的。于是，接下来，就来看看这个`Car`对象它到底是怎么创建的。

其实，之前早已用过这个方法了，查阅之前自定义编写的单元测试类，例如`IOCTest_AOP`，就能看到确实是调用了`IOC`容器的`getBean`方法，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/237.jpg)

按下`F5`快捷键进入`getBean`方法里面去看一看，如下图所示，可以看到它里面又调用了一个叫`doGetBean`的方法。

```java
	@Override
	public Object getBean(String name) throws BeansException {
		return doGetBean(name, null, null, false);
	}
```

继续按下`F5`快捷键进入`doGetBean`方法里面去看一看，如下图所示，可以看到一开始会拿到我们的`bean`的名字。

![](http://120.77.237.175:9080/photos/springanno/238.jpg)

然后，根据我们`bean`的名字尝试获取缓存中保存的单实例`bean`。可以看到这儿调用的是`getSingleton`方法，而且从缓存中获取到了之后会赋值给一个叫`sharedInstance`的变量，它翻译过来就是共享的`bean`。

```java
Object sharedInstance = getSingleton(beanName);
```

为什么这儿会先尝试从缓存中获取我们单实例`bean`呢？这是因为以前有一些单实例`bean`已经被创建好了，而且这些单实例`bean`也已经被缓存起来了，所有创建过的单实例bean都会被缓存起来，所以这儿会调用`getSingleton`方法先从缓存中获取。如果能获取到，那么说明这个单实例`bean`之前已经被创建过了。

为了看得更加清楚，我们不妨按下F5快捷键进入`getSingleton`方法里面去看一看，如下图所示，发现它里面是下面这个样子的，好像是又调用了一个重载的`getSingleton`方法。

```java
	@Override
	@Nullable
	public Object getSingleton(String beanName) {
		return getSingleton(beanName, true);
	}
```

再继续按下`F5`快捷键进入以上`getSingleton`方法里面去看一看，如下图所示，可以看到从缓存中获取其实就是从`singletonObjects`属性里面来获取。

```java
	@Nullable
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
        //singletonObjects它是该类里面的一个属性
		Object singletonObject = this.singletonObjects.get(beanName);
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			synchronized (this.singletonObjects) {
				singletonObject = this.earlySingletonObjects.get(beanName);
				if (singletonObject == null && allowEarlyReference) {
					ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
					if (singletonFactory != null) {
						singletonObject = singletonFactory.getObject();
						this.earlySingletonObjects.put(beanName, singletonObject);
						this.singletonFactories.remove(beanName);
					}
				}
			}
		}
		return singletonObject;
	}
```

说明一下，`singletonObjects`是`DefaultSingletonBeanRegistry`类里面的一个属性，点它，如下图所示。

```java
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
```

可以看到`singletonObjects`属性就是一个`Map`集合，该`Map`集合里面缓存的就是所有的单实例`bean`，而且还是按照`bean`的名字和其实例对象缓存起来的

还是回到`getSingleton`方法处，`Inspect`一下`singletonObjects`属性的值，发现不仅能看到一些已经创建好的`bean`，而且还能看到一些其他的属性信息以及环境变量等等。

![](http://120.77.237.175:9080/photos/springanno/239.jpg)

按下`F6`快捷键让程序继续往下运行，运行一步即可，此时`Inspect`一下`singletonObjec`变量的值，发现是`null`，如下图所示，这说明名字为`car`的`bean`从缓存中是获取不到的。

![](http://120.77.237.175:9080/photos/springanno/240.jpg)

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/241.jpg)

这说明，第一次想从缓存中获取的`bean`，是肯定获取不到的。

继续按下`F6`快捷键让程序往下运行，此时程序并没有进入到以上if判断语句中，而是来到了下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/242.jpg)

好了，现在是该开始创建`bean`的对象了，从上图中可以看到，首先会来获取一个（父）`BeanFactory`，因为后来也是用它来创建对象的。然后，立马会有一个判断，即判断是不是能获取到（父）`BeanFactory`。这儿为什么会强调要获取 （父） `BeanFactory`呢？是因为跟`Spring MVC`与`Spring`的整合有关，它俩整合起来以后，就会有父子容器了
按下`F6`快捷键让程序继续往下运行，会发现程序并没有进入到`if`判断语句中，而是来到了下面这行代码处，这说明并没有获取到（父）`BeanFactory`。

![](http://120.77.237.175:9080/photos/springanno/243.jpg)

可以看到这儿又有一个判断，而且程序能够进入到该if判断语句中，如下图所示。

```java
if (!typeCheckOnly) {
    //先来标记当前bean已经被创建,相当于做了一个标记
	markBeanAsCreated(beanName);
}
```

那么，`markBeanAsCreated`方法主要是来做什么的呢？它是在`bean`被创建之前，先来标记其为已创建，相当于做了一个小标记，这主要是为了防止多个线程同时来创建同一个`bean`，从而保证了`bean`的单实例特性。

按下F6快捷键让程序继续往下运行，当程序运行至下面这行代码处时，可以看到这是来获取`bean`的定义信息的。

```java
final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
```

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处。

```java
String[] dependsOn = mbd.getDependsOn();
```

可以看到这儿调用了`bean`定义信息对象的一个`getDependsOn`方法，它是来获取当前`bean`所依赖的其他`bean`的。

还记得之前在编写`Spring`的`XML`配置文件时，使用<bean>标签向容器中注册某个组件吗？比如如下的<bean>标签向容器中注册了一个名字为`person`的`bean`。

```
<bean id="person" class="com.anno.bean.Person">
    <property name="name" value="张三"/>
    <property name="age" value="10"/>
</bean>
```

其实，我们还可以在`<bean>`标签内使用一个`depends-on`属性，如下所示。

```
<bean id="person" class="com.anno.bean.Person" depends-on="book,user">
    <property name="name" value="张三"/>
    <property name="age" value="10"/>
</bean>
```

添加上depends-on="book,user"`这样一个属性之后，那么在创建名字为`person`的`bean`之前，得先把名字为`book`和`user`的`bean`给创建出来。也就是说，`depends-on`属性决定了`bean`的创建顺序。

回到主题，可以看到，`depends-on`属性也在`Spring`的源码中得到了体现，这可以参考上图。可以看到，会先获取当前`bean`所依赖的其他`bean`，如果要创建的`bean`确实有依赖其他`bean`的话，那么还是会调用`getBean`方法把所依赖的bean都创建出来。

![](http://120.77.237.175:9080/photos/springanno/244.jpg)

有没有发现一直在研究这个`getBean`方法啊？研究到这里，又会发现使用它来创建`bean`之前，它做的一件大事，就是把要创建的`bean`所依赖的`bean`先创建出来，当然了，前提是要创建的`bean`是确实是真的有依赖其他`bean`。

继续按下F6快捷键让程序往下运行，会发现程序并没有进入到if判断语句中，而是来到了下面这行代码处。
![](http://120.77.237.175:9080/photos/springanno/245.jpg)

在这会做一个判断，即判断`bean`是不是单实例的，由于`bean`就是单实例的，所以程序会进入到if判断语句中，来启动单实例`bean`的创建流程。

那么是怎么来启动单实例`bean`的创建流程的呢？可以看到，现在是调用了一个叫`getSingleton`的方法，而且在调用该方法时，还传入了两个参数，第一个参数是单实例`bean`的名字，第二个参数是`ObjectFactory`（是不是可以叫它Object工厂呢？）对象。

`Spring`就是利用它来创建单实例`bean`的对象的。里面还调用了一个`createBean`方法呢?

所以，为了方便继续跟踪`Spring`源码的执行过程，不妨在`createBean`方法处打上一个断点，如下图所示
![](http://120.77.237.175:9080/photos/springanno/246.jpg)

于是，按下`F8`快捷键让程序直接运行到下一个断点，此时程序来到了`createBean`方法处，现在要调用该方法来创建`bean`的对象,为了搞清楚单实例`bean`的创建流程，不妨按下`F5`快捷键进入到`createBean`方法里面去看一看，如下图所示。当程序往下运行时，可以看到会先拿到`bean`的定义信息，然后再来解析要创建的`bean`的类型。

![](http://120.77.237.175:9080/photos/springanno/247.jpg)

继续让程序往下运行，直至运行到下面这行代码处为止。

![](http://120.77.237.175:9080/photos/springanno/248.jpg)

可以看到，在创建`bean`的对象之前，会调用了一个`resolveBeforeInstantiation`方法。看该方法上的注释，它说是给`BeanPostProcessor`一个机会来提前返回`bean`的代理对象，这主要是为了解决依赖注入问题。也就是说，这是让`BeanPostProcessor`先拦截并返回代理对象。

但是，这究竟是哪个`BeanPostProcessor`在工作呢？之前就说过，`BeanPostProcessor`是有非常多的，一般而言，`BeanPostProcessor`都是在创建完`bean`对象初始化前后进行拦截的。而现在还没创建对象呢，因为是调用`createBean`方法来创建对象的，还记得吗？这也就是说，`bean`的对象还未创建之前，就已经有了一个`BeanPostProcessor`，那么这个`BeanPostProcess`是谁呢？

不妨按下F5快捷键进入`resolveBeforeInstantiation`方法里面，如下图所示，当程序运行到下面这行代码处时，原来是`InstantiationAwareBeanPostProcessor`这种类型的`BeanPostProcessor`。

![](http://120.77.237.175:9080/photos/springanno/249.jpg)

可以看到这儿是来判断是否有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器的。如果有，那么就会来执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器。那么，其执行逻辑又是怎么样的呢？看到那个`applyBeanPostProcessorsBeforeInstantiation`方法了没有，直接点进去看下，如下图所示。

```java
	@Nullable
	protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
```

是不是看到了这样的逻辑？在该方法中，会先判断遍历出的每一个`BeanPostProcessor`是不是`InstantiationAwareBeanPostProcessor`这种类型的，如果是，那么便来触发`其postProcessBeforeInstantiation`方法，该方法定义在`InstantiationAwareBeanPostProcessor`接口中。

![](http://120.77.237.175:9080/photos/springanno/250.jpg)

如果`applyBeanPostProcessorsBeforeInstantiation`方法执行完之后返回了一个对象，并且还不为`null`，那么紧接着就会来执行后面的`applyBeanPostProcessorsAfterInitialization`方法。

不妨直接点进`applyBeanPostProcessorsAfterInitialization`方法里面去看一下，如下图所示。

```java
	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

可以看到它里面是来执行每一个`BeanPostProcessor`的`postProcessAfterInitialization`方法的。注意，`postProcessAfterInitialization`方法是定义在`BeanPostProcessor`接口中的，只不过是`InstantiationAwareBeanPostProcessor`接口继承过来了而已。

也就是说，如果有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器，那么会先执行其`postProcessBeforeInstantiation`方法，并看该方法有没有返回值（即创建代理对象），若有则再执行其`postProcessAfterInitialization`方法。现在，该知道`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中两个方法的执行时机了吧

按下F6快捷键让程序继续往下运行，直至运行到下面这行代码处，看来确实是有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器。

![](http://120.77.237.175:9080/photos/springanno/251.jpg)

然后，按下F5快捷键进入`applyBeanPostProcessorsBeforeInstantiation`方法里面，如下图所示，可以看到里面会遍历获取到的所有的`BeanPostProcessor`，接着再来判断遍历出的每一个`BeanPostProcessor`是不是`InstantiationAwareBeanPostProcessor`这种类型的。很明显，遍历出的第一个`BeanPostProcessor`并不是`InstantiationAwareBeanPostProcessor`这种类型的，所以程序并没有进入到最外面的`if`判断语句中。
![](http://120.77.237.175:9080/photos/springanno/252.jpg)

继续让程序往下运行，发现这时遍历出的第二个`BeanPostProcessor`是`ConfigurationClassPostProcessor`，而且它还是`InstantiationAwareBeanPostProcessor`这种类型的，于是，程序自然就进入到了最外面的`if`判断语句中，如下图所示。
![](http://120.77.237.175:9080/photos/springanno/253.jpg)

`ConfigurationClassPostProcessor`这种后置处理器是来解析标准了`@Configuration`注解的配置类的。

紧接着便会来执行`ConfigurationClassPostProcessor`这种后置处理器的`postProcessBeforeInstantiation`方法了，但是该方法的返回值为`null`。于是，继续让程序往下运行，直至遍历完所有的`BeanPostProcessor`，返回到下面这行代码处。
![](http://120.77.237.175:9080/photos/springanno/254.jpg)

此时，`applyBeanPostProcessorsBeforeInstantiation`方法便执行完了，也知道它里面做了些什么，只不过它并没有返回创建的代理对象，因此，程序继续往下运行，并不会进入到下面的`if`判断语句中，而是来到了下面这行代码处。

```java
bd.beforeInstantiationResolved = (bean != null);
```

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处为止。

![](http://120.77.237.175:9080/photos/springanno/255.jpg)

这时，`resolveBeforeInstantiation`方法总算是执行完了。它是在创建单实例`bean`之前，先来给`BeanPostProcessor`一个返回其代理对象的机会。但是，此刻是没有返回单实例`bean`的代理对象的,如上图所示bean为null

如果1InstantiationAwareBeanPostProcessor1这种类型的后置处理器并没有返回`bean`的代理对象，那么接下来该怎么办呢？

那继续按下F6快捷键让程序往下运行了，继续执行下面的流程，当程序运行到下面这行代码处时，发现调用了一个叫`doCreateBean`的方法，顾名思义，该方法就是来创建`bean`的实例的。
![](http://120.77.237.175:9080/photos/springanno/256.jpg)

## 单实例bean的创建流程

按下`F5`快捷键进入``doCreateBean``方法里面去看一下，如下图所示，可以看到会用``BeanWrapper``接口来接收创建的bean。

![](http://120.77.237.175:9080/photos/springanno/257.jpg)

继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处为止，可以看到这儿调用的是一个叫`createBeanInstance`的方法，顾名思义，它是来创建`bean`实例的。

![](http://120.77.237.175:9080/photos/springanno/258.jpg)

也就是说，创建`bean`的流程的第一步就是先来创建`bean`实例。

### 创建bean实例

当执行完`createBeanInstance`方法之后，`bean`的对象就创建出来了。那么`bean`实例的创建流程又是怎样的呢？按下`F5`快捷键进入`createBeanInstance`方法里面去看一下，如下图所示，可以看到一开始就要来解析一下要创建的`bean`的类型。

![](http://120.77.237.175:9080/photos/springanno/260.jpg)

于是，继续按下`F6`快捷键让程序往下运行，由于解析出来的类型为null，所以程序并不会进入到下面的`if`判断语句中，而是来到了下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/259.jpg)

首先，在`if`判断语句中的条件表达式中，可以看到调用了`bean`定义信息对象的一个`getFactoryMethodName`方法，该方法是来获取工厂方法的名字的。不妨`Inspect`一下`mbd.getFactoryMethodName()`表达式的值，发现其值就是`car`，如下图所示。
![](http://120.77.237.175:9080/photos/springanno/261.jpg)

为什么叫工厂方法呢？还记得自定义编写的`Car`对象是如何注册到`IOC`容器中的吗？如下图所示，使用标注了`@Bean`注解的`car`方法来创建`Blue`对象并将其注册到`IOC`容器中的

```java
@ComponentScan("com.anno.ext")
@Configuration
public class ExtConfig {

    @Bean
    public Car car() {
        return new Car();
    }
}
```

也就是说，以上`car`方法就相当于`Car`对象的工厂方法。

还是回到`Spring`的源码中来，现在程序是停留在了`if`判断语句块内，不难猜测此时就是来执行`Car`对象的工厂方法（即`car`方法）来创建`Car`对象的。按下F5快捷键进入`instantiateUsingFactoryMethod`方法里面去看一下，如下图所示

```java
	protected BeanWrapper instantiateUsingFactoryMethod(
			String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {

		return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
	}
```

直接按下`F6`快捷键继续让程序往下运行，运行一步，发现程序来到了ExtConfig配置类的car方法中，如下图所示。

```java
    @Bean
    public Car car() {
        return new Car();
    }
```

继续让程序往下运行，这时可以从控制台中看到打印了如下内容，即调用了Car类的无参构造器创建出了Car对象

```
car construct .....
```

是不是可以这样说呢？这儿就是利用工厂方法或对象的构造器创建出`bean`实例呢？当这个`bean`实例（也即`Car`对象）创建出来以后，继续按下`F6`快捷键让程序往下运行，直至运行到下面这行代码处为止。

```java
instanceWrapper = createBeanInstance(beanName, mbd, args);
```

这时，以上`createBeanInstance`方法就算是执行完了，也就是说，创建出了`bean`实例（即`Car`对象）。

最后，让程序继续往下运行，直至运行到下面这行代码处为止，从这行代码上面的注释中，可以看到这块允许后置处理器来修改咱们这个`bean`的定义信息。

![](http://120.77.237.175:9080/photos/springanno/262.jpg)

很明显，`bean`实例创建完了以后，接下来就得来调用这个`applyMergedBeanDefinitionPostProcessors`方法了。

### 遍历获取到的所有后置处理器，若是`MergedBeanDefinitionPostProcessor`这种类型，则调用其postProcessMergedBeanDefinition方法

首先，按下`F6`快捷键让程序继续往下运行，直至运行到下面这行代码处为止，可以看到这儿调用了一个applyMergedBeanDefinitionPostProcessors方法。

![](http://120.77.237.175:9080/photos/springanno/263.jpg)

直接点击该方法进去它里面看一下，如下图所示，可以看到先是来获取到所有的后置处理器，然后再来遍历它们，如果是`MergedBeanDefinitionPostProcessor`这种类型的，那么就调用其`postProcessMergedBeanDefinition`方法。

```java
	protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof MergedBeanDefinitionPostProcessor) {
				MergedBeanDefinitionPostProcessor bdp = (MergedBeanDefinitionPostProcessor) bp;
				bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);
			}
		}
	}
```

从这儿也能看到，每一个后置处理器（或者说它里面的方法）的执行时机都是不一样的，比如在上一讲中所讲述的`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的两个方法的执行时机是在创建`bean`实例之前，而现在`MergedBeanDefinitionPostProcessor`这种类型的后置处理器，是在创建完`bean`实例以后，来执行它里面的`postProcessMergedBeanDefinition`方法的。

于是，让程序继续往下运行，直至运行到下面这行代码处为止，很明显，`populateBean`方法是来为`bean`的属性赋值的

![](http://120.77.237.175:9080/photos/springanno/264.jpg)

也就是说，创建完`bean`实例以后，首先就是来为`bean`实例的属性赋值。

### 为bean实例的属性赋值

程序现在依然还在`doCreateBean`方法内运行！在该方法内，首先会创建出`bean`实例，然后再执行`MergedBeanDefinitionPostProcessor`这种类型的后置处理器，接着，创建完`bean`实例之后就得为其属性赋值了。按下`F5`快捷键进入`populateBean`方法里面去看一下，如下图所示

![](http://120.77.237.175:9080/photos/springanno/265.jpg)

首先`bw`默认不为`null`如上图所示,直接跳过if判断可以看到，接下来会遍历获取到的所有后置处理器，如果是`InstantiationAwareBeanPostProcessor`这种类型的，那么就调用其`postProcessAfterInstantiation`方法。

#### 再来遍历获取到的所有后继续按下F6快捷键让程序往下运行，直至遍历完所有的后置处理器，在这一过程中，如果遍历出的后置处理器是`InstantiationAwareBeanPostProcessor`这种类型，那么就会调用其`postProcessAfterInstantiation`方法。

续让程序往下运行，你会发现程序并没有进入到`if`判断语句中，而是来到了下面这行代码处。

```java
PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.getPropertyValues() : null);
```

如果有值就拿到赋给所有属性的属性值,否则为null

![](http://120.77.237.175:9080/photos/springanno/266.jpg)

继续让程序往下运行，你会发现程序并没有进入到上面`if`判断语句中，而是来到`hasInstantiationAwareBeanPostProcessors()`。它是来判断是否有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器的。

继续让程序往下运行，会发现程序进入到了下面的`if`判断语句中，来到了下面这行代码处，这说明是有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器的。

接着，继续让程序往下运行，很显然程序会再进入到下面的`if`判断语句中，因为确实是有`InstantiationAwareBeanPostProcessor`这种类型的后置处理器。

![](http://120.77.237.175:9080/photos/springanno/267.jpg)

其实，就是遍历获取到的所有后置处理器，如果是`InstantiationAwareBeanPostProcessor`这种类型的，那么就调用其`postProcessPropertyValues`方法。

即使是执行了`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的`postProcessAfterInstantiation`和`postProcessPropertyValues`这俩方法，`bean`实例的属性依然还没有被赋值。不妨让程序继续往下运行，直至遍历完所有的后置处理器。在这一过程中，如果遍历出的后置处理器是`InstantiationAwareBeanPostProcessor`这种类型，那么就会调用其`postProcessPropertyValues`方法。

唉可以点进去`InstantiationAwareBeanPostProcessor`接口里面看一看它的源码哟，如下图所示，可以看到它里面定义了一个`postProcessPropertyValues`方法，该方法会返回一个`PropertyValues`对象，它就是`bean`实例属性要被赋予的属性值，最终这些属性值会被赋值给`bean`的属性

```java
	@Deprecated
	@Nullable
	default PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		return pvs;
	}
```

> 注意,注解显示以上方法已经在Spring5.1开始废弃,详细看`postProcessProperties(PropertyValues, Object, String)`

`Spring`获取`bean`的实例时，需要把配置的属性值解析到`PropertyValues`中，然后再填充入`BeanWrapper`。

当程序运行到下面这行代码处时，你会发现这儿调用了一个`applyPropertyValues`方法，这里才是正式开始为`bean`的属性赋值。

![](http://120.77.237.175:9080/photos/springanno/268.jpg)

#### 正式开始为bean的属性赋值

现在调用`applyPropertyValues`方法才是开始为`bean`的属性赋值，在为`bean`的属性赋值之前，会执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的`postProcessAfterInstantiation`和`postProcessPropertyValues`这俩方法。

其实，为`bean`的属性赋值，说到底就是利用`setter`方法为`bean`的属性进行赋值。这里，就不再进入`applyPropertyValues`方法去一探究竟了，它里面无非就是利用反射调`setter`方法之类的。

接下来，继续让程序往下运行，直至运行到下面这行代码处为止，此时，`populateBean`方法就执行完了，也就是说，已经为`bean`的属性赋完值了。接着继续让程序往下运行，运行一步即可，这时程序来到了下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/269.jpg)

可以看到，这儿调用了一个`initializeBean`方法，之前就已经研究过它了，它就是来初始化`bean`的

### 初始化bean

1. 在创建`bean`实例之前，会执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的两个方法，即`postProcessBeforeInstantiation`方法和`postProcessAfterInitialization`方法
2. 创建`bean`实例
3. 为`bean`实例的属性赋值。在赋值的过程中，会依次执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的两个方法，即`postProcessAfterInstantiation`方法和`postProcessPropertyValues`方法
4. 初始化`bean`

按下`F5`快捷键进入`initializeBean`方法中去一探究竟，如下图所示

![](http://120.77.237.175:9080/photos/springanno/270.jpg)

这儿调用了一个`invokeAwareMethods`方法，顾名思义，它是来执行`XxxAware`接口中的方法的。

#### 执行`xxxAware`接口的方法

对于`XxxAware`接口，之前曾经编写过这样一个`Dog`组件，如下所示，在该组件里面如果想要用`IOC`容器，那么就得让其实现`ApplicationContextAware`接口，这样，`Spring`就会在`setApplicationContext`方法中把`IOC`容器给传过来。

```java
@Component
public class Dog implements ApplicationContextAware {

    private ApplicationContext applicationContext;

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
        //容器初如化,将applicationContext传进来,那么其它方法就可使用到IOC容器了
        //这个功能是ApplicationContextAware做的
        this.applicationContext = applicationContext;
    }
}

```

那么，在`invokeAwareMethods`方法中是怎么来执行`XxxAware`接口的方法的呢？按下`F5`快捷键进入该方法里面去看一下，如下图所示。

```java
	private void invokeAwareMethods(final String beanName, final Object bean) {
		if (bean instanceof Aware) {
			if (bean instanceof BeanNameAware) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
			if (bean instanceof BeanClassLoaderAware) {
				ClassLoader bcl = getBeanClassLoader();
				if (bcl != null) {
					((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
				}
			}
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
			}
		}
	}
```

可以看到，它就是来判断`bean`是不是实现了`BeanNameAware`、`BeanClassLoaderAware`、`BeanFactoryAware`这些`Aware`接口的，若是则回调接口中对应的方法。

当然了，现在的bean（即`Car`对象）是没有实现以上这些`Aware`接口的，所以，我们直接让程序继续往下运行，直至运行到下面这行代码处为止。

![](http://120.77.237.175:9080/photos/springanno/271.jpg)

#### 执行后置处理器初始化之前的方法（即`postProcessBeforeInitialization`方法）

执行完`XxxAware`接口中的方法之后，可以看到会再来调用一个`applyBeanPostProcessorsBeforeInitialization`方法，该方法之前也研究过，按下F5快捷键进入该方法里面去看一下，如下图所示。

```java
	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessBeforeInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

可以看到，在`applyBeanPostProcessorsBeforeInitialization`方法中，会遍历所有的后置处理器，然后依次执行所有后置处理器的`postProcessBeforeInitialization`方法，一旦后置处理器的`postProcessBeforeInitialization`方法返回了`null`以后，则后面的后置处理器便不再执行了，而是直接退出`for`循环。

然后，让程序继续往下运行，一直运行到下面这行代码处为止。
![](http://120.77.237.175:9080/photos/springanno/272.jpg)

#### 执行初始化方法

执行完后置处理器的`postProcessBeforeInitialization`方法之后，可以看到现在又调用了一个`invokeInitMethods`方法，其作用就是执行初始化方法。

初始化方法究竟是指哪些方法呢？包括了一个实现`InitializingBean`接口的方法,之前曾经编写过如下所示这样一个`Cat`组件

```java
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
```

可以看到，以上`Cat`组件实现了一个`InitializingBean`接口，而该接口中定义了一个`afterPropertiesSet`方法，必然在`Cat`组件内就会实现该方法，这样，该方法就是`Cat`组件的初始化方法了。

除了通过以上方式来指定初始化方法之外，还可以在`@Bean`注解中使用`initMehod`属性来指定初始化方法，就像下面这样

```java
@ComponentScan("com.anno.bean")
@Configuration
public class MyConfigLifeCycle {

    //@Scope("prototype")
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public Car car()
    {
        return new Car();
    }
}

```

以上配置类的代码中可以看出，指定了`Car`对象中的`init`方法为初始化方法，`destroy`方法为销毁方法，而`Car`类的代码如下所示

```java
@Component
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

```

来看一下究竟是如何来执行初始化方法的。于是，我们按下`F5`快捷键进入`invokeInitMethods`方法里面去看一下，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/273.jpg)

可以看到，一开始就会来判断的`bean`是否是`InitializingBean`接口的实现，若是则执行该接口中定义的初始化方法。

如果不是的继续让程序往下运行，可以发现程序并没有进入到下面的`if`判断语句中，而是来到了下面这行代码处，这是因为当前`bean`并没有实现`InitializingBean`接口。
![](http://120.77.237.175:9080/photos/springanno/274.jpg)

这时，是来看`bean`是否自定义了初始化方法，如果是的话，那么就来执行初始化方法。

但是，现在当前的`bean`是没有自定义初始化方法的，因此在程序继续往下运行的过程中，程序并不会进入到下面的`if`判断语句中，而是来到了下面这行代码处。

![](http://120.77.237.175:9080/photos/springanno/275.jpg)

此时，`invokeInitMethods`方法便执行完了

#### 执行后置处理器初始化之后的方法（即`postProcessAfterInitialization`方法）

初始化方法执行完了以后，下一步就是来调用`applyBeanPostProcessorsAfterInitialization`方法

![](http://120.77.237.175:9080/photos/springanno/276.jpg)

按下`F5`快捷键进入该方法里面去看一下，如下图所示。

```java
	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

可以看到，在`applyBeanPostProcessorsAfterInitialization`方法中，会遍历所有的后置处理器，然后依次执行所有后置处理器的``postProcessAfterInitialization``方法，一旦后置处理器的`postProcessAfterInitialization`方法返回了`null`以后，则后面的后置处理器便不再执行了，而是直接退出`for`循环。

然后，让程序继续往下运行，一直运行到下面这行代码处为止，可以看到我们的bean已经初始化完了。
![](http://120.77.237.175:9080/photos/springanno/277.jpg)

以上就是`bean`的整个初始化逻辑。

`bean`初始化完了以后，继续让程序往下运行，直至运行到下面这行代码处为止，很显然，这儿是来获取单实例`bean`的，因为单实例`bean`都已经创建好了。

![](http://120.77.237.175:9080/photos/springanno/278.jpg)

这里确实是来获取单实例`bean`的，只不过是先从缓存中来获取，但缓存中还没有`bean`呢。`Inspect`了一下`bean`变量的值，如下图所示，发现`bean`确实是已经创建好了。

![](http://120.77.237.175:9080/photos/springanno/279.jpg)

好吧，从缓存中获取不到就获取不到吧，让程序继续往下运行，很显然程序是不会进入到下面的if判断语句中的，而是来到了这行代码处。

![](http://120.77.237.175:9080/photos/springanno/280.jpg)

### 注册`bean`的销毁方法

`registerDisposableBeanIfNecessary`方法是来注册`bean`的销毁方法的。注意，这里只是注册而不是调用

销毁方法是在`IOC`容器关闭以后才被调用的。上面的那个`Cat`组件就实现了一个`DisposableBean`接口，因此该组件里面的`destroy`方法就是销毁方法，它会在容器关闭的时候进行调用。

按下F5快捷键进入`registerDisposableBeanIfNecessary`方法里面去看一下，如下图所示，看一下而已，没必要深究，因为这一块呢，只是提前把bean的销毁方法注册了一下。

```java
	protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
		AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
		if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
			if (mbd.isSingleton()) {
				// Register a DisposableBean implementation that performs all destruction
				// work for the given bean: DestructionAwareBeanPostProcessors,
				// DisposableBean interface, custom destroy method.
				registerDisposableBean(beanName,
						new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
			}
			else {
				// A bean with a custom scope...
				Scope scope = this.scopes.get(mbd.getScope());
				if (scope == null) {
					throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
				}
				scope.registerDestructionCallback(beanName,
						new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
			}
		}
	}
```

然后，继续让程序往下运行，直至运行到下面这行代码处为止，此时，`doCreateBean`方法就算是返回了一个`bean`实例。

![](http://120.77.237.175:9080/photos/springanno/281.jpg)

经过以上这么多的步骤，终于将`bean`实例创建出来了，其实，这些步骤都包含在以上`doCreateBean`方法中，总结一下。

1. 在创建`bean`实例之前，会执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的两个方法，即`postProcessBeforeInstantiation`方法和`postProcessAfterInitialization`方法
2. 创建`bean`实例
3. 为`bean`实例的属性赋值。在赋值的过程中，会依次执行`InstantiationAwareBeanPostProcessor`这种类型的后置处理器中的两个方法，即`postProcessAfterInstantiation`方法和`postProcessPropertyValues`方法
4. 初始化`bean`，而且在初始化前后会执行后置处理器中的两个方法，即`postProcessBeforeInitialization`方法和`postProcessAfterInitialization`方法
5. 注册`bean`的销毁方法

经过上面这些繁琐的步骤，`bean`就创建出来了。

于是继续让程序往下运行，先运行到下面这行代码处，这时，`createBean`方法就算是彻底执行完了，而且它会返回创建好的`bean`实例

![](http://120.77.237.175:9080/photos/springanno/282.jpg)

然后，再运行到下面这行代码处，这时，就能获取到创建出的`bean`实例了。

![](http://120.77.237.175:9080/photos/springanno/283.jpg)

不妨`Inspect`一下`singletonObject`变量的值，如下图所示，发现`bean`实例（即`Car`对象）确实是被获取到了。

![](http://120.77.237.175:9080/photos/springanno/284.jpg)

接着，让程序运行到下面这行代码处，可以看到，这儿会调用一个`addSingleton`方法

![](http://120.77.237.175:9080/photos/springanno/285.jpg)

## 将创建出的单实例`bean`添加到缓存中

当单实例`bean`创建出来之后，接下来就得调用一个`addSingleton`方法了，该方法的作用就是将创建的单实例`bean`添加到缓存中。按下F5快捷键进入`addSingleton`方法里面去看一下，如下图所示

```java
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (this.singletonObjects) {
			this.singletonObjects.put(beanName, singletonObject);
			this.singletonFactories.remove(beanName);
			this.earlySingletonObjects.remove(beanName);
			this.registeredSingletons.add(beanName);
		}
	}
```

`singletonObjects`是`DefaultSingletonBeanRegistry`类里面的一个属性，点它，你就能看到了，如下图所示。

```java
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
```

可以看到`singletonObjects`属性就是一个`Map`集合，该Map集合里面缓存的就是所有的单实例`bean`，而且还是按照`bean`的名字和其实例对象缓存起来的，这可以从该属性上面的注释中看出来。

将创建的单实例`bean`添加到缓存中指的不就是将创建的单实例`bean`添加到`singletonObjects`指代的`Map`集合中吗？第一次获取单实例`bean`，就是从`singletonObjects`里面来获取的

将创建的单实例`bean`添加到`singletonObjects`指代的`Map`集合中，好处是以后就可以直接从这个`Map`集合里面来获取了。

现在，该知道什么叫IOC容器了吧！所谓的IOC容器就是指各种各样的Map集合，而且这些Map集合有很多,如下图所示，这些`Map`集合里面保存了创建的所有组件以及`IOC`容器的其他信息。

![](http://120.77.237.175:9080/photos/springanno/286.jpg)

也就是说，以上所有这些`Map`集合就构成了`Spring`的`IOC`容器，容器里面保存了所有的组件。以后，从容器中来获取组件，其实就是从这些`Map`集合里面来获取组件。

接下来，继续让程序往下运行，直至运行到下面这行代码处为止，至此，单实例`bean`就获取到了

![](http://120.77.237.175:9080/photos/springanno/287.jpg)

然后，让程序往下运行到下面这行代码处，可以看到这儿将会返回获取到的单实例`bean`

![](http://120.77.237.175:9080/photos/springanno/288.jpg)

接着，让程序往下运行到下面这行代码处，可以看到`getBean`方法总算是执行完了，这时，也就获取到了单实例`bean`。

![](http://120.77.237.175:9080/photos/springanno/289.jpg)

`bean`创建出来之后，继续让程序往下运行，可以看到接下来就是通过以下`for`循环来将所有的`bean`都创建完。
![](http://120.77.237.175:9080/photos/springanno/290.jpg)

创建流程不用我再详述一遍吧，跟单实例`bean`（即`car`对象）的创建流程是一模一样的，不停地按下F6快捷键让程序不停地往下运行，快速地过一遍就行了。

当程序运行到下面这行代码处时，上面的那个`for`循环就整个地执行完了，也就是说，所有的`bean`都创建完成了。
![](http://120.77.237.175:9080/photos/springanno/291.jpg)

让程序继续往下运行，直至运行到下面这行代码处为止。

![](http://120.77.237.175:9080/photos/springanno/292.jpg)

可以看到，这儿是来遍历所有的`bean`，并来判断遍历出来的每一个`bean`是否实现了`SmartInitializingSingleton`接口的。对`SmartInitializingSingleton`接口还有印象吗？在讲解`@EventListener`注解的内部原理时，就讲解过它

所有的`bean`都利用`getBean`方法创建完成以后，接下来要做的事情就是检查所有的`bean`中是否有实现`SmartInitializingSingleton`接口的，如果有的话，那么便会来执行该接口中的`afterSingletonsInstantiated`方法。

继续往下运行，当程序运行至下面这行代码处时，发现有一个`bean`实现了`SmartInitializingSingleton`接口，不然程序是不会进入到`if`判断语句中的。

![](http://120.77.237.175:9080/photos/springanno/293.jpg)

那么，到底是哪一个`bean`实现了`SmartInitializingSingleton`接口呢？不妨`Inspect`一下`singletonInstance`变量的值，就是`EventListenerMethodProcessor`，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/294.jpg)

于是，接下来便会执行该`bean`中的`afterSingletonsInstantiated`方法，也就是`SmartInitializingSingleton`接口中定义的方法。

继续让程序往下运行，直至执行完整个`for`循环，由于`IOC`容器中的`bean`还是蛮多的，所以要执行完整个`for`循环，得不停地按下`F6`快捷键。当程序运行至下面这行代码处时，发现`beanFactory.preInstantiateSingletons()`这行代码总算是执行完了。

![](http://120.77.237.175:9080/photos/springanno/295.jpg)

它是来初始化所有剩下的单实例bean的。

接着，继续让程序往下运行，直至运行至下面这行代码处为止，此时，程序来到了`Spring IOC`容器创建的最后一步了，即完成`BeanFactory`的初始化创建工作。

![](http://120.77.237.175:9080/photos/springanno/296.jpg)

该方法一旦执行完，那么`Spring IOC`容器就创建完成了。接下来，就看看`finishRefresh`方法

## 完成BeanFactory的初始化创建工作

`finishRefresh`方法执行完，就意味着完成了`BeanFactory`的初始化创建工作，Spring IOC`容器就创建完成了。

其实，`IOC`容器在前一步（即`finishBeanFactoryInitialization`(`beanFactory`)）就已经创建完成了，而且所有的单实例`bean`也都已经加载完了。

那么，`finishRefresh`方法里面究竟都做了些啥事呢？不妨按下F5快捷键进入该方法里面去看一下，如下图所示，

```java
	protected void finishRefresh() {
		// Clear context-level resource caches (such as ASM metadata from scanning).
		clearResourceCaches();

		// Initialize lifecycle processor for this context.
		initLifecycleProcessor();

		// Propagate refresh to lifecycle processor first.
		getLifecycleProcessor().onRefresh();

		// Publish the final event.
		publishEvent(new ContextRefreshedEvent(this));

		// Participate in LiveBeansView MBean, if active.
		LiveBeansView.registerApplicationContext(this);
	}
```

### 初始化和生命周期有关的后置处理器

可以看到`finishRefresh`方法里面首先会调用一个`initLifecycleProcessor`方法，该方法是来初始化和生命周期有关的后置处理器的。不妨按下`F5`快捷键进入该方法里面去看一下，如下图所示。

```java
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
			this.lifecycleProcessor =
					beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
			}
		}
		else {
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			this.lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
			if (logger.isTraceEnabled()) {
				logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " +
						"[" + this.lifecycleProcessor.getClass().getSimpleName() + "]");
			}
		}
	}
```

#### 获取BeanFactory

从上图中可以知道，在`initLifecycleProcessor`方法里面一开始就是来获取·BeanFactory·的，而这个·BeanFactory·，之前早就准备好了。

#### 看容器中是否有·id·为·lifecycleProcessor·，类型是·LifecycleProcessor·的组件

按下F6快捷键让程序继续往下运行，会发现有一个判断，即判断`BeanFactory`中是否有一个`id`为`lifecycleProcessor`的组件。为什么会这么说呢，只要看一下常量`LIFECYCLE_PROCESSOR_BEAN_NAME`的值就知道了，如下图所示，该常量的值就是`lifecycleProcessor`。

![](http://120.77.237.175:9080/photos/springanno/297.jpg)

##### 若有，则赋值给`this.lifecycleProcessor`

如果有的话，那么会从`BeanFactory`中获取到`id`为`lifecycleProcessor`，类型是`LifecycleProcessor`的组件，并将其赋值给`this.lifecycleProcessor`。这可以从下面这行代码看出。

![](http://120.77.237.175:9080/photos/springanno/298.jpg)

不难发现，首先默认会从`BeanFactory`中寻找`LifecycleProcessor`这种类型的组件，即生命周期组件。由于是初次与`LifecycleProcessor`见面，可以点过去看一看它的源码，如下图所示，发现它是一个接口。

```java
public interface LifecycleProcessor extends Lifecycle {

	/**
	 * Notification of context refresh, e.g. for auto-starting components.
	 */
	void onRefresh();

	/**
	 * Notification of context close phase, e.g. for auto-stopping components.
	 */
	void onClose();

}
```

而且，可以看到该接口中还定义了两个方法，一个是`onRefresh`方法，一个是`onClose`方法，它俩能够在BeanFactory的生命周期期间进行回调

如此一来，就可以自己来编写`LifecycleProcessor`接口的一个实现类了，该实现类的作用就是可以在`BeanFactory`的生命周期期间进行拦截，即在`BeanFactory`刷新完成以及关闭的时候，回调其里面的`onRefresh`和`onClose`这俩方法。

当程序继续往下运行时，很显然，它并不会进入到`if`判断语句中，而是来到了下面的`else`分支语句中，这是因为容器在刚开始创建的时候，肯定是还没有生命周期组件的。

##### 若没有，则创建一个`DefaultLifecycleProcessor`类型的组件，并把创建好的组件注册在容器中

如果没有的话，那么`Spring`自己会创建一个`DefaultLifecycleProcessor`类型的对象，即默认的生命周期组件。

然后，把创建好的`DefaultLifecycleProcessor`类型的组件注册到容器中，所执行的是下面这行代码。

![](http://120.77.237.175:9080/photos/springanno/299.jpg)

也就是说，容器中会有一个默认的生命周期组件。这样，以后其他组件想要使用生命周期组件，直接自动注入这个生命周期组件即可。

所有`Spring`创建的组件，基本上都是这个逻辑，它把组件创建过来以后，就会添加到容器中，这样就能方便使用了。

最后，让程序继续往下运行，直至运行到下面这行代码处为止。

![](http://120.77.237.175:9080/photos/springanno/300.jpg)

### 回调生命周期处理器的`onRefresh`方法

从上图中可以看到，当程序运行到`getLifecycleProcessor().onRefresh();`这行代码处时，会先拿到前面定义的生命周期处理器（即监听`BeanFactory`生命周期的处理器），然后再回调其`onRefresh`方法，也就是容器刷新完成的方法。

### 发布容器刷新完成事件

让程序继续往下运行，运行一步即可，这时，程序来到了下面这行代码处

```java
// Publish the final event.
publishEvent(new ContextRefreshedEvent(this));
```

很明显，这儿是来发布容器刷新完成事件的。如何来发布容器刷新完成事件，在上面的 **`ApplicationListener`的原理----容器刷新完成，发布ContextRefreshedEvent事件**已经有介绍过过了,可以往上查找

接着，继续让程序往下运行，运行一步即可，这时，程序来到了下面这行代码处

```java
// Participate in LiveBeansView MBean, if active.
LiveBeansView.registerApplicationContext(this);
```

这是`finishRefresh`方法里面的最后一步了，暴露一些MBean的之类的,直接略过,不关心。

## Spring IOC容器创建源码总结

Spring IOC容器在启动的时候，会先保存所有注册进来的`bean`的定义信息，将来，`BeanFactory`就会按照这些`bean`的定义信息来创建对象。

那么，如何来编写这些`bean`的定义信息呢？你可以有如下两种方式来编写这些`bean`的定义信息。

使用`XML`配置文件的方式来注册`bean`。其实，这种方式说到底无非就是使用`<bean>`标签来向`IOC`容器中注册一个`bea`n的定义信息
使用`@Service`、`@Component`、`@Bean`等等注解来注册`bean`。其实，这种方式就是使用注解向`IOC`容器中注册一个`bean`的定义信息
要掌握的第二个核心思想就是，当`IOC`容器中有保存一些`bean`的定义信息的时候，它便会在合适的时机来创建这些`bea`n，而且主要有两个合适的时机，分别如下：

就是在用到某个`bean`的时候。在统一创建所有剩下的单实例`bean`之前，有一些`bean`，比如像后置处理器啦等等这些组件，需要用到它的时候，都会利用`getBean`方法创建出来，创建好以后便会保存在容器中，以后就可以直接从容器中获取了

统一创建所有剩下的单实例`bean`的时候。这不就是在跟踪`Spring IOC`容器创建过程的源码时所分析的一个步骤嘛，即`finishBeanFactoryInitialization(beanFactory)`，这一步便是来初始化所有剩下的单实例`bean`的。

也就是说，所有`IOC`容器中注册的单实例`bean`，如果还没创建对象，那么就在这个时机创建出来。

当然了，在整个单实例`bean`创建的过程中，最核心的一个思想就是`BeanPostProcessor`（即后置处理器）。

每一个单实例`bean`在创建完成以后，都会使用各种各样的后置处理器进行处理，以此来增强这个`bean`的功能。举一个例子，使用`@Autowired`注解即可完成自动注入，这是因为`Spring`中有一个专门来处理`@Autowired`注解的后置处理器，即`AutowiredAnnotationBeanPostProcessor`。

在讲述Spring AOP底层原理时，有一个叫`AnnotationAwareAspectJAutoProxyCreator`的后置处理器吗？它的作用就是来为`bean`来创建代理对象的，通过代理对象来增强这个`bean`的`AOP`功能。

这里只举了以上两个后置处理器为例子，但是，在`Spring`中其实是有非常多的后置处理器的，它们一般都是在`bean`初始化前后进行逻辑增强的。可以看到`Spring`中的后置处理器是多么的重要

最后，掌握的第四个核心思想就是，`Spring`的事件驱动模型。它涉及到了两个元素，分别如下：

`ApplicationListener`：它是用来做事件监听
ApplicationEventMulticaster`：事件派发器。它就是来进行事件派发的
以上就是Spring源码中的一些比较核心的思想。最重要的是需要理解与掌握后置处理器，因为`Spring`都是利用各种各样的后置处理器来对`bean`进行增强处理的。除此之外，还得理解`Spring`中的事件驱动模型。

# 关于Servlet3.0

> Servlet 3.0标准是需要`Tomcat 7.0.x`及以上版本的服务器来支持的,而且Servlet 3.0是属于JSR 315系列中的规范

1. 创建一个JAVA EE WEB工程

2. 创建`HelloServlet`

   ```java
   @WebServlet("/hello")
   public class HelloServlet extends HttpServlet {
   
       @Override
       protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           // super.doGet(req, resp);
           resp.getWriter().write("hello....");
       }
   }
   ```

   如果是以前的话，需要将以上编写好的Servlet配置在web.xml文中，例如配置一下其拦截路径等等。而现在只需要使用一个简单的注解就行了，即`@WebServlet`。并且，还可以在该注解中配置要拦截哪些路径，例如@WebServlet("/hello")，这样就会拦截一个hello请求了。

   在以上`HelloServlet`标注上一个`@WebServlet("/hello")`注解之后，只要`hello`请求发过来，那么就会来到这个`HelloServlet`，并调用其`doGet`方法来进行处理。

   紧接着，我们就要运行项目进行测试了。项目成功启动后，咱们在浏览器地址栏中输入http://localhost:8080/servlet3/hello进行访问，成功显示

   关于Servlet3.0的规范,可以上https://jcp.org/aboutJava/communityprocess/mrel/jsr315/index.html上载文档

## ServletContainerInitializer

### Shared libraries（共享库）/ runtimes pluggability（运行时插件能力）

`container`（即`Servlet`容器，比如`Tomcat`服务器之类的）在启动应用的时候，它会来扫描`jar`包里面的`ServletContainerInitializer`的实现类。

当`Servlet`容器启动应用时，它会扫描当前应用中每一个`jar`里面的`ServletContainerInitializer`的实现类。那究竟是一个怎么扫描法呢？文档里说，得提供`ServletContainerInitializer`的一个实现类，提供完这个实现类之后还不行，还必须得把它绑定在`META-INF/services/`目录下面的名字叫`javax.servlet.ServletContainerInitializer`的文件里面。

也就是说，必须将提供的实现类绑定在`META-INF/services/javax.servlet.ServletContainerInitializer`文件中，所谓的绑定就是在`javax.servlet.ServletContainerInitializer`文件里面写上`ServletContainerInitializer`实现类的全类名，也就是说，`javax.servlet.ServletContainerInitializer`文件中的内容就是咱们提供的`ServletContainerInitializer`实现类的全类名。

总结一下就是，`Servlet`容器在启动应用的时候，会扫描当前应用每一个`jar`包里面的`META-INF/services/javax.servlet.ServletContainerInitializer`文件中指定的实现类，然后，再运行该实现类中的方法。

首先，编写一个类，例如`MyServletContainerInitializer`，来实现`ServletContainerInitializer`接口。

```java
public class MyServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

    }
}
```

然后，按照`Servlet 3.0`标准规范文档中所说的，将以上类的全类名配置在`META-INF/services`目录下的`javax.servlet.ServletContainerInitializer`文件中。在当前项目的 类路径（即`src`目录） 下把`META-INF/services`这个目录给创建出来，接着在该目录下创建一个名字为`javax.servlet.ServletContainerInitializer`的文件。

将自定义编写的 `MyServletContainerInitializer`类的全类名配置在`javax.servlet.ServletContainerInitializer`文件，如下图所示。

![](http://120.77.237.175:9080/photos/springanno/301.jpg)

这样的话，`Servlet`容器在应用一启动的时候，就会找到以上这个实现类，并来运行它其中的方法。

那么运行该实现类的什么方法呢？发现`MyServletContainerInitializer`实现类中就只有一个叫`onStartup`的方法，因此`Servlet`容器在应用一启动的时候，就会运行该实现类中的`onStartup`方法。

而且，还可以看到该方法里面有两个参数，其中一个参数是`ServletContext`对象，它就是用来代表当前`web`应用的，一个`web`应用就对应着一个`ServletContext`对象。此外，它也是四大域对象之一，我们给它里面存个东西，只要应用在不关闭之前，都可以在任何位置获取到。

说完其中一个参数，着重来说第二个参数，即`Set<Class<?>> arg0`，它又是什么呢？可以参照`Servlet 3.0`标准规范文档中的下面第三段描述，描述说，可以在`ServletContainerInitializer`的实现类上使用一个`@HandlesTypes`注解，而且在该注解里面可以写上一个类型数组哟，也就是说可以指定各种类型。

那么，`@HandlesTypes`注解有什么作用呢？`Servlet`容器在启动应用的时候，会将`@HandlesTypes`注解里面指定的类型下面的子类，包括实现类或者子接口等，全部给传递过来。

编写一个`HelloService`，如下所示。

```java
package com.servlet.service;

/**
 * @title: HelloService
 * @Author Wen
 * @Date: 2021/5/12 16:41
 * @Version 1.0
 */
public interface HelloService {
}

```

现在可以在自定义的的`MyServletContainerInitializer`实现类上写上这样一个`@HandlesTypes(value={HelloService.class})`注解了。

```java
@HandlesTypes(value = {HelloService.class})
public class MyServletContainerInitializer implements ServletContainerInitializer {
	...
}
```

只要在`@HandlesTypes`注解里面指定上感兴趣的类型，那么`Servlet`容器在启动的时候就会自动地将该类型（即`HelloService`接口）下面的子类，包括实现类或者子接口等全部都传递过来，很显然，参数`Set<Class<?>> arg0`指的就是感兴趣的类型的所有后代类型。

接着，就为以上`HelloService`接口来写上几个实现。比如，先来写一个该接口的子接口，就叫`HelloServiceExt`，如下所示。

```java
public interface HelloServiceExt extends HelloService {
}
```

再来创建一个实现该接口的抽象类，可以叫`AbstractHelloService`

```java
public abstract class AbstractHelloService implements HelloService {
}
```

再再来创建一个该接口的实现类，例如`HelloServiceImpl`，如下所示。

```java
public class HelloServiceImpl implements HelloService {
}
```

现在，`HelloService`接口下面有以上这三种不同的后代类型了。如此一来，`Servlet`容器在一启动的时候，就会把感兴趣的所有类型能传递过来，可以来输出一下了。

```java
@HandlesTypes(value = {HelloService.class})
public class MyServletContainerInitializer implements ServletContainerInitializer {

    /**
     * 应用启动的时候，会运行onStartup方法；
     * <p>
     * Set<Class<?>> arg0：感兴趣的类型的所有子类型；
     * ServletContext arg1:代表当前Web应用的ServletContext；一个Web应用一个ServletContext；
     * <p>
     * 1）、使用ServletContext注册Web组件（Servlet、Filter、Listener）
     * 2）、使用编码的方式，在项目启动的时候给ServletContext里面添加组件；
     * 必须在项目启动的时候来添加；
     * 1）、ServletContainerInitializer得到的ServletContext；
     * 2）、ServletContextListener得到的ServletContext；
     */
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

        System.out.println("感兴趣的所有类型:");
        for (Class<?> clz : set) {
            System.out.println(clz);
        }

    }
}
```

可以看到，目前，暂时还用不到`ServletContext`对象参数。

最后，启动项目，如下图所示，确实是打印出了感兴趣的所有类型。

```java
感兴趣的所有类型:
interface com.servlet.service.HelloServiceExt
class com.servlet.service.AbstractHelloService
class com.servlet.service.HelloServiceImpl
```

而且，还可以看到感兴趣的类型本身（即`HelloService`接口）没有打印之外，它下面的所有后代类型，不管是抽象类，还是子接口，还是实现类，都给打印出来了。

这也验证了这一点，即`Servlet`容器在启动应用的时候，会将`@HandlesTypes`注解里面指定的类型下面的子类，包括实现类或者子接口等，全部都给传递过来。那这样有什么作用呢？只要传入了某一感兴趣的类型，就可以利用反射来创建对象了啊！

以上就是基于运行时插件的`ServletContainerInitializer`机制。这个机制最重要的就是要启动`ServletContainerInitializer`的实现类，然后就能传入感兴趣的类型了，该机制有两个核心，一个是`ServletContainerInitializer`，一个是`@HandlesTypes`注解。

## 使用ServletContext注册web三大组件

ServletContext里面有如下这些方法。分别是

- 注册`Filter`
- 注册`Listener`
- 注册`Servlet`

![](http://120.77.237.175:9080/photos/springanno/302.jpg)

有了这些方法，就可以利用它们给`ServletContext`里面注册一些组件了

首先，编写一个`Servlet`，例如`UserServlet`，如下所示。

```java
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().write("tomcat...");
    }
}
```

然后，再来编写一个`Filter`，例如`UserFilter`，要想成为一个`Filter`，它必须得实现`Servlet`提供的`Filter`接口。

```java
public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 过滤请求
        System.out.println("UserFilter...doFilter...");
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
```

接着，再来编写一个`Listener`，例如`UserListener`，要知道监听器是有很多的，所以这儿不妨让`UserListener`来实现`ServletContextListener`接口，以监听`ServletContext`的创建和启动过程。

```java
public class UserListener implements ServletContextListener {
    // 这个方法是来监听ServletContext启动初始化的
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("UserListener...contextInitialized...");
    }

    // 这个方法是来监听ServletContext销毁的，也就是说，我们这个项目的停止
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("UserListener...contextDestroyed...");
    }
}
```

以上三个`web`组件，即`Servlet`、`Filter`以及`Listener`。直接调用`ServletContext`的方法来注册这些组件了，由于项目中没有`web.xml`配置文件，直接写Java代码进行加载。

### 注册`Servlet`

先来注册`Servlet`，即`UserServlet`。当调用`ServletContext`对象的`addServlet`方法来注册`Servlet`时,调用哪一个`addServlet`方法来注册`UserServlet`呢？不妨调用如下第二个`addServlet`方法，在第一个参数的位置传入`UserServlet`的名字，例如`userServlet`，在第二个参数的位置传入自定义`new`的一个`UserServlet`对象。

应该会返回一个`Dynamic`类型的对象，但是为了将返回类型写得更详细点，我们可以将其写成`ServletRegistration.Dynamic`，如下所示。

```java
// 注册Servlet组件
ServletRegistration.Dynamic servlet = servletContext.addServlet("userServlet", new UserServlet());
```

至此，只是给`ServletContext`中注册了一个`UserServlet`组件。但是，该`UserServlet`的映射信息还没配置，即它是来处理什么样的请求的。返回的`ServletRegistration.Dynamic`对象有一个`addMapping`方法，调用它即可配置`UserServlet`的映射信息，如下所示。

```java
//配置Servlet的映射信息
servlet.addMapping("/user");
```

从上可以看到，`UserServlet`现在是来处理`user`请求的。

### 注册`Listener`

注册`Listener`同上。调用第一个`addListener`方法，在参数位置传入`UserListener`的类型，这样就会自动帮创建出`UserListener`对象，并将其注册到`ServletContext`中了。

```java
//注册Listener
servletContext.addListener(UserListener.class);
```

### 注册`Filter`

注册`Filter`，即`UserFilter`。注册`Servlet`和`Filter`有一点特殊之处，那就是注册它俩之后都得配置其映射信息。

调用如下第一个`addFilter`方法，在第一个参数的位置传入`UserFilter`的名字，例如`userFilter`，在第二个参数的位置传入`UserFilter`的类型，即`UserFilter.class`，这样，`Servlet`容器（即`Tomcat`服务器）就会创建出一个`UserFilter`对象，并将其注册到`ServletContext`中

```java
//注册Filter  FilterRegistration
FilterRegistration.Dynamic filter = servletContext.addFilter("userFilter", UserFilter.class);
```

调用`ServletContext`对象的`addServlet`方法（即注册`Servlet`）和`addFilter`方法（即注册`Filter`），都会返回一个`Dynamic`对象，只不过一个是`ServletRegistration`里面的`Dynamic`，一个是`FilterRegistration`里面`的Dynamic`

然后，需要利用返回的`FilterRegistration.Dynamic`对象中的`addMappingForXxx`方法配置`UserFilter`的映射信息。

```java
// 配置Filter的映射信息
filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
```

可以看到，`addMappingForUrlPatterns`方法中传入的第一个参数还是蛮奇怪的，居然是`EnumSet.of(DispatcherType.REQUEST)`，该参数表示的是`Filter`拦截的请求类型，即通过什么方式过来的请求，Filter会进行拦截。不妨点进`DispatcherType`枚举的源码里面去看一看，如下图所示，可以看到好多的请求类型，不过常用的就应该是`FORWARD`和`REQUEST`。

```java
public enum DispatcherType {
    FORWARD,
    INCLUDE,
    REQUEST,
    ASYNC,
    ERROR;

    private DispatcherType() {
    }
}
```

现在`addMappingForUrlPatterns`方法中传入的第一个参数是`EnumSet.of(DispatcherType.REQUEST)`，表明写的`UserFilter`会拦截通过`request`方式发送过来的请求。

该方法中的第二个参数（即`isMatchAfter`）直接传入`true`就行，第三个参数（即`urlPatterns`）就是`Filter`要拦截的路径，目前传入的是/*，即拦截所有请求。

### 测试

现在启动项目进行测试，看上三个组件有没有起到作用。如果注册的`UserFilter`真的起到作用了，那么它就会在放行目标请求之前打印相应内容；如果注册的`UserListener`真的起到作用了，那么在其创建和销毁过程中也会有相应内容打印；如果注册的`UserServlet`真的起到作用了，那么当发送一个`user`请求后，就能在浏览器页面中看到有相应内容输出了。
![](http://120.77.237.175:9080/photos/springanno/303.jpg)

可以看到注册的`UserListener`确实起到作用了，在项目启动的时候，有相关内容输出，因为它本来就是监听项目的启动和停止的。

注册的`UserFilter也起到作用了，在目标请求放行之前打印了相应内容。

停止`Tomcat`服务器，此时，注册的`UserListener`会监听到项目的停止，因此监听`ServletContext`销毁的方法也会运行，控制台也会有相应内容输出，如下图所示

### 总结

通过编码的方式在项目启动的时候，给`ServletContext`（即当前项目）里面来注册组件。当然，并不是说，拿到了`ServletContext`对象就能注册组件了，因为必须是在项目启动的时候，才能注册组件。

而且，在项目启动的时候，可以有两处来使用`ServletContext`对象注册组件。

第一处就是利用基于运行时插件的`ServletContainerInitializer`机制得到`ServletContext`对象，然后再往其里面注册组件。

第二处就是编写过一个监听器（即`UserListener`）,它是来监听项目的启动和停止的，在监听项目启动的方法中，传入了一个`ServletContextEvent`对象，即事件对象，就可以通过该事件对象的`getServletContext`方法拿到`ServletContext`对象，拿到之后，就可以往它里面注册组件了


## Servlet 3.0与Spring MVC的整合分析

创建一个新的maven工程，例如springmvc-annotation，注意其打包方式是war。pom文件如下:

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.anno</groupId>
    <artifactId>springmvc-annotation</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.2.6.RELEASE</version>
        </dependency>

            <!--再来导入对servlet api的依赖，注意其版本是3.1.0，因为现在是在用Servlet 3.0以上的特性。-->
        <!--
        此外，还要注意<scope>provided</scope>配置哟！由于Tomcat服务器里面也有servlet api，即目标环境已经该jar包了，
        所以在这儿将以上servlet api的scope设置成provided。这样的话，项目在被打成war包时，就不会带上该jar包了，否则就会引起jar包冲突。
        -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>3.0-alpha-1</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- java编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.1.0</version>
                <!--
                注意<configuration>标签里面的<failOnMissingWebXml>false</failOnMissingWebXml>这个配置哟，它是来告诉maven工程即使没有web.xml文件,也不要报错
                -->
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

