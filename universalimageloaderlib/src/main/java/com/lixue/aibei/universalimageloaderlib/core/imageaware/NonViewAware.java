package com.lixue.aibei.universalimageloaderlib.core.imageaware;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;

/**
 * Created by Administrator on 2016/3/28.
 */
public class NonViewAware implements ImageAware {
    protected final String imageUri;
    protected final ImageSize imageSize;
    protected final ViewScaleType scaleType;

    public NonViewAware(ImageSize imageSize, ViewScaleType scaleType) {
        this(null, imageSize, scaleType);
    }

    public NonViewAware(String imageUri, ImageSize imageSize, ViewScaleType scaleType) {
        if (imageSize == null) throw new IllegalArgumentException("imageSize must not be null");
        if (scaleType == null) throw new IllegalArgumentException("scaleType must not be null");

        this.imageUri = imageUri;
        this.imageSize = imageSize;
        this.scaleType = scaleType;
    }
    @Override
    public int getWidth() {
        return imageSize.getWidth();
    }

    @Override
    public int getHeight() {
        return imageSize.getHeight();
    }

    @Override
    public ViewScaleType getScaleType() {
        return scaleType;
    }

    @Override
    public View getWrappedView() {
        return null;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public int getId() {
        return TextUtils.isEmpty(imageUri) ? super.hashCode() : imageUri.hashCode();
    }

    @Override
    public boolean setImageDrawable(Drawable drawable) {
        return true;
    }

    @Override
    public boolean setImageBitmap(Bitmap bitmap) {
        return true;
    }
}
