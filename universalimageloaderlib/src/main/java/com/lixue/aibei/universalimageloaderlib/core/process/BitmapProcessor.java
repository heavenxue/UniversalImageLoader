package com.lixue.aibei.universalimageloaderlib.core.process;

import android.graphics.Bitmap;

/**
 * 对图像进行处理
 * 这个方法在 additional thread (not on UI thread)上执行
 * Created by Administrator on 2016/3/24.
 */
public interface BitmapProcessor {
    /**这个方法在 additional thread (not on UI thread)上执行
     * 如果处理器在DisplayImageOptions.Builder#preProcessor里面用过了，如果返回一个新的图像不要忘了回收
     * **/
    Bitmap process(Bitmap bitmap);
}
