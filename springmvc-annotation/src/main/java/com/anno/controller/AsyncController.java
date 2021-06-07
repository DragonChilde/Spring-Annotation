package com.anno.controller;

import com.anno.service.DeferredResultQueue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @title: AsyncController
 * @Author Wen
 * @Date: 2021/6/5 14:56
 * @Version 1.0
 */
@Controller
public class AsyncController {

    @ResponseBody
    @RequestMapping("/createOrder")
    public DeferredResult<Object> createOrder() {
        /*
         * 在创建DeferredResult对象时，可以像下面这样传入一些参数哟！
         *
         * 第一个参数（timeout）： 超时时间。限定（请求？）必须在该时间内执行完，如果超出时间限制，那么就会返回一段错误的提示信息（timeoutResult）
         * 第二个参数（timeoutResult）：超出时间限制之后，返回的错误提示信息
         */
        DeferredResult<Object> deferredResult = new DeferredResult<>((long) 3000, "create fail...");

        DeferredResultQueue.save(deferredResult);
        return deferredResult;
    }

    @ResponseBody
    @RequestMapping("/create")
    public String create() {
        String order = UUID.randomUUID().toString();
        /*
         * 如果我们想在上一个请求（即createOrder）中使用订单，那么该怎么办呢？从临时保存DeferredResult对象的地方获取
         * 到刚才保存的DeferredResult对象，然后调用其setResult方法设置结果，例如设置订单的订单号
         */

        DeferredResult<Object> deferredResult = DeferredResultQueue.get();
        deferredResult.setResult(order);
        return "success====>" + order;
    }

    @ResponseBody
    @RequestMapping("/async01")
    public Callable<String> async01() {
        System.out.println("主线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
        Callable callable = new Callable<String>() {

            @Override
            public String call() throws Exception {
                System.out.println("副线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
                Thread.sleep(3000); //睡上3秒
                System.out.println("副线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
                // 响应给客户端一串字符串，即"Callable<String> async01()"
                return "Callable<String> async01()";
            }
        };

        System.out.println("主线程结束..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
        return callable;
    }

}