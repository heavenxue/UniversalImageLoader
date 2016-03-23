package com.lixue.aibei.universalimageloaderlib.core;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import com.lixue.aibei.universalimageloaderlib.cache.disc.DiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.disc.Impl.UnlimitedDiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.disc.Impl.ext.LruDiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.FileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.HashCodeFileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.cache.memory.Impl.LruMemoryCache;
import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;
import com.lixue.aibei.universalimageloaderlib.core.assist.QueueProcessingType;
import com.lixue.aibei.universalimageloaderlib.core.assist.deque.LIFOLinkBlockingDeque;
import com.lixue.aibei.universalimageloaderlib.utils.L;
import com.lixue.aibei.universalimageloaderlib.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 针对图片加载器提供了一些默认选项与配置
 * Created by Administrator on 2016/3/22.
 */
public class DefaultConfigurationFactory {
    /**文件名生成器**/
    public static FileNameGenerator FileNameGenerator(){
        return new HashCodeFileNameGenerator();
    }

    /**创建线程池**/
    public static Executor createExecutor(int threadPoolSize, int threadPriority, QueueProcessingType tasksProcessingType) {
        boolean lifo = tasksProcessingType == QueueProcessingType.LIFO;//先进先出
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

    /** 创建任务分配器 */
    public static Executor createTaskDistributor() {
        return Executors.newCachedThreadPool(createThreadFactory(Thread.NORM_PRIORITY, "uil-pool-d-"));
    }

    /** 创建磁盘缓存器 **/
    public static DiskCache createDiskCache(Context context, FileNameGenerator diskCacheFileNameGenerator, long diskCacheSize, int diskCacheFileCount) {
        File reserveCacheDir = createReserveDiskCache(context);
        if (diskCacheSize > 0 || diskCacheFileCount > 0){
            File individualCacheDir = StorageUtils.getIndividualCacheDirectory(context);
            try {
                return new LruDiskCache(individualCacheDir,reserveCacheDir,diskCacheFileNameGenerator,diskCacheSize,diskCacheFileCount);
            }catch (IOException e){
                L.e(e);
            }
        }
        File cacheDir = StorageUtils.getCacheDirectory(context);
        return new UnlimitedDiskCache(cacheDir, reserveCacheDir, diskCacheFileNameGenerator);
    }

    /**创建备用磁盘缓存目录，以防主缓存目录不可用**/
    public static File createReserveDiskCache(Context context){
        File cacheDir = StorageUtils.getCacheDirectory(context, false);//放在内存上
        File individualDir = new File(cacheDir, "uil-images");
        if (individualDir.exists() || individualDir.mkdir()) {
            cacheDir = individualDir;
        }
        return cacheDir;
    }

    public static MemoryCache createMemoryCache(Context context, int memoryCacheSize) {
        if (memoryCacheSize == 0) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int memoryClass = am.getMemoryClass();
            if (hasHoneycomb() && isLargeHeap(context)) {
                memoryClass = getLargeMemoryClass(am);
            }
            memoryCacheSize = 1024 * 1024 * memoryClass / 8;
        }
        return new LruMemoryCache(memoryCacheSize);
    }

    private static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isLargeHeap(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static int getLargeMemoryClass(ActivityManager am) {
        return am.getLargeMemoryClass();
    }
}
