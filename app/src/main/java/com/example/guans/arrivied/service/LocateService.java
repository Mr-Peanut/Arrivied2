package com.example.guans.arrivied.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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
//            if (amapLocation.getErrorCode() == 0) {
//                //定位成功回调信息，设置相关消息
//                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                amapLocation.getLatitude();//获取纬度
//                amapLocation.getLongitude();//获取经度
//                amapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(amapLocation.getTime());
//                df.format(date);//定位时间
//            } else {
//                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                Log.e("AmapError","location Error, ErrCode:"
//                        + amapLocation.getErrorCode() + ", errInfo:"
//                        + amapLocation.getErrorInfo());
//            }
            locationResultIntent.putExtra("LocationResult", amapLocation);
            sendBroadcast(locationResultIntent);
            LOGUtil.logE(this, amapLocation.getAddress());

        }
        LOGUtil.logE(this, "getLocationResult");
    }


}
