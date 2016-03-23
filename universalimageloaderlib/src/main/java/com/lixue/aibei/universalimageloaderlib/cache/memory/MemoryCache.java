package com.lixue.aibei.universalimageloaderlib.cache.memory;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Interface for memory cache
 * Created by Administrator on 2016/3/23.
 */
public interface MemoryCache {
    boolean put(String key, Bitmap value);

    /** Returns value by key. If there is no value for key then null will be returned. */
    Bitmap get(String key);

    /** Removes item by key */
    Bitmap remove(String key);

    /** Returns all keys of cache */
    Collection<String> keys();

    /** Remove all items from cache */
    void clear();
}
