package com.lixue.aibei.universalimageloader.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.lixue.aibei.universalimageloader.Constants;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloaderlib.core.DisplayImageOptions;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;
import com.lixue.aibei.universalimageloaderlib.core.assist.FailReason;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingProgressListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.SimpleImageLoadingListener;

/**
 * Created by Administrator on 2016/3/29.
 */
public class ImageGridFragment extends AbsListViewBaseFragment {
    public static final int INDEX = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fr_image_grid,container,false);
        listView = (GridView) rootView.findViewById(R.id.grid);
        ((GridView) listView).setAdapter(new GridViewAdapter(getActivity()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });
        return rootView;
    }

    private static class GridViewAdapter extends BaseAdapter{
        private Context mContext;
        private LayoutInflater inflater;
        private String[] IMAGE_URLS = Constants.IMAGES;
        private DisplayImageOptions options;


        public GridViewAdapter(Context context){
            this.mContext = context;
            this.inflater = LayoutInflater.from(mContext);
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .consinderExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public int getCount() {
            return IMAGE_URLS.length;
        }

        @Override
        public Object getItem(int i) {
            return IMAGE_URLS[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null){
                convertView = inflater.inflate(R.layout.item_grid_image,viewGroup,false);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            UniversalImageLoader.getInstance()
                    .displayImage(IMAGE_URLS[position], holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    });

            return convertView;
        }
    }
    private static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }
}
