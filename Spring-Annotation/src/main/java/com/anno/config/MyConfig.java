package com.anno.config;

import com.anno.bean.Person;
import com.anno.dao.BookDao;
import com.anno.service.BookService;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Controller;

//配置类==配置文件
@Configuration//告诉Spring这是一个配置类
//@ComponentScan(value = "com.anno")  //@ComponentScan  value:指定要扫描的包
//@ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})},useDefaultFilters = false)
//@ComponentScan(value = "com.anno",excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = {BookService.class})},useDefaultFilters = false)
/*@ComponentScans(value = {
        @ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})},useDefaultFilters = false)
})*/
@ComponentScans(value = {
        @ComponentScan(value = "com.anno",includeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM,classes = {MyTypeFilter.class})},useDefaultFilters = false)
})
//@ComponentScan  value:指定要扫描的包
//excludeFilters = Filter[] ：指定扫描的时候按照什么规则排除那些组件
//includeFilters = Filter[] ：指定扫描的时候只需要包含哪些组件
//FilterType.ANNOTATION：按照注解
//FilterType.ASSIGNABLE_TYPE：按照给定的类型；
//FilterType.ASPECTJ：使用ASPECTJ表达式
//FilterType.REGEX：使用正则指定
//FilterType.CUSTOM：使用自定义规则
public class MyConfig {

    //给容器中注册一个Bean;类型为返回值的类型，id默认是用方法名作为id
    @Bean("person")
    public Person person01()
    {
        return new Person("李四",20);
    }
}
