package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.LimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 所有图像的缓存大小不得不超过限定的大小
 * 当缓存达到最高限制时，使用最不频繁的图片缓存将被删除
 * 注意：这个缓存利用强、弱引用来存储图像
 * 强引用：存储有限定大小的图像
 * 弱应用：存储其他缓存的图像
 * Created by Administrator on 2016/3/23.
 */
public class UsingFreqLimitedMemoryCache extends LimitedMemoryCache {
    private final Map<Bitmap, Integer> usingCounts = Collections.synchronizedMap(new HashMap<Bitmap, Integer>());
    public UsingFreqLimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            usingCounts.put(value, 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Bitmap get(String key) {
        Bitmap value = super.get(key);
        // Increment usage count for value if value is contained in hardCahe
        if (value != null) {
            Integer usageCount = usingCounts.get(value);
            if (usageCount != null) {
                usingCounts.put(value, usageCount + 1);
            }
        }
        return value;
    }

    @Override
    public Bitmap remove(String key) {
        Bitmap value = super.get(key);
        if (value != null) {
            usingCounts.remove(value);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        usingCounts.clear();
        super.clear();
    }

    @Override
    protected int getSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    protected Bitmap removeNext() {
        Integer minUsageCount = null;
        Bitmap leastUsedValue = null;
        Set<Map.Entry<Bitmap, Integer>> entries = usingCounts.entrySet();
        synchronized (usingCounts) {
            for (Map.Entry<Bitmap, Integer> entry : entries) {
                if (leastUsedValue == null) {
                    leastUsedValue = entry.getKey();
                    minUsageCount = entry.getValue();
                } else {
                    Integer lastValueUsage = entry.getValue();
                    if (lastValueUsage < minUsageCount) {
                        minUsageCount = lastValueUsage;
                        leastUsedValue = entry.getKey();
                    }
                }
            }
        }
        usingCounts.remove(leastUsedValue);
        return leastUsedValue;
    }

    @Override
    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference<Bitmap>(value);
    }
}
