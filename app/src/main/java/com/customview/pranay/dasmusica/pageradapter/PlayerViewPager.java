package com.customview.pranay.dasmusica.pageradapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.customview.pranay.dasmusica.fragment.PlayerViewPagerFragment;
import com.customview.pranay.dasmusica.model.MusicPOJO;

/**
 * Created by Pranay on 22/04/2017.
 */

public class PlayerViewPager extends FragmentPagerAdapter {

    private Context context;
    private MusicPOJO music;

    public PlayerViewPager(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        music = MusicPOJO.getInstance();
    }

    @Override
    public Fragment getItem(int position) {
        return PlayerViewPagerFragment.newInstance(position,music.getNowPlayingList()!=null?music.getNowPlayingList().size():0);
    }

    @Override
    public int getCount() {
        return music.getNowPlayingList()!=null?music.getNowPlayingList().size():0;
    }
}
