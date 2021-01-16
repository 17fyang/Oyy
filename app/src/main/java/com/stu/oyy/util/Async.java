package com.stu.oyy.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: 乌鸦坐飞机亠
 * @date: 2020/12/7 17:34
 * @Description:
 */
public class Async {
    //线程池大小
    public static final int THREAD_NUM = 8;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_NUM);

    /**
     * 异步执行一个简单的任务
     *
     * @param runnable
     */
    public static void run(Runnable runnable) {
        threadPool.execute(runnable);
    }

}
