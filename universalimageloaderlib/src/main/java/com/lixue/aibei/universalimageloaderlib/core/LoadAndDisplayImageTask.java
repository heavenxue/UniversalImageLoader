package com.lixue.aibei.universalimageloaderlib.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.lixue.aibei.universalimageloaderlib.core.assist.FailReason;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageScaleType;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;
import com.lixue.aibei.universalimageloaderlib.core.decode.ImageDecoder;
import com.lixue.aibei.universalimageloaderlib.core.decode.ImageDecodingInfo;
import com.lixue.aibei.universalimageloaderlib.core.download.ImageDownloader;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingProgressListener;
import com.lixue.aibei.universalimageloaderlib.utils.IoUtils;
import com.lixue.aibei.universalimageloaderlib.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/3/25.
 */
public final class LoadAndDisplayImageTask implements Runnable,IoUtils.CopyListener{
    /**Log日志打印**/
    private static final String LOG_WAITING_FOR_RESUME = "ImageLoader is paused. Waiting...  [%s]";
    private static final String LOG_RESUME_AFTER_PAUSE = ".. Resume loading [%s]";//暂停后重新加载
    private static final String LOG_DELAY_BEFORE_LOADING = "Delay %d ms before loading...  [%s]";
    private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
    private static final String LOG_WAITING_FOR_IMAGE_LOADED = "Image already is loading. Waiting... [%s]";
    private static final String LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING = "...Get cached bitmap from memory after waiting. [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_NETWORK = "Load image from network [%s]";
    private static final String LOG_LOAD_IMAGE_FROM_DISK_CACHE = "Load image from disk cache [%s]";
    private static final String LOG_RESIZE_CACHED_IMAGE_FILE = "Resize image in disk cache [%s]";
    private static final String LOG_PREPROCESS_IMAGE = "PreProcess image before caching in memory [%s]";
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";
    private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
    private static final String LOG_CACHE_IMAGE_ON_DISK = "Cache image on disk [%s]";
    private static final String LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK = "Process image before cache on disk [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";
    private static final String LOG_TASK_INTERRUPTED = "Task was interrupted [%s]";

    private static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    private static final String ERROR_PRE_PROCESSOR_NULL = "Pre-processor returned null [%s]";
    private static final String ERROR_POST_PROCESSOR_NULL = "Post-processor returned null [%s]";
    private static final String ERROR_PROCESSOR_FOR_DISK_CACHE_NULL = "Bitmap processor for disk cache returned null [%s]";

    private final ImageLoaderEngine engine;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    // Helper references
    private final ImageLoaderConfiguration configuration;
    private final ImageDownloader downloader;
    private final ImageDownloader networkDeniedDownloader;
    private final ImageDownloader slowNetworkDownloader;
    private final ImageDecoder decoder;
    final String uri;
    private final String memoryCacheKey;
    final ImageAware imageAware;
    private final ImageSize targetSize;
    final DisplayImageOptions options;
    final ImageLoadingListener listener;
    final ImageLoadingProgressListener progressListener;
    private final boolean syncLoading;

    // State vars
    private LoadedFrom loadedFrom = LoadedFrom.NETWORK;

    public LoadAndDisplayImageTask(ImageLoaderEngine engine, ImageLoadingInfo imageLoadingInfo, Handler handler){
        this.engine = engine;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;

        configuration = engine.configuration;
        downloader = configuration.downloader;
        networkDeniedDownloader = configuration.networkDeniedDownloader;
        slowNetworkDownloader = configuration.slowNetworkDownloader;
        decoder = configuration.decoder;
        uri = imageLoadingInfo.uri;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        imageAware = imageLoadingInfo.imageAware;
        targetSize = imageLoadingInfo.targetSize;
        options = imageLoadingInfo.options;
        listener = imageLoadingInfo.listener;
        progressListener = imageLoadingInfo.progressListener;
        syncLoading = options.isSyncLoading();
    }

