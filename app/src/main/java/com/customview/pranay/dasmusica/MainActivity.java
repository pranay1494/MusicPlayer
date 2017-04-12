package com.customview.pranay.dasmusica;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.customview.pranay.dasmusica.fragment.DashBoardFragment;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.interfaces.SongSelected;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.pagetransformers.ZoomOutPageTransformer;
import com.customview.pranay.dasmusica.service.MusicService;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongSelected,SongsListFragment.SongListUpdated {

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AppBarLayout appbar;
    private AnimationSet mShowSet;
    private AnimationSet mHideSet;
    private LinearLayout llAppBarNowPlaying;
    private RelativeLayout rlNextSongForAppBar;
    public MusicService musicService;
    private boolean mServiceBound;
    private Intent playerIntent;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBound = true;
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    @Override
    public void songToPlay(int position) {
        //get data from now playing list

    }

    @Override
    public void nowPlayingListUpdated(boolean listUpdated) {
        if (listUpdated){
            play();
        }
    }

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
        llAppBarNowPlaying = (LinearLayout) findViewById(R.id.llAppBarNowPlaying);
        rlNextSongForAppBar = (RelativeLayout) findViewById(R.id.rlNextSongForAppBar);

        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        setupTabs();
        showAnim();
        hideAnim();
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
                        rlNextSongForAppBar.setVisibility(View.GONE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (mLayoutState != CollapsingToolbarLayoutState.COLLAPSED) {
                        mLayoutState = CollapsingToolbarLayoutState.COLLAPSED;
                        Toast.makeText(MainActivity.this, "collapsed", Toast.LENGTH_SHORT).show();
                        llAppBarNowPlaying.startAnimation(mHideSet);
                        llAppBarNowPlaying.setVisibility(View.GONE);
                        rlNextSongForAppBar.startAnimation(mShowSet);
                        rlNextSongForAppBar.setVisibility(View.VISIBLE);

                    }
                } else {
                    if (mLayoutState != CollapsingToolbarLayoutState.INTERNEDTATE) {
                        if (mLayoutState == CollapsingToolbarLayoutState.COLLAPSED) {
                            Toast.makeText(MainActivity.this, "Intermediate", Toast.LENGTH_SHORT).show();
                            llAppBarNowPlaying.startAnimation(mShowSet);
                            llAppBarNowPlaying.setVisibility(View.VISIBLE);
                            rlNextSongForAppBar.startAnimation(mHideSet);
                            rlNextSongForAppBar.setVisibility(View.GONE);
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
    private void showAnim() {
        mShowSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        TranslateAnimation showAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowSet.addAnimation(alphaAnimation);
        mShowSet.addAnimation(scaleAnimation);
//        mShowSet.addAnimation(showAction);
        mShowSet.setDuration(500);
    }

    private void hideAnim() {
        mHideSet = new AnimationSet(true);
        mHideSet.setInterpolator(new LinearInterpolator());
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        TranslateAnimation  hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHideSet.addAnimation(alphaAnimation);
        mHideSet.addAnimation(hiddenAction);
        mHideSet.setDuration(300);
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

    @Override
    protected void onStart() {
        super.onStart();
        if (playerIntent == null){
            playerIntent = new Intent(MainActivity.this,MusicService.class);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }
    }

    public void play(){
        musicService.playSong();
    }
}
