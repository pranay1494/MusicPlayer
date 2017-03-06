package com.customview.pranay.dasmusica;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranay on 25-02-2017.
 */

public class MusicHelper implements LoaderManager.LoaderCallbacks<Cursor>{

    private Context context;
    private Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private Uri albumsUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    private String musicSelectionArgs = MediaStore.Audio.Media.IS_MUSIC + "!=0";
    private String albumMusicselectionArgs = musicSelectionArgs + " and album_id = ?";

    private ContentResolver contentResolver;

    public MusicHelper(Context context) {
        this.context = context;
        setMusicList();
        setAlbumSongsList();
    }

    public void setMusicList(){
        contentResolver = context.getContentResolver();
        Cursor cursor= contentResolver.query(musicUri,null,musicSelectionArgs,null,null);
        SongsPojo songsPojo = new SongsPojo();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                setSongList(cursor, songsPojo);
            }
        }
        if (songsPojo.getId() != null){
            MusicPOJO.getInstance().setSong(songsPojo);
        }
    }

    public void setAlbumSongsList(){
        contentResolver = context.getContentResolver();
        List<SongsPojo> songs = new ArrayList<>();
        Cursor cursor= contentResolver.query(albumsUri,null,null,null,null);
        if (cursor!=null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                AlbumPojo albumPojo = new AlbumPojo();
                songs.clear();
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
