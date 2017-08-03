package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.WatchItem;

public class SearchResultFragment extends Fragment {
    private BusStationItem targetStation;
    private BusLineItem targetLine;
    private TextView targetStationName;
    private TextView targetLineName;
    private Button startWatch;
    private WatchItem targetItem;

    private OnFragmentInteractionListener mListener;

    public SearchResultFragment() {
    }

    public static SearchResultFragment newInstance() {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetItem = getArguments().getParcelable("WATCH_ITEM");

        }
//        targetStation=getArguments().getParcelable("STATION_ITEM");
//        targetLine=getArguments().getParcelable("LINE_ITEM");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_seach_result, container, false);
        targetLineName = contentView.findViewById(R.id.target_line_name);
        targetStationName = contentView.findViewById(R.id.target_station_name);
        targetStationName.setText(targetItem.getBusStationItem().getBusStationName());
        targetLineName.setText(targetItem.getBusLineItem().getBusLineName());
        startWatch = contentView.findViewById(R.id.start_watch);
        startWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnStartWatchClicked(targetItem);
            }
        });
        return contentView;
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

    public void flush() {
//        targetStation=getArguments().getParcelable("STATION_ITEM");
//        targetLine=getArguments().getParcelable("LINE_ITEM");
        targetItem = getArguments().getParcelable("WATCH_ITEM");
        targetStationName.setText(targetItem != null ? targetItem.getBusStationItem().getBusStationName() : null);
        targetLineName.setText(targetItem.getBusLineItem().getBusLineName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        void OnStartWatchClicked(WatchItem targetStation);

        void showProgressBar();

        void dismissProgressBar();
    }
}
