package com.customview.pranay.dasmusica.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranay on 27-02-2017.
 */

public class MusicPOJO {

    public static MusicPOJO musicPOJO = null;
    private ArrayList<SongsPojo> songs = new ArrayList<>();
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
}
