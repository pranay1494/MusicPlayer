package com.customview.pranay.dasmusica.models;

/**
 * Created by Pranay on 17-02-2017.
 */

/**
 * songs main model
 */
public class SongsPojo {
    private String title;
    private String artist;
    private String path;
    private String displayName;
    private Long id;
    private String album;
    private String albumID;
    private long duration;
    private boolean isPalying;
    private String DateAdded;
    //add seek time


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(String dateAdded) {
        DateAdded = dateAdded;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPalying() {
        return isPalying;
    }

    public void setPalying(boolean palying) {
        isPalying = palying;
    }
}
