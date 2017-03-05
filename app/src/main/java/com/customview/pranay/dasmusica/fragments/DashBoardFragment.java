package com.customview.pranay.dasmusica.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.customview.pranay.dasmusica.R;
import com.customview.pranay.dasmusica.adapters.DashboardAdapter;

import java.util.zip.Inflater;

/**
 * Created by Pranay on 02-03-2017.
 */

public class DashBoardFragment extends Fragment implements View.OnClickListener{
    Context context;
    private DashboardAdapter adapter;
    private Button btnMore;
    BtnmoreClicked clicked;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        clicked = (BtnmoreClicked) activity;
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvDashboard = (RecyclerView) view.findViewById(R.id.rvRecentActivity);
        btnMore = (Button) view.findViewById(R.id.btnMore);
        adapter = new DashboardAdapter(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        rvDashboard.setLayoutManager(layoutManager);
        rvDashboard.setAdapter(adapter);

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked.btnClicked(true);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }
    public interface BtnmoreClicked{
        void btnClicked(boolean clicked);
    }
}
