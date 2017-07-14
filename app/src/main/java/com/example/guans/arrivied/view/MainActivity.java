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
import com.example.guans.arrivied.service.LocateService;
import com.example.guans.arrivied.receiver.LocationReceiver;
import com.example.guans.arrivied.util.LOGUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationReceiver.LocationReceiveListener,BusStationSearch.OnBusStationSearchListener {
    private LocationReceiver receiver;
    private LocationClient locationClient;
    private ServiceConnection locationServiceConnection;
    private TextView showText;
    private TextView busSearchView;
    private Button searchButton;
    private BusStationQuery busStationQuery;
    private String city;
    private BusStationSearch busStationSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        busStationSearch = new BusStationSearch(MainActivity.this, null);
        busStationSearch.setOnBusStationSearchListener(MainActivity.this);
        initView();
        initReceiver();
        bindLocationService();
    }

    private void initView() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        busSearchView= (TextView) findViewById(R.id.bus_search_view);
        searchButton= (Button) findViewById(R.id.searchButton);
        showText= (TextView) findViewById(R.id.showText);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTarget=String.valueOf(busSearchView.getText()).trim();
                if(searchTarget.length()==0){
                    Toast.makeText(MainActivity.this,"搜索的内容不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(city==null){
                    Toast.makeText(MainActivity.this,"城市不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                busStationQuery=new BusStationQuery(searchTarget,city);
               // 设置查询结果的监听
                busStationSearch.setQuery(busStationQuery);
                busStationSearch.searchBusStationAsyn();
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
        showText.setText(result.getCity());

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
    public void onBusStationSearched(BusStationResult busStationResult, int i) {
        LOGUtil.logE(this,"busstationResult"+String.valueOf(i)+" "+busStationResult.getPageCount());
        StringBuffer stringBuffer=new StringBuffer();
        for(int k=0;k<busStationResult.getBusStations().size();k++){
            BusStationItem item=busStationResult.getBusStations().get(k);
            stringBuffer.append(item.getLatLonPoint()+item.getBusStationName());
        }
        showText.append(stringBuffer);
//        List<BusStationItem> resultList=busStationResult.getBusStations();
//        if (resultList.size()==0){
//            showText.setText("noResult");
//        }
//        else {
//            for(int k=0;k<=resultList.size();i++){
//               showText.append( resultList.get(k).getBusStationName());
//            }
//        }
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
