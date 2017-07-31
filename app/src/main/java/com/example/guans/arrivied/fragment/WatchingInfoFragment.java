package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.WatchItem;

public class WatchingInfoFragment extends Fragment {

    private Button cancelWatchButton;
    private TextView showStatueTextView;
    private BusStationItem busStationItem;
    private BusLineItem busLineItem;
    private ImageButton showMapButton;
    private WatchItem onWatchItem;

    private OnFragmentInteractionListener mListener;

    public WatchingInfoFragment() {
        // Required empty public constructor
    }

    public static WatchingInfoFragment newInstance() {
        WatchingInfoFragment fragment = new WatchingInfoFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

//            busStationItem=getArguments().getParcelable("STATION_ITEM");
//            busStationItem=getArguments().getParcelable("LINE_ITEM");
            onWatchItem=getArguments().getParcelable("ON_WATCH_ITEM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView=inflater.inflate(R.layout.fragment_watching_info, container, false);
        cancelWatchButton=contentView.findViewById(R.id.cancel_watch);
        showStatueTextView=contentView.findViewById(R.id.station_info);
        showStatueTextView.setText("正在监控"+onWatchItem.getBusStationItem().getBusStationName()+"\n"+onWatchItem.getBusLineItem().getBusLineName());
        cancelWatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCancelWatchingClick(view);
            }
        });
        return contentView ;
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
    public void flush(){
        onWatchItem=getArguments().getParcelable("ON_WATCH_ITEM");
//        showStatueTextView.setText("正在监控"+busStationItem.getBusStationName());
        showStatueTextView.setText("正在监控" + (onWatchItem != null ? onWatchItem.getBusStationItem().getBusStationName() : null) + "/n" + (onWatchItem != null ? onWatchItem.getBusLineItem().getBusLineName() : null));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
        void onCancelWatchingClick(View view);

        void showProgressBar();

        void dismissProgressBar();
    }
}

