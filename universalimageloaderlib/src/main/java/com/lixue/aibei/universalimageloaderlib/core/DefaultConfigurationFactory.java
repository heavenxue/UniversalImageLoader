package com.lixue.aibei.universalimageloaderlib.core;

import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.FileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.HashCodeFileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.core.assist.QueueProcessingType;
import com.lixue.aibei.universalimageloaderlib.core.assist.deque.LIFOLinkBlockingDeque;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供了一些默认选项针对图片加载器
 * Created by Administrator on 2016/3/22.
 */
public class DefaultConfigurationFactory {
    /**文件名生成器**/
    public static FileNameGenerator FileNameGenerator(){
        return new HashCodeFileNameGenerator();
    }

    /**创建线程池**/
    public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType) {
        boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;
        BlockingQueue<Runnable> taskQueue = lifo ? new LIFOLinkBlockingDeque<Runnable>() : new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(threadPriority, "uil-pool-"));
    }

    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory{
        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        public DefaultThreadFactory(int threadPriority,String threadnamePrefix){
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            this.namePrefix = threadnamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread t = new Thread(group,runnable,namePrefix + threadNumber.getAndIncrement(),0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }
}
