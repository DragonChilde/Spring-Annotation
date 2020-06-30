package com.anno;

import com.anno.config.MainConfigOfPropertyValues;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;


public class IOCTest_PropertyValue {

    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);

    @Test
    public void test01()
    {
        Object person = annotationConfigApplicationContext.getBean("person");
        System.out.println(person);


        ConfigurableEnvironment environment = annotationConfigApplicationContext.getEnvironment();
        String property = environment.getProperty("person.nickName");
        System.out.println(property);
        //关闭容器
        annotationConfigApplicationContext.close();
    }
}
