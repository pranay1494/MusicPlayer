package com.customview.pranay.dasmusica;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.adapter.AlbumDetailsAdapter;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.interfaces.PageChangeForViewPager;
import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.customview.pranay.dasmusica.pageradapter.PlayerViewPager;
import com.customview.pranay.dasmusica.service.MusicService;
import com.customview.pranay.dasmusica.utils.SeekbarTime;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Pranay on 30/04/2017.
 */

public class AlbumActivity extends AppCompatActivity implements AlbumDetailsAdapter.AlbumSongSelected,View.OnClickListener,SlidingUpPanelLayout.PanelSlideListener,PageChangeForViewPager,SeekBar.OnSeekBarChangeListener {

    private SlidingUpPanelLayout slidingUpPanel;
    private AlbumDetailsAdapter detailsAdapter;
    int position;
    private MusicPOJO music = MusicPOJO.getInstance();
    private ArrayList<SongsPojo> songs = new ArrayList<>();
    private RecyclerView rvAlbumDetails;
    private ImageView albumArt;
    private AlbumPojo album;
    private ViewPager vpSongPlaying;
    private PlayerViewPager playerViewPager;
    private boolean mServiceBound;
    public MusicService musicService;
    private SeekBar seekBar;
    private TextView tvTotalDuration;
    private TextView tvCurrentDuration;
    private ImageView ivNext;
    private ImageView ivPrevious;
    private TextView tvMusicName;
    private Intent playerIntent;
    private ImageView ivShuffle;
    private ImageView ivShuffleSelected;
    private ImageView ivplayPause;
    private ImageView ivRepeatSelected;
    private ImageView ivRepeat;
    private ImageView ivCross;
    private MusicPOJO musicObject = MusicPOJO.getInstance();
    private PageChangeListener pageChangeListener;




    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (mServiceBound && musicService.isPlaying()) {
                long totalDuration = musicService.getDuration();
                long currentDuration = musicService.getCurrentPosition();
                tvTotalDuration.setText(""+ SeekbarTime.milliSecondsToTimer(totalDuration));
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


    /**
     * Made to update seekBar progress with media.
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            /*if (musicService.isPlaying()) {
                seekBar.setEnabled(true);
                seekBar.setThumb(ContextCompat.getDrawable(AlbumActivity.this,R.drawable.seekbarthumb_24dp));
            }*/
            setControlBtnsWhilecreating();
            if (msg.arg2 == MusicService.SONG_CHANGED) {
                seekBar.setEnabled(true);
                seekBar.setThumb(ContextCompat.getDrawable(AlbumActivity.this,R.drawable.seekbarthumb_24dp));
                handler.removeCallbacks(mUpdateTimeTask);
                handler.postDelayed(mUpdateTimeTask, 100);
                if (tvMusicName != null) {
                    if (musicObject.getNowPlayingList() != null && musicObject.getNowPlayingList().size() > 0 && position!=musicObject.getIndexOfCurrentSong()) {
                        String name = musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle();
                        tvMusicName.setText(name);
                        if (position <musicObject.getIndexOfCurrentSong()) {
                            position = musicObject.getIndexOfCurrentSong();
                        }
                    }
                }
                if (mServiceBound && musicService.isPlaying())
                    vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBound = true;
            MusicService.MyBinder binder = (MusicService.MyBinder) service;
            musicService = binder.getService();
            musicService.setHandlerForAlbum(handler);
            if (mServiceBound && !musicService.isPlaying()) {
                seekBar.setEnabled(false);
                seekBar.setThumb(null);

            }
            if (tvMusicName!=null && vpSongPlaying!=null && musicObject.getNowPlayingList()!=null && musicObject.getNowPlayingList().size()>musicObject.getIndexOfCurrentSong()) {
                tvMusicName.setText(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle());
                vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            }
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            setControlBtnsWhilecreating();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));
        tvTotalDuration = (TextView) findViewById(R.id.tvTotalDuration);
        tvCurrentDuration = (TextView) findViewById(R.id.tvCurrentDuration);
        tvMusicName = (TextView) findViewById(R.id.tvMusicName);
        ivShuffle = (ImageView) findViewById(R.id.ivShuffle);
        ivShuffleSelected = (ImageView) findViewById(R.id.ivShuffleSelected);
        ivplayPause = (ImageView) findViewById(R.id.ivplayPause);
        ivRepeatSelected = (ImageView) findViewById(R.id.ivRepeatSelected);
        ivRepeat = (ImageView) findViewById(R.id.ivRepeat);
        ivCross = (ImageView) findViewById(R.id.ivCross);
        vpSongPlaying = (ViewPager) findViewById(R.id.vpPlayer);
        ivPrevious = (ImageView) findViewById(R.id.ivPrevious);
        ivNext = (ImageView) findViewById(R.id.ivNext);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);


        rvAlbumDetails = (RecyclerView) findViewById(R.id.rvAlbumDetails);
        albumArt = (ImageView) findViewById(R.id.albumArt);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        ivRepeatSelected.setOnClickListener(this);
        ivRepeat.setOnClickListener(this);
        ivplayPause.setOnClickListener(this);
        ivShuffleSelected.setOnClickListener(this);
        ivPrevious.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        ivShuffle.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);


        playerViewPager = new PlayerViewPager(AlbumActivity.this, getSupportFragmentManager());
        vpSongPlaying.setAdapter(playerViewPager);
        pageChangeListener = new PageChangeListener(AlbumActivity.this, this);
        vpSongPlaying.addOnPageChangeListener(pageChangeListener);

        if (getIntent().getExtras()!=null){
            position = getIntent().getIntExtra("POSITION",0);
            for (SongsPojo song : music.getAlbums().get(position).getAlbumSongsList()) {
                songs.add(song);
            }
            album = music.getAlbums().get(position);
            getAlbumArtWithoutLibrary(album.getAlbumArtUri(),albumArt);
        }
        detailsAdapter = new AlbumDetailsAdapter(this,songs,position,this);
        rvAlbumDetails.setLayoutManager(manager);
        rvAlbumDetails.setAdapter(detailsAdapter);
    }

    private void getAlbumArtWithoutLibrary(String albumArtUri, ImageView albumArt) {
        if(!TextUtils.isEmpty(albumArtUri)) {
            Glide.with(this).load(albumArtUri).centerCrop().placeholder(R.drawable.nowplaying).into(albumArt);
        }
        else {
            Glide.with(this).load(R.drawable.guitar).centerCrop().into(albumArt);
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

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
    }

    public void play() {
        if (mServiceBound) {
            //setBackgroundRelativeToCurrentSong(tvSongPlayingAppbar,tvNextSongAppbar,ivNextSong,appbar,ivThisSong);
            musicService.playSong();
            playerViewPager.notifyDataSetChanged();
            seekBar.setEnabled(true);
            seekBar.setThumb(ContextCompat.getDrawable(AlbumActivity.this,R.drawable.seekbarthumb_24dp));
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            vpSongPlaying.setCurrentItem(musicObject.getIndexOfCurrentSong());
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
            ivShuffle.setVisibility(View.VISIBLE);
            ivShuffleSelected.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceBound && serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (playerIntent == null && !mServiceBound && serviceConnection != null) {
            playerIntent = new Intent(AlbumActivity.this, MusicService.class);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);
        }
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
    @Override
    public void selectedPage(final int position) {
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
            seekBar.setThumb(ContextCompat.getDrawable(AlbumActivity.this,R.drawable.seekbarthumb_24dp));
            handler.removeCallbacks(mUpdateTimeTask);
            handler.postDelayed(mUpdateTimeTask, 100);
            ivRepeat.setVisibility(View.VISIBLE);
            ivRepeatSelected.setVisibility(View.GONE);
        }
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
        seekBar.setThumb(ContextCompat.getDrawable(AlbumActivity.this,R.drawable.seekbarthumb_24dp));
        handler.removeCallbacks(mUpdateTimeTask);
        handler.postDelayed(mUpdateTimeTask, 100);

    }

    @Override
    public void songSelected(int position) {
        musicObject.getNowPlayingList().clear();
        for (SongsPojo songsPojo : songs) {
            musicObject.getNowPlayingList().add(songsPojo);
        }
        musicObject.setIndexOfCurrentSong(position);
        playerViewPager.notifyDataSetChanged();
        vpSongPlaying.setAdapter(playerViewPager);
        vpSongPlaying.setCurrentItem(position);
        if (musicObject.getNowPlayingList()!=null && musicObject.getNowPlayingList().size()>0)
            tvMusicName.setText(musicObject.getNowPlayingList().get(musicObject.getIndexOfCurrentSong()).getTitle());
        play();
        handler.postDelayed(mUpdateTimeTask,200);
    }
}
