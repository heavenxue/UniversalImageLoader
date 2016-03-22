package com.lixue.aibei.universalimageloaderlib.core.assist.deque;

import android.annotation.TargetApi;
import android.os.Build;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * 先进先出队列
 * Created by Administrator on 2016/3/22.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class LIFOLinkBlockingDeque<T> extends LinkedBlockingDeque<T> {
    private static final long serialVersionUID = -4114786347960826192L;

    @Override
    public boolean offer(T t) {
        return super.offerFirst(t);
    }

    @Override
    public T remove() {
        return super.removeFirst();
    }
}
