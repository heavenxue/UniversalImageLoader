package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.LimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 所有图像的缓存大小不得不超过限定的大小
 * 当缓存达到最高限制时，最近最少使用的图片缓存将被删除
 * 注意：这个缓存利用强、弱引用来存储图像
 * 强引用：存储有限定大小的图像
 * 弱应用：存储其他缓存的图像
 * Created by Administrator on 2016/3/23.
 */
public class LRULimitedMemoryCache extends LimitedMemoryCache {
    private static final int INITIAL_CAPACITY = 10;//初始化数量
    private static final float LOAD_FACTOR = 1.1f;//负载因子

    /** 用来存储最近最少使用过的图像 */
    private final Map<String, Bitmap> lruCache = Collections.synchronizedMap(new LinkedHashMap<String, Bitmap>(INITIAL_CAPACITY, LOAD_FACTOR, true));

    public LRULimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            lruCache.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Bitmap get(String key) {
        lruCache.get(key); // call "get" for LRU logic
        return super.get(key);
    }

    @Override
    public Bitmap remove(String key) {
        lruCache.remove(key);
        return super.remove(key);
    }

    @Override
    public void clear() {
        lruCache.clear();
        super.clear();
    }

    @Override
    protected int getSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    @Override
    protected Bitmap removeNext() {
        Bitmap mostLongUsedValue = null;
        synchronized (lruCache) {
            Iterator<Map.Entry<String, Bitmap>> it = lruCache.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry<String, Bitmap> entry = it.next();
                mostLongUsedValue = entry.getValue();
                it.remove();
            }
        }
        return mostLongUsedValue;
    }

    @Override
    protected Reference<Bitmap> createReference(Bitmap bitmap) {
        return new WeakReference<Bitmap>(bitmap);
    }
}
