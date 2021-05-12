package com.servlet.servlet;

import javax.servlet.*;
import java.io.IOException;

/**
 * @title: UserFilter
 * @Author Wen
 * @Date: 2021/5/12 17:26
 * @Version 1.0
 */
public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        // 过滤请求
        System.out.println("UserFilter...doFilter...");
        //放行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}