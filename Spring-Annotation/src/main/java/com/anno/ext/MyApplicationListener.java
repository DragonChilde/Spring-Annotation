package com.anno.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @title: MyApplicationListener
 * @Author Wen
 * @Date: 2021/2/23 14:14
 * @Version 1.0
 */
@Component
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {

    //当容器中发布此事件以后，方法触发
    public void onApplicationEvent(ApplicationEvent event) {

        System.out.println("收到的事件:" + event);
    }
}