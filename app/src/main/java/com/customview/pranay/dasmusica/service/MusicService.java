package com.customview.pranay.dasmusica.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.interfaces.GlideInterface;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.utils.GildeUtils;
import com.customview.pranay.dasmusica.utils.SeekbarTime;

import java.io.ByteArrayOutputStream;

/**
 * Created by Pranay on 11/04/2017.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener{

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
    private PhoneStateListener phoneStateListener;
    boolean isPausedDueToCall;
    private TelephonyManager telephonyManager;
    private MediaSessionCompat mediaSession;
    private MediaMetadataRetriever mmr;
    private MediaControllerCompat.TransportControls transportControls;
    private static final int NOTIFICATION_ID = 101;
    public static final String ACTION_PLAY = "com.customview.pranay.dasmusica.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.customview.pranay.dasmusica.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.customview.pranay.dasmusica.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.customview.pranay.dasmusica.ACTION_NEXT";
    public static final String ACTION_STOP = "com.customview.pranay.dasmusica.ACTION_STOP";
    private AudioManager audioManager;
    private Context context;
    NotificationCompat.Builder notificationBuilder;
    private boolean isLockscreenUpdated;

    public MusicService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        music = MusicPOJO.getInstance();
        initMusicPlayer();
        context = this;
        mmr = new MediaMetadataRetriever();
        mediaSession = new  MediaSessionCompat(this,"MusicService");
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                stopForeground(true);
                stopSelf();
            }
        });
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        transportControls = mediaSession.getController().getTransportControls();
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
            mhandler.sendMessageDelayed(message2, 300);
        }
        if (mhandlerForAlbum!=null) {
            mhandlerForAlbum.sendMessageDelayed(message,300);
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
            mediaSession.setActive(true);
            sendNewSongInfoToActivity();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);

            //updateProgressBar();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateMetaData() {
        final int currentIndex = music.getIndexOfCurrentSong();
        byte[] data = null;
        try {
            mmr.setDataSource(music.getNowPlayingList().get(currentIndex).getPath());
            data = mmr.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (music.getNowPlayingList()!=null && music.getNowPlayingList().size()>music.getIndexOfCurrentSong()) {

            GildeUtils.getBitmapFromGlide(this,data , new GlideInterface() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    if(bitmap==null) {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nowplaying);
                    }
                            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap)
                            .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,bitmap)
                            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getNowPlayingList().get(currentIndex).getArtist())
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getNowPlayingList().get(currentIndex).getAlbum())
                            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getNowPlayingList().get(currentIndex).getTitle())
                            .build());
                    PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
                    isLockscreenUpdated = true;
                    stateBuilder.setActiveQueueItemId(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getId());

                    long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

                    stateBuilder.setActions(actions);
                    stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f);
                    mediaSession.setPlaybackState(stateBuilder.build());
                }


            });
            if (!isLockscreenUpdated){
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nowplaying);

                mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,bitmap)
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON,bitmap)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getNowPlayingList().get(currentIndex).getArtist())
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, music.getNowPlayingList().get(currentIndex).getAlbum())
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, music.getNowPlayingList().get(currentIndex).getTitle())
                        .build());
                PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
                isLockscreenUpdated = true;
                stateBuilder.setActiveQueueItemId(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getId());

                long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_STOP | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;

                stateBuilder.setActions(actions);
                stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, 0, 1.0f);
                mediaSession.setPlaybackState(stateBuilder.build());
            }
            isLockscreenUpdated = false;
        }
    }

    public void pause() {
        player.pause();
        buildNotification(PlaybackStatus.PAUSED);
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
                buildNotification(PlaybackStatus.PLAYING);
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
            try{
                startForeground(NOTIFICATION_ID,notificationBuilder.build());
            }catch(Exception e){
                e.printStackTrace();
            }
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
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        removeNotification();
        removeAudioFocus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (player != null && isPlaying()) {
                            pause();
                            isPausedDueToCall = true;
                        }

                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (player != null) {
                            if (isPausedDueToCall) {
                                isPausedDueToCall = false;
                                resume();
                            }
                            Message message = Message.obtain();
                            message.arg1 = 12345;
                            if (mhandler!=null)
                            mhandler.sendMessageDelayed(message,200);
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        handleIncomingActions(intent);
        if (!requestAudioFocus()) {
            stopSelf();
        }
        return START_STICKY;
    }

    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = R.drawable.ic_pause_black_24dp;//needs to be initialized
        PendingIntent play_pauseAction = null;
        Intent notificationIntent = new Intent(this, MainActivity.class);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        mediaSession.setSessionActivity(pendingIntent);

        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_pause_black_24dp;
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.ic_play_arrow_black_24dp;
            play_pauseAction = playbackAction(0);
        }
        byte[] data = null;
        try {
            mmr.setDataSource(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getPath());
            data = mmr.getEmbeddedPicture();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final int finalNotificationAction = notificationAction;
        final PendingIntent finalPlay_pauseAction = play_pauseAction;
        if (data == null){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.nowplaying);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            data = stream.toByteArray();
        }
        GildeUtils.getBitmapFromGlide(this, data, new GlideInterface() {
            @Override
            public void getBitmap(Bitmap bitmap) {
                try {
                    notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                            .setShowWhen(false)
                            .setStyle(new NotificationCompat.MediaStyle()
                                    .setMediaSession(mediaSession.getSessionToken())
                                    .setShowActionsInCompactView(0, 1, 2))
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setLargeIcon(bitmap)
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(android.R.drawable.stat_sys_headset)
                            .setContentText(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getArtist())
                            .setContentTitle(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getTitle())
                            .setContentInfo(music.getNowPlayingList().get(music.getIndexOfCurrentSong()).getTitle())
                            .addAction(R.drawable.ic_skip_previous_black_24dp, "previous", playbackAction(3))
                            .addAction(finalNotificationAction, "pause", finalPlay_pauseAction)
                            .addAction(R.drawable.ic_skip_next_black_24dp, "next", playbackAction(2));

                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        startForeground(NOTIFICATION_ID,notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public enum PlaybackStatus {
        PLAYING,
        PAUSED
    }
    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MusicService.class);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }
    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
            if (player!=null)
                player.start();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
            if (player!=null)
            player.pause();
            notificationBuilder.setOngoing(false);
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
            playNext(false);
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
            playPrevious(false);
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
            player.stop();
            player.reset();
            this.stopForeground(true);
        }
    }

    @Override
    public void onAudioFocusChange(int focusState) {
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (player == null) playSong();
                else if (!player.isPlaying()) player.start();
                player.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (player.isPlaying()) player.stop();
                player.release();
                player = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (player.isPlaying()) player.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (player.isPlaying()) player.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }
}
