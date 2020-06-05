package com.anno;


import com.anno.bean.Car;
import com.anno.config.MyConfigLifeCycle;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTestLifeCycle {

    @Test
    public void test01()
    {
        //1、创建ioc容器
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MyConfigLifeCycle.class);
        Object car = annotationConfigApplicationContext.getBean("car");
        //System.out.println(car);

        //关闭容器
        annotationConfigApplicationContext.close();
    }
}
