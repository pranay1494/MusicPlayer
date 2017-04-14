package com.customview.pranay.dasmusica.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.v8.renderscript.RenderScript;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.customview.pranay.dasmusica.MainActivity;
import com.customview.pranay.dasmusica.model.MusicPOJO;
import com.customview.pranay.dasmusica.model.SongsPojo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Utilities {
    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
        final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }

    public static BitmapDrawable changeAppbarBackground(final Context context){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        MusicPOJO musicPOJO = MusicPOJO.getInstance();
        final Bitmap[] bitmap = {null};
        SongsPojo song = musicPOJO.getNowPlayingList().get(musicPOJO.getIndexOfCurrentSong());
        byte [] data = null;
        try {
            mmr.setDataSource(song.getPath());
            data = mmr.getEmbeddedPicture();
        }catch (Exception e){
            e.printStackTrace();
        }


        Glide.with(context).load(data).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                bitmap[0] =  resource;
            }
        });
        return new BitmapDrawable(context.getResources(),bitmap[0]);
    }
}
