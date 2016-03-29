package com.lixue.aibei.universalimageloaderlib.core.display;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;

/**
 * 利用了渐入动画的显示器
 * Created by Administrator on 2016/3/29.
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer{
    private final int durationMills;
    private final boolean animateFromNetwork;
    private final boolean animateFromDisk;
    private final boolean animateFromMemory;

    public FadeInBitmapDisplayer(int durationMills){
        this(durationMills,true,true,true);
    }

    public FadeInBitmapDisplayer(int durationMills,boolean animateFromNetwork,boolean animateFromDisk,boolean animateFromMemory){
        this.durationMills = durationMills;
        this.animateFromDisk = animateFromDisk;
        this.animateFromNetwork = animateFromNetwork;
        this.animateFromMemory = animateFromMemory;
    }

    @Override
    public void dispaly(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        imageAware.setImageBitmap(bitmap);

        if ((animateFromNetwork && loadedFrom == LoadedFrom.NETWORK) ||
                (animateFromDisk && loadedFrom == LoadedFrom.DISC_CACHE) ||
                (animateFromMemory && loadedFrom == LoadedFrom.MEMORY_CACHE)) {
            animate(imageAware.getWrappedView(), durationMills);
        }
    }

    public static void animate(View imageView, int durationMillis) {
        if (imageView != null) {
            AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
            fadeImage.setDuration(durationMillis);
            fadeImage.setInterpolator(new DecelerateInterpolator());
            imageView.startAnimation(fadeImage);
        }
    }
}
