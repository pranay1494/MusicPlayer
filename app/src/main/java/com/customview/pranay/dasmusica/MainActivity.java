package com.customview.pranay.dasmusica;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.customview.pranay.dasmusica.fragment.AlbumsListFragment;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.interfaces.GlideInterface;
import com.customview.pranay.dasmusica.interfaces.SongSelected;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.customview.pranay.dasmusica.pagetransformers.ZoomOutPageTransformer;
import com.customview.pranay.dasmusica.service.MusicService;
import com.customview.pranay.dasmusica.utils.GildeUtils;
import com.customview.pranay.dasmusica.utils.Utilities;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongSelected,SongsListFragment.SongListUpdated,View.OnClickListener ,SeekBar.OnSeekBarChangeListener{

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public AppBarLayout appbar;
    private AnimationSet mShowSet;
    private AnimationSet mHideSet;
    private LinearLayout llAppBarNowPlaying;
    private RelativeLayout rlNextSongForAppBar;
    public MusicService musicService;
    private boolean mServiceBound;
    private Intent playerIntent;
    private TextView tvAppName;
    private TextView tvSongPlayingAppbar;
    private TextView tvNextSongAppbar;
    private TextView tvMusicName;
    public ImageView ivThisSong;
    private ImageView ivNextSong;
    private SeekBar seekBar;
    private SeekBarController seekBarController;
    private MusicPOJO musicObject = MusicPOJO.getInstance();


    /**
     * Made to update seekBar progress with media.
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg2 == MusicService.SONG_CHANGED){
                if (tvMusicName != null){
                    if (musicObject.getNowPlayingList()!=null && musicObject.getNowPlayingList().size()>0) {
                        String name = musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle();
                        tvMusicName.setText(name);
                        setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar,tvNextSongAppbar,ivNextSong,appbar,ivThisSong);
                    }
                }
            }
            if (msg != null) {
                Bundle bundle = msg.getData();
                if (bundle != null && seekBar != null) {
                    seekBar.setProgress(bundle.getInt("PROGRESS"));
                }
            }
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBound = true;
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
            musicService.setHandler(handler);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivThisSong){
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicService.seekToTime(seekBar.getProgress());
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
        tvAppName = (TextView) findViewById(R.id.tvAppName);
        tvMusicName = (TextView) findViewById(R.id.tvMusicName);
        tvNextSongAppbar = (TextView) findViewById(R.id.tvNextSongAppbar);
        tvSongPlayingAppbar = (TextView) findViewById(R.id.tvSongPlayingAppbar);
        ivThisSong = (ImageView) findViewById(R.id.ivThisSong);
        ivNextSong = (ImageView) findViewById(R.id.ivnextSong);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarController = new SeekBarController();
        seekBar.setMax(100);

        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        tvSongPlayingAppbar.setSelected(true);
        tvNextSongAppbar.setSelected(true);
        ivThisSong.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

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
                        tvAppName.startAnimation(mShowSet);
                        tvAppName.setVisibility(View.VISIBLE);
                        rlNextSongForAppBar.setVisibility(View.GONE);
                    }
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    if (mLayoutState != CollapsingToolbarLayoutState.COLLAPSED) {
                        mLayoutState = CollapsingToolbarLayoutState.COLLAPSED;
                        llAppBarNowPlaying.startAnimation(mHideSet);
                        llAppBarNowPlaying.setVisibility(View.GONE);
                        tvAppName.startAnimation(mHideSet);
                        tvAppName.setVisibility(View.GONE);
                        rlNextSongForAppBar.startAnimation(mShowSet);
                        rlNextSongForAppBar.setVisibility(View.VISIBLE);

                    }
                } else {
                    if (mLayoutState != CollapsingToolbarLayoutState.INTERNEDTATE) {
                        if (mLayoutState == CollapsingToolbarLayoutState.COLLAPSED) {
                            llAppBarNowPlaying.startAnimation(mShowSet);
                            llAppBarNowPlaying.setVisibility(View.VISIBLE);
                            rlNextSongForAppBar.startAnimation(mHideSet);
                            rlNextSongForAppBar.setVisibility(View.GONE);
                            tvAppName.startAnimation(mHideSet);
                            tvAppName.setVisibility(View.GONE);
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
        viewPagerAdapter.add(new AlbumsListFragment(),"Albums");
        viewPagerAdapter.add(new SongsListFragment(),"Genre");
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
        if (playerIntent == null && !mServiceBound){
            playerIntent = new Intent(MainActivity.this,MusicService.class);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }
    }

    public void play(){
        if (mServiceBound) {
            //setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar,tvNextSongAppbar,ivNextSong,appbar,ivThisSong);
            musicService.playSong();
        }
    }

    private void setBackgroundRelativeToCurrentSong(TextView thisSong, TextView nextSong, final ImageView ivNext, final View... view) {
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        SongsPojo song = musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong());
        if (thisSong != null){
            thisSong.setText(song.getTitle());
        }
        if (nextSong != null){
            if (musicPOJO.getNowPlayingList()!= null && musicPOJO.getNowPlayingList().size() > musicPOJO.getIndexOfCurrentSong()+ 1){
                nextSong.setText(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong()+1).getTitle());
            }
        }
        byte [] data = null;
        try {
            mmr.setDataSource(song.getPath());
            data = mmr.getEmbeddedPicture();
        }catch (Exception e){
            e.printStackTrace();
        }

        tvSongPlayingAppbar.setText(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong()).getTitle());

        Glide.with(MainActivity.this).load(data).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                BitmapDrawable drawable = (BitmapDrawable) Utilities.createBlurredImageFromBitmap(resource,MainActivity.this,12);
                for (View i:view) {
                    if (i instanceof ImageView){
                        ((ImageView) i).setImageBitmap(resource);
                    }
                    else {
                        i.setBackground(drawable);
                    }
                }
            }
        });

        data = null;
        try {
            if (musicPOJO.getNowPlayingList()!=null) {
                mmr.setDataSource(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong() + 1).getPath());
                data = mmr.getEmbeddedPicture();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        GildeUtils.getBitmapFromGlide(MainActivity.this,data, new GlideInterface() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                ivNext.setImageBitmap(bitmap);
            }
        });

    }
}
