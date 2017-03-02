package com.customview.pranay.dasmusica.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.customview.pranay.dasmusica.R;

/**
 * Created by Pranay on 02-03-2017.
 */

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashBoardViewHolder> {
    Context context;
    public DashboardAdapter(Context context) {
        this.context = context;
    }

    @Override
    public DashBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_recentactivity, parent, false);
        return new DashBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DashBoardViewHolder holder, int position) {
        holder.albumArt.setImageResource(R.drawable.guitar);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class DashBoardViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArt;
        public DashBoardViewHolder(View itemView) {
            super(itemView);
            albumArt = (ImageView) itemView.findViewById(R.id.albumArt);
        }
    }
}
