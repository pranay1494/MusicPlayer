package com.customview.pranay.dasmusica.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.adapters.SongsListAdapter;
import com.github.florent37.hollyviewpager.HollyViewPager;
import com.github.florent37.hollyviewpager.HollyViewPagerBus;

/**
 * Created by Pranay on 03-03-2017.
 */

public class SongsListFragment extends Fragment{
    private RecyclerView recyclerView;
    private HollyViewPager viewPager;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songslist,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvSongsList);
        SongsListAdapter adapter = new SongsListAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        HollyViewPagerBus.registerRecyclerView(getActivity(), recyclerView);
    }
}
