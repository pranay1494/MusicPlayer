package com.customview.pranay.dasmusica;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.adapter.AlbumDetailsAdapter;
import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

/**
 * Created by Pranay on 30/04/2017.
 */

public class AlbumActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener{

    private SlidingUpPanelLayout slidingUpPanel;
    private AlbumDetailsAdapter detailsAdapter;
    int position;
    private MusicPOJO music = MusicPOJO.getInstance();
    private ArrayList<SongsPojo> songs = new ArrayList<>();
    private RecyclerView rvAlbumDetails;
    private ImageView albumArt;
    private AlbumPojo album;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        slidingUpPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        rvAlbumDetails = (RecyclerView) findViewById(R.id.rvAlbumDetails);
        albumArt = (ImageView) findViewById(R.id.albumArt);

        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        if (getIntent().getExtras()!=null){
            position = getIntent().getIntExtra("POSITION",0);
            for (SongsPojo song : music.getAlbums().get(position).getAlbumSongsList()) {
                songs.add(song);
            }
            album = music.getAlbums().get(position);
            getAlbumArtWithoutLibrary(album.getAlbumArtUri(),albumArt);
        }
        detailsAdapter = new AlbumDetailsAdapter(this,songs,position);
        rvAlbumDetails.setLayoutManager(manager);
        rvAlbumDetails.setAdapter(detailsAdapter);
    }

    private void getAlbumArtWithoutLibrary(String albumArtUri, ImageView albumArt) {
        if(!TextUtils.isEmpty(albumArtUri)) {
            Glide.with(this).load(albumArtUri).centerCrop().placeholder(R.drawable.nowplaying).into(albumArt);
        }
        else {
            Glide.with(this).load(R.drawable.guitar).centerCrop().into(albumArt);
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }
}
