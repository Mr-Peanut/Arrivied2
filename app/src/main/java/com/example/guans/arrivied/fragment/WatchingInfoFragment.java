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
import com.example.guans.arrivied.util.LOGUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WatchingInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WatchingInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WatchingInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WatchingInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WatchingInfoFragment newInstance(String param1, String param2) {
        WatchingInfoFragment fragment = new WatchingInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        showStatueTextView.setText("正在监控"+onWatchItem.getBusStationItem().getBusStationName()+"/n"+onWatchItem.getBusLineItem().getBusLineName());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void onCancelWatchingClick(View view);
    }
}

