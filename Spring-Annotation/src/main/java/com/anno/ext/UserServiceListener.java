package com.anno.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @title: UserServiceListener
 * @Author Wen
 * @Date: 2021/2/24 16:05
 * @Version 1.0
 */
@Service
public class UserServiceListener {

    @EventListener(classes={ApplicationEvent.class})
    public void listen(ApplicationEvent event) {
        System.out.println("UserService...监听到的事件：" + event);
    }
}