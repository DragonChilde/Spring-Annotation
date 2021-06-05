package com.servlet.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @title: HelloServlet
 * @Author Wen
 * @Date: 2021/5/12 10:55
 * @Version 1.0
 */
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 打印一下都是哪些线程在工作，Thread.currentThread()就是来打印当前线程的
        System.out.println(Thread.currentThread() + " start");
        try {
            sayHello();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // super.doGet(req, resp);
        resp.getWriter().write("hello....");


        System.out.println(Thread.currentThread() + " end");
    }


    public void sayHello() throws Exception {
        // 打印一下究竟是哪些线程在工作
        System.out.println(Thread.currentThread() + " processing...");
        Thread.sleep(3000); // 睡上3秒
    }

}