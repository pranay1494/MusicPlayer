package com.customview.pranay.dasmusica.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.interfaces.GlideInterface;

/**
 * Created by Pranay on 14/04/2017.
 */

public class GildeUtils {
    public static void getBitmapFromGlide(Context context, byte[] data, final GlideInterface glideInterface){
        Glide.with(context).load(data).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                glideInterface.getBitmap(resource);
            }
        });

    }


}
