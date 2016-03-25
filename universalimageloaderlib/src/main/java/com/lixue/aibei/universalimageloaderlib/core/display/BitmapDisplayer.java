package com.lixue.aibei.universalimageloaderlib.core.display;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;

/**
 * 图像显示器接口
 * Created by Administrator on 2016/3/25.
 */
public interface BitmapDisplayer {
    void dispaly(Bitmap bitmap,ImageAware imageAware,LoadedFrom loadedFrom);
}
