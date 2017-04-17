package com.customview.pranay.dasmusica.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.widget.SeekBar;

import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.utils.SeekbarTime;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Pranay on 11/04/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    public static final int SONG_CHANGED = 121;
    IBinder binder = new MyBinder();
    private MediaPlayer player;
    private boolean isShuffle;
    private boolean isRepeat;
    private MusicPOJO music;
    private Handler mhandler;
    private boolean updateSeekBar;

    public MusicService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        music = MusicPOJO.getInstance();
        initMusicPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat){

        }else if (isShuffle){

        }else{
            if (music.getNowPlayingList()!=null && ((music.getNowPlayingList().size()-1) > music.getIndexOfCurrentSong())){
                music.setIndexOfCurrentSong(music.getIndexOfCurrentSong() + 1);
                sendNewSongInfoToActivity();
            }else{
                music.setIndexOfCurrentSong(0);
            }
            playSong();
        }
    }

    private void sendNewSongInfoToActivity() {
        Message message = Message.obtain();
        message.arg2 = SONG_CHANGED;
        mhandler.sendMessage(message);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        try {
            mp.start();
            sendNewSongInfoToActivity();
            updateProgressBar();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class MyBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }
    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        try {
            player.reset();
            player.setDataSource(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong()).getPath());
            player.prepareAsync();
//            mhandler.sendMessageDelayed(msg,1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player.isPlaying()){
                    try {
                       // Thread.sleep(1000);
                        Message msg = Message.obtain();
                        Bundle bundle = getSeekBarData();
                        msg.setData(bundle);
                        mhandler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private Bundle getSeekBarData() {
        Bundle bundle = new Bundle();
        long totalDuration = player.getDuration();
        long currentDuration = player.getCurrentPosition();
        int progress = SeekbarTime.getProgressPercentage(currentDuration,totalDuration);
        bundle.putInt("PROGRESS",progress);
        return bundle;
    }

    public void setHandler(Handler handler){
        mhandler = handler;
    }

    public void seekToTime(int progress){
        int totalDuration = player.getDuration();
        int currentPosition = SeekbarTime.progressToTimer(progress, totalDuration);
        player.seekTo(currentPosition);
        updateProgressBar();
    }

    public boolean isPlaying(){
        return player.isPlaying();
    }
}
