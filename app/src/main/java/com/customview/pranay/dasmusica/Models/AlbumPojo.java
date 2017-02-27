package com.customview.pranay.dasmusica.Models;

import java.util.List;

/**
 * Created by Pranay on 17-02-2017.
 */

public class AlbumPojo {
    private Long id;
    private String artist;
    private int numberOfSongs;
    private String albumName;
    private List<SongsPojo> albumSongsList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
