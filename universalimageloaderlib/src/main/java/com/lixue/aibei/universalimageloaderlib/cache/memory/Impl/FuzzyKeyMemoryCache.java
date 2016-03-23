package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;

import java.util.Collection;
import java.util.Comparator;

/**
 * 用比较器来作为不同的键来进行，相当于把不同于键的值都要移除缓存
 * Created by Administrator on 2016/3/23.
 */
public class FuzzyKeyMemoryCache implements MemoryCache {

    private final MemoryCache cache;
    private final Comparator<String> keyComparator;

    public FuzzyKeyMemoryCache(MemoryCache cache, Comparator<String> keyComparator) {
        this.cache = cache;
        this.keyComparator = keyComparator;
    }

    @Override
    public boolean put(String key, Bitmap value) {
        // Search equal key and remove this entry
        synchronized (cache) {
            String keyToRemove = null;
            for (String cacheKey : cache.keys()) {
                if (keyComparator.compare(key, cacheKey) == 0) {
                    keyToRemove = cacheKey;
                    break;
                }
            }
            if (keyToRemove != null) {
                cache.remove(keyToRemove);
            }
        }
        return cache.put(key, value);
    }

    @Override
    public Bitmap get(String key) {
        return cache.get(key);
    }

    @Override
    public Bitmap remove(String key) {
        return cache.remove(key);
    }

    @Override
    public Collection<String> keys() {
        return cache.keys();
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
