package com.anno;

import com.anno.service.UserService;
import com.anno.tx.TxConfig;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @title: IOCTest_tx
 * @Author Wen
 * @Date: 2021/1/28 11:55
 * @Version 1.0
 */
public class IOCTest_Tx {

    @Test
    public void test01() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(TxConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
        userService.insertUser();
        applicationContext.close();
    }
}