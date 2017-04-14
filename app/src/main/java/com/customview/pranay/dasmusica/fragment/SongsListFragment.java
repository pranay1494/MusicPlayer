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
                    //// TODO: 14/04/2017 compplete this
                        /*    for (SongsPojo song : MusicPOJO.getInstance().getSongsList()) {
                            if (song.getId().equals(MusicPOJO.getInstance().getNowPlayingList().get(MusicPOJO.getInstance().getIndexOfCurrentSong()))){

                            }
                        }*/
                        songListUpdated.nowPlayingListUpdated(true);
                        adapter.notifyDataSetChanged();
                        recyclerView.invalidate();
                    }
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);
    }
    public interface SongListUpdated{
        void nowPlayingListUpdated(boolean listUpdated);
    }
}
