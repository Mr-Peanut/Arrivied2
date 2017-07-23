package com.example.guans.arrivied.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.bean.OfflineLocationClient;
import com.example.guans.arrivied.util.LOGUtil;

public class OfflineLocationService extends Service implements LocationListener {
    private OfflineLocationClient offlineLocationClient;

    private LocationClient locationClient;
    private ServiceConnection serviceConnection;

    public OfflineLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(offlineLocationClient==null){
            offlineLocationClient=new OfflineLocationClient(this);
        }
        serviceConnection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                locationClient= (LocationClient) iBinder;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        bindService(new Intent(this,LocateService.class),serviceConnection,BIND_AUTO_CREATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        offlineLocationClient.startLocation(this);
        LOGUtil.logE(this,"startLocation");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
      return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        LOGUtil.logE(this,location.getProvider()+String.valueOf(location.getLatitude())+"/"+location.getLongitude());
        locationClient.startLocateOneTime();
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
}
