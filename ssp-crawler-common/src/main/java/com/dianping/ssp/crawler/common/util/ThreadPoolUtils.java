package com.dianping.ssp.crawler.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池控制，即实例化一个线程池，在多个爬虫系统都在爬取的时候同用一个线程池，保证数据库连接等操作的不阻塞
 */
public class ThreadPoolUtils {

    private static final Integer MAX_THREAD_COUNT = 50;

    private static final ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    public static ExecutorService getExecutorService() {
        return executorService;
    }
}
