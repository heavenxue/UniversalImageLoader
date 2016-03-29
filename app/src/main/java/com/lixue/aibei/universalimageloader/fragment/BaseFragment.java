package com.lixue.aibei.universalimageloader.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;

/**
 *给Fragment添加菜单项
 * Created by Administrator on 2016/3/29.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 通过onCreateOptionsMenu()，fragment可以为activity的Options Menu提供菜单项。
         * 为了确保这一方法成功实现回调。必须在onCreate()期间调用setHasOptionsMenu()告知Options Menu fragment要添加菜单项。
         * **/
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_clear_disc_cache:
                UniversalImageLoader.getInstance().clearDiskCache();
                return true;
            case R.id.item_clear_memory_cache:
                UniversalImageLoader.getInstance().clearMemoryCache();
                return true;
            default:
                return false;
        }
    }
}
