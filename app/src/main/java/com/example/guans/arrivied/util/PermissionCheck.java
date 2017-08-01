package com.example.guans.arrivied.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;


/**
 * Created by guans on 2017/8/1.
 */

public class PermissionCheck {
    public static void checkPermission(Context context, String[] permissions, int requestCode) {
        ArrayList<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
            if (permissionList.size() != 0) {
//                if(ActivityCompat.shouldShowRequestPermissionRationale(context))
                ActivityCompat.requestPermissions((Activity) context, permissionList.toArray(new String[permissionList.size()]), requestCode);
            }
        }
    }
}
