package com.anno.ext;

import com.anno.bean.Blue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

/** @title: MyBeanDefinitionRegistryPostProcessor @Author Wen @Date: 2021/1/31 21:28 @Version 1.0 */
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

  //BeanDefinitionRegistry bean定义信息的保存中心,以后BeanFactory就是按照BeanDefinitionRegistry里面保存的每一个bean定义信息创建bean实例
  public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
       {
    System.out.println(
        "MyBeanDefinitionRegistryPostProcessor...postProcessBeanDefinitionRegistry...bean的数量:"
            + registry.getBeanDefinitionCount());

    // RootBeanDefinition beanDefinition = new RootBeanDefinition(Blue.class);
    // 与上面的 初始化方法一样,两个都作用都是一样的
    AbstractBeanDefinition beanDefinition =
        BeanDefinitionBuilder.rootBeanDefinition(Blue.class).getBeanDefinition();
    registry.registerBeanDefinition("hello", beanDefinition);
  }

  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
      throws BeansException {
    System.out.println(
        "MyBeanDefinitionRegistryPostProcessor...postProcessBeanFactory...bean的数量:"
            + beanFactory.getBeanDefinitionCount());
  }
}
