package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.BaseMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/3/23.
 */
public class WeakMemoryCache extends BaseMemoryCache {
    @Override
    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference<Bitmap>(value);
    }
}
