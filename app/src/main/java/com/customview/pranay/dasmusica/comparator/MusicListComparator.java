package com.customview.pranay.dasmusica.comparator;

import com.customview.pranay.dasmusica.model.SongsPojo;

import java.util.Comparator;

/**
 * Created by Pranay on 09/04/2017.
 */

public class MusicListComparator implements Comparator<SongsPojo>{
    @Override
    public int compare(SongsPojo o1, SongsPojo o2) {

        String s1 = new String();
        String s2 = new String();

        try{
            s1 = o1.getTitle();
            s2 = o2.getTitle();
        }catch (Exception e){
            e.printStackTrace();
        }

        return s1.compareTo(s2);
    }
}
