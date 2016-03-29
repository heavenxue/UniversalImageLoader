package com.lixue.aibei.universalimageloaderlib.core;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.lixue.aibei.universalimageloaderlib.core.assist.ImageScaleType;
import com.lixue.aibei.universalimageloaderlib.core.display.BitmapDisplayer;
import com.lixue.aibei.universalimageloaderlib.core.process.BitmapProcessor;

/**
 * 图像的显示选项
 * Created by Administrator on 2016/3/25.
 */
public final class DisplayImageOptions {
    private final int imageResOnLoading;//图像加载时图像资源
    private final int imageResForEmptyUri;//图像为空时图像资源
    private final int imageResOnFail;//图像加载失败时图像资源

    private final Drawable imageOnLoading;//加载时图像
    private final Drawable imageForEmpty;//为空时图像
    private final Drawable imageOnFail;//失败时图像

    private final boolean resetViewBeforeLoading;//是否加载图象时重置view
    private final boolean cacheInMemory;//是否缓存内存中
    private final boolean cacheOnDisk;//是否缓存sd卡中

    private final ImageScaleType imageScaleType;
    private final BitmapFactory.Options decodingOptions;
    private final int delayBeforLoading;//加载之前的延迟
    private final boolean consinderExifParams;
    private final Object extraForDownloader;
    private final BitmapProcessor preProcessor;//预处理器
    private final BitmapProcessor postProcessor;//后处理器
    private final BitmapDisplayer displayer;
    private final Handler handler;
    private final boolean isSyncLoading;//是否是同步加载

    private DisplayImageOptions(Builder builder) {
        imageResOnLoading = builder.imageResOnLoading;
        imageResForEmptyUri = builder.imageResForEmptyUri;
        imageResOnFail = builder.imageResOnFail;
        imageOnLoading = builder.imageOnLoading;
        imageForEmpty = builder.imageForEmpty;
        imageOnFail = builder.imageOnFail;
        resetViewBeforeLoading = builder.resetViewBeforeLoading;
        cacheInMemory = builder.cacheInMemory;
        cacheOnDisk = builder.cacheOnDisk;
        imageScaleType = builder.imageScaleType;
        decodingOptions = builder.decodingOptions;
        delayBeforLoading = builder.delayBeforLoading;
        consinderExifParams = builder.consinderExifParams;
        extraForDownloader = builder.extraForDownloader;
        preProcessor = builder.preProcessor;
        postProcessor = builder.postProcessor;
        displayer = builder.displayer;
        handler = builder.handler;
        isSyncLoading = builder.isSyncLoading;
    }

    public boolean shouldShowImageOnLoading() {
        return imageOnLoading != null || imageResOnLoading != 0;
    }

    public boolean shouldShowImageForEmptyUri() {
        return imageForEmpty != null || imageResForEmptyUri != 0;
    }

    public boolean shouldShowImageOnFail() {
        return imageOnFail != null || imageResOnFail != 0;
    }

    public boolean shouldPreProcess() {
        return preProcessor != null;
    }

    public boolean shouldPostProcess() {
        return postProcessor != null;
    }

    public boolean shouldDelayBeforeLoading() {
        return delayBeforLoading > 0;
    }

    public Drawable getImageOnLoading(Resources res) {
        return imageResOnLoading != 0 ? res.getDrawable(imageResOnLoading) : imageOnLoading;
    }

    public Drawable getImageForEmptyUri(Resources res) {
        return imageResForEmptyUri != 0 ? res.getDrawable(imageResForEmptyUri) : imageForEmpty;
    }

    public Drawable getImageOnFail(Resources res) {
        return imageResOnFail != 0 ? res.getDrawable(imageResOnFail) : imageOnFail;
    }

    public boolean isResetViewBeforeLoading() {
        return resetViewBeforeLoading;
    }

    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    public boolean isCacheOnDisk() {
        return cacheOnDisk;
    }

    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    public BitmapFactory.Options getDecodingOptions() {
        return decodingOptions;
    }

    public int getDelayBeforeLoading() {
        return delayBeforLoading;
    }

    public boolean isConsiderExifParams() {
        return consinderExifParams;
    }

    public Object getExtraForDownloader() {
        return extraForDownloader;
    }

    public BitmapProcessor getPreProcessor() {
        return preProcessor;
    }

    public BitmapProcessor getPostProcessor() {
        return postProcessor;
    }

    public BitmapDisplayer getDisplayer() {
        return displayer;
    }

    public Handler getHandler() {
        return handler;
    }

    public boolean isSyncLoading() {
        return isSyncLoading;
    }

    public static class Builder{
        private int imageResOnLoading = 0;//图像加载时图像资源
        private int imageResForEmptyUri = 0;//图像为空时图像资源
        private int imageResOnFail = 0;//图像加载失败时图像资源

