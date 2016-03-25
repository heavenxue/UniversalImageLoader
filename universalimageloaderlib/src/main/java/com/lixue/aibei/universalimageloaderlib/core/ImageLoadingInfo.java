package com.lixue.aibei.universalimageloaderlib.core;

import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingProgressListener;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/3/25.
 */
public final class ImageLoadingInfo {
    public final String uri;
    public final String memoryCacheKey;
    public final ImageAware imageAware;
    public final ImageSize targetSize;
    public final DisplayImageOptions options;
    public final ImageLoadingListener listener;
    public final ImageLoadingProgressListener progressListener;
    public final ReentrantLock loadFromUriLock;

    public ImageLoadingInfo(String uri, ImageAware imageAware, ImageSize targetSize, String memoryCacheKey,
                            DisplayImageOptions options, ImageLoadingListener listener,
                            ImageLoadingProgressListener progressListener, ReentrantLock loadFromUriLock) {
        this.uri = uri;
        this.imageAware = imageAware;
        this.targetSize = targetSize;
        this.options = options;
        this.listener = listener;
        this.progressListener = progressListener;
        this.loadFromUriLock = loadFromUriLock;
        this.memoryCacheKey = memoryCacheKey;
    }
}
