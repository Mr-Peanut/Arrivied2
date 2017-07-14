package com.example.guans.arrivied.view;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import  android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationSearch;
import com.example.guans.arrivied.R;

public class SearchActivity extends AppCompatActivity implements BusLineSearch.OnBusLineSearchListener{
    private BusStationQuery busStationQuery;
    private BusStationSearch busStationSearch;
    private BusLineSearch busLineSearch;
    private BusLineQuery busLineQuery;
    private SearchView busSearchView;
    private String city;
    private TextView statuText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initData();
        initView();
        initBusSearch();
    }

    private void initData() {
        Intent startIntent=getIntent();
        city=startIntent.getStringExtra("LOCATION_CITY");
    }

    private void initView() {
        busSearchView= (SearchView) findViewById(R.id.bus_search);
        busSearchView.setSubmitButtonEnabled(true);
         busSearchView.setOnSearchClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                String searchTarget= busSearchView.getQuery().toString().trim();
                 if(searchTarget.length()==0){
                     Toast.makeText(SearchActivity.this,"搜索内容不能为空",Toast.LENGTH_SHORT).show();
                     busSearchView.requestFocus();
                 }else {
                     busLineQuery=new BusLineQuery(city, BusLineQuery.SearchType.BY_LINE_NAME,searchTarget);
                     busLineSearch=new BusLineSearch(SearchActivity.this,busLineQuery);
                     busLineSearch.setOnBusLineSearchListener(SearchActivity.this);
                     busLineSearch.searchBusLineAsyn();
                 }
             }
         });
        busSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        statuText= (TextView) findViewById(R.id.statue);
        statuText.setVisibility(View.VISIBLE);
        if(city!=null){
            statuText.setText(city);
        }else {
            statuText.setText("没有获取城市信息");
        }
    }

    private void initBusSearch() {
        //        busStationSearch = new BusStationSearch(MainActivity.this, null);
//        busStationSearch.setOnBusStationSearchListener(MainActivity.this);
    }

    @Override
    public void onBusLineSearched(BusLineResult busLineResult, int i) {

    }
}
