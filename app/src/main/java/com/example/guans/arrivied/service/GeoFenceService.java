package com.example.guans.arrivied.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.util.LOGUtil;

import java.util.List;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;

public class GeoFenceService extends Service implements GeoFenceListener {
    public static final int ADD_DPOINT = 1;
    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    public static final String ADD_DPOINT_ACTION="com.example.guans.arrivied.service.GeoFenceService.ADD_GEOFENCE";
    private GeoFenceClient mGeoFenceClient;
    private GeoFenceClientProxy mGeoFenceClientProxy;
    public GeoFenceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mGeoFenceClient == null) {
            mGeoFenceClient = new GeoFenceClient(getApplicationContext());
            mGeoFenceClient.setActivateAction(GEOFENCE_IN | GEOFENCE_OUT | GEOFENCE_STAYED);
            //创建并设置PendingIntent
            mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
            mGeoFenceClient.setGeoFenceListener(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            //先实现单点提醒，后续可以实现多点提醒
            mGeoFenceClient.removeGeoFence();
            DPoint dPoint = new DPoint();
            dPoint.setLatitude(intent.getDoubleExtra("Latitude", 0));
            dPoint.setLongitude(intent.getDoubleExtra("Longitude", 0));
            mGeoFenceClient.addGeoFence(dPoint, 10, "BUS_STATION");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mGeoFenceClientProxy == null) {
            mGeoFenceClientProxy = new GeoFenceClientProxy(mGeoFenceClient);
        }
        return mGeoFenceClientProxy;
    }

    @Override
    public void onDestroy() {
        if (!mGeoFenceClient.getAllGeoFence().isEmpty())
            mGeoFenceClient.removeGeoFence();
        super.onDestroy();
    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
            LOGUtil.logE(this, "围栏创建成功" + s);
            //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
        } else {
            //geoFenceList就是已经添加的围栏列表
            LOGUtil.logE(this, "围栏创建失败" + s);
        }

    }
}
