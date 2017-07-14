package com.example.guans.arrivied.view;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.service.GeoFenceService;
import com.example.guans.arrivied.service.LocateService;
import com.example.guans.arrivied.receiver.LocationReceiver;
import com.example.guans.arrivied.util.LOGUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationReceiver.LocationReceiveListener{
    private LocationReceiver receiver;
    private LocationClient locationClient;
    private ServiceConnection locationServiceConnection;
    private TextView showText;
    private TextView busSearch;
    private TextView locationCity;

    private String city;
    private BusStationItem targetStationItem;
    private TextView targetStationText;
    private Button start_watch;
    public static final int BUS_STATION_SEARCH_RESULT_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initReceiver();
        bindLocationService();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        busSearch= (TextView) findViewById(R.id.bus_search);
        showText= (TextView) findViewById(R.id.showText);
        locationCity= (TextView) findViewById(R.id.locationCity);
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
        LOGUtil.logE(this, "bindLocationService");
        Intent locationIntent = new Intent(this, LocateService.class);
        locationIntent.setAction(LocateService.ACTION_LOCATION_BIND);
        locationServiceConnection = new LocationServiceConnection();
        bindService(locationIntent, locationServiceConnection, Service.BIND_AUTO_CREATE);
    }

    private void initReceiver() {
        if (receiver==null)
            receiver=new LocationReceiver();
        receiver.setLocationListener(this);
        IntentFilter intentFilter = new IntentFilter(LocateService.ACTION_LOCATION_RESULT);
        registerReceiver(receiver,intentFilter);
    }


    @Override
    public void onBroadcastReceive(Intent intent) {
        AMapLocation result=intent.getParcelableExtra("LocationResult");
        city=result.getCity();
        LOGUtil.logE(this, city);
        locationCity.setText(result.getCity());
    }
    @Override
    protected void onDestroy() {
        if (receiver != null) {
            receiver.setLocationListener(null);
            unregisterReceiver(receiver);
        }
        unbindService(locationServiceConnection);
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
            targetStationText.append(targetStationItem.getBusStationName());
            start_watch.setClickable(true);
        }  else {
            showText.setText("没有拿到任何结果");
        }


    }

    private class LocationServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationClient = (LocationClient) iBinder;
            LOGUtil.logE(this, "onServiceConnection");
            locationClient.startLocateOneTime();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationClient = null;
        }
    }
}
