package com.anno.service;

import org.springframework.stereotype.Service;

/**
 * @title: HelloService
 * @Author Wen
 * @Date: 2021/6/4 11:10
 * @Version 1.0
 */
@Service
public class HelloService {

    public String sayHello(String name){

        return "Hello "+name;
    }
}