package com.lixue.aibei.universalimageloaderlib.core;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.display.BitmapDisplayer;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.utils.L;

/**
 * 图像显示线程任务
 * Created by Administrator on 2016/3/25.
 */
public class DisplayBitmapTask implements Runnable{
    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "Display image in ImageAware (loaded from %1$s) [%2$s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "ImageAware was collected by GC. Task is cancelled. [%s]";

    private final Bitmap bitmap;
    private final String imageUri;
    private final ImageAware imageAware;
    private final String memoryCacheKey;
    private final BitmapDisplayer displayer;
    private final ImageLoadingListener listener;
    private final ImageLoaderEngine engine;
    private final LoadedFrom loadedFrom;

    public DisplayBitmapTask(Bitmap bitmap,ImageLoadingInfo loadingInfo,ImageLoaderEngine engine,LoadedFrom from){
        this.bitmap = bitmap;
        this.imageUri = loadingInfo.uri;
        this.imageAware = loadingInfo.imageAware;
        this.memoryCacheKey = loadingInfo.memoryCacheKey;
        this.displayer = loadingInfo.options.getDisplayer();
        this.listener = loadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = from;
    }

    @Override
    public void run() {
        if (imageAware.isCollected()){
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED,memoryCacheKey);
            listener.onLoadingCancled(imageUri,imageAware.getWrappedView());
        }else if(isViewWasReused()){
            L.d(LOG_TASK_CANCELLED_IMAGEAWARE_REUSED, memoryCacheKey);
            listener.onLoadingCancled(imageUri, imageAware.getWrappedView());
        }else{
            L.d(LOG_DISPLAY_IMAGE_IN_IMAGEAWARE,loadedFrom,memoryCacheKey);
            displayer.dispaly(bitmap, imageAware, loadedFrom);
            engine.cancelDisplayTaskFor(imageAware);
            listener.onLoadingCompleted(imageUri,imageAware.getWrappedView(),bitmap);
        }
    }

    /** Checks whether memory cache key (image URI) for current ImageAware is actual */
    private boolean isViewWasReused() {
        String currentCacheKey = engine.getLoadingUriForView(imageAware);
        return !memoryCacheKey.equals(currentCacheKey);
    }
}
