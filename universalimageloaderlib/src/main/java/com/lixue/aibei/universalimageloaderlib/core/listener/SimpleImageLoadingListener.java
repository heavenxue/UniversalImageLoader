package com.lixue.aibei.universalimageloaderlib.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.lixue.aibei.universalimageloaderlib.core.assist.FailReason;

/**
 * 简单的图像加载监听器（实现了ImageLoading接口）
 * Created by Administrator on 2016/3/25.
 */
public class SimpleImageLoadingListener implements ImageLoadingListener {
    @Override
    public void onLoadingStarted(String imageUri, View view) {

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

    }

    @Override
    public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {

    }

    @Override
    public void onLoadingCancled(String imageUri, View view) {

    }
}
