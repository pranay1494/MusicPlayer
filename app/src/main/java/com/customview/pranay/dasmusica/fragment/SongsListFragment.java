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

import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.adapter.SongsListAdapter;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;

/**
 * Created by Pranay on 03-03-2017.
 */

public class SongsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private Context context;
    private FastScroller fastScroller;
    private SongListUpdated songListUpdated;

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

        SongsListAdapter adapter = new SongsListAdapter(context, new SongsListAdapter.SongClicked() {
            @Override
            public void songclicked(boolean clicked) {
                if (clicked)
                    songListUpdated.nowPlayingListUpdated(true);
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
