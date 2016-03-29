package com.lixue.aibei.universalimageloader.fragment;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.lixue.aibei.universalimageloader.Constants;
import com.lixue.aibei.universalimageloader.R;
import com.lixue.aibei.universalimageloader.activity.SimpleImageActivity;
import com.lixue.aibei.universalimageloaderlib.core.UniversalImageLoader;
import com.lixue.aibei.universalimageloaderlib.core.listener.PauseOnScrollListener;

/**
 * Created by Administrator on 2016/3/29.
 */
public abstract class AbsListViewBaseFragment extends BaseFragment{
    protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
    protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";

    protected AbsListView listView;

    protected boolean pauseOnScroll = false;
    protected boolean pauseOnFling = true;

    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem pauseOnScrollItem = menu.findItem(R.id.item_pause_on_scroll);
        pauseOnScrollItem.setVisible(true);
        pauseOnScrollItem.setChecked(pauseOnScroll);

        MenuItem pauseOnFlingItem = menu.findItem(R.id.item_pause_on_fling);
        pauseOnFlingItem.setVisible(true);
        pauseOnFlingItem.setChecked(pauseOnFling);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_pause_on_scroll:
                pauseOnScroll = !pauseOnScroll;
                item.setChecked(pauseOnScroll);
                applyScrollListener();
                return true;
            case R.id.item_pause_on_fling:
                pauseOnFling = !pauseOnFling;
                item.setChecked(pauseOnFling);
                applyScrollListener();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**absListview的滑动事件**/
    private void applyScrollListener(){
        listView.setOnScrollListener(new PauseOnScrollListener(UniversalImageLoader.getInstance(), pauseOnScroll, pauseOnFling));
    }

    protected void startImagePagerActivity(int position) {
        Intent intent = new Intent(getActivity(), SimpleImageActivity.class);
        intent.putExtra(Constants.Extra.FRAGMENT_INDEX, ImagePagerFragment.INDEX);
        intent.putExtra(Constants.Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }
}
