package com.lixue.aibei.universalimageloader.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RemoteViews;

import com.lixue.aibei.universalimageloader.Constants;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloader.UILApplication;
import com.lixue.aibei.universalimageloaderlib.core.DisplayImageOptions;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.listener.SimpleImageLoadingListener;

/**
 * Created by Administrator on 2016/3/29.
 */
public class UILWidgetProvider extends AppWidgetProvider {

    private static DisplayImageOptions displayOptions;

    static {
        displayOptions = DisplayImageOptions.createSimple();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        UILApplication.initImageLoader(context);

        final int widgetCount = appWidgetIds.length;
        for (int i = 0; i < widgetCount; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        ImageSize minImageSize = new ImageSize(70, 70); // 70 - approximate size of ImageView in widget
        UniversalImageLoader.getInstance()
                .loadImage(Constants.IMAGES[0], minImageSize, displayOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {
                        views.setImageViewBitmap(R.id.image_left, loadedImage);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                });
        UniversalImageLoader.getInstance()
                .loadImage(Constants.IMAGES[1], minImageSize, displayOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {
                        views.setImageViewBitmap(R.id.image_right, loadedImage);
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                });
    }
}
