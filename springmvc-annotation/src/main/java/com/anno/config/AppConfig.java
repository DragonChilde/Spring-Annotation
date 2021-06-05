package com.anno.config;

import com.anno.interceptor.MyFirstInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @title: AppConfig
 * @Author Wen
 * @Date: 2021/6/4 11:23
 * @Version 1.0
 */
@ComponentScan(value = "com.anno", includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, useDefaultFilters = false)
@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {

    /**
     * 定制视图解析器
     *
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {

        // 如果直接调用jsp方法，那么默认所有的页面都从/WEB-INF/目录下开始找，即找所有的jsp页面
        // registry.jsp();
        /*
         * 当然了，编写规则，比如指定一个前缀，即/WEB-INF/views/，再指定一个后缀，即.jsp，
         * 很显然，此时，所有的jsp页面都会存放在/WEB-INF/views/目录下，自然地，程序就会去/WEB-INF/views/目录下面查找jsp页面了
         */
        registry.jsp("/WEB-INF/views/", ".jsp");
    }

    /**
     * 静态资源访问
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();

    }

    /**
     * 配置静态资源访问路径
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/").addResourceLocations("classpath:/img/");
    }

    /**
     * 定制拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        /*
         * addInterceptor方法里面要传一个拦截器对象，该拦截器对象可以从容器中获取过来，也可以new一个，
         * 很显然，这儿是new了一个自定义的拦截器对象。
         *
         * 虽然创建出了一个拦截器，但是最关键的一点还是指示拦截器要拦截哪些请求，因此还得继续使用addPathPatterns方法来配置一下，
         * 若在addPathPatterns方法中传入了"/**"，则表示拦截器会拦截任意请求，而不管该请求是不是有任意多层路径
         */
        registry.addInterceptor(new MyFirstInterceptor()).addPathPatterns("/**");
    }
}