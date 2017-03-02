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
import com.customview.pranay.dasmusica.adapters.DashboardAdapter;

import java.util.zip.Inflater;

/**
 * Created by Pranay on 02-03-2017.
 */

public class DashBoardFragment extends Fragment {
    Context context;
    private DashboardAdapter adapter;

    @Override
    public void onAttach(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);
        RecyclerView rvDashboard = (RecyclerView) view.findViewById(R.id.rvRecentActivity);
        adapter = new DashboardAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        rvDashboard.setLayoutManager(layoutManager);
        rvDashboard.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.notifyDataSetChanged();
    }
}
