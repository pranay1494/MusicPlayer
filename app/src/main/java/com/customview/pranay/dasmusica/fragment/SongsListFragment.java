package com.customview.pranay.dasmusica.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.adapter.SongsListAdapter;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;
import com.futuremind.recyclerviewfastscroll.FastScroller;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Pranay on 03-03-2017.
 */

public class SongsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private FastScroller fastScroller;
    private SongListUpdated songListUpdated;
    private SongsListAdapter adapter;
    private MusicPOJO music;
    ImageView ivListNotFound;
    private LinearLayoutManager layoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        songListUpdated = (SongListUpdated) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songslist,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvSongsList);
        fastScroller = (FastScroller) view.findViewById(R.id.fastscroll);
        ivListNotFound = (ImageView) view.findViewById(R.id.ivListNotFound);

        music = MusicPOJO.getInstance();
        if (music.getSongsList()!=null && music.getSongsList().size()>0){
            ivListNotFound.setVisibility(View.GONE);
        }else{
            ivListNotFound.setVisibility(View.VISIBLE);
            ivListNotFound.setImageResource(R.drawable.listnotfound);
        }

        adapter = new SongsListAdapter(context, recyclerView,new SongsListAdapter.SongClicked() {
            @Override
            public void songclicked(boolean clicked, boolean shuffleClicked) {
                if (clicked) {
                    songListUpdated.nowPlayingListUpdated(true);
                    adapter.notifyDataSetChanged();
                    recyclerView.invalidate();
                }else{
                    if (shuffleClicked){
                        ArrayList<SongsPojo> list = new ArrayList<>(MusicPOJO.getInstance().getSongsList());
                        Collections.shuffle(list);
                        MusicPOJO.getInstance().clearNowPlayingList();
                        MusicPOJO.getInstance().setNowPlayingList(list);
                        MusicPOJO.getInstance().setIndexOfCurrentSong(0);
                        MusicPOJO.getInstance().getNowPlayingList().get(0).setPalying(true);
                        songListUpdated.nowPlayingListUpdated(true);
                        adapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                    }
                }
            }
        },(MainActivity)context);
        layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);

        ((MainActivity)context).ivThisSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).appbar.setExpanded(false,true);
                scroll();
            }
        });

    }
    public interface SongListUpdated{
        void nowPlayingListUpdated(boolean listUpdated);
    }

    public void scroll(){
        Toast.makeText(context, "worked", Toast.LENGTH_SHORT).show();
        recyclerView.smoothScrollToPosition(music.getIndexOfCurrentSong());
    }

    public void refreshList(){
        if (adapter!=null)
            adapter.notifyDataSetChanged();
    }

}
