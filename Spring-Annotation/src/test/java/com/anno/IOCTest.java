package com.anno;


import com.anno.config.MyConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest {

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


}
