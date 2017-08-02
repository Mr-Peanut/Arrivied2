package com.example.guans.arrivied.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.receiver.ControllerReceiver;
import com.example.guans.arrivied.util.CheckSystemActive;
import com.example.guans.arrivied.util.LatLonPointTransferLatLon;

import static com.example.guans.arrivied.service.GeoFenceService.MONITOR_NOTIFICATION_ID;
import static com.example.guans.arrivied.service.GeoFenceService.PROXIMITY_REQUEST_CODE;

public class MonitorService extends Service implements ControllerReceiver.ControlReceiveListener, AMapLocationListener {
    public static final String ACTION_LOCATION = "com.example.guan.arrived.MonitorService.LOCATION";
    public static final String ACTION_WAKE_UP = "com.example.guan.arrived.MonitorService.WAKE_UP";
    private boolean isLocated = false;
    private WatchItem watchItem;
    private AlarmManager alarmManager;
    private LocationClient locationClient;
    private PendingIntent locationPendingIntent;
    private ControllerReceiver controller;
    private BusStationItem stationItem;
    private PendingIntent wakeupPendingIntent;
    private float r;

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        locationClient = new LocationClient(this, this);
        controller = new ControllerReceiver(this);
        IntentFilter controlIntentFilter = new IntentFilter();
        controlIntentFilter.addAction(ACTION_LOCATION);
        controlIntentFilter.addAction(ACTION_WAKE_UP);
        registerReceiver(controller, controlIntentFilter);
        locationPendingIntent = PendingIntent.getBroadcast(this, 1, new Intent(ACTION_LOCATION), PendingIntent.FLAG_UPDATE_CURRENT);
        wakeupPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_WAKE_UP), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        watchItem = intent.getParcelableExtra("TARGET_ITEM");
        r = intent.getFloatExtra("R", 500);
        stationItem = watchItem.getBusStationItem();
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 60 * 1000, wakeupPendingIntent);
        Notification monitorNotification = new NotificationCompat.Builder(this)
                .setContentText("守护进程")
                .setContentTitle("守护进程正在运行")
                .setOngoing(true)
                .build();
        startForeground(MONITOR_NOTIFICATION_ID, monitorNotification);
        return Service.START_NOT_STICKY;
    }

    private void cancelMonitor() {
        isLocated = false;
        alarmManager.cancel(wakeupPendingIntent);
        alarmManager.cancel(locationPendingIntent);
    }

    @Override
    public void onDestroy() {
        if (locationClient != null) {
            locationClient.destory();
        }
        unregisterReceiver(controller);
        cancelMonitor();
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void onControlBroadcastReceive(Intent intent) {
        switch (intent.getAction()) {
            case ACTION_LOCATION:
                locationClient.startLocateOneTime();
                isLocated = true;
                break;
            case ACTION_WAKE_UP:
                if (!CheckSystemActive.isScreenOn(this)) {
                    if (!isLocated) {
                        locationClient.startLocateOneTime();
                        isLocated = true;
                    }
                } else {
                    if (isLocated)
                        cancelMonitor();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (aMapLocation.getErrorCode()) {
                case 1000:
                    float distance = AMapUtils.calculateLineDistance(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()), LatLonPointTransferLatLon.getLatLonFromLatLngPoint(watchItem.getBusStationItem().getLatLonPoint()));
                    if (distance >= 1000) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) ((distance - 1000) / 18 * 1000), locationPendingIntent);
                    } else if (distance > r) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, Math.max((long) (distance / 18 * 1000), 30 * 1000), locationPendingIntent);
                    } else {
                        Intent alarmIntent = new Intent();
                        alarmIntent.putExtra("TARGET_ITEM", stationItem);
                        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, PROXIMITY_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, alarmPendingIntent);
                    }
                    break;
                default:
                    locationClient.startLocateOneTime();
                    break;
            }
        }
    }
}