        private Drawable imageOnLoading = null;//加载时图像
        private Drawable imageForEmpty = null;//为空时图像
        private Drawable imageOnFail = null;//失败时图像

        private boolean resetViewBeforeLoading = false;//是否加载图象时重置view
        private boolean cacheInMemory = false;//是否缓存内存中
        private boolean cacheOnDisk = false;//是否缓存sd卡中

        private ImageScaleType imageScaleType = ImageScaleType.IN_SAMPLE_POWER_2;
        private BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
        private int delayBeforLoading = 0;//加载之前的延迟
        private boolean consinderExifParams = false;
        private Object extraForDownloader = null;
        private BitmapProcessor preProcessor = null;//预处理器
        private BitmapProcessor postProcessor = null;//后处理器
        private BitmapDisplayer displayer = DefaultConfigurationFactory.createBitmapDisplayer();
        private Handler handler = null;
        private boolean isSyncLoading = false;//是否是同步加载

        public Builder showImageOnLoading(int imageRes){
            this.imageResOnLoading = imageRes;
            return this;
        }

        public Builder showImageOnLoading(Drawable drawable){
            this.imageOnLoading = drawable;
            return this;
        }

        public Builder showImageForEmptyUri(int imageRes){
            this.imageResForEmptyUri = imageRes;
            return this;
        }

        public Builder showImageForEmptyUri(Drawable drawable){
            this.imageForEmpty = drawable;
            return this;
        }

        public Builder showImageOnFail(int imageRes){
            this.imageResOnFail = imageRes;
            return this;
        }

        public Builder showImageOnFail(Drawable drawable){
            this.imageOnFail = drawable;
            return this;
        }

        public Builder resetViewBeforLoading(){
            this.resetViewBeforeLoading = true;
            return this;
        }

        public Builder resetViewBeforLoading(boolean resetViewBeforeLoading){
            this.resetViewBeforeLoading = resetViewBeforeLoading;
            return this;
        }

        public Builder cacheInMemory(boolean cacheInMemory){
            this.cacheInMemory = cacheInMemory;
            return this;
        }

        public Builder cacheOnDisk(boolean cacheInDisk){
            this.cacheOnDisk = cacheInDisk;
            return this;
        }

        public Builder imageScaleType(ImageScaleType scaleType){
            this.imageScaleType = scaleType;
            return this;
        }

        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            if (bitmapConfig == null) throw new IllegalArgumentException("bitmapConfig can't be null");
            decodingOptions.inPreferredConfig = bitmapConfig;
            return this;
        }

        public Builder decodingOptions(BitmapFactory.Options options){
            if (options == null) throw new IllegalArgumentException("decodingOptions can't be null");
            this.decodingOptions = options;
            return this;
        }

        public Builder delayBeforLoading(int delay){
            this.delayBeforLoading = delay;
            return this;
        }

        public Builder consinderExifParams(boolean consinderExifParams){
            this.consinderExifParams = consinderExifParams;
            return this;
        }

        public Builder extraForDownloader(Object extraForDownloader){
            this.extraForDownloader = extraForDownloader;
            return this;
        }

        public Builder preProcessor(BitmapProcessor preProcessor) {
            this.preProcessor = preProcessor;
            return this;
        }

        public Builder postProcessor(BitmapProcessor postProcessor) {
            this.postProcessor = postProcessor;
            return this;
        }

        public Builder displayer(BitmapDisplayer displayer) {
            if (displayer == null) throw new IllegalArgumentException("displayer can't be null");
            this.displayer = displayer;
            return this;
        }

        Builder syncLoading(boolean isSyncLoading) {
            this.isSyncLoading = isSyncLoading;
            return this;
        }

        public Builder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder cloneFrom(DisplayImageOptions options){
            imageResOnLoading = options.imageResOnLoading;
            imageResForEmptyUri = options.imageResForEmptyUri;
            imageResOnFail = options.imageResOnFail;
            imageOnLoading = options.imageOnLoading;
            imageResForEmptyUri = options.imageResForEmptyUri;
            imageOnFail = options.imageOnFail;
            resetViewBeforeLoading = options.resetViewBeforeLoading;
            cacheInMemory = options.cacheInMemory;
            cacheOnDisk = options.cacheOnDisk;
            imageScaleType = options.imageScaleType;
            decodingOptions = options.decodingOptions;
            delayBeforLoading = options.delayBeforLoading;
            consinderExifParams = options.consinderExifParams;
            extraForDownloader = options.extraForDownloader;
            preProcessor = options.preProcessor;
            postProcessor = options.postProcessor;
            displayer = options.displayer;
            handler = options.handler;
            isSyncLoading = options.isSyncLoading;
            return this;
        }

        public DisplayImageOptions build() {
            return new DisplayImageOptions(this);
        }
    }

    public static DisplayImageOptions createSimple() {
        return new Builder().build();
    }
}
