package com.anno.service;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @title: DeferredResultQueue
 * @Author Wen
 * @Date: 2021/6/7 10:10
 * @Version 1.0
 */
public class DeferredResultQueue {

    /**
     * DeferredResult对象临时保存的地方
     */
    private static Queue<DeferredResult<Object>> queue = new ConcurrentLinkedDeque<DeferredResult<Object>>();

    /**
     * 临时保存DeferredResult对象的方法
     *
     * @param deferredResult
     */
    public static void save(DeferredResult<Object> deferredResult) {
        queue.add(deferredResult);
    }

    /**
     * 获取DeferredResult对象的方法
     *
     * @return
     */
    public static DeferredResult<Object> get() {
        /*
         * poll()：检索并且移除，移除的是队列头部的元素
         */
        return queue.poll();
    }
}