package com.anno;


import com.anno.bean.Person;
import com.anno.config.MyConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainTest {

    public static void main(String[] args) {
     /*
        *//*使用XML配置获取Person Bean*//*
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("bean.xml");
        Person bean = context.getBean(Person.class);
        System.out.println(bean);*//*Person{name='张三', age=10}*//*
        */

        /*使用注解配置获取Person Bean*/
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfig.class);
        Person bean = annotationConfigApplicationContext.getBean(Person.class);
        System.out.println(bean);/*Person{name='李四', age=20}*/

        /*获取注解配置的Bean Id*/
        String[] beanNamesForType = annotationConfigApplicationContext.getBeanNamesForType(Person.class);
        for (String s:
        beanNamesForType) {
            System.out.println(s);  //person01
        }

    }
}
