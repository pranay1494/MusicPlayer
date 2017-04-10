package com.customview.pranay.dasmusica;

import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.customview.pranay.dasmusica.fragment.DashBoardFragment;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.pagetransformers.ZoomOutPageTransformer;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DashBoardFragment.BtnmoreClicked{

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appbar;

    public enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDTATE
    }
    private CollapsingToolbarLayoutState mLayoutState = CollapsingToolbarLayoutState.EXPANDED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appbar = (AppBarLayout) findViewById(R.id.appbar);

        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        /*FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.primaryFrame,new DashBoardFragment(),"DashBoard Fragment");
        transaction.commit();*/

        setupTabs();
        offsetchangeListening();
        tabLayout.setupWithViewPager(viewPager);
    }

    private void offsetchangeListening() {
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0) {
                    if (mLayoutState != CollapsingToolbarLayoutState.EXPANDED) {
                        mLayoutState = CollapsingToolbarLayoutState.EXPANDED;
                        Toast.makeText(MainActivity.this, "expanded", Toast.LENGTH_SHORT).show();
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (mLayoutState != CollapsingToolbarLayoutState.COLLAPSED) {
                        mLayoutState = CollapsingToolbarLayoutState.COLLAPSED;
                        Toast.makeText(MainActivity.this, "collapsed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (mLayoutState != CollapsingToolbarLayoutState.INTERNEDTATE) {
                        if (mLayoutState == CollapsingToolbarLayoutState.COLLAPSED) {
                            Toast.makeText(MainActivity.this, "Intermediate", Toast.LENGTH_SHORT).show();
                        }
                        mLayoutState = CollapsingToolbarLayoutState.INTERNEDTATE;
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.searchview,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupTabs() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new SongsListFragment(),"Songs");
        viewPagerAdapter.add(new SongsListFragment(),"Albums");
        viewPagerAdapter.add(new SongsListFragment(),"Genere");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageTransformer(false,new ZoomOutPageTransformer());
    }

    @Override
    public void btnClicked(boolean clicked) {
        if (clicked){
//            FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            transaction.replace(R.id.primaryFrame,new SongsListFragment(),"SongsList Fragment");
//            transaction.addToBackStack(null);
//            transaction.commit();
           // viewPager.setVisibility(View.VISIBLE);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void add(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}
