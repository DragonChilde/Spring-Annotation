package com.servlet.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @title: ServletContextListener的作用：监听项目的启动和停止
 * @Author Wen
 * @Date: 2021/5/12 17:28
 * @Version 1.0
 */
public class UserListener implements ServletContextListener {
    // 这个方法是来监听ServletContext启动初始化的
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("UserListener...contextInitialized...");
    }

    // 这个方法是来监听ServletContext销毁的，也就是说，我们这个项目的停止
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        System.out.println("UserListener...contextDestroyed...");
    }
}