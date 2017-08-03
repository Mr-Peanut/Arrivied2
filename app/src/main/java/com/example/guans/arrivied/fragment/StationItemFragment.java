package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.adapter.LineAdapter;
import com.example.guans.arrivied.view.ItemDivider;

public class StationItemFragment extends Fragment implements LineAdapter.OnStationItemClickListener {
    private BusLineItem busLineItem;
    private RecyclerView stationList;
    private LineAdapter lineAdapter;

    private OnFragmentInteractionListener mListener;

    public StationItemFragment() {
    }

    public static StationItemFragment newInstance() {
        StationItemFragment fragment = new StationItemFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            busLineItem = getArguments().getParcelable("LINE_ITEM");
        }

        lineAdapter = new LineAdapter(getActivity(), busLineItem);
        lineAdapter.setStationItemClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_item, container, false);
        stationList = view.findViewById(R.id.stationList);
        stationList.setLayoutManager(new LinearLayoutManager(getContext()));
        stationList.setAdapter(lineAdapter);
        stationList.addItemDecoration(new ItemDivider());
        return view;
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

    public void flush() {
        busLineItem = getArguments().getParcelable("LINE_ITEM");
        lineAdapter.setBusLineItem(busLineItem);
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
