package com.anno;


import com.anno.aop.MathCalculator;
import com.anno.bean.Boss;
import com.anno.bean.Car;
import com.anno.bean.Color;
import com.anno.config.MainConfigOfAOP;
import com.anno.config.MainConifgOfAutowired;
import com.anno.service.BookService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Aop {
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
