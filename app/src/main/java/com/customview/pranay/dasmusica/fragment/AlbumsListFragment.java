package com.customview.pranay.dasmusica.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.adapter.AlbumsAdapter;
import com.futuremind.recyclerviewfastscroll.FastScroller;

/**
 * Created by Pranay on 14/04/2017.
 */

public class AlbumsListFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private FastScroller fastScroller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_songslist,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        recyclerView = (RecyclerView) view.findViewById(R.id.rvSongsList);
        fastScroller = (FastScroller) view.findViewById(R.id.fastscroll);
        AlbumsAdapter adapter = new AlbumsAdapter(context);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);
    }
}
