package com.example.guans.arrivied.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.adapter.StationRecordAdapter;
import com.example.guans.arrivied.bean.StationRecordItem;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.database.StationsRecordHelper;
import com.example.guans.arrivied.view.ItemDivider;

public class StationRecordFragment extends Fragment implements StationRecordAdapter.RecordClickedListener, BusLineSearch.OnBusLineSearchListener {
    private OnFragmentInteractionListener mListener;
    private BusLineSearch busLineSearch;
    private BusLineQuery busLineQuery;
    private StationRecordItem selectStationRecordItem;
    private TextView recordStatue;
    private RecyclerView recordList;
    private StationRecordAdapter recordAdapter;
    private RecordUpdateReceiver recordUpdateReceiver;

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
        recordAdapter = new StationRecordAdapter(getContext());
        recordAdapter.setRecordClickedListener(this);
        recordUpdateReceiver = new RecordUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(StationsRecordHelper.RECORD_UPDATED_ACTION);
        getActivity().registerReceiver(recordUpdateReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_record, container, false);
        recordStatue = view.findViewById(R.id.record_statue);
        recordList = view.findViewById(R.id.record_list);
        recordList.setAdapter(recordAdapter);
        recordList.addItemDecoration(new ItemDivider());
        return view;
    }

    public void flush() {
        recordAdapter.flushData();
    }

    @Override
    public void onDestroy() {
        recordAdapter.closeCursor();
        if (recordUpdateReceiver != null) {
            getActivity().unregisterReceiver(recordUpdateReceiver);
        }
        super.onDestroy();
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

    class RecordUpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case StationsRecordHelper.RECORD_UPDATED_ACTION:
                    recordAdapter.flushData();
                    break;
            }
        }
    }
}
