package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.adapter.StationRecordAdapter;
import com.example.guans.arrivied.bean.StationRecordItem;
import com.example.guans.arrivied.bean.WatchItem;

public class StationRecordFragment extends Fragment implements StationRecordAdapter.RecordClickedListener, BusLineSearch.OnBusLineSearchListener {
    private OnFragmentInteractionListener mListener;
    private BusLineSearch busLineSearch;
    private BusLineQuery busLineQuery;
    private StationRecordItem selectStationRecordItem;

    public StationRecordFragment() {
        // Required empty public constructor
    }

    public static StationRecordFragment newInstance() {
        StationRecordFragment fragment = new StationRecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_station_record, container, false);
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
    public void OnRecordClicked(StationRecordItem stationRecordItem) {
        selectStationRecordItem = stationRecordItem;
        if (busLineQuery == null) {
            busLineQuery = new BusLineQuery(stationRecordItem.getLineID(), BusLineQuery.SearchType.BY_LINE_ID, stationRecordItem.getCityCode());
        } else {
            busLineQuery.setCity(stationRecordItem.getCityCode());
            busLineQuery.setQueryString(stationRecordItem.getLineID());
        }
        if (busLineSearch == null) {
            busLineSearch = new BusLineSearch(getContext(), busLineQuery);
            busLineSearch.setOnBusLineSearchListener(this);
        } else {
            busLineSearch.setQuery(busLineQuery);
        }
        busLineSearch.searchBusLineAsyn();
        showProgress();
    }

    private void showProgress() {
    }

    @Override
    public void onBusLineSearched(BusLineResult busLineResult, int i) {
        if (i == 1000) {
            BusStationItem targetStation = null;
            BusLineItem targetLine = null;
            for (BusLineItem busLineItem : busLineResult.getBusLines()) {
                for (BusStationItem busStationItem : busLineItem.getBusStations()) {
                    if (busStationItem.getBusStationName().equals(selectStationRecordItem.getStationName()) && busStationItem.getBusStationId().equals(selectStationRecordItem.getStationID())) {
                        targetLine = busLineItem;
                        targetStation = busStationItem;
                    }
                }
            }
            if (targetLine != null) {
                WatchItem selectRecordItem = new WatchItem(targetLine, targetStation);
                mListener.onRecordItemSelected(selectRecordItem);
            } else {
                showError();
            }
        } else {
            showError();
        }
        selectStationRecordItem = null;
    }

    private void showError() {
        deleteRecord(selectStationRecordItem);
        selectStationRecordItem = null;
    }

    private void deleteRecord(StationRecordItem selectStationRecordItem) {
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void onRecordItemSelected(WatchItem watchItem);
    }
}
