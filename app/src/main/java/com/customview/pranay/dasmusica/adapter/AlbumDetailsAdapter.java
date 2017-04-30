package com.customview.pranay.dasmusica.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.util.ArrayList;

/**
 * Created by Pranay on 30/04/2017.
 */

public class AlbumDetailsAdapter extends RecyclerView.Adapter {

    private Context context;
    private static final int TYPE_HEADER_ALBUMS = 0;
    private static final int TYPE_CELL_ALBUMS = 1;
    private int modelPosition;
    private ArrayList<SongsPojo> songs;
    private MusicPOJO music = MusicPOJO.getInstance();

    public AlbumDetailsAdapter(Context context, ArrayList<SongsPojo> songs,int pos) {
        this.songs = songs;
        this.context = context;
        modelPosition = pos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER_ALBUMS){
            return new AlbumDetailsHeaderVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.album_detail_header,parent,false));
        }else{
            return new AlbumSongsVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_album_details,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER_ALBUMS){
            AlbumDetailsHeaderVH headerVH = (AlbumDetailsHeaderVH) holder;
            headerVH.tvNameofSong.setText(music.getAlbums().get(modelPosition).getAlbumName());
            headerVH.tvartistName.setText(music.getAlbums().get(modelPosition).getArtist());
            if (music.getAlbums().get(modelPosition).getNumberOfSongs()==1){
                headerVH.tvnumberOfSongs.setText(""+music.getAlbums().get(modelPosition).getNumberOfSongs() + " Song Found");
            }
            else{
                headerVH.tvnumberOfSongs.setText(""+music.getAlbums().get(modelPosition).getNumberOfSongs() + " Songs Found");
            }
        }else {
            AlbumSongsVH songsVH = (AlbumSongsVH) holder;
            songsVH.tvSno.setText(""+position);
            songsVH.songTitle.setText(songs.get(position-1).getTitle());
            songsVH.songArtist.setText(songs.get(position-1).getArtist());
        }
    }

    @Override
    public int getItemCount() {
        return songs!=null?songs.size()+1:0;
    }
    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0: return TYPE_HEADER_ALBUMS;
            default: return TYPE_CELL_ALBUMS;
        }
    }

    public class AlbumSongsVH extends RecyclerView.ViewHolder{
        TextView tvSno;
        TextView songTitle;
        TextView songArtist;
        public AlbumSongsVH(View itemView) {
            super(itemView);
            tvSno = (TextView) itemView.findViewById(R.id.tvSno);
            songTitle = (TextView) itemView.findViewById(R.id.songTitle);
            songArtist = (TextView) itemView.findViewById(R.id.songArtist);
        }
    }
    public class AlbumDetailsHeaderVH extends RecyclerView.ViewHolder{
        TextView tvNameofSong;
        TextView tvnumberOfSongs;
        TextView tvartistName;
        public AlbumDetailsHeaderVH(View itemView) {
            super(itemView);
            tvNameofSong = (TextView) itemView.findViewById(R.id.tvNameofSong);
            tvnumberOfSongs = (TextView) itemView.findViewById(R.id.tvnumberOfSongs);
            tvartistName = (TextView) itemView.findViewById(R.id.tvartistName);
        }
    }
}
