package com.example.guans.arrivied.view;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.service.GeoFenceService;
import com.example.guans.arrivied.service.LocateService;
import com.example.guans.arrivied.receiver.ControllerReceiver;
import com.example.guans.arrivied.util.LOGUtil;


public class MainActivity extends AppCompatActivity implements ControllerReceiver.ControlReceiveListener {
    private ControllerReceiver receiver;
    private LocationClient locationClient;
    private ServiceConnection locationServiceConnection;
    private TextView onWatching;
    private TextView locationCity;
    private Handler mHandler;
    private Runnable locationNoResultRunnable;
    private GeoFenceClientProxy geoFenceClientProxy;
    private GeoFenServiceConnection geoFenceConnection;

    private String city;
    private BusStationItem targetStationItem;
    private TextView targetStationText;
    private Button start_watch;
    private Button cancel_watch;
    public static final int BUS_STATION_SEARCH_RESULT_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler=new Handler(getMainLooper());
        locationNoResultRunnable=new Runnable() {
            @Override
            public void run() {
                if(city==null) {
                    locationCity.setText("无法定位");
                }
              }
            };
        initView();
        initReceiver();
        bindLocationService();
        bindGeoFenceService();
    }

    private void bindGeoFenceService() {
        geoFenceConnection=new GeoFenServiceConnection();
        Intent geoFenceServiceIntent=new Intent(MainActivity.this,GeoFenceService.class);
        bindService(geoFenceServiceIntent,geoFenceConnection,BIND_AUTO_CREATE);
    }
    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView busSearch = (TextView) findViewById(R.id.bus_search);
        onWatching = (TextView) findViewById(R.id.onWatch);
        locationCity= (TextView) findViewById(R.id.locationCity);
        cancel_watch= (Button) findViewById(R.id.cancel_watch);

        cancel_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(geoFenceClientProxy!=null){
                    geoFenceClientProxy.removeDPoint();
                }
            }
        });
        locationCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationCity.setText("重新定位中请稍后");
                locationClient.startLocateOneTime();
                mHandler.postDelayed(locationNoResultRunnable,5000);
            }
        });
        targetStationText= (TextView) findViewById(R.id.station_result);
        start_watch= (Button) findViewById(R.id.start_watch);
        busSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stationSearchIntent = new Intent(MainActivity.this,SearchActivity.class);
                stationSearchIntent.putExtra("LOCATION_CITY",city);
                startActivityForResult(stationSearchIntent,BUS_STATION_SEARCH_RESULT_CODE);
            }
        });
        start_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent watchIntent=new Intent(MainActivity.this, GeoFenceService.class);
                watchIntent.setAction("com.example.guans.arrivied.service.GeoFenceService.ADD_GEOFENCE");
                // dPoint.setLatitude(intent.getDoubleExtra("Latitude", 0));
//                dPoint.setLongitude(intent.getDoubleExtra("Longitude", 0));
                watchIntent.putExtra("Latitude",targetStationItem.getLatLonPoint().getLatitude());
                watchIntent.putExtra("Longitude",targetStationItem.getLatLonPoint().getLongitude());
                startService(watchIntent);
            }
        });
    }

    private void bindLocationService() {
        Intent locationIntent = new Intent(this, LocateService.class);
        locationIntent.setAction(LocateService.ACTION_LOCATION_BIND);
        locationServiceConnection = new LocationServiceConnection();
        bindService(locationIntent, locationServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private void initReceiver() {
        if (receiver==null)
            receiver=new ControllerReceiver();
        receiver.setControlListener(this);
        IntentFilter intentFilter = new IntentFilter(LocateService.ACTION_LOCATION_RESULT);
        intentFilter.addAction(GeoFenceService.ADD_GEOFENCE_SUCCESS_ACTION);
        intentFilter.addAction(GeoFenceService.ACTION_GEOFENCE_REMOVED);
        registerReceiver(receiver,intentFilter);
    }
    @Override
    public void onBroadcastReceive(Intent intent) {
        switch (intent.getAction()){
            case LocateService.ACTION_LOCATION_RESULT:
                mHandler.removeCallbacks(locationNoResultRunnable);
                AMapLocation result=intent.getParcelableExtra("LocationResult");
                city=result.getCity();
                locationCity.setText(result.getCity());
                break;
            case GeoFenceService.ADD_GEOFENCE_SUCCESS_ACTION:
//                onWatching.setText("正在监控"+geoFenceClientProxy.getGeoFences().get(0).getPoiItem().toString());
                onWatching.setText("正在监控");
//                LOGUtil.logE(this,"proxy is null"+String.valueOf(geoFenceClientProxy==null));
//                LOGUtil.logE(this,"GeoFences is null"+String.valueOf(geoFenceClientProxy.getGeoFences()==null));
//                LOGUtil.logE(this,"item is null"+String.valueOf(geoFenceClientProxy.getGeoFences().get(0)==null));
                break;
            case GeoFenceService.ACTION_GEOFENCE_REMOVED:
                onWatching.setText("没有监控对象");
                break;
        }

    }
    @Override
    protected void onDestroy() {
        if (receiver != null) {
            receiver.setControlListener(null);
            unregisterReceiver(receiver);
        }
        unbindService(locationServiceConnection);
        unbindService(geoFenceConnection);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case BUS_STATION_SEARCH_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    targetStationItem=data.getParcelableExtra("STATION_ITEM") ;
                    initViewAfterGetStation();
                }
                break;
            default:
                break;
        }
    }

    private void initViewAfterGetStation() {
        if(targetStationItem!=null){
            targetStationText.setText(targetStationItem.getBusStationName());
            start_watch.setClickable(true);
        }  else {
            onWatching.setText("没有拿到任何结果");
        }
    }

    private class LocationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationClient = (LocationClient) iBinder;
            locationClient.startLocateOneTime();
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationClient = null;
        }
    }
    private class GeoFenServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            geoFenceClientProxy= (GeoFenceClientProxy) iBinder;
            if(geoFenceClientProxy!=null&&geoFenceClientProxy.getGeoFences()!=null&&geoFenceClientProxy.getGeoFences().size()!=0){
                onWatching.setText("正在监控");
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            geoFenceClientProxy=null ;
        }
    }
}
