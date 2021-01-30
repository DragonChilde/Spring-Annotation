package com.anno.ext;

import com.anno.bean.Car;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @title: ExtConfig
 * @Author Wen
 * @Date: 2021/1/28 17:42
 * @Version 1.0
 */
@ComponentScan("com.anno.ext")
@Configuration
public class ExtConfig {

    @Bean
    public Car car() {
        return new Car();
    }
}