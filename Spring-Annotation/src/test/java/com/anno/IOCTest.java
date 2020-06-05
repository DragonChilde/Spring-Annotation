package com.anno;


import com.anno.bean.ColorFactoryBean;
import com.anno.bean.Yellow;
import com.anno.config.MyConfig;
import com.anno.config.MyConfig2;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class IOCTest {

    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig2.class);

    @Test
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

    @Test
    public void test02()
    {
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig2.class);

        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
        for (String s:
                beanDefinitionNames) {
            System.out.println(s);
        }
        Object person = annotationConfigApplicationContext.getBean("person");
        Object person2 = annotationConfigApplicationContext.getBean("person");

        System.out.println(person.equals(person2));

    }



    @Test
    public void test03()
    {
        String[] beanDefinitionNames = annotationConfigApplicationContext.getBeanDefinitionNames();
        for (String s:
                beanDefinitionNames) {
            System.out.println(s);
        }

        ////动态获取环境变量的值
        ConfigurableEnvironment environment = annotationConfigApplicationContext.getEnvironment();
        String property = environment.getProperty("os.name");
        System.out.println(property);


    }

    @Test
    public void test04()
    {
        printBean(annotationConfigApplicationContext);

        Object yellow = annotationConfigApplicationContext.getBean(Yellow.class);
        System.out.println(yellow);
    }


    private void printBean(AnnotationConfigApplicationContext context)
    {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String s :
        beanDefinitionNames) {
            System.out.println(s);
        }
    }


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


    }

}
