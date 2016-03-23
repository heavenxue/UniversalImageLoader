package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供了有特殊特征的高速缓存
 * 如果缓存所待的时间大于所定义的时间，那么就移除这个缓存
 * Created by Administrator on 2016/3/23.
 */
public class LimitedAgeMemoryCache implements MemoryCache {
    private final MemoryCache cache;

    private final long maxAge;
    private final Map<String, Long> loadingDates = Collections.synchronizedMap(new HashMap<String, Long>());

    public LimitedAgeMemoryCache(MemoryCache cache, long maxAge) {
        this.cache = cache;
        this.maxAge = maxAge * 1000; // to milliseconds
    }

    @Override
    public boolean put(String key, Bitmap value) {
        boolean putSuccesfully = cache.put(key, value);
        if (putSuccesfully) {
            loadingDates.put(key, System.currentTimeMillis());
        }
        return putSuccesfully;
    }

    @Override
    public Bitmap get(String key) {
        Long loadingDate = loadingDates.get(key);
        if (loadingDate != null && System.currentTimeMillis() - loadingDate > maxAge) {
            cache.remove(key);
            loadingDates.remove(key);
        }

        return cache.get(key);
    }

    @Override
    public Bitmap remove(String key) {
        loadingDates.remove(key);
        return cache.remove(key);
    }

    @Override
    public Collection<String> keys() {
        return cache.keys();
    }

    @Override
    public void clear() {
        cache.clear();
        loadingDates.clear();
    }
}
