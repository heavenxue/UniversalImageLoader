package com.lixue.aibei.universalimageloaderlib.cache.memory;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.utils.L;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 有限的高速缓存
 * Created by Administrator on 2016/3/23.
 */
public abstract class LimitedMemoryCache extends BaseMemoryCache {
    private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16; //正常最大缓存空间16M
    private static final int MAX_NORMAL_CACHE_SIZE = MAX_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

    private final int sizeLimit;//限制大小

    private final AtomicInteger cacheSize;//线程安全有好用的自增自减方法

    /**包含对存储对象的强引用。每一个下一个对象都是最后一个。如果硬盘缓存大小将超过
     *限制首先删除对象（但它继续存在# softmap }和并且任何时候都可以被回收**/
    private final List<Bitmap> hardCache = Collections.synchronizedList(new LinkedList<Bitmap>());
    public LimitedMemoryCache(int sizeLimit){
        this.sizeLimit = sizeLimit;
        cacheSize = new AtomicInteger();
        if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
            L.w("You set too large memory cache size (more than %1$d Mb)", MAX_NORMAL_CACHE_SIZE_IN_MB);
        }
    }

    @Override
    public boolean put(String key, Bitmap value) {
        boolean putSuccessfully = false;
        //向hardCache中添加值
        int valueSize = getSize(value);
        int sizeLimit = getSizeLimit();
        int curCacheSize = cacheSize.get();
        if (valueSize < sizeLimit){
            while (curCacheSize + valueSize > sizeLimit){
                Bitmap removedValue = removeNext();
                if (hardCache.remove(removedValue)){
                    curCacheSize = cacheSize.addAndGet(-getSize(removedValue));
                }
            }
            hardCache.add(value);
            cacheSize.addAndGet(valueSize);
            putSuccessfully = true;
        }
        //向软引用中添加值
        super.put(key,value);
        return putSuccessfully;
    }

    @Override
    public Bitmap remove(String key) {
        Bitmap value = super.get(key);
        if (value != null) {
            if (hardCache.remove(value)) {
                cacheSize.addAndGet(-getSize(value));
            }
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        hardCache.clear();
        cacheSize.set(0);
        super.clear();
    }

    protected int getSizeLimit() {
        return sizeLimit;
    }

    protected abstract int getSize(Bitmap bitmap);
    protected abstract Bitmap removeNext();
}
