package com.example.guans.arrivied.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amap.api.interfaces.IMarker;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.SupportMapFragment;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.guans.arrivied.R;

public class MapActivity extends AppCompatActivity {
    private SupportMapFragment mapFragment;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        initView();
        initData();
    }

    private void initData() {
        Intent dataIntent=getIntent();
        MarkerOptions markerOptions=new MarkerOptions().position(new LatLng(31.929,120.315)).title("江阴赛勒罗亚车业有限公司").visible(true);
//        Marker originalMarker=new Marker(new MarkerOptions());
//        originalMarker.setPosition();
        aMap.addMarker(markerOptions);
    }

    private void initView() {
        mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        aMap=mapFragment.getMap();
    }
}
