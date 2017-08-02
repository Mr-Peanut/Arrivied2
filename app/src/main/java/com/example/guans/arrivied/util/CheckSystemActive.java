package com.example.guans.arrivied.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;

/**
 * Created by shiqian.guan on 2017/8/2.
 */

public class CheckSystemActive {
    public static boolean isKeyLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return keyguardManager.isDeviceLocked() || keyguardManager.isKeyguardLocked();
        } else {
            return keyguardManager.isKeyguardLocked();
        }
    }

    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }
}
