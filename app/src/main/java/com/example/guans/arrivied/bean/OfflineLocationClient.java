package com.example.guans.arrivied.bean;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.example.guans.arrivied.util.GCPoint;
import com.example.guans.arrivied.util.LOGUtil;

/**
 * Created by guans on 2017/7/22.
 */

public class OfflineLocationClient extends Binder {
    private Context mContext;
    private LocationManager locationManager;
    //    private GPS2AMap gps2AMap;
    private LocationListener locationListener;

    public OfflineLocationClient(Context mContext) {
        this.mContext = mContext;
        locationManager = (LocationManager) mContext.getSystemService(Service.LOCATION_SERVICE);
//        gps2AMap=new GPS2AMap(mContext);
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public void startLocation(long invigiat, float distance, LocationListener locationListener) {
        setLocationListener(locationListener);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            LOGUtil.logE(this, "No permission");
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        DPoint dPoint=gps2AMap.gps2AMap(last);
//        LOGUtil.logE(this,"转换之后的坐标"+String.valueOf(dPoint.getLatitude())+"/"+String.valueOf(dPoint.getLongitude()));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, invigiat, distance, locationListener);
    }

    public void stopLocation(LocationListener locationListener) {
        locationManager.removeUpdates(locationListener);
    }

    public void addProximityAlert(LatLonPoint latLonPoint, float radius, long timeAlarm, PendingIntent alarmIntent) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "no location permission", Toast.LENGTH_SHORT).show();
            return;
        }
        removeProximityAlert(alarmIntent);
        GCPoint gcPoint = new GCPoint(latLonPoint);
        locationManager.addProximityAlert(gcPoint.getLatitude(), gcPoint.getLongitude(), radius, timeAlarm, alarmIntent);
    }

    public void removeProximityAlert(PendingIntent pendingIntent) {
        locationManager.removeProximityAlert(pendingIntent);
    }
}
