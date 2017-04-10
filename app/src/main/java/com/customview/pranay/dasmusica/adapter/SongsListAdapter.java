package com.customview.pranay.dasmusica.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

/**
 * Created by Pranay on 03-03-2017.
 */

public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionTitleProvider {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_CELL = 1;
    private Context context;
    private android.media.MediaMetadataRetriever mmr = null;

    public SongsListAdapter(Context context) {
        this.context = context;
        mmr = new MediaMetadataRetriever();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
           return  new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hvp_header_placeholder,parent,false)) {};
        }else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_songlist,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_CELL) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.songName.setText(MusicPOJO.getInstance().getSongsList().get(position).getTitle());
            viewHolder.artistName.setText(MusicPOJO.getInstance().getSongsList().get(position).getArtist());
            getAlbumArtWithoutLibrary(MusicPOJO.getInstance().getSongsList().get(position).getPath(), viewHolder.albumArt);
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
//            case 0: return TYPE_HEADER;
            default: return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        return MusicPOJO.getInstance().getSongsList().size();
    }

    @Override
    public String getSectionTitle(int position) {
        String sectionedText = "";
        if (MusicPOJO.getInstance().getSongsList()!=null && !TextUtils.isEmpty(MusicPOJO.getInstance().getSongsList().get(position).getTitle())) {
            sectionedText = MusicPOJO.getInstance().getSongsList().get(position).getTitle().substring(0, 1);
        }
        return sectionedText;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView albumArt;
        TextView songName;
        TextView artistName;
        public ViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            songName = (TextView) itemView.findViewById(R.id.songTitle);
            artistName = (TextView) itemView.findViewById(R.id.songArtist);
        }
    }
    private void getAlbumArtWithoutLibrary(String s, ImageView albumArt) {
        byte [] data = null;
        try {
           mmr.setDataSource(s);
           data = mmr.getEmbeddedPicture();
       }catch (Exception e){
           e.printStackTrace();
       }

        if(data != null)
        {
            /*Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            albumArt.setImageBitmap(bitmap);*/
            Glide.with(context).load(data).centerCrop().into(albumArt);
        }
        else
        {
//            albumArt.setImageResource(R.drawable.guitar);
            Glide.with(context).load(R.drawable.guitar).centerCrop().into(albumArt);
        }
    }

}
