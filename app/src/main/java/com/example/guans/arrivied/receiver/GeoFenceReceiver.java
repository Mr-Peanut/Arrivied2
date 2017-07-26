package com.example.guans.arrivied.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.service.GeoFenceService;
import com.example.guans.arrivied.util.LOGUtil;
import com.example.guans.arrivied.view.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;

public class GeoFenceReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取Bundle
        Bundle bundle = intent.getExtras();
//获取围栏行为：
        int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
//获取自定义的围栏标识：
        String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
//获取围栏ID:
        String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
//获取当前有触发的围栏对象：
        GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE);

//        Toast.makeText(context,String.valueOf(status)+"/"+customId+"/"+fenceId+"/"+fence.getPoiItem().getAddress(),Toast.LENGTH_LONG).show();
        switch (status){
            case  GEOFENCE_IN:
                    Toast.makeText(context,"GEOFENCE_IN",Toast.LENGTH_SHORT).show();
                    notifyArrived(context,intent);
                break;
            case GEOFENCE_OUT:
                LOGUtil.logE(context,"GEOFENCE_OUT"+context.toString());
//                Toast.makeText(context,"GEOFENCE_OUT",Toast.LENGTH_LONG).show();
                break;
            case GEOFENCE_STAYED:
//                Toast.makeText(context,"GEOFENCE_STAYEC",Toast.LENGTH_LONG).show();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void notifyArrived(Context context, Intent intent) {
        Intent intent2=new Intent(context,MainActivity.class);
        intent2.setFlags(FLAG_ACTIVITY_NEW_TASK);
        PendingIntent arrivedPendingIntent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context.getApplicationContext())
                .setSmallIcon(R.drawable.bus_station)
                .setContentTitle("Arrived 友情提示")
                .setContentText("您已经到站，请准备下车,滑动停止提醒")
                .setWhen(System.currentTimeMillis())
                .setTicker("您已到站！")
                .setAutoCancel(true)
//                .setContentIntent(arrivedPendingIntent)
//                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE)
                .build();
        notification.flags=Notification.FLAG_INSISTENT|Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GeoFenceService.ARRIVED_NOTIFICATION_ID,notification);
        Intent arrivedIntent=new Intent(GeoFenceService.ARRIVED_ACTION);
        context.sendBroadcast(arrivedIntent);
    }
}
