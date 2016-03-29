package com.lixue.aibei.universalimageloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lixue.aibei.universalimageloader.Constants;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloader.fragment.ImageGalleryFragment;
import com.lixue.aibei.universalimageloader.fragment.ImageGridFragment;
import com.lixue.aibei.universalimageloader.fragment.ImageListFragment;
import com.lixue.aibei.universalimageloader.fragment.ImagePagerFragment;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;
import com.lixue.aibei.universalimageloaderlib.utils.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
        if (!testImageOnSdCard.exists()) {
            copyTestImageToSdCard(testImageOnSdCard);
        }
    }

    public void onImageListClick(View view) {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageListFragment.INDEX);
        startActivity(intent);
    }

    public void onImageGridClick(View view) {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGridFragment.INDEX);
        startActivity(intent);
    }

    public void onImagePagerClick(View view) {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
        startActivity(intent);
    }

    public void onImageGalleryClick(View view) {
        Intent intent = new Intent(this, SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImageGalleryFragment.INDEX);
        startActivity(intent);
    }

    public void onFragmentsClick(View view) {
        Intent intent = new Intent(this, ComplexImageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        UniversalImageLoader.getInstance().stop();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_clear_memory_cache:
                UniversalImageLoader.getInstance().clearMemoryCache();
                return true;
            case R.id.item_clear_disc_cache:
                UniversalImageLoader.getInstance().clearDiskCache();
                return true;
            default:
                return false;
        }
    }

    private void copyTestImageToSdCard(final File testImageOnSdCard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = getAssets().open(TEST_FILE_NAME);
                    FileOutputStream fos = new FileOutputStream(testImageOnSdCard);
                    byte[] buffer = new byte[8192];
                    int read;
                    try {
                        while ((read = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, read);
                        }
                    } finally {
                        fos.flush();
                        fos.close();
                        is.close();
                    }
                } catch (IOException e) {
                    L.w("Can't copy test image onto SD card");
                }
            }
        }).start();
    }
}
