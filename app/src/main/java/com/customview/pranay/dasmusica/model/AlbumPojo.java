package com.customview.pranay.dasmusica.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranay on 17-02-2017.
 */

/**
 * Model for albums based songs.
 */
public class AlbumPojo {
    private String id;
    private String artist;
    private int numberOfSongs;
    private String albumName;
    private List<SongsPojo> albumSongsList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<SongsPojo> getAlbumSongsList() {
        return albumSongsList;
    }

    public void setAlbumSongsList(List<SongsPojo> albumSongsList) {
        this.albumSongsList = albumSongsList;
    }
}
