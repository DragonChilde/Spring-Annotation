package com.anno.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @title: RootConfig
 * @Author Wen
 * @Date: 2021/6/1 17:51
 * @Version 1.0
 */
//该配置类相当于Spring的配置文件
//Spring容器不扫描Controller，它是一个父容器
@ComponentScan(value = "com.anno", excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})})
public class RootConfig {
}