package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.adapter.LineAdapter;
import com.example.guans.arrivied.util.LOGUtil;

public class StationChosenFragment extends Fragment implements LineAdapter.OnStationItemClickListener {
    private BusLineItem busLineItem;
    private TextView tittle;
    private MapFragment mapFragment;
    private StationItemFragment stationItemFragment;
    private OnFragmentInteractionListener mListener;
    private FragmentManager fragmentManager;

    public StationChosenFragment() {
    }
    public static StationChosenFragment newInstance() {
        StationChosenFragment fragment = new StationChosenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
        fragmentManager=getChildFragmentManager();
    }

    private void initData(Bundle savedInstanceState ) {
        Intent intent=getActivity().getIntent();
        busLineItem=intent.getParcelableExtra("BUS_LINE_ITEM");
        if(stationItemFragment==null&&savedInstanceState!=null) {
            stationItemFragment = (StationItemFragment) fragmentManager.getFragment(savedInstanceState, "STATION_ITEM_FRAGMENT");
            mapFragment = (MapFragment) fragmentManager.getFragment(savedInstanceState, "MAP_FRAGMENT");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_station_chosen, container, false);
        initView(view,savedInstanceState);
        return view ;
    }

    public void flushData(){
        Intent intent=getActivity().getIntent();
        busLineItem=intent.getParcelableExtra("BUS_LINE_ITEM");
        tittle.setText(busLineItem.getBusLineName());
        if(stationItemFragment!=null){
            stationItemFragment.getArguments().putParcelable("LINE_ITEM",busLineItem);
            stationItemFragment.flush();
        }
        if(mapFragment!=null){
            mapFragment.getArguments().putParcelable("LINE_ITEM",busLineItem);
            mapFragment.flush();
        }
    }

    private void initView(View view, final Bundle savedInstanceStat) {
        tittle=view.findViewById(R.id.line_info);
        tittle.setText(busLineItem.getBusLineName());
        RadioGroup showStyleChoose=view.findViewById(R.id.show_style_choose);
        initStationFragment();
        showStyleChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.show_in_list:
                        fragmentManager.beginTransaction().show(stationItemFragment).commit();
                        if(mapFragment!=null){
                            fragmentManager.beginTransaction().hide(mapFragment).commit();
                        }
                        break;
                    case R.id.show_in_map:
                        if(mapFragment==null){
                            mapFragment=MapFragment.newInstance("BUS_LINE_ITEM","MAP_FRAGMENT");
                            LOGUtil.logE(this,"putLine"+busLineItem.getBusLineName());
                            mapFragment.getArguments().putParcelable("LINE_ITEM",busLineItem);
                            fragmentManager.beginTransaction().add(R.id.line_item_window,mapFragment,"MAP_FRAGMENT").commit();
                        }
                        fragmentManager.beginTransaction().show(mapFragment).commit();
                        if(stationItemFragment!=null){
                            fragmentManager.beginTransaction().hide(stationItemFragment).commit();
                        }
                        break;
                }
            }
        });
    }
    private void initStationFragment() {
        if(stationItemFragment==null){
            stationItemFragment=StationItemFragment.newInstance("BUS_LINE_ITEM","STATION_ITEM_FRAGMENT");
            stationItemFragment.getArguments().putParcelable("LINE_ITEM",busLineItem);
            fragmentManager.beginTransaction().add(R.id.line_item_window,stationItemFragment,"STATION_ITEM_FRAGMENT").commit();
        }
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStationItemClick(BusStationItem busStationItem) {
        mListener.onStationItemClick(busStationItem);
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
        void onStationItemClick(BusStationItem busStationItem);
    }
}
