package com.lixue.aibei.universalimageloader.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lixue.aibei.slidingtitlebarlib.SlidingTabScript;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloader.fragment.ImageGridFragment;
import com.lixue.aibei.universalimageloader.fragment.ImageListFragment;

public class ComplexImageActivity extends FragmentActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    private SlidingTabScript slidingTabScript;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_image);
        int pagerPosition = savedInstanceState == null ? 0 : savedInstanceState.getInt(STATE_POSITION);
        slidingTabScript = (SlidingTabScript) findViewById(R.id.slidingTabScript);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(pagerPosition);
        slidingTabScript.setViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, viewPager.getCurrentItem());
    }

    private static class ImagePagerAdapter extends FragmentPagerAdapter{

        Fragment listFragment;
        Fragment gridFragment;

        public ImagePagerAdapter(FragmentManager fm){
            super(fm);
            listFragment = new ImageListFragment();
            gridFragment = new ImageGridFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return listFragment;
                case 1:
                    return gridFragment;
                default:
                    return null;
            }
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }
}
