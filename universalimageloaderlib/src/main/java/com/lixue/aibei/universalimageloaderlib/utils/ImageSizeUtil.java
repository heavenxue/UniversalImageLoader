package com.lixue.aibei.universalimageloaderlib.utils;

import android.opengl.GLES10;

import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;

import javax.microedition.khronos.opengles.GL10;

/**
 * 图像大小的公共类
 * Created by Administrator on 2016/3/28.
 */
public final class ImageSizeUtil {
    private static final int DEFAULT_MAX_BITMAP_DIMENSION = 2048;//默认图像最大值

    private static ImageSize maxBitmapSize;//最大的图像大小
    static {
        int[] maxTextureSize = new int[1];
        GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        int maxBitmapDimension = Math.max(maxTextureSize[0], DEFAULT_MAX_BITMAP_DIMENSION);
        maxBitmapSize = new ImageSize(maxBitmapDimension, maxBitmapDimension);
    }
    private ImageSizeUtil() {
    }

    /**定义默认的图像大小，首先以显示图像的view的大小为基础**/
    public static ImageSize defineTargetSizeForView(ImageAware imageAware,ImageSize maxImageSize){
        int width = imageAware.getWidth();
        if (width <= 0) width = maxImageSize.getWidth();
        int height = imageAware.getHeight();
        if (height <= 0) height = maxImageSize.getHeight();
        return new ImageSize(width,height);
    }

    /**计算压缩后图像的大小以匹配显示图像的view(比如imageView),样本是解码后的图像**/
    public static int computeImageSampleSize(ImageSize srcSize,ImageSize targetSize,ViewScaleType scaleType,boolean powerOf2Scale){
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        final int targetHeight = targetSize.getHeight();
        int scale = 1;//压缩比例e
        switch (scaleType){
            case FIT_INSIDE:
                /**如果采样比例是2的倍数**/
                if (powerOf2Scale){
                    final int halfWidth = srcWidth / 2;
                    final int halfHeight = srcHeight /2;
                    while ((halfWidth / scale) > targetWidth || (halfHeight / scale) > targetHeight){//||
                        scale *= 2;
                    }
                }else{
                    scale = Math.max(srcWidth / targetWidth,srcHeight / targetHeight);// max
                }
                break;
            case CROP:
                if (powerOf2Scale) {
                    final int halfWidth = srcWidth / 2;
                    final int halfHeight = srcHeight / 2;
                    while ((halfWidth / scale) > targetWidth && (halfHeight / scale) > targetHeight) { // &&
                        scale *= 2;
                    }
                } else {
                    scale = Math.min(srcWidth / targetWidth, srcHeight / targetHeight); // min
                }
                break;
        }
        if (scale < 1) {
            scale = 1;
        }
        scale = considerMaxTextureSize(srcWidth, srcHeight, scale, powerOf2Scale);

        return scale;
    }

    /**计算最小样品尺寸缩减图像，结果图像的大小不会超过最大可接受的OpenGL纹理大小。
     *我们不能创建在内存位图大小超过最大纹理尺寸（通常这是2048x2048）这种方法
     *计算应适用于图像的最小样本量，以适应这些限制。**/
    private static int considerMaxTextureSize(int srcWidth, int srcHeight, int scale, boolean powerOf2) {
        final int maxWidth = maxBitmapSize.getWidth();
        final int maxHeight = maxBitmapSize.getHeight();
        while ((srcWidth / scale) > maxWidth || (srcHeight / scale) > maxHeight) {
            if (powerOf2) {
                scale *= 2;
            } else {
                scale++;
            }
        }
        return scale;
    }

    public static int computeMinImageSampleSize(ImageSize srcSize) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = maxBitmapSize.getWidth();
        final int targetHeight = maxBitmapSize.getHeight();

        final int widthScale = (int) Math.ceil((float) srcWidth / targetWidth);
        final int heightScale = (int) Math.ceil((float) srcHeight / targetHeight);

        return Math.max(widthScale, heightScale); // max
    }

    /**计算目标尺寸对于源尺寸的缩放比例**/
    public static float computeImageScale(ImageSize srcSize, ImageSize targetSize, ViewScaleType viewScaleType,
                                          boolean stretch) {
        final int srcWidth = srcSize.getWidth();
        final int srcHeight = srcSize.getHeight();
        final int targetWidth = targetSize.getWidth();
        final int targetHeight = targetSize.getHeight();

        final float widthScale = (float) srcWidth / targetWidth;
        final float heightScale = (float) srcHeight / targetHeight;

        final int destWidth;
        final int destHeight;
        if ((viewScaleType == ViewScaleType.FIT_INSIDE && widthScale >= heightScale) || (viewScaleType == ViewScaleType.CROP && widthScale < heightScale)) {
            destWidth = targetWidth;
            destHeight = (int) (srcHeight / widthScale);
        } else {
            destWidth = (int) (srcWidth / heightScale);
            destHeight = targetHeight;
        }

        float scale = 1;
        if ((!stretch && destWidth < srcWidth && destHeight < srcHeight) || (stretch && destWidth != srcWidth && destHeight != srcHeight)) {
            scale = (float) destWidth / srcWidth;
        }

        return scale;
    }

}
