package com.anno.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * @title: RootConfig
 * @Author Wen
 * @Date: 2021/6/4 11:24
 * @Version 1.0
 */
@ComponentScan(value="com.anno",excludeFilters={
        @ComponentScan.Filter(type= FilterType.ANNOTATION,classes={Controller.class})
})
public class RootConfig {
}