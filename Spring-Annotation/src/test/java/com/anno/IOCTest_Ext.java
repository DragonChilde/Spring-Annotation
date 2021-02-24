package com.anno;

import com.anno.ext.ExtConfig;
import org.junit.Test;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @title: IOCTest_Ext
 * @Author Wen
 * @Date: 2021/1/28 17:46
 * @Version 1.0
 */
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