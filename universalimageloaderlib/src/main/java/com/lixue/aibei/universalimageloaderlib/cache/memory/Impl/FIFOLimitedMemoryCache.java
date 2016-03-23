package com.lixue.aibei.universalimageloaderlib.cache.memory.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.LimitedMemoryCache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 有限制的高速内存缓存
 * 当所存储的图片的缓存已经超过了上限，那么会按照先进先出的规则进行缓存清理
 * note:这种缓存是利用强和弱引用存储图片，强引用存储有限制缓存大小的图片，弱引用存储其他缓存的图片
 * Created by Administrator on 2016/3/23.
 */
public class FIFOLimitedMemoryCache extends LimitedMemoryCache {

    private final List<Bitmap> queue = Collections.synchronizedList(new LinkedList<Bitmap>());

    public FIFOLimitedMemoryCache(int sizeLimit) {
        super(sizeLimit);
    }

    @Override
    public boolean put(String key, Bitmap value) {
        if (super.put(key, value)) {
            queue.add(value);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected int getSize(Bitmap bitmap) {
        return queue.size();
    }

    @Override
    protected Bitmap removeNext() {
        return queue.remove(0);
    }

    @Override
    public Bitmap remove(String key) {
        Bitmap value = super.get(key);
        if (value != null) {
            queue.remove(value);
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        queue.clear();
        super.clear();
    }

    @Override
    protected Reference<Bitmap> createReference(Bitmap bitmap) {
        return new WeakReference<Bitmap>(bitmap);
    }
}
