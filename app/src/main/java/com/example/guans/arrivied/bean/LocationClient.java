package com.example.guans.arrivied.bean;

import android.os.Binder;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.example.guans.arrivied.util.LOGUtil;

/**
 * Created by guans on 2017/7/11.
 */

public class LocationClient extends Binder {
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;

    public LocationClient(AMapLocationClient mLocationClient, AMapLocationClientOption mLocationOption) {
        this.mLocationClient = mLocationClient;
        this.mLocationOption = mLocationOption;
        LOGUtil.logE(this, "constructor");
    }

    public void startLocateOneTime() {
        stopLocation();
        LOGUtil.logE(this, mLocationClient.toString());
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    public void startLocateAlways() {
        stopLocation();
        mLocationOption.setOnceLocation(false);
        mLocationOption.setNeedAddress(false);
        mLocationClient.startLocation();
    }

    public void startLocateAlways(long interval) {
        mLocationOption.setInterval(interval);
        startLocateAlways();
    }

    public void stopLocation() {
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.stopLocation();
    }
}

