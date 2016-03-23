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
 * 所有图片缓存的大小不得超过限定的缓存大小
 * 当缓存大小达到最高限定大小时，把有最大大小的缓存清除
 * 注意：这个缓存利用强、弱引用来存储图像
 * 强引用：存储有限定大小的图像
 * 弱应用：存储其他缓存的图像
 * Created by Administrator on 2016/3/23.
 */
public class LargestLimitedMemoryCache extends LimitedMemoryCache {
    private final Map<Bitmap, Integer> valueSizes = Collections.synchronizedMap(new HashMap<Bitmap, Integer>());

    public LargestLimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            valueSizes.put(value, getSize(value));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Bitmap remove(String key) {
        Bitmap value = super.get(key);
        if (value != null) {
            valueSizes.remove(value);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        valueSizes.clear();
        super.clear();
    }

    @Override
    protected int getSize(Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    protected Bitmap removeNext() {
        Integer maxSize = null;
        Bitmap largestValue = null;
        Set<Map.Entry<Bitmap, Integer>> entries = valueSizes.entrySet();
        synchronized (valueSizes) {
            for (Map.Entry<Bitmap, Integer> entry : entries) {
                if (largestValue == null) {
                    largestValue = entry.getKey();
                    maxSize = entry.getValue();
                } else {
                    Integer size = entry.getValue();
                    if (size > maxSize) {
                        maxSize = size;
                        largestValue = entry.getKey();
                    }
                }
            }
        }
        valueSizes.remove(largestValue);
        return largestValue;
    }

    @Override
    protected Reference<Bitmap> createReference(Bitmap bitmap) {
        return new WeakReference<Bitmap>(bitmap);
    }
}
