package com.lixue.aibei.universalimageloaderlib.core.listener;

import android.widget.AbsListView;

import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;

/**
 * 对于那些listView或是GridView滑动过程中能够暂停的滑动事件
 * Created by Administrator on 2016/3/29.
 */
public class PauseOnScrollListener implements AbsListView.OnScrollListener{
    private UniversalImageLoader imageLoader;
    private boolean pauseOnScroll;
    private boolean pauseOnFling;
    private final AbsListView.OnScrollListener externalListener;

    public PauseOnScrollListener(UniversalImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
        this(imageLoader, pauseOnScroll, pauseOnFling, null);
    }

    public PauseOnScrollListener(UniversalImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling,
                                 AbsListView.OnScrollListener customListener) {
        this.imageLoader = imageLoader;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        switch (scrollState){
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                if (pauseOnScroll){
                    imageLoader.pause();
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                if (pauseOnFling){
                    imageLoader.pause();
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                imageLoader.resume();
                break;
        }
        if (externalListener != null){
            externalListener.onScrollStateChanged(absListView, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (externalListener != null){
            externalListener.onScroll(absListView,firstVisibleItem,visibleItemCount,totalItemCount);
        }
    }
}
