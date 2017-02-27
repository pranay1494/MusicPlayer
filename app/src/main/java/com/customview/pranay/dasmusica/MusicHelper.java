package com.customview.pranay.dasmusica;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.customview.pranay.dasmusica.Models.MusicPOJO;
import com.customview.pranay.dasmusica.Models.SongsPojo;

/**
 * Created by Pranay on 25-02-2017.
 */

public class MusicHelper {

    private Context context;
    private Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private String musicSelectionArgs = MediaStore.Audio.Media.IS_MUSIC + "!=0";
    private ContentResolver contentResolver;

    public MusicHelper(Context context) {
        this.context = context;
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
}
