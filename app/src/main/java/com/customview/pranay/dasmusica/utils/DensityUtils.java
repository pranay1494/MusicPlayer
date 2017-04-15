package com.customview.pranay.dasmusica.utils;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by Pranay on 15/04/2017.
 */

public class DensityUtils {
    public static String getDensityBucket(Resources resources) {
        switch (resources.getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }
}
