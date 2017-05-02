package com.customview.pranay.dasmusica.service;

import android.app.Notification;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.R;
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
    private Handler mhandlerForAlbum;
    private boolean nextClicked;
    private boolean previousClicked;
    private boolean vpPrevious;

    public MusicService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        music = MusicPOJO.getInstance();
        initMusicPlayer();
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Awesome App")
                .setContentText("Doing some work...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat){
            playSong();
        }else if (isShuffle){

        }else{
            playNext(false);
        }
    }

    private void sendNewSongInfoToActivity() {
        Message message = Message.obtain();
        Message message2 = Message.obtain();
        message.arg2 = SONG_CHANGED;
        message2.arg2 = SONG_CHANGED;
        if (mhandler!=null) {
            mhandler.sendMessageDelayed(message2, 200);
        }
        if (mhandlerForAlbum!=null) {
            mhandlerForAlbum.sendMessageDelayed(message,200);
        }
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
            //updateProgressBar();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause() {
        player.pause();
    }

    public long getDuration() {
        if (isPlaying()){
            return player.getDuration();
        }
        return 0;
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void repeatSong(boolean repeat){
        isRepeat = repeat;
    }

    public void setHandlerForAlbum(Handler handler) {
        mhandlerForAlbum = handler;
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
        /*player.stop();
        player.release();*/
        return false;
    }

    public void resume(){
        try {
            if (player != null) {
                player.start();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void playSong(){
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        try {
            player.reset();
            player.setDataSource(musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong()).getPath());
            player.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "Sorry This Song Cannot Be Played !!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (player.isPlaying()){
                    try {
                        Thread.sleep(1000);
                        Message msg = Message.obtain();
                        Bundle bundle = getSeekBarData();
                        msg.setData(bundle);
                        if (mhandlerForAlbum!=null)
                            mhandlerForAlbum.sendMessage(msg);
                        else{
                            mhandler.sendMessage(msg);
                        }
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
    }

    public boolean isPlaying(){
        if (player!=null)
            return player.isPlaying();
        else
            return false;
    }

    public void playNext(boolean clicked) {
        nextClicked = clicked;
        if (music.getNowPlayingList()!=null && ((music.getNowPlayingList().size()-1) > music.getIndexOfCurrentSong())){
            music.setIndexOfCurrentSong(music.getIndexOfCurrentSong() + 1);
        }else{
            music.setIndexOfCurrentSong(0);
        }
        if (music!=null && music.getNowPlayingList()!=null &&music.getNowPlayingList().size()>0) {
            music.getNowPlayingList().get(music.getIndexOfCurrentSong()).setPalying(true);
        }
        Log.d("current_index",music.getIndexOfCurrentSong()+"");
        playSong();
    }

    public void playPreviousForVp(boolean clicked) {
        vpPrevious = clicked;
        if (music.getNowPlayingList()!=null && music.getNowPlayingList().size() > 0 && music.getIndexOfCurrentSong()>0 ){
            music.setIndexOfCurrentSong(music.getIndexOfCurrentSong()-1);
        }
        else{
            player.seekTo(0);
        }
        if (music!=null && music.getNowPlayingList()!=null &&music.getNowPlayingList().size()>0) {
            music.getNowPlayingList().get(music.getIndexOfCurrentSong()).setPalying(true);
        }
        playSong();

    }

    public void playPrevious(boolean clicked){
        previousClicked = clicked;
        if (player.getCurrentPosition() <= 5000){
            //play previous song if exists.
            if (music.getNowPlayingList()!=null && music.getNowPlayingList().size() > 0 && music.getIndexOfCurrentSong()>0 ){
                music.setIndexOfCurrentSong(music.getIndexOfCurrentSong()-1);
            }
            else{
                player.seekTo(0);
            }

        }else{
            //play current song from start
            player.seekTo(0);
        }
        if (music!=null && music.getNowPlayingList()!=null &&music.getNowPlayingList().size()>0) {
            music.getNowPlayingList().get(music.getIndexOfCurrentSong()).setPalying(true);
        }
        playSong();
    }
    public void nextSong(){
        player.stop();
        player.reset();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //player.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
