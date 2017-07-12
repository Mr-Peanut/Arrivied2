package com.example.guans.arrivied.util;

import android.util.Log;

/**
 * Created by guans on 2017/7/11.
 */

public class LOGUtil {
    public static void logE(Object obj, String message) {
        Log.e(obj.getClass().getSimpleName(), message);
    }

    public static void logV(Object obj, String message) {
        Log.v(obj.getClass().getSimpleName(), message);
    }

    public static void logI(Object obj, String message) {
        Log.i(obj.getClass().getSimpleName(), message);
    }

    public static void logD(Object obj, String message) {
        Log.d(obj.getClass().getSimpleName(), message);
    }
}
