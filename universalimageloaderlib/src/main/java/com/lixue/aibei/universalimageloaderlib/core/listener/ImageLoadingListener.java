package com.lixue.aibei.universalimageloaderlib.core.listener;

import android.graphics.Bitmap;
import android.view.View;

import com.lixue.aibei.universalimageloaderlib.core.assist.FailReason;

/**
 * 图像加载监听器
 * Created by Administrator on 2016/3/25.
 */
public interface ImageLoadingListener {
    void onLoadingStarted(String imageUri,View view);
    void onLoadingFailed(String imageUri,View view,FailReason failReason);
    void onLoadingCompleted(String imageUri,View view,Bitmap loadedImage);
    void onLoadingCancled(String imageUri,View view);
}
