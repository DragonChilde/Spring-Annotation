package com.anno;


import com.anno.bean.Boss;
import com.anno.bean.Car;
import com.anno.bean.Color;
import com.anno.config.MainConfigOfPropertyValues;
import com.anno.config.MainConifgOfAutowired;
import com.anno.dao.BookDao;
import com.anno.service.BookService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class IOCTest_Autowired {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(MainConifgOfAutowired.class);

    @Test
    public void test01()
    {
        BookService bookService = annotationConfigApplicationContext.getBean(BookService.class);
        System.out.println(bookService);


       /* BookDao bookDao = (BookDao) annotationConfigApplicationContext.getBean("bookDao2");
        System.out.println(bookDao);*/

        Boss boss = annotationConfigApplicationContext.getBean(Boss.class);
        System.out.println(boss);
        Car car = annotationConfigApplicationContext.getBean(Car.class);
        System.out.println(car);

        Color color = annotationConfigApplicationContext.getBean(Color.class);
        System.out.println(color);
        annotationConfigApplicationContext.close();
    }

}
