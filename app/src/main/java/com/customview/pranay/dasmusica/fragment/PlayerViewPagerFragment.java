package com.customview.pranay.dasmusica.fragment;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

/**
 * Created by Pranay on 23/04/2017.
 */

public class PlayerViewPagerFragment extends Fragment {

    private MusicPOJO music = null;
    private Context context;
    private MediaMetadataRetriever mmr;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        mmr = new MediaMetadataRetriever();
    }

    public static PlayerViewPagerFragment newInstance(int position, int count) {
        Bundle args = new Bundle();
        args.putInt("count",count);
        args.putInt("position",position);
        PlayerViewPagerFragment fragment = new PlayerViewPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vpplayer,container,false);
        ImageView img = (ImageView) view.findViewById(R.id.ivCurrentSong);
        int position = getArguments().getInt("position");
        Log.d("frag","inside frag" + position);
        music = MusicPOJO.getInstance();
        if (music.getNowPlayingList()!=null && music.getNowPlayingList().size() > position){
            setImage(img,position);
        }
        return view;
    }

    private void setImage(ImageView view,int position) {
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        SongsPojo song = musicPOJO.getNowPlayingList().get(position);
        byte [] data = null;
        try {
            mmr.setDataSource(song.getPath());
            data = mmr.getEmbeddedPicture();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (data!=null)
        Glide.with(context).load(data).placeholder(R.drawable.guitar).into(view);
    }
}

