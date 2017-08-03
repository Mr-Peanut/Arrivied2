package com.example.guans.arrivied.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.util.LOGUtil;

public class LocateService extends Service implements AMapLocationListener {
    public static final String ACTION_LOCATION_RESULT = "com.example.guan.arrived.loctionservice.RESULT_FIND";
    public static String ACTION_LOCATION_BIND = "com.example.guan.arrived.loctionservice.SERVICE_BIND";
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption = null;
    private Intent locationResultIntent;
    private LocationClient locationClient;

    public LocateService() {
        LOGUtil.logE(this, "constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LOGUtil.logE(this, "onCreate");
        initLocationClient();
        if (locationClient == null) {
            locationClient = new LocationClient(mLocationClient, mLocationOption);
        }
    }

    private void initLocationClient() {
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        LOGUtil.logE(this, mLocationClient.toString());
//        mLocationClient.startLocation();
//设置定位参数
    }

    @Override
    public IBinder onBind(Intent intent) {
        LOGUtil.logE(this, "onBind");
        return locationClient;
    }

    @Override
    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient.onDestroy();
        }
        LOGUtil.logV(this, "OnDestory");
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (locationResultIntent == null) {
                locationResultIntent = new Intent(ACTION_LOCATION_RESULT);
            }
            locationResultIntent.putExtra("LocationResult", amapLocation);
            sendBroadcast(locationResultIntent);
            LOGUtil.logE(this, amapLocation.getAddress());

        }
        LOGUtil.logE(this, "getLocationResult");
    }


}
