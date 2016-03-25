package com.lixue.aibei.universalimageloaderlib.core.display;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;

/**
 * 简单的显示在imageview上
 * Created by Administrator on 2016/3/25.
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer{
    @Override
    public void dispaly(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);
    }
}
