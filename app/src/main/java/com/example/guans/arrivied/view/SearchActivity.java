package com.example.guans.arrivied.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.fragment.BusSearchFragment;
import com.example.guans.arrivied.fragment.MapFragment;
import com.example.guans.arrivied.fragment.StationChosenFragment;
import com.example.guans.arrivied.fragment.StationItemFragment;
import com.example.guans.arrivied.util.LOGUtil;

public class SearchActivity extends AppCompatActivity implements BusSearchFragment.OnFragmentInteractionListener, StationChosenFragment.OnFragmentInteractionListener, StationItemFragment.OnFragmentInteractionListener, MapFragment.OnFragmentInteractionListener {
    private BusSearchFragment busSearchFragment;
    private StationChosenFragment stationChosenFragment;
    private FragmentManager fragmentManager;
    private BusLineItem busLineItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fragmentManager = getSupportFragmentManager();
        busSearchFragment = BusSearchFragment.newInstance();
        fragmentManager.beginTransaction()
                .add(R.id.container, busSearchFragment, "searchFragment").commit();
//                replace(R.id.container,busSearchFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onStationItemClick(BusStationItem busStationItem) {
        Intent intent = new Intent();
        if (busStationItem != null) {
            intent.putExtra("TARGET_ITEM", new WatchItem(busLineItem, busStationItem));
//            intent.putExtra("STATION_ITEM",busStationItem);
//            intent.putExtra("LINE_ITEM",busLineItem);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onLineItemClicked(BusLineItem targetLineItem) {
        //打开站点选择fragment
        busLineItem = targetLineItem;
        getIntent().putExtra("BUS_LINE_ITEM", targetLineItem);
        LOGUtil.logE(this, targetLineItem.getBusLineName());
        if (stationChosenFragment == null) {
            stationChosenFragment = StationChosenFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.container, stationChosenFragment, "stationChosenFragment").commit();
        } else
            stationChosenFragment.flushData();
        fragmentManager.beginTransaction().show(stationChosenFragment).hide(busSearchFragment).addToBackStack("searchFragment").commit();

    }
}

