package com.customview.pranay.dasmusica;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Pranay on 02-03-2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashBoardViewHolder> {

    public DashboardAdapter(Context context) {
    }

    @Override
    public DashBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(DashBoardViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class DashBoardViewHolder extends RecyclerView.ViewHolder {
        public DashBoardViewHolder(View itemView) {
            super(itemView);

        }
    }
}
