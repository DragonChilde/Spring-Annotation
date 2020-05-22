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