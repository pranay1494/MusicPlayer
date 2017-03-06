package com.customview.pranay.dasmusica.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.io.File;
import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Pranay on 06-03-2017.
 */

public class DashBoardAlbumsAdapter extends RecyclerView.Adapter<DashBoardAlbumsAdapter.DashBoardViewHolder>{
    Context context;
    private ArrayList<AlbumPojo> songs;

    public DashBoardAlbumsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public DashBoardAlbumsAdapter.DashBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_recentactivity, parent, false);
        return new DashBoardAlbumsAdapter.DashBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashBoardAlbumsAdapter.DashBoardViewHolder holder, int position) {
        if(MusicPOJO.getInstance().getAlbums() != null && MusicPOJO.getInstance().getAlbums().size() > 0 && MusicPOJO.getInstance().getAlbums().size() >= position) {
            songs = MusicPOJO.getInstance().getAlbums();
            holder.name.setText(songs.get(position).getAlbumName());
            getAlbumArtWithoutLibrary(songs.get(position).getAlbumArtUri(),holder.albumArt,position);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (MusicPOJO.getInstance().getAlbums() != null){
            if (MusicPOJO.getInstance().getAlbums().size()<=4){
                count = MusicPOJO.getInstance().getSongsList().size();
            }else{
                count = 4;
            }
        }
        return count;
    }

    public class DashBoardViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        TextView name;
        public DashBoardViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            name = (TextView) itemView.findViewById(R.id.songName);
        }
    }

    private void getAlbumArtWithoutLibrary(String s, ImageView albumArt, int position) {
        Bitmap bm= BitmapFactory.decodeFile(s);
        albumArt.setImageBitmap(bm);
    }
}
