package com.anno.ext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @title: MyBeanFactoryPostProcessor
 * @Author Wen
 * @Date: 2021/1/28 17:39
 * @Version 1.0
 */
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("MyBeanFactoryPostProcessor...postProcessBeanFactory()...");
        int count = beanFactory.getBeanDefinitionCount();   //获取容器bean的数量
        String[] names = beanFactory.getBeanDefinitionNames();  //获取所有容器bean的名
        System.out.println("当前BeanFactory中有" + count + " 个Bean");
        System.out.println(Arrays.asList(names));
    }
}