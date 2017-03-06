package com.customview.pranay.dasmusica.models;

import java.util.ArrayList;

/**
 * Created by Pranay on 27-02-2017.
 */

/**
 * Driver model
 * bgfhg
 */
public class MusicPOJO {

    public static MusicPOJO musicPOJO = null;
    private ArrayList<SongsPojo> songs = new ArrayList<>();
    private ArrayList<AlbumPojo> albums = new ArrayList<>();
    private MusicPOJO(){}

    public static MusicPOJO getInstance(){
        if (musicPOJO==null){
            musicPOJO = new MusicPOJO();
        }
        return musicPOJO;
    }

    public ArrayList<SongsPojo> getSongsList() {
        return songs;
    }

    public void setSongsList(ArrayList<SongsPojo> songs) {
        this.songs = songs;
    }

    public void setSong(SongsPojo song){
        songs.add(song);
    }

    public ArrayList<AlbumPojo> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<AlbumPojo> albums) {
        this.albums = albums;
    }

    public void addAlbum(AlbumPojo albumPojo){
        albums.add(albumPojo);
    }
}
