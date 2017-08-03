package com.example.guans.arrivied.util;

import android.content.Context;
import android.location.Location;

import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;

/**
 * Created by guans on 2017/7/23.
 */

public class GPS2AMap {
    private CoordinateConverter coordinateConverter;
    private Context mContext;

    public GPS2AMap(Context context) {
        mContext = context;
        coordinateConverter = new CoordinateConverter(mContext);
    }

    public DPoint gps2AMap(Location location) {
        DPoint resultDPoint = null;
        coordinateConverter.from(CoordinateConverter.CoordType.GPS);
        try {
            coordinateConverter.coord(new DPoint(location.getLatitude(), location.getLongitude()));
            resultDPoint = coordinateConverter.convert();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultDPoint;
    }

    public float distance(Location location, DPoint targetDPoint) {
        return CoordinateConverter.calculateLineDistance(gps2AMap(location), targetDPoint);
    }
}
