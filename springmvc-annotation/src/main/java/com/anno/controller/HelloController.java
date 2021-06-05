package com.anno.controller;

import com.anno.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @title: HelloController
 * @Author Wen
 * @Date: 2021/6/4 11:09
 * @Version 1.0
 */
@Controller
public class HelloController {
    @Autowired
    HelloService helloService;


    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        String hello = helloService.sayHello("tomcat..");
        return hello;
    }

    @RequestMapping("/success")
    public String success()
    {
        // 这儿直接返回"success"，那么它就会跟我们视图解析器中指定的那个前后缀进行拼串，来到指定的页面
        return "success";
    }
}