package com.customview.pranay.dasmusica;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.customview.pranay.dasmusica.comparator.MusicListComparator;
import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pranay on 22-02-2017.
 */

public class SplashActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private RelativeLayout mainLayout;
    private AnimationDrawable animationDrawable;
    public static final String FONT_PATH = "fonts/AlexBrush-Regular.ttf";
    private Typeface typeface;
    private TextView splashText;
    private Animation animation;
    private ContentResolver contentResolver;
    private Context context;

    private Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Uri albumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    private String musicSelectionArgs = MediaStore.Audio.Media.IS_MUSIC + "!=0";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mainLayout= (RelativeLayout) findViewById(R.id.llsplash);
        splashText = (TextView) findViewById(R.id.tvsplash);
        typeface = Typeface.createFromAsset(getAssets(),FONT_PATH);
        splashText.setTypeface(typeface);

        animation = AnimationUtils.loadAnimation(this,R.anim.splashtext);
        splashText.startAnimation(animation);

        animationDrawable = (AnimationDrawable) mainLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        context = this;

        setMusicList();
        setAlbumSongsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    public void setMusicList(){
        getLoaderManager().initLoader(1,null,this);
    }

    public void setAlbumSongsList(){
//        getLoaderManager().initLoader(2,null,this);
        contentResolver = context.getContentResolver();
        List<SongsPojo> songs = new ArrayList<>();
        Cursor cursor= contentResolver.query(albumsUri,null,null,null,null);
        if (cursor!=null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                AlbumPojo albumPojo = new AlbumPojo();
                songs.clear();
//                getLoaderManager().initLoader(3,null,SplashActivity.this);
                Cursor cursorForMusic= contentResolver.query(musicUri,null,musicSelectionArgs,null,null);
                SongsPojo songsOfAlbums = new SongsPojo();
                if (cursorForMusic != null && cursorForMusic.getCount() > 0) {
                    while (cursorForMusic.moveToNext()) {
                        setSongList(cursorForMusic, songsOfAlbums);
                    }
                    songs.add(songsOfAlbums);
                }
                albumPojo.setAlbumSongsList(songs);
                albumPojo.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID)));
                albumPojo.setAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)));
                albumPojo.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)));
                albumPojo.setAlbumArtUri(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                albumPojo.setNumberOfSongs(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS))));
                MusicPOJO.getInstance().addAlbum(albumPojo);
            }
        }
    }

    private void setSongList(Cursor cursor, SongsPojo songsPojo) {
        songsPojo.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
        songsPojo.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
        songsPojo.setAlbumID(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
        songsPojo.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        songsPojo.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
        songsPojo.setDuration(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
        songsPojo.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
        songsPojo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
        songsPojo.setDateAdded(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case 1:
                return new CursorLoader(SplashActivity.this,musicUri,null,musicSelectionArgs,null,MediaStore.Audio.Media.TITLE);
            case 2:
                break;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()){
            case 1:
                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        SongsPojo songsPojo = new SongsPojo();
                        setSongList(cursor, songsPojo);
                        if (songsPojo.getId() != null){
                            MusicPOJO.getInstance().setSong(songsPojo);
                        }
                    }
                }
//                Collections.sort(MusicPOJO.getInstance().getSongsList(),new MusicListComparator());
                //// TODO: 03-03-2017 change this logic
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case 2:
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
