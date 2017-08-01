package com.example.guans.arrivied.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;

import static android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS;

/**
 * Created by shiqian.guan on 2017/8/1.
 */

public class CheckBatteryOptimizationUtil {
    public static void check(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = context.getPackageName();

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
//                intent.setAction(ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                Toast.makeText(context.getApplicationContext(), "已设置白名单", Toast.LENGTH_SHORT).show();
//                context.startActivity(intent);
            } else {
                intent.setAction(ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                context.startActivity(intent);
            }

        }
    }
}
