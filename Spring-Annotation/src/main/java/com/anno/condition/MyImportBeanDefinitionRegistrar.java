package com.anno.condition;

import com.anno.bean.Rainbow;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;


public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * AnnotationMetadata：当前类的注解信息
     * BeanDefinitionRegistry:BeanDefinition注册类；
     * 		把所有需要添加到容器中的bean；调用
     * 		BeanDefinitionRegistry.registerBeanDefinition手工注册进来
     */
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean red = registry.containsBeanDefinition("com.anno.bean.Red");
        boolean blue = registry.containsBeanDefinition("com.anno.bean.Blue");
        if (red && blue)
        {
            //指定Bean定义信息；（Bean的类型，Bean。。。）
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Rainbow.class);
            //注册一个Bean，指定bean名
            registry.registerBeanDefinition("rainBow",rootBeanDefinition);
        }
    }
}
