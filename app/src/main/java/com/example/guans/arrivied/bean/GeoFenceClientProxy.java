package com.example.guans.arrivied.bean;

import android.os.Binder;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.DPoint;
import com.amap.api.services.busline.BusStationItem;

import java.util.List;

/**
 * Created by guans on 2017/7/12.
 */

public class GeoFenceClientProxy extends Binder {
    private GeoFenceClient mGeoFenceClient;
    private GenFenceTaskObserver genFenceTaskObserver;

    public BusStationItem getBusStationItem() {
        return busStationItem;
    }

    public void setBusStationItem(BusStationItem busStationItem) {
        this.busStationItem = busStationItem;
    }

    private BusStationItem busStationItem;

    public void setGenFenceTaskObserver(GenFenceTaskObserver genFenceTaskObserver) {
        this.genFenceTaskObserver = genFenceTaskObserver;
    }

    public GeoFenceClientProxy(GeoFenceClient mGeoFenceClient) {
        this.mGeoFenceClient = mGeoFenceClient;
    }

    public void addGeoFencePoint(DPoint dPoint, float r, String id) {
        if (mGeoFenceClient != null) {
            mGeoFenceClient.addGeoFence(dPoint, r, id);
            genFenceTaskObserver.onGeoPointAdd(dPoint,r,id);
        }
    }

    public void removeDPoint() {
        if (mGeoFenceClient != null) {
            mGeoFenceClient.removeGeoFence();
            genFenceTaskObserver.onGeoPointRemoved();
        }
    }
    public List<GeoFence> getGeoFences(){
        if(mGeoFenceClient!=null)
            return mGeoFenceClient.getAllGeoFence();
        return null;
    }
    public interface GenFenceTaskObserver {
        void onGeoPointAdd(DPoint dPoint, float r, String id);
        void onGeoPointRemoved();
    }
}
