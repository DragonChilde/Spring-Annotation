package com.servlet.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @title: HelloAsyncServlet
 * @Author Wen
 * @Date: 2021/6/5 10:36
 * @Version 1.0
 */
// @WebServlet注解表明该Servlet应该处理哪个请求
@WebServlet(value = "/async", asyncSupported = true)
public class HelloAsyncServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 先来让该Servlet支持异步处理，即asyncSupported=true
        // 2. 开启异步模式
        System.out.println("主线程开始。。。"+Thread.currentThread()+"==>"+System.currentTimeMillis());
        AsyncContext asyncContext = req.startAsync();
        // 3. 调用业务逻辑，进行异步处理，这儿是开始异步处理
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("副线程开始。。。"+Thread.currentThread()+"==>"+System.currentTimeMillis());
                    sayHello();

                    asyncContext.complete();

                    /*
                     * 通过下面这种方式来获取响应对象是不可行的哟！否则，会报如下异常：
                     * java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                     */
                    // 获取到异步上下文
                    //AsyncContext asyncContext = req.getAsyncContext();
                    // ServletResponse response = asyncContext.getResponse();


                    // 4. 获取响应
                    ServletResponse response = asyncContext.getResponse();
                    // 然后，我们还是利用这个响应往客户端来写数据
                    resp.getWriter().write("hello async...");

                    System.out.println("副线程结束。。。"+Thread.currentThread()+"==>"+System.currentTimeMillis());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        System.out.println("主线程结束。。。"+Thread.currentThread()+"==>"+System.currentTimeMillis());
    }

    public void sayHello() throws Exception {
        // 打印一下究竟是哪些线程在工作
        System.out.println(Thread.currentThread() + " processing...");
        Thread.sleep(3000); // 睡上3秒
    }
}