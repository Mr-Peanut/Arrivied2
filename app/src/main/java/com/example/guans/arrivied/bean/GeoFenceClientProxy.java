package com.example.guans.arrivied.bean;

import android.os.Binder;

import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.DPoint;

/**
 * Created by guans on 2017/7/12.
 */

public class GeoFenceClientProxy extends Binder {
    private GeoFenceClient mGeoFenceClient;

    public GeoFenceClientProxy(GeoFenceClient mGeoFenceClient) {
        this.mGeoFenceClient = mGeoFenceClient;
    }

    public void addGeoFencePoint(DPoint dPoint, float r, String id) {
        if (mGeoFenceClient != null) {
            mGeoFenceClient.addGeoFence(dPoint, r, id);
        }
    }

    public void removeDPoint() {
        if (mGeoFenceClient != null) {
            mGeoFenceClient.removeGeoFence();
        }
    }
}
