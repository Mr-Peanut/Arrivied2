package com.example.guans.arrivied.util;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;

/**
 * Created by shiqian.guan on 2017/7/20.
 */

public class LatLonPointTransferLatLon {
    public static LatLng getLatLonFromLatLngPoint(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    public static LatLonPoint getLatLonPointFromLatLon(LatLng latLng) {
        return new LatLonPoint(latLng.latitude, latLng.longitude);
    }
}
