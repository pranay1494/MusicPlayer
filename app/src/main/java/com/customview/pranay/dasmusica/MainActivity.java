package com.customview.pranay.dasmusica;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.customview.pranay.dasmusica.fragments.DashBoardFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity {

    private SlidingUpPanelLayout slidingUpPanel;
    private ImageView ivFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivFavorite = (ImageView) findViewById(R.id.ivFavorite);

        slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanel.setDragView(this.findViewById(R.id.slideHeader));

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.primaryFrame,new DashBoardFragment(),"DashBoard Fragment");
        transaction.commit();

    }
}
