package com.lixue.aibei.universalimageloaderlib.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;

/**
 * 加载图像视图
 * 该视图通过ImageLoader可将任何提供用于图像处理和属性以及行为
 * 通过getwrappedview()进行包裹
 * 通过imageLoadingListener进行回调
 * Created by Administrator on 2016/3/25.
 */
public interface ImageAware {
    /**返回imageView的宽度，这个值一般用来表示原始图像的尺寸，如果是0表明没有定义宽度**/
    int getWidth();
    /**返回imageView的高度，这个值一般用来表示原始图像的尺寸，如果是0表明没有定义高度**/
    int getHeight();
    /**显示缩放类型**/
    ViewScaleType getScaleType();
    /**返回显示图像的容器，一般是imageview**/
    View getWrappedView();
    /**返回imageview是否被回收，如果是的话，图像加载器将停止处理要显示图像的线程池**/
    boolean isCollected();
    /**返回imageview的id**/
    int getId();
    /**给imageview设置drawable**/
    boolean setImageDrawable(Drawable drawable);
    /**给imageview设置bitmap**/
    boolean setImageBitmap(Bitmap bitmap);
}
