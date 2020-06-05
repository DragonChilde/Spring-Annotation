package com.anno.config;

import com.anno.bean.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@ComponentScan("com.anno.bean")
@Configuration
public class MyConfigLifeCycle {

    //@Scope("prototype")
    @Bean(initMethod = "init",destroyMethod = "destroy")
    public Car car()
    {
        return new Car();
    }
}
