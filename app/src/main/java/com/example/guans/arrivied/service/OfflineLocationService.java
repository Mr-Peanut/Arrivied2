package com.example.guans.arrivied.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

import com.amap.api.location.DPoint;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.example.guans.arrivied.bean.OfflineLocationClient;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.util.GPS2AMap;

public class OfflineLocationService extends Service implements LocationListener {
    private OfflineLocationClient offlineLocationClient;
    private WatchItem onWatchItem;
    private BusStationItem busStationItem;
    private DPoint stationPoint;
    private float warningLength;
    private GPS2AMap gps2AMap;
    public OfflineLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        offlineLocationClient = new OfflineLocationClient(this);
        gps2AMap = new GPS2AMap(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onWatchItem = intent.getParcelableExtra("ON_WATCH_ITEM");
        warningLength = intent.getParcelableExtra("WARNING_LENGTH");
        busStationItem = onWatchItem.getBusStationItem();
        LatLonPoint latLonPoint = busStationItem.getLatLonPoint();
        stationPoint = new DPoint(latLonPoint.getLatitude(), latLonPoint.getLongitude());
        offlineLocationClient.startLocation(30000, 50, this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return offlineLocationClient;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (gps2AMap.distance(location, stationPoint) <= warningLength) {
            notifiedArrived();
        }

    }

    private void notifiedArrived() {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {


    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onDestroy() {
        offlineLocationClient.stopLocation(this);
        super.onDestroy();
    }
}
