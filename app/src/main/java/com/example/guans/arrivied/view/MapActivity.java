package com.example.guans.arrivied.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;

public class MapActivity extends AppCompatActivity implements AMap.OnMarkerDragListener {
    private SupportMapFragment mapFragment;
    private AMap aMap;
    private Marker mMarker;
    private BusStationItem targetBustStationItem;
    private MyLocationStyle myLocationStyle;
    public final static String SHOW_STATION_ITEM_ACTION="com.example.guans.arrivied.view.MapActivity.SHOW_STATION_ITEM";
    public final static String SHOW_BUS_LINE_ACTION="com.example.guans.arrivied.view.MapActivity.SHOW_BUS_LINE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        initData();
    }

    private void initData() {
        Intent dataIntent=getIntent();
        switch (dataIntent.getAction()){
            case SHOW_STATION_ITEM_ACTION:
                showStationItem(dataIntent);
                break;
            case SHOW_BUS_LINE_ACTION:
                showLine(dataIntent);
                break;
        }

    }
    private void showLine(Intent dataIntent) {
    }

    private void showStationItem(Intent dataIntent) {
        targetBustStationItem=dataIntent.getParcelableExtra("StationItem");
        MarkerOptions markerOptions=new MarkerOptions()
                .position(new LatLng(targetBustStationItem.getLatLonPoint().getLatitude(),targetBustStationItem.getLatLonPoint().getLongitude()))
                .title(targetBustStationItem.getBusStationName())
                .visible(true);
//        Marker originalMarker=new Marker(new MarkerOptions());
//        originalMarker.setPosition();
        mMarker =aMap.addMarker(markerOptions);
        mMarker.showInfoWindow();
        mMarker.setDraggable(true);
    }


    private void initView() {
        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        aMap=mapFragment.getMap();
        aMap.setOnMarkerDragListener(this);
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//以下三种模式从5.1.0版本开始提供
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
    }

    @Override
    protected void onDestroy() {
        if(mMarker!=null)
        mMarker.destroy();
        super.onDestroy();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {


    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
