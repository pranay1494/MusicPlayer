package com.customview.pranay.dasmusica.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.fragment.SongsListFragment;
import com.customview.pranay.dasmusica.interfaces.SongListPopupInterface;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.ArrayList;

/**
 * Created by Pranay on 03-03-2017.
 */

public class SongsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int TYPE_HEADER = 0;
    protected static final int TYPE_CELL = 1;
    private PopupMenu popup;
    private Context context;
    private SongClicked songClickedCallback;
    private android.media.MediaMetadataRetriever mmr = null;
    private RecyclerView recyclerView;
    private SongListPopupInterface popupInterface;

    public SongsListAdapter(Context context, RecyclerView recyclerView, SongClicked clicked, SongListPopupInterface popupInterface) {
        this.context = context;
        songClickedCallback = clicked;
        mmr = new MediaMetadataRetriever();
        this.recyclerView = recyclerView;
        this.popupInterface = popupInterface;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER){
           return  new ShuffleVH(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shuffle,parent,false));
        }else
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_songlist,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == TYPE_CELL) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.songName.setText(MusicPOJO.getInstance().getSongsList().get(position-1).getTitle());
            viewHolder.artistName.setText(MusicPOJO.getInstance().getSongsList().get(position-1).getArtist());
            getAlbumArtWithoutLibrary(MusicPOJO.getInstance().getSongsList().get(position-1).getPath(), viewHolder.albumArt);

            /**
             * todo Add Logic for showing play instead of playnext and add to queue.
             */
            viewHolder.popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup = new PopupMenu(context, viewHolder.popupMenu);
                    popup.getMenuInflater().inflate(R.menu.menu_songpopup, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            MusicPOJO music = MusicPOJO.getInstance();
                            if (item.getTitle().equals("Play next")){
                                if (music.getNowPlayingList().size()>0){
                                    int index = music.getIndexOfCurrentSong()+1;
                                    music.getNowPlayingList().add(index,music.getSongsList().get(position-1));
                                    popupInterface.popupItemClicked(1);
                                }
                            }else if (item.getTitle().equals("Add to queue")){
                                music.getNowPlayingList().add(music.getSongsList().get(position-1));
                                popupInterface.popupItemClicked(2);
                            }else if (item.getTitle().equals("Add to playlist")){
                                Toast.makeText(context, "Add to playlist clicked", Toast.LENGTH_SHORT).show();
                                popupInterface.popupItemClicked(3);
                            }else if (item.getTitle().equals("Information")){
                                Toast.makeText(context, "Information clicked", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });

            viewHolder.cardSongs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPOJO musicPOJO = MusicPOJO.getInstance();
//                    ArrayList<SongsPojo> list = new ArrayList<>();
                    musicPOJO.clearNowPlayingList();
                    musicPOJO.setIndexOfCurrentSong(viewHolder.getAdapterPosition()-1);
//                    musicPOJO.setNowPlayingList(list);
                    for (SongsPojo song : musicPOJO.getSongsList()) {
                        musicPOJO.getNowPlayingList().add(song);
                    }
                    if(viewHolder.getAdapterPosition()-1<=musicPOJO.getNowPlayingList().size()) {
                        musicPOJO.getNowPlayingList().get(viewHolder.getAdapterPosition()-1).setPalying(true);
                        songClickedCallback.songclicked(true, false);
                    }
                }
            });
            if (MusicPOJO.getInstance().getSongsList()!=null && MusicPOJO.getInstance().getNowPlayingList()!=null && MusicPOJO.getInstance().getNowPlayingList().size()>0){
                SongsPojo songsPojo = MusicPOJO.getInstance().getSongsList().get(position-1);
                SongsPojo current = MusicPOJO.getInstance().getNowPlayingList().get(MusicPOJO.getInstance().getIndexOfCurrentSong());
                if (songsPojo.getId().equals(current.getId())){
                    viewHolder.rlSongs.setBackground(context.getResources().getDrawable(R.drawable.bgnowplaying));
                    recyclerView.invalidate();
                }else{
                    viewHolder.rlSongs.setBackground(null);
                }
            }
        }
        else {
            ShuffleVH viewHolder = (ShuffleVH) holder;
            viewHolder.llShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songClickedCallback.songclicked(false,true);
                }
            });

        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position){
            case 0: return TYPE_HEADER;
            default: return TYPE_CELL;
        }
    }

    @Override
    public int getItemCount() {
        if (MusicPOJO.getInstance().getSongsList() != null && MusicPOJO.getInstance().getSongsList().size()>0)
            return MusicPOJO.getInstance().getSongsList().size()+1;
        return 0;
    }

    /*@Override
    public String getSectionTitle(int position) {
        String sectionedText = "";
        if (position!=0 &&MusicPOJO.getInstance().getSongsList()!=null &&MusicPOJO.getInstance().getSongsList().size()>0 && !TextUtils.isEmpty(MusicPOJO.getInstance().getSongsList().get(position-1).getTitle())) {
            sectionedText = MusicPOJO.getInstance().getSongsList().get(position-1).getTitle().substring(0, 1);
        }
        return sectionedText;
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView albumArt;
        TextView songName;
        TextView artistName;
        CardView cardSongs;
        RelativeLayout rlSongs;
        ImageView popupMenu;
        public ViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
            songName = (TextView) itemView.findViewById(R.id.songTitle);
            artistName = (TextView) itemView.findViewById(R.id.songArtist);
            cardSongs = (CardView) itemView.findViewById(R.id.cardSongs);
            rlSongs = (RelativeLayout) itemView.findViewById(R.id.rlsongs);
            popupMenu = (ImageView) itemView.findViewById(R.id.popupMenu);
        }
    }

    public class ShuffleVH extends RecyclerView.ViewHolder{
        LinearLayout llShuffle;
        public ShuffleVH(View itemView) {
            super(itemView);
            llShuffle = (LinearLayout) itemView.findViewById(R.id.llShuffle);
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

        if(data != null) {
            Glide.with(context).load(data).centerCrop().into(albumArt);
        }
        else {
            Glide.with(context).load(R.drawable.guitar).centerCrop().into(albumArt);
        }
    }

    public interface SongClicked{
        void songclicked(boolean clicked, boolean shuffleClicked);
    }
}
