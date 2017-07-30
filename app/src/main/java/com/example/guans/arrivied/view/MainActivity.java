package com.example.guans.arrivied.view;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.fragment.SearchResultFragment;
import com.example.guans.arrivied.fragment.StationRecordFragment;
import com.example.guans.arrivied.fragment.WatchingInfoFragment;
import com.example.guans.arrivied.receiver.ControllerReceiver;
import com.example.guans.arrivied.service.GeoFenceService;
import com.example.guans.arrivied.service.LocateService;


public class MainActivity extends AppCompatActivity implements ControllerReceiver.ControlReceiveListener, WatchingInfoFragment.OnFragmentInteractionListener, SearchResultFragment.OnFragmentInteractionListener, StationRecordFragment.OnFragmentInteractionListener {
    public static final int BUS_STATION_SEARCH_RESULT_CODE = 1;
    private static final String WATCH_INFO_FRAGMENT_TAG = "WATCH_INFO";
    private static final String SEARCH_RESULT_TAG = "SEARCH_RESULT";
    private static final String RECORD_FRAGMENT_TAG = "RECORD_FRAGMENT";
    private ControllerReceiver receiver;
    private LocationClient locationClient;
    private ServiceConnection locationServiceConnection;
    private TextView locationCity;
    private Handler mHandler;
    private Runnable locationNoResultRunnable;
    private GeoFenceClientProxy geoFenceClientProxy;
    private GeoFenServiceConnection geoFenceConnection;
    private String city;
    private BusStationItem targetStationItem;
    private BusStationItem onWatchStation;
    private FragmentManager fragmentManager;
    private WatchingInfoFragment watchingInfoFragment;
    private BusLineItem targetLineItem;
    private SearchResultFragment searchResultFragment;
    private WatchItem targetItem;
    private WatchItem onWatchItem;
    private TextView busSearch;
    private StationRecordFragment stationRecordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler=new Handler(getMainLooper());
        locationNoResultRunnable=new Runnable() {
            @Override
            public void run() {
                if(city==null) {
                    locationCity.setText("无法定位，请检查定位和网络权限后点击重试");
                }
              }
            };
        fragmentManager=getSupportFragmentManager();
        initView();
        showRecordFragment(savedInstanceState);
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
//       findViewById(R.id.openMap).setOnClickListener(new View.OnClickListener() {
//           @Override
//           public void onClick(View view) {
//               Intent mapIntent=new Intent(MainActivity.this,MapActivity.class);
//               mapIntent.setAction(SHOW_STATION_ITEM_ACTION);
//               mapIntent.putExtra("StationItem",targetStationItem);
//               startActivity(mapIntent);
//           }
//       });
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if (getSupportActionBar() != null)
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        busSearch = (TextView) findViewById(R.id.bus_search);
        locationCity= (TextView) findViewById(R.id.locationCity);
        locationCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationCity.setText("重新定位中请稍后");
                locationClient.startLocateOneTime();
                mHandler.postDelayed(locationNoResultRunnable,5000);
            }
        });
        busSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stationSearchIntent = new Intent(MainActivity.this,SearchActivity.class);
                stationSearchIntent.putExtra("LOCATION_CITY",city);
                startActivityForResult(stationSearchIntent,BUS_STATION_SEARCH_RESULT_CODE);
            }
        });

    }

    private void showRecordFragment(Bundle savedInstanceState) {

        if (stationRecordFragment == null) {
            if (savedInstanceState != null) {
                fragmentManager.getFragment(savedInstanceState, RECORD_FRAGMENT_TAG);
            }
            if (stationRecordFragment == null) {
                fragmentManager.findFragmentByTag(RECORD_FRAGMENT_TAG);
            }
            if (stationRecordFragment == null) {
                stationRecordFragment = StationRecordFragment.newInstance();
                fragmentManager.beginTransaction().add(R.id.record, stationRecordFragment, RECORD_FRAGMENT_TAG).commit();
            }
            fragmentManager.beginTransaction().show(stationRecordFragment).commit();
        }
    }
    private void bindLocationService() {
        Intent locationIntent = new Intent(this, LocateService.class);
        locationIntent.setAction(LocateService.ACTION_LOCATION_BIND);
        locationServiceConnection = new LocationServiceConnection();
        bindService(locationIntent, locationServiceConnection, Service.BIND_AUTO_CREATE);
    }
    private void showWatchInfoFragment() {
        if (watchingInfoFragment != null) {
//            watchingInfoFragment.getArguments().putParcelable("STATION_ITEM", onWatchStation);
            watchingInfoFragment.getArguments().putParcelable("ON_WATCH_ITEM", onWatchItem);
            fragmentManager.beginTransaction().show(watchingInfoFragment).commit();
            watchingInfoFragment.flush();
        } else {
            watchingInfoFragment = (WatchingInfoFragment) fragmentManager.findFragmentByTag(WATCH_INFO_FRAGMENT_TAG);
            if (watchingInfoFragment != null) {
                watchingInfoFragment.getArguments().putParcelable("ON_WATCH_ITEM", onWatchItem);
                fragmentManager.beginTransaction().show(watchingInfoFragment).commit();
                watchingInfoFragment.flush();
            } else {
                watchingInfoFragment = WatchingInfoFragment.newInstance();
                fragmentManager.beginTransaction().add(R.id.taskStatue, watchingInfoFragment, WATCH_INFO_FRAGMENT_TAG).commit();
                watchingInfoFragment.getArguments().putParcelable("ON_WATCH_ITEM", onWatchItem);
                fragmentManager.beginTransaction().show(watchingInfoFragment).commit();
            }
        }
        if(searchResultFragment!=null){
            fragmentManager.beginTransaction().hide(searchResultFragment).commit();
        }
    }
    private void showSearchResult(){
        if (searchResultFragment!= null) {
            searchResultFragment.getArguments().putParcelable("WATCH_ITEM",targetItem);
            fragmentManager.beginTransaction().show(searchResultFragment).commit();
            searchResultFragment.flush();
        } else {
            searchResultFragment = (SearchResultFragment) fragmentManager.findFragmentByTag(SEARCH_RESULT_TAG);
            if (searchResultFragment!= null) {
                searchResultFragment.getArguments().putParcelable("WATCH_ITEM",targetItem);
                fragmentManager.beginTransaction().show(searchResultFragment).commit();
                searchResultFragment.flush();
            } else {
                searchResultFragment = SearchResultFragment.newInstance();
                fragmentManager.beginTransaction().add(R.id.taskStatue, searchResultFragment, SEARCH_RESULT_TAG).commit();
                searchResultFragment.getArguments().putParcelable("WATCH_ITEM",targetItem);
                fragmentManager.beginTransaction().show(searchResultFragment).commit();
            }
        }
        if(watchingInfoFragment!=null){
            fragmentManager.beginTransaction().hide(watchingInfoFragment).commit();
        }
//        if(stationRecordFragment!=null){
//            fragmentManager.beginTransaction().hide(stationRecordFragment).commit();
//        }
    }

    private void initReceiver() {
        if (receiver==null)
            receiver=new ControllerReceiver(this);
        IntentFilter intentFilter = new IntentFilter(LocateService.ACTION_LOCATION_RESULT);
        intentFilter.addAction(GeoFenceService.ADD_GEOFENCE_SUCCESS_ACTION);
        intentFilter.addAction(GeoFenceService.ACTION_GEOFENCE_REMOVED);
        registerReceiver(receiver,intentFilter);
    }
    @Override
    public void onControlBroadcastReceive(Intent intent) {
        switch (intent.getAction()){
            case LocateService.ACTION_LOCATION_RESULT:
                AMapLocation result=intent.getParcelableExtra("LocationResult");
                            if (result.getErrorCode() == 0) {
                                mHandler.removeCallbacks(locationNoResultRunnable);
                                city=result.getCity();
                                locationCity.setText(result.getCity());
                                busSearch.setVisibility(View.VISIBLE);
                            }else {
                                showErrorPage();
                            }
                break;
            case GeoFenceService.ADD_GEOFENCE_SUCCESS_ACTION:
                onWatchItem=intent.getParcelableExtra("ON_WATCH_ITEM");
                showWatchInfoFragment();
                break;
            case GeoFenceService.ACTION_GEOFENCE_REMOVED:
                onWatchStation=null;
                if(!watchingInfoFragment.isHidden())
                fragmentManager.beginTransaction().hide(watchingInfoFragment).commit();
                break;
        }

    }

    private void showErrorPage() {
        busSearch.setVisibility(View.GONE);

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
                    targetItem=data.getParcelableExtra("TARGET_ITEM");
                   showSearchResult();
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRecordItemSelected(WatchItem watchItem) {
        targetItem = watchItem;
        showSearchResult();
    }

    @Override
    public void OnStartWatchClicked(WatchItem targetItem) {
        Intent watchIntent=new Intent(MainActivity.this, GeoFenceService.class);
        watchIntent.setAction("com.example.guans.arrivied.service.GeoFenceService.ADD_GEOFENCE");
        watchIntent.putExtra("TARGET_ITEM",targetItem);
        startService(watchIntent);

    }

    @Override
    public void onCancelWatchingClick(View view) {
        if(geoFenceClientProxy!=null){
            geoFenceClientProxy.removeDPoint();
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
                onWatchItem=geoFenceClientProxy.getWatchItem();
                showWatchInfoFragment();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            geoFenceClientProxy=null ;
        }
    }
}
