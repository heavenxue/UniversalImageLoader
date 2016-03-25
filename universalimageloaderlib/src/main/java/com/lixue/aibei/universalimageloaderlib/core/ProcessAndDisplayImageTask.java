package com.lixue.aibei.universalimageloaderlib.core;

import android.graphics.Bitmap;
import android.os.Handler;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.process.BitmapProcessor;
import com.lixue.aibei.universalimageloaderlib.utils.L;

/**
 * 图像处理且显示的线程
 * Created by Administrator on 2016/3/25.
 */
public class ProcessAndDisplayImageTask implements Runnable {
    private static final String LOG_POSTPROCESS_IMAGE = "PostProcess image before displaying [%s]";

    private final ImageLoaderEngine engine;
    private final Bitmap bitmap;
    private final ImageLoadingInfo imageLoadingInfo;
    private final Handler handler;

    public ProcessAndDisplayImageTask(ImageLoaderEngine engine, Bitmap bitmap, ImageLoadingInfo imageLoadingInfo,
                                      Handler handler) {
        this.engine = engine;
        this.bitmap = bitmap;
        this.imageLoadingInfo = imageLoadingInfo;
        this.handler = handler;
    }

    @Override
    public void run() {
        L.d(LOG_POSTPROCESS_IMAGE, imageLoadingInfo.memoryCacheKey);

        BitmapProcessor processor = imageLoadingInfo.options.getPostProcessor();
        Bitmap processedBitmap = processor.process(bitmap);
        DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(processedBitmap, imageLoadingInfo, engine,
                LoadedFrom.MEMORY_CACHE);
        LoadAndDisplayImageTask.runTask(displayBitmapTask, imageLoadingInfo.options.isSyncLoading(), handler, engine);
    }
}
