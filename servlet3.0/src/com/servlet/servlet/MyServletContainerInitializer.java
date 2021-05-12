package com.servlet.servlet;

import com.servlet.service.HelloService;

import javax.servlet.*;
import javax.servlet.annotation.HandlesTypes;
import java.util.EnumSet;
import java.util.Set;

/**
 * @title: MyServletContainerInitializer
 * @Author Wen
 * @Date: 2021/5/12 11:31
 * @Version 1.0
 */
@HandlesTypes(value = {HelloService.class})
public class MyServletContainerInitializer implements ServletContainerInitializer {

    /**
     * 应用启动的时候，会运行onStartup方法；
     * <p>
     * Set<Class<?>> arg0：感兴趣的类型的所有子类型；
     * ServletContext arg1:代表当前Web应用的ServletContext；一个Web应用一个ServletContext；
     * <p>
     * 1）、使用ServletContext注册Web组件（Servlet、Filter、Listener）
     * 2）、使用编码的方式，在项目启动的时候给ServletContext里面添加组件；
     * 必须在项目启动的时候来添加；
     * 1）、ServletContainerInitializer得到的ServletContext；
     * 2）、ServletContextListener得到的ServletContext；
     */
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

        System.out.println("感兴趣的所有类型:");
        for (Class<?> clz : set) {
            System.out.println(clz);
        }

        // 注册Servlet组件
        ServletRegistration.Dynamic servlet = servletContext.addServlet("userServlet", new UserServlet());

        //配置Servlet的映射信息
        servlet.addMapping("/user");

        //注册Listener
        servletContext.addListener(UserListener.class);

        //注册Filter  FilterRegistration
        FilterRegistration.Dynamic filter = servletContext.addFilter("userFilter", UserFilter.class);

        // 配置Filter的映射信息
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
    }
}