package com.customview.pranay.dasmusica;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.customview.pranay.dasmusica.interfaces.PageChangeForViewPager;
import com.customview.pranay.dasmusica.model.MusicPOJO;

/**
 * Created by Pranay on 23/04/2017.
 */

public class PageChangeListener implements ViewPager.OnPageChangeListener {


    private Context context;
    PageChangeForViewPager listener;

    public PageChangeListener(Context context,PageChangeForViewPager listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        listener.selectedPage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
