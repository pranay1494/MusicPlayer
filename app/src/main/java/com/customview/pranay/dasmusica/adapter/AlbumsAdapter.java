package com.customview.pranay.dasmusica.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.AlbumPojo;
import com.customview.pranay.dasmusica.model.MusicPOJO;

import java.util.ArrayList;

/**
 * Created by Pranay on 14/04/2017.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.AlbumViewHolder> {

    private Context context;
    private ArrayList<AlbumPojo> albumsList;
    private MediaMetadataRetriever mmr;

    public AlbumsAdapter(Context context) {
        this.context = context;
        albumsList = new ArrayList<>();
        albumsList = MusicPOJO.getInstance().getAlbums();
        mmr = new MediaMetadataRetriever();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AlbumViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_albums,parent,false));
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        if (holder != null && albumsList.size() > 0){
            AlbumPojo album = albumsList.get(position);
            holder.tvAlbums.setText(album.getAlbumName());
            holder.tvArtist.setText(album.getArtist());
            getAlbumArtWithoutLibrary(album.getAlbumArtUri(), holder.ivAlbumArt);
        }
    }

    @Override
    public int getItemCount() {
        ArrayList<AlbumPojo> list = MusicPOJO.getInstance().getAlbums();
        if (list!=null && list.size()>0)
            return list.size();
        return 0;
    }
    private void getAlbumArtWithoutLibrary(String s, ImageView albumArt) {
        if(!TextUtils.isEmpty(s)) {
            Glide.with(context).load(s).centerCrop().into(albumArt);
        }
        else {
            Glide.with(context).load(R.drawable.guitar).centerCrop().into(albumArt);
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        CardView cvAlbums;
        ImageView ivAlbumArt;
        TextView tvAlbums;
        TextView tvArtist;
        public AlbumViewHolder(View itemView) {
            super(itemView);
            cvAlbums = (CardView) itemView.findViewById(R.id.cvAlbums);
            ivAlbumArt = (ImageView) itemView.findViewById(R.id.ivAlbumArt);
            tvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            tvAlbums = (TextView) itemView.findViewById(R.id.tvAlbums);
        }
    }
}
