package com.lixue.aibei.universalimageloaderlib.core;

import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 图像加载器引擎
 * 对显示图像线程的执行
 * Created by Administrator on 2016/3/25.
 */
public class ImageLoaderEngine {
    public final ImageLoaderConfiguration configuration;
    private Executor taskExecutor;
    private Executor taskExeCutorForCachedImages;
    private Executor taskDistributor;//线程分发器

    /**缓存关键字**/
    private final Map<Integer,String> cacheKeysForImageAwares = Collections.synchronizedMap(new HashMap<Integer,String>());
    /**ui锁**/
    private final Map<String,ReentrantLock> uriLocks = new WeakHashMap<String,ReentrantLock>();

    /**是否暂停**/
    private final AtomicBoolean paused = new AtomicBoolean(false);
    /**是否网络拒绝**/
    private final AtomicBoolean networkDenied = new AtomicBoolean(false);
    /**是否网络慢**/
    private final AtomicBoolean slowNetwork = new AtomicBoolean(false);

    private final Object pauseLock = new Object();//暂停锁对象

    public ImageLoaderEngine(ImageLoaderConfiguration configuration){
        this.configuration = configuration;
        taskExecutor = configuration.taskExecutor;
        taskExeCutorForCachedImages = configuration.taskExecutorForCachedImages;
        taskDistributor = DefaultConfigurationFactory.createTaskDistributor();
    }

    public void submit(final LoadAndDisplayImageTask task){
        taskDistributor.execute(new Runnable() {
            @Override
            public void run() {
                File image = configuration.diskCache.get(task.getLoadingUri());
                boolean isImageCachedOnDisk = image != null && image.exists();
                initExecutorsIfNeed();//如果需要初始化线程池
                if (isImageCachedOnDisk){
                    taskExeCutorForCachedImages.execute(task);
                }else{
                    taskExecutor.execute(task);
                }
            }
        });
    }

    public void submit(ProcessAndDisplayImageTask task) {
        initExecutorsIfNeed();
        taskExeCutorForCachedImages.execute(task);
    }

    public void initExecutorsIfNeed(){
        if (!configuration.customExecutor && ((ExecutorService) taskExecutor).isShutdown()){
            taskExecutor = createTaskExecutor();
        }
        if (!configuration.customExecutorForCachedImages &&((ExecutorService) taskExeCutorForCachedImages).isShutdown()){
            taskExeCutorForCachedImages = createTaskExecutor();
        }
    }

    private Executor createTaskExecutor() {
        return DefaultConfigurationFactory
                .createExecutor(configuration.threadPoolSize, configuration.threadPriority,
                        configuration.tasksProcessingType);
    }

    /**
     * Returns URI of image which is loading at this moment into passed
     */
    String getLoadingUriForView(ImageAware imageAware) {
        return cacheKeysForImageAwares.get(imageAware.getId());
    }

    /**
     * Associates <b>memoryCacheKey</b> with <b>imageAware</b>. Then it helps to define image URI is loaded into View at
     * exact moment.
     */
    void prepareDisplayTaskFor(ImageAware imageAware, String memoryCacheKey) {
        cacheKeysForImageAwares.put(imageAware.getId(), memoryCacheKey);
    }

    /**
     * Cancels the task of loading and displaying image for incoming <b>imageAware</b>.
     */
    void cancelDisplayTaskFor(ImageAware imageAware) {
        cacheKeysForImageAwares.remove(imageAware.getId());
    }

    /**
     * Denies or allows engine to download images from the network.<br />
     */
    void denyNetworkDownloads(boolean denyNetworkDownloads) {
        networkDenied.set(denyNetworkDownloads);
    }

    /**
     * 是否处理网络慢
     */
    void handleSlowNetwork(boolean handleSlowNetwork) {
        slowNetwork.set(handleSlowNetwork);
    }

    /**
     * Pauses engine. All new "load&display" tasks won't be executed until ImageLoader is {@link #resume() resumed}.<br
     * /> Already running tasks are not paused.
     */
    void pause() {
        paused.set(true);
    }

    /** Resumes engine work. Paused "load&display" tasks will continue its work. */
    void resume() {
        paused.set(false);
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    /**
     * Stops engine, cancels all running and scheduled display image tasks. Clears internal data.
     */
    void stop() {
        if (!configuration.customExecutor) {
            ((ExecutorService) taskExecutor).shutdownNow();
        }
        if (!configuration.customExecutorForCachedImages) {
            ((ExecutorService) taskExeCutorForCachedImages).shutdownNow();
        }

        cacheKeysForImageAwares.clear();
        uriLocks.clear();
    }

    void fireCallback(Runnable r) {
        taskDistributor.execute(r);
    }

    ReentrantLock getLockForUri(String uri) {
        ReentrantLock lock = uriLocks.get(uri);
        if (lock == null) {
            lock = new ReentrantLock();
            uriLocks.put(uri, lock);
        }
        return lock;
    }

    AtomicBoolean getPause() {
        return paused;
    }

    Object getPauseLock() {
        return pauseLock;
    }

    boolean isNetworkDenied() {
        return networkDenied.get();
    }

    boolean isSlowNetwork() {
        return slowNetwork.get();
    }
}
