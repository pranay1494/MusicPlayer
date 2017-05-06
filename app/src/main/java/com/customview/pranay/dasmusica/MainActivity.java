package com.customview.pranay.dasmusica;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
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
import com.customview.pranay.dasmusica.interfaces.PageChangeForViewPager;
import com.customview.pranay.dasmusica.interfaces.SongListPopupInterface;
import com.customview.pranay.dasmusica.interfaces.SongSelected;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.customview.pranay.dasmusica.pageradapter.PlayerViewPager;
import com.customview.pranay.dasmusica.pagetransformers.ZoomOutPageTransformer;
import com.customview.pranay.dasmusica.service.MusicService;
import com.customview.pranay.dasmusica.utils.GildeUtils;
import com.customview.pranay.dasmusica.utils.SeekbarTime;
import com.customview.pranay.dasmusica.utils.Utilities;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongListPopupInterface,PageChangeForViewPager, SlidingUpPanelLayout.PanelSlideListener, SongSelected, SongsListFragment.SongListUpdated, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;
    private ImageView ivShuffle;
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
    private TextView tvNextSongToolBar;
    private TextView tvTotalDuration;
    private TextView tvCurrentDuration;
    private TextView tvMusicName;
    public ImageView ivThisSong;
    private ImageView ivNextSong;
    private ImageView ivRepeatSelected;
    private ImageView ivRepeat;
    private ImageView ivCross;
    private ImageView ivNext;
    private ImageView ivPrevious;
    private ImageView ivplayPause;
    private ImageView ivShuffleSelected;
    private ImageView ivEq;
    private SeekBar seekBar;
    private SeekBarController seekBarController;
    private MusicPOJO musicObject = MusicPOJO.getInstance();
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager vpSongPlaying;
    private PlayerViewPager playerViewPager;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private int position =-1;
    /**
     * Made to update seekBar progress with media.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (musicService.isPlaying()) {
                seekBar.setEnabled(true);
                seekBar.setThumb(ContextCompat.getDrawable(MainActivity.this,R.drawable.seekbarthumb_24dp));
            }
            setControlBtnsWhilecreating();
            if (msg.arg1 == 12345){
                handler.removeCallbacks(mUpdateTimeTask);
                handler.postDelayed(mUpdateTimeTask,200);
            }
            if (msg.arg2 == MusicService.SONG_CHANGED) {
                seekBar.setEnabled(true);
                seekBar.setThumb(ContextCompat.getDrawable(MainActivity.this,R.drawable.seekbarthumb_24dp));
                handler.removeCallbacks(mUpdateTimeTask);
                handler.postDelayed(mUpdateTimeTask, 100);
                if (tvMusicName != null) {
                    if (musicObject.getNowPlayingList() != null && musicObject.getNowPlayingList().size() > 0 && position!=musicObject.getIndexOfCurrentSong()) {
                        String name = musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle();
                        tvMusicName.setText(name);
                        setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong,ivEq);
                        if (viewPagerAdapter != null) {
                            /**
                             * to refresh the list if song is changed to indicate currently playing song.
                             */
                            Fragment fragment = viewPagerAdapter.getFragment(0);
                            ((SongsListFragment) fragment).refreshList();
                        }
                        if (position <musicObject.getIndexOfCurrentSong()) {
                            position = musicObject.getIndexOfCurrentSong();
                        }
                    }
                }
                if (mServiceBound && musicService.isPlaying()) {
                    playerViewPager.notifyDataSetChanged();
                    vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
                }
            }
            /*if (msg != null) {
                Bundle bundle = msg.getData();
                if (bundle != null && seekBar != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        seekBar.setProgress(bundle.getInt("PROGRESS"),true);
                    }
                }
            }*/
        }
    };
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mServiceBound && musicService.isPlaying()) {
                long totalDuration = musicService.getDuration();
                long currentDuration = musicService.getCurrentPosition();
                tvTotalDuration.setText(""+SeekbarTime.milliSecondsToTimer(totalDuration));
                tvCurrentDuration.setText(""+SeekbarTime.milliSecondsToTimer(currentDuration));
                int progress = (int) (SeekbarTime.getProgressPercentage(currentDuration, totalDuration));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    seekBar.setProgress(progress, true);
                } else {
                    seekBar.setProgress(progress);
                }
                //handler.removeCallbacks(mUpdateTimeTask);
                handler.postDelayed(this, 100);
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
            if (mServiceBound && !musicService.isPlaying()) {
                seekBar.setEnabled(false);
                seekBar.setThumb(null);
            }
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            setControlBtnsWhilecreating();
            if (mServiceBound && musicService!=null && musicService.isPlaying()) {
                setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong, ivEq);
                tvMusicName.setText(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle());
                playerViewPager.notifyDataSetChanged();
                vpSongPlaying.setAdapter(playerViewPager);
                vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
                handler.removeCallbacks(mUpdateTimeTask);
                handler.postDelayed(mUpdateTimeTask,100);
                musicService.setHandler(handler);
                if (viewPagerAdapter != null) {
                    Fragment fragment = viewPagerAdapter.getFragment(0);
                    ((SongsListFragment) fragment).refreshList();
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };
    private PageChangeListener pageChangeListener;

    @Override
    public void songToPlay(int position) {
        //get data from now playing list

    }

    @Override
    public void nowPlayingListUpdated(boolean listUpdated) {
        if (listUpdated) {
            play();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivThisSong) {
        } else if (v.getId() == R.id.ivplayPause) {
            setControlBtns(true);
        } else if (v.getId() == R.id.ivNext) {
            Log.d("index", "clicked Next");
            musicService.playNext(false);
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
        } else if (v.getId() == R.id.ivPrevious) {
            musicService.playPrevious(true);
//            if (seekBar.getProgress()<5)
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
        }else if (v.getId() == R.id.ivShuffle){
            shuffleSongList();
        }else if (v.getId() == R.id.ivShuffleSelected){
            shuffleSongList();
        }else if (v.getId() == R.id.ivRepeat){
            musicService.repeatSong(true);
            ivRepeatSelected.setVisibility(View.VISIBLE);
            ivRepeat.setVisibility(View.GONE);
        }else if (v.getId() == R.id.ivRepeatSelected){
            musicService.repeatSong(false);
            ivRepeatSelected.setVisibility(View.GONE);
            ivRepeat.setVisibility(View.VISIBLE);
        }
        seekBar.setEnabled(true);
        seekBar.setThumb(ContextCompat.getDrawable(MainActivity.this,R.drawable.seekbarthumb_24dp));
        handler.removeCallbacks(mUpdateTimeTask);
        handler.postDelayed(mUpdateTimeTask, 100);

    }

    private void repeatSong() {
        if (ivRepeat.isShown()){
            if (musicObject.getNowPlayingList()!=null && musicObject.getNowPlayingList().size() > musicObject.getIndexOfCurrentSong()) {
                ArrayList<SongsPojo> templist = musicObject.getTempListForRepeat();
                for (SongsPojo song : musicObject.getNowPlayingList()) {
                    templist.add(song);
                }
                musicObject.setTempIndexForRepeat(musicObject.getIndexOfCurrentSong());
                SongsPojo song = musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong());
                musicObject.getNowPlayingList().clear();
                musicObject.getNowPlayingList().add(song);
                musicObject.setIndexOfCurrentSong(0);
                musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).setPalying(true);
                vpSongPlaying.setAdapter(playerViewPager);
                vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            }

            ivRepeatSelected.setVisibility(View.VISIBLE);
            ivRepeat.setVisibility(View.GONE);
        }
        else if (ivRepeatSelected.isShown()){
            musicObject.getNowPlayingList().clear();
            for (SongsPojo song : musicObject.getTempListForRepeat()) {
                musicObject.getNowPlayingList().add(song);
            }
            musicObject.setIndexOfCurrentSong(musicObject.getTempIndexForRepeat());
            musicObject.setTempIndexForRepeat(0);
            musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).setPalying(true);
            vpSongPlaying.setAdapter(playerViewPager);
            vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            ivRepeatSelected.setVisibility(View.GONE);
            ivRepeat.setVisibility(View.VISIBLE);

        }
        setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong,ivEq);

    }

    private void shuffleSongList() {
        SongsPojo song;
        if (ivShuffle.isShown()){
            if (musicObject.getNowPlayingList()!=null &&musicObject.getNowPlayingList().size()>musicObject.getIndexOfCurrentSong()) {
                musicObject.setTempNowPlayingList(musicObject.getNowPlayingList());
                song = musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong());
                musicObject.getNowPlayingList().remove(musicObject.getIndexOfCurrentSong());
                musicObject.setTempIndexOfCurrentSong(musicObject.getIndexOfCurrentSong());
                Collections.shuffle(musicObject.getNowPlayingList());
                musicObject.getNowPlayingList().add(0,song);
                musicObject.setIndexOfCurrentSong(0);
                musicObject.getNowPlayingList().get(0).setPalying(true);
                vpSongPlaying.setAdapter(playerViewPager);
                vpSongPlaying.setCurrentItem(0);
            }
            ivShuffle.setVisibility(View.GONE);
            ivShuffleSelected.setVisibility(View.VISIBLE);
        }else if(ivShuffleSelected.isShown()){
            int index = 0;
            if (null != musicObject.getTempNowPlayingList()){
                if (musicObject.getTempNowPlayingList().contains(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()))){
                    index = musicObject.getTempNowPlayingList().indexOf(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()));
                }
                musicObject.getNowPlayingList().clear();
                musicObject.setNowPlayingList(musicObject.getTempNowPlayingList());
                musicObject.setIndexOfCurrentSong(index);
                vpSongPlaying.setAdapter(playerViewPager);
                vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            }else{
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
            ivShuffle.setVisibility(View.VISIBLE);
            ivShuffleSelected.setVisibility(View.GONE);
        }
        setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong,ivEq);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        musicService.seekToTime(seekBar.getProgress());
        handler.postDelayed(mUpdateTimeTask,100);
    }

    /**
     * @param panel
     * @param slideOffset
     */
    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            ivEq.setVisibility(View.GONE);
            ivCross.setVisibility(View.VISIBLE);
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            ivEq.setVisibility(View.VISIBLE);
            ivCross.setVisibility(View.GONE);
        }else if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {
            if (!ivEq.isShown()) {
                ivEq.setVisibility(View.VISIBLE);
                ivCross.setVisibility(View.GONE);
            }else{
                ivEq.setVisibility(View.GONE);
                ivCross.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void selectedPage(final int position) {
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (position == musicObject.getIndexOfCurrentSong()) {
                        //do nothing
                    } else {
                        if (position > musicObject.getIndexOfCurrentSong()) {
                            Log.d("index", position + "");
                            musicService.playNext(true);
                        } else {
                            musicService.playPreviousForVp(true);
                        }
                    }

            }
        },50);*/
        if (position == musicObject.getIndexOfCurrentSong()) {
            //do nothing
        } else {
            if (position > musicObject.getIndexOfCurrentSong()) {
                Log.d("index", position + "");
                musicService.playNext(false);
            } else {
                musicService.playPreviousForVp(true);
            }
            seekBar.setEnabled(true);
            seekBar.setThumb(ContextCompat.getDrawable(MainActivity.this,R.drawable.seekbarthumb_24dp));
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public void popupItemClicked(int position) {
        String text = "";
        if (position == 1 || position == 2){
            if (position == 1)
                text = "Selected song wll play next";
            else
                text ="Song added to queue";
            vpSongPlaying.setAdapter(playerViewPager);
            vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
        }
        Snackbar snackbar = Snackbar.make((CoordinatorLayout)findViewById(R.id.rlMain), text, Snackbar.LENGTH_SHORT);
        snackbar.show();
        setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong,ivEq);
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
        ivEq = (ImageView) findViewById(R.id.ivEq);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpSongPlaying = (ViewPager) findViewById(R.id.vpPlayer);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        llAppBarNowPlaying = (LinearLayout) findViewById(R.id.llAppBarNowPlaying);
        rlNextSongForAppBar = (RelativeLayout) findViewById(R.id.rlNextSongForAppBar);
        tvAppName = (TextView) findViewById(R.id.tvAppName);
        tvMusicName = (TextView) findViewById(R.id.tvMusicName);
        tvTotalDuration = (TextView) findViewById(R.id.tvTotalDuration);
        tvCurrentDuration = (TextView) findViewById(R.id.tvCurrentDuration);
        tvNextSongAppbar = (TextView) findViewById(R.id.tvNextSongAppbar);
        tvNextSongToolBar = (TextView) findViewById(R.id.tvNextSongToolBar);
        tvSongPlayingAppbar = (TextView) findViewById(R.id.tvSongPlayingAppbar);
        ivThisSong = (ImageView) findViewById(R.id.ivThisSong);
        ivNextSong = (ImageView) findViewById(R.id.ivnextSong);
        ivplayPause = (ImageView) findViewById(R.id.ivplayPause);
        ivRepeatSelected = (ImageView) findViewById(R.id.ivRepeatSelected);
        ivRepeat = (ImageView) findViewById(R.id.ivRepeat);
        ivCross = (ImageView) findViewById(R.id.ivCross);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivNext = (ImageView) findViewById(R.id.ivNext);
        ivShuffle = (ImageView) findViewById(R.id.ivShuffle);
        ivShuffleSelected = (ImageView) findViewById(R.id.ivShuffleSelected);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBarController = new SeekBarController();
        seekBar.setMax(100);

        slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));
        slidingUpPanel.addPanelSlideListener(this);
        tvMusicName.setSelected(true);
        tvNextSongToolBar.setSelected(true);
        tvSongPlayingAppbar.setSelected(true);
        tvNextSongAppbar.setSelected(true);
        ivThisSong.setOnClickListener(this);
        ivRepeatSelected.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivplayPause.setOnClickListener(this);
        ivShuffleSelected.setOnClickListener(this);

        ivNext.setOnClickListener(this);
        ivShuffle.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);

        playerViewPager = new PlayerViewPager(MainActivity.this, getSupportFragmentManager());
        setupTabs();
        showAnim();
        hideAnim();
        offsetchangeListening();
        tabLayout.setupWithViewPager(viewPager);
        pageChangeListener = new PageChangeListener(MainActivity.this, this);
        vpSongPlaying.addOnPageChangeListener(pageChangeListener);
    }

    private void setControlBtnsWhilecreating() {
        if (mServiceBound && musicService.isPlaying()) {
            ivplayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
        } else {
            ivplayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
        }
    }

    private void setControlBtns(boolean changeMediaPlayback) {
        if (mServiceBound && musicService.isPlaying()) {
            //pause the music
            ivplayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
            if (mServiceBound && changeMediaPlayback) {
                musicService.pause();
            }
        } else {
            ivplayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
            if (mServiceBound && changeMediaPlayback) {
                musicService.resume();
            }
        }
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
        menuInflater.inflate(R.menu.searchview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupTabs() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new SongsListFragment(), "Songs");
        viewPagerAdapter.add(new AlbumsListFragment(), "Albums");
        viewPagerAdapter.add(new SongsListFragment(), "Playlists");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
        vpSongPlaying.setAdapter(playerViewPager);
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
        TranslateAnimation hiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f);
        mHideSet.addAnimation(alphaAnimation);
        mHideSet.addAnimation(hiddenAction);
        mHideSet.setDuration(300);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
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

        public void add(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        public Fragment getFragment(int position) {
            if (fragmentList != null && fragmentList.size() > position)
                return fragmentList.get(position);

            return null;

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playerIntent == null && !mServiceBound && serviceConnection != null) {
            playerIntent = new Intent(MainActivity.this, MusicService.class);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceBound && serviceConnection != null) {
            unbindService(serviceConnection);
            //stopService(playerIntent);
            musicService.setHandler(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound && serviceConnection != null) {
            // unbindService(serviceConnection);
            //stopService(playerIntent);
            handler.removeCallbacks(mUpdateTimeTask);
        }
    }

    public void play() {
        if (mServiceBound) {
            //setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar,tvNextSongAppbar,ivNextSong,appbar,ivThisSong);
            musicService.playSong();
            playerViewPager.notifyDataSetChanged();
            seekBar.setEnabled(true);
            seekBar.setThumb(ContextCompat.getDrawable(MainActivity.this,R.drawable.seekbarthumb_24dp));
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
            ivShuffle.setVisibility(View.VISIBLE);
            ivShuffleSelected.setVisibility(View.GONE);
            setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong, ivEq);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mServiceBound && musicService!=null && musicService.isPlaying()) {
            setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar, tvNextSongAppbar, ivNextSong, tvNextSongToolBar, appbar, ivThisSong, ivEq);
            tvMusicName.setText(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle());
            playerViewPager.notifyDataSetChanged();
            vpSongPlaying.setAdapter(playerViewPager);
            vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask,100);
            musicService.setHandler(handler);
            if (viewPagerAdapter != null) {
                Fragment fragment = viewPagerAdapter.getFragment(0);
                ((SongsListFragment) fragment).refreshList();
            }
        }
    }

    private void setBackgroundRelativeToCurrentSong(TextView thisSong, TextView nextSong, final ImageView ivNext, TextView tvNextSongToolBar, final View... view) {
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        SongsPojo song = musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong());
        if (thisSong != null) {
            thisSong.setText(song.getTitle());
        }
        if (nextSong != null) {
            if (musicPOJO.getNowPlayingList() != null && musicPOJO.getNowPlayingList().size() > musicPOJO.getIndexOfCurrentSong() + 1) {
                nextSong.setText(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong() + 1).getTitle());
            }
        }
        if (tvNextSongToolBar != null) {
            if (musicPOJO.getNowPlayingList() != null && musicPOJO.getNowPlayingList().size() > musicPOJO.getIndexOfCurrentSong() + 1) {
                tvNextSongToolBar.setText(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong() + 1).getTitle());
            }
        }
        byte[] data = null;
        try {
            mmr.setDataSource(song.getPath());
            data = mmr.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvSongPlayingAppbar.setText(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong()).getTitle());

        Glide.with(MainActivity.this).load(data).asBitmap().centerCrop().placeholder(R.drawable.nowplaying).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (resource!=null) {
                    BitmapDrawable drawable = (BitmapDrawable) Utilities.createBlurredImageFromBitmap(resource, MainActivity.this, 12);
                    for (View i : view) {
                        if (i instanceof ImageView) {
                            ((ImageView) i).setImageBitmap(resource);
                        } else {
                            i.setBackground(drawable);
                        }
                    }
                }
            }
        });

        data = null;
        try {
            if (musicPOJO.getNowPlayingList() != null) {
                mmr.setDataSource(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong() + 1).getPath());
                data = mmr.getEmbeddedPicture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        GildeUtils.getBitmapFromGlide(MainActivity.this, data, new GlideInterface() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                ivNext.setImageBitmap(bitmap);
            }
        });
    }
}
