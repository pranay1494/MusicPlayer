package com.customview.pranay.dasmusica;

import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Pranay on 22-02-2017.
 */

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout mainLayout;
    private AnimationDrawable animationDrawable;
    public static final String FONT_PATH = "fonts/AlexBrush-Regular.ttf";
    private Typeface typeface;
    private TextView splashText;
    private Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mainLayout= (RelativeLayout) findViewById(R.id.llsplash);
        splashText = (TextView) findViewById(R.id.tvsplash);
        typeface = Typeface.createFromAsset(getAssets(),FONT_PATH);
        splashText.setTypeface(typeface);

        animation = AnimationUtils.loadAnimation(this,R.anim.splashtext);
        splashText.startAnimation(animation);

        animationDrawable = (AnimationDrawable) mainLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }
}