    @Override
    public boolean onBytesCopied(int current, int total) {
        return syncLoading || fireProgressEvent(current,total);
    }

    @Override
    public void run() {
        if (waitIfPaused()) return;
        if (delayIfNeed()) return;
        ReentrantLock loadFromUriLock = imageLoadingInfo.loadFromUriLock;
        L.d(LOG_START_DISPLAY_IMAGE_TASK,memoryCacheKey);
        if (loadFromUriLock.isLocked()){
            L.d(LOG_WAITING_FOR_IMAGE_LOADED,memoryCacheKey);
        }
        loadFromUriLock.lock();
        Bitmap btp;
        try {
            /**检查显示图像的视图是否存在**/
            checkTaskNotActual();
            btp = configuration.memoryCache.get(memoryCacheKey);
            if (btp == null || btp.isRecycled()){
                btp = tryLoadBitmap();
                if (btp == null) return;

                checkTaskNotActual();
                checkTaskInterrupted();
                if (options.shouldPreProcess()){
                    L.d(LOG_PREPROCESS_IMAGE,memoryCacheKey);
                    btp = options.getPreProcessor().process(btp);
                    if (btp == null) {
                        L.e(ERROR_PRE_PROCESSOR_NULL, memoryCacheKey);
                    }
                }
                if (btp != null && options.isCacheInMemory()) {
                    L.d(LOG_CACHE_IMAGE_IN_MEMORY, memoryCacheKey);
                    configuration.memoryCache.put(memoryCacheKey, btp);
                }
            }else{
                loadedFrom = LoadedFrom.MEMORY_CACHE;
                L.d(LOG_GET_IMAGE_FROM_MEMORY_CACHE_AFTER_WAITING, memoryCacheKey);
            }

            if (btp != null && options.shouldPostProcess()) {
                L.d(LOG_POSTPROCESS_IMAGE, memoryCacheKey);
                btp = options.getPostProcessor().process(btp);
                if (btp == null) {
                    L.e(ERROR_POST_PROCESSOR_NULL, memoryCacheKey);
                }
            }
            checkTaskNotActual();
            checkTaskInterrupted();
        } catch (TaskCancelledException e) {
            fireCancelEvent();
            return;
        }finally {
            loadFromUriLock.unlock();
        }
        DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(btp, imageLoadingInfo, engine, loadedFrom);
        runTask(displayBitmapTask, syncLoading, handler, engine);
    }

    private Bitmap tryLoadBitmap() throws TaskCancelledException {
        Bitmap bitmap = null;
        try {
            //从sd卡中获取缓存图片
            File imageFile = configuration.diskCache.get(uri);
            if (imageFile != null && imageFile.exists() && imageFile.length() > 0){
                L.d(LOG_LOAD_IMAGE_FROM_DISK_CACHE,memoryCacheKey);
                loadedFrom = LoadedFrom.DISC_CACHE;
                checkTaskNotActual();
                bitmap = decodeImage(ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath()));
            }
            //如果从sd卡取图像失败，那么
            if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0){
                L.d(LOG_LOAD_IMAGE_FROM_NETWORK,memoryCacheKey);
                loadedFrom = LoadedFrom.NETWORK;
                String imageForDecodingUri = uri;
                /**如果可以缓存在sd卡那么久缓存在sd卡中**/
                if (options.isCacheOnDisk() && tryCacheImageOnDisk()){
                    imageFile = configuration.diskCache.get(uri);
                    if (imageFile != null){
                        imageForDecodingUri = ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath());

                    }
                }
                checkTaskNotActual();
                bitmap = decodeImage(imageForDecodingUri);

