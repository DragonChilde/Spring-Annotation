package com.anno.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.Callable;

/**
 * @title: AsyncController
 * @Author Wen
 * @Date: 2021/6/5 14:56
 * @Version 1.0
 */
@Controller
public class AsyncController {

    @ResponseBody
    @RequestMapping("/async01")
    public Callable<String> async01() {
        System.out.println("主线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
        Callable callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                System.out.println("副线程开始..."+Thread.currentThread()+"==>"+System.currentTimeMillis());
                Thread.sleep(3000); //睡上3秒
                System.out.println("副线程开始..."+Thread.currentThread()+"==>"+System.currentTimeMillis());
                // 响应给客户端一串字符串，即"Callable<String> async01()"
                return "Callable<String> async01()";
            }
        };

        System.out.println("主线程结束..."+Thread.currentThread()+"==>"+System.currentTimeMillis());
        return callable;
    }

}