package com.anno;

import com.anno.ext.ExtConfig;
import org.junit.Test;
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
        applicationContext.close();
    }
}