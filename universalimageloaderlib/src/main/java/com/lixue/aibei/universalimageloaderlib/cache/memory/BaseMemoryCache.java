package com.lixue.aibei.universalimageloaderlib.cache.memory;

import android.graphics.Bitmap;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/23.
 */
public abstract class BaseMemoryCache implements MemoryCache{
    /**存储图片的一个非强引用**/
    private final Map<String,Reference<Bitmap>> softMap = Collections.synchronizedMap(new HashMap<String, Reference<Bitmap>>());

    @Override
    public Bitmap get(String key) {
        Bitmap result = null;
        Reference<Bitmap> reference = softMap.get(key);
        if (reference != null){
            result = reference.get();
        }
        return result;
    }

    @Override
    public boolean put(String key, Bitmap value) {
        softMap.put(key, createReference(value));
        return true;
    }

    @Override
    public Bitmap remove(String key) {
        Reference<Bitmap> bmpRef = softMap.remove(key);
        return bmpRef==null ? null :bmpRef.get();
    }

    @Override
    public Collection<String> keys() {
        synchronized (softMap){
            return new HashSet<String>(softMap.keySet());
        }
    }

    @Override
    public void clear() {
        softMap.clear();
    }

    /**根据值创建reference**/
    protected abstract Reference<Bitmap> createReference(Bitmap bitmap);
}
