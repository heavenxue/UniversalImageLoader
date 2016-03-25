package com.lixue.aibei.universalimageloaderlib.core.listener;

import android.view.View;

/**
 * 图像加载处理监听器
 * Created by Administrator on 2016/3/25.
 */
public interface ImageLoadingProgressListener {
    /**图像加载处处理更新时候调用**/
    void onProgressUpdate(String imageUri, View view, int current, int total);
}