                if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
                    fireFailEvent(FailReason.FailType.DECODING_ERROR, null);
                }
            }

        }catch (IllegalStateException e){
            fireFailEvent(FailReason.FailType.NETWORK_DENIED, null);
        }catch (TaskCancelledException e){
            throw e;
        }catch (IOException e){
            L.e(e);
            fireFailEvent(FailReason.FailType.IO_ERROR,e);
        }catch (OutOfMemoryError e){
            L.e(e);
            fireFailEvent(FailReason.FailType.OUT_OF_MEMORY,e);
        }catch (Throwable e){
            L.e(e);
            fireFailEvent(FailReason.FailType.UNKNOWN,e);
        }

        return bitmap;
    }

    /**如果图像成功下载那么返回true，否则返回false**/
    private boolean tryCacheImageOnDisk(){
        L.d(LOG_CACHE_IMAGE_ON_DISK,memoryCacheKey);
        boolean loaded;
        try {
            loaded = downloadImage();
            if (loaded){
                /**如果下载成功，如果设置了图像缓存的宽高，那么按最大缓存的宽高进行缓存**/
                int width = configuration.maxImageWidthForDiskCache;
                int height = configuration.maxImageHeightForDiskCache;
                if (width > 0 || height > 0 ){
                    L.d(LOG_RESIZE_CACHED_IMAGE_FILE,memoryCacheKey);
                    resizeAndSaveImage(width, height);
                }
            }
        }catch (IOException e){
            L.e(e);
            loaded = false;
        }
        return loaded;
    }

    /**从网络下载图像，成功后加入sd卡缓存**/
    private boolean downloadImage() throws IOException {
        InputStream inputStream = getDownloader().getStream(uri, options.getExtraForDownloader());
        if (inputStream == null){
            L.e(ERROR_NO_IMAGE_STREAM, memoryCacheKey);
            return false;
        }else{
            try{
                return configuration.diskCache.save(uri,inputStream,this);
            }finally {
                IoUtils.closeSilently(inputStream);
            }
        }
    }

    /**解码图像文件，调整它的大小重新缓存**/
    private boolean resizeAndSaveImage(int width,int height) throws IOException {
        //在重置大小之前，已经将下载的原图像缓存到了内存中
        boolean saved = false;
       File imageFile = configuration.diskCache.get(uri);
        if (imageFile != null && imageFile.length() > 0){
            ImageSize targetSize = new ImageSize(width,height);
            DisplayImageOptions specialOptions = new DisplayImageOptions.Builder().cloneFrom(options).imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
            ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey, ImageDownloader.Scheme.FILE.wrap(imageFile.getAbsolutePath()),uri,targetSize, ViewScaleType.FIT_INSIDE,getDownloader(),specialOptions);
            Bitmap btp = decoder.decode(decodingInfo);
            if (btp != null && configuration.processorForDiskCache != null){
                L.d(LOG_PROCESS_IMAGE_BEFORE_CACHE_ON_DISK,memoryCacheKey);
                btp = configuration.processorForDiskCache.process(btp);
                if (btp == null){
                    L.d(ERROR_PROCESSOR_FOR_DISK_CACHE_NULL,memoryCacheKey);
                }
            }
            if (btp != null) {
                saved = configuration.diskCache.save(uri, btp);
                btp.recycle();
            }
        }
        return saved;
    }

    /**通过uri进行解码得到图像**/
    private Bitmap decodeImage(String imageUri) throws IOException {
        ViewScaleType scaleType = imageAware.getScaleType();
        ImageDecodingInfo decodingInfo = new ImageDecodingInfo(memoryCacheKey,imageUri,uri,targetSize,scaleType,getDownloader(),options);
       return decoder.decode(decodingInfo);
    }

    private boolean fireProgressEvent(final int current, final int total) {
        if (isTaskInterrupted() || isTaskNotActual()) return false;
        if (progressListener != null) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    progressListener.onProgressUpdate(uri, imageAware.getWrappedView(), current, total);
                }
            };
            runTask(r, false, handler, engine);
        }
        return true;
    }

    private void fireCancelEvent(){
        if (syncLoading || isTaskInterrupted()) return;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                listener.onLoadingCancled(uri,imageAware.getWrappedView());
            }
        };
        runTask(r,false,handler,engine);
    }

    public String getLoadingUri(){
        return null;
    }

    /**执行线程任务**/
    public static void runTask(Runnable runnable,boolean syncLoading,Handler handler,ImageLoaderEngine engine){
        if (syncLoading){
            L.i("执行了runTask()方法，syncLoading is true");
            runnable.run();
        }else if(handler == null){
            L.i("执行了runTask()方法，handler is null,执行了engine.fireCallback(runnable)");
            engine.fireCallback(runnable);
        }else{
            L.i("执行了runTask()方法，handler.post(runnable)");
            handler.post(runnable);
        }
    }

    /**等待是否暂停**/
    private boolean waitIfPaused() {
        AtomicBoolean pause = engine.getPause();
        if (pause.get()) {
            synchronized (engine.getPauseLock()) {
                if (pause.get()) {
                    L.d(LOG_WAITING_FOR_RESUME, memoryCacheKey);
                    try {
                        engine.getPauseLock().wait();
                    } catch (InterruptedException e) {
                        L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
                        return true;
                    }
                    L.d(LOG_RESUME_AFTER_PAUSE, memoryCacheKey);
                }
            }
        }
        return isTaskNotActual();
    }

    private boolean delayIfNeed(){
        if (options.shouldDelayBeforeLoading()){
            L.d(LOG_DELAY_BEFORE_LOADING,options.getDelayBeforeLoading(),memoryCacheKey);
            try {
                Thread.sleep(options.getDelayBeforeLoading());
            } catch (InterruptedException e) {
                L.e(LOG_TASK_INTERRUPTED, memoryCacheKey);
                return true;
            }
            return isTaskNotActual();
        }
        return false;
    }

    /**线程任务是否不存在**/
    private boolean isTaskNotActual(){
        return isViewCollected() || isViewReused();
    }

    /**view是否被回收**/
    private boolean isViewCollected() {
        if (imageAware.isCollected()) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    /**view是否被重用**/
    private boolean isViewReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        // Check whether memory cache key (image URI) for current ImageAware is actual.
        // If ImageAware is reused for another task then current task should be cancelled.
        boolean imageAwareWasReused = !memoryCacheKey.equals(currentCacheKey);
        if (imageAwareWasReused) {
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            return true;
        }
        return false;
    }

    /**检查显示图像的视图是否存在，否则就抛异常**/
    private void checkTaskNotActual() throws TaskCancelledException {
        checkViewCollected();
        checkViewReused();
    }

    private void checkTaskInterrupted() throws TaskCancelledException{
        if (isTaskInterrupted()) {
            throw new TaskCancelledException();
        }
    }
    private boolean isTaskInterrupted() {
        if (Thread.interrupted()) {
            L.d(LOG_TASK_INTERRUPTED, memoryCacheKey);
            return true;
        }
        return false;
    }

    private void checkViewCollected() throws TaskCancelledException {
        if (isViewCollected()) {
            throw new TaskCancelledException();
        }
    }

    private void checkViewReused() throws TaskCancelledException {
        if (isViewReused()) {
            throw new TaskCancelledException();
        }
    }

    //失败
    private void fireFailEvent(final FailReason.FailType failtype, final Throwable e){
        if (syncLoading || isTaskInterrupted() || isTaskNotActual()) return;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                if (options.shouldShowImageOnFail()) {
                    imageAware.setImageDrawable(options.getImageOnFail(configuration.resources));
                }
                listener.onLoadingFailed(uri, imageAware.getWrappedView(), new FailReason(failtype, e));
            }
        };
        runTask(r, false, handler, engine);
    }

    /**线程取消的异常类**/
    class TaskCancelledException extends Exception {
    }

    public ImageDownloader getDownloader() {
        return downloader;
    }

}