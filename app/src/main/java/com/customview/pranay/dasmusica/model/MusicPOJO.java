package com.customview.pranay.dasmusica.model;

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
    private ArrayList<SongsPojo> nowPlayingList = new ArrayList<>();
    private ArrayList<SongsPojo> tempNowPlayingList = new ArrayList<>();
    private ArrayList<SongsPojo> tempListForRepeat = new ArrayList<>();
    private ArrayList<PlaylistPojo> playlists = new ArrayList<>();
    private int indexOfCurrentSong;
    private int tempIndexOfCurrentSong;
    private int tempIndexForRepeat;

    public ArrayList<SongsPojo> getTempListForRepeat() {
        return tempListForRepeat;
    }

    public void setTempListForRepeat(ArrayList<SongsPojo> tempListFooRepeat) {
        this.tempListForRepeat = tempListForRepeat;
    }

    public int getTempIndexForRepeat() {
        return tempIndexForRepeat;
    }

    public void setTempIndexForRepeat(int tempIndexForRepeat) {
        this.tempIndexForRepeat = tempIndexForRepeat;
    }

    public int getTempIndexOfCurrentSong() {

        return tempIndexOfCurrentSong;
    }

    public void setTempIndexOfCurrentSong(int tempIndexOfCurrentSong) {
        this.tempIndexOfCurrentSong = tempIndexOfCurrentSong;
    }

    public int getIndexOfCurrentSong() {
        return indexOfCurrentSong;
    }

    public void setIndexOfCurrentSong(int indexOfCurrentSong) {
        this.indexOfCurrentSong = indexOfCurrentSong;
    }

    private MusicPOJO(){}

    public static MusicPOJO getInstance(){
        if (musicPOJO==null){
            musicPOJO = new MusicPOJO();
        }
        return musicPOJO;
    }

    public ArrayList<SongsPojo> getNowPlayingList() {
        return nowPlayingList;
    }

    public void setNowPlayingList(ArrayList<SongsPojo> nowPlayingList) {
        for (SongsPojo song: nowPlayingList) {
            this.nowPlayingList.add(song);
        }
    }

    public void clearNowPlayingList(){
        nowPlayingList.clear();
    }

    public ArrayList<SongsPojo> getSongsList() {
        return songs;
    }

    public void setSongsList(ArrayList<SongsPojo> songs) {
        this.songs = songs;
    }

    public ArrayList<SongsPojo> getTempNowPlayingList() {
        return tempNowPlayingList;
    }

    public void setTempNowPlayingList(ArrayList<SongsPojo> tempNowPlayingList) {
        this.tempNowPlayingList.clear();
        for (SongsPojo song: tempNowPlayingList) {
            this.tempNowPlayingList.add(song);
        }
    }
    public void addToTempList(SongsPojo song){
        tempListForRepeat.add(song);
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

    public ArrayList<PlaylistPojo> getPlaylist() {
        return playlists;
    }

    public void setPlaylist(ArrayList<PlaylistPojo> playlist) {
        this.playlists = playlist;
    }

    public void addPlaylist(PlaylistPojo playlist) {
        playlists.add(playlist);
    }
}
