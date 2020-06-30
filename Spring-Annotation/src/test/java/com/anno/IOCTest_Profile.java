package com.anno;


import com.anno.bean.Boss;
import com.anno.bean.Car;
import com.anno.bean.Color;
import com.anno.config.MainConfigOfProfile;
import com.anno.config.MainConifgOfAutowired;
import com.anno.service.BookService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

public class IOCTest_Profile {
    AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();

    //1、使用命令行动态参数: 在虚拟机参数位置加载 -Dspring.profiles.active=test
    //2、代码的方式激活某种环境；

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
            System.out.println(s);
        }


        annotationConfigApplicationContext.close();
    }

}
