package com.customview.pranay.dasmusica.model;

import java.util.ArrayList;

/**
 * Created by Pranay on 18/05/2017.
 */

public class PlaylistPojo
{
    private String name;
    private ArrayList<SongsPojo> songs = new ArrayList<>();
    private String date;

    public String getName() {
        return name;
    }

    public PlaylistPojo setName(String name) {
        this.name = name;
        return this;
    }

    public ArrayList<SongsPojo> getSongs() {
        return songs;
    }

    public PlaylistPojo setSongs(ArrayList<SongsPojo> songs) {
        this.songs = songs;
        return this;
    }

    public String getDate() {
        return date;
    }

    public PlaylistPojo setDate(String date) {
        this.date = date;
        return this;
    }
    public void addSong(SongsPojo song){
        songs.add(song);
    }
}
