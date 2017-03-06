package com.customview.pranay.dasmusica.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Pranay on 02-03-2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashBoardViewHolder> {
    Context context;
    private ArrayList<SongsPojo> songs;

    public DashboardAdapter(Context context) {
        this.context = context;
    }

    @Override
    public DashBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_recentactivity, parent, false);
        return new DashBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashBoardViewHolder holder, int position) {
        if(MusicPOJO.getInstance().getSongsList() != null && MusicPOJO.getInstance().getSongsList().size() > 0 && MusicPOJO.getInstance().getSongsList().size() >= position) {
            songs = MusicPOJO.getInstance().getSongsList();
            holder.name.setText(songs.get(position).getTitle());
            getAlbumArtWithoutLibrary(songs.get(position).getPath(),holder.albumArt);
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (MusicPOJO.getInstance().getSongsList() != null){
            if (MusicPOJO.getInstance().getSongsList().size()<=4){
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

    private void getAlbumArt(String s, ImageView albumArt) {
        if (s!=null&&!s.equalsIgnoreCase("")) {
            FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
            retriever.setDataSource(s);
            byte[] data = retriever.getEmbeddedPicture();
            if(data!=null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                albumArt.setImageBitmap(bitmap);
            }
            retriever.release();
        }
    }
    private void getAlbumArtWithoutLibrary(String s, ImageView albumArt) {
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(s);

        byte [] data = mmr.getEmbeddedPicture();
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            albumArt.setImageBitmap(bitmap);
            albumArt.setAdjustViewBounds(true);
        }
        else
        {
            albumArt.setImageResource(R.drawable.guitar);
            albumArt.setAdjustViewBounds(true);
        }
    }
}
