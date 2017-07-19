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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StationChosenFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationChosenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationChosenFragment extends Fragment implements LineAdapter.OnStationItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BusLineItem busLineItem;
    private TextView tittle;
    private MapFragment mapFragment;
    private StationItemFragment stationItemFragment;
    private OnFragmentInteractionListener mListener;
    private FragmentManager fragmentManager;

    public StationChosenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationChosenFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationChosenFragment newInstance(String param1, String param2) {
        StationChosenFragment fragment = new StationChosenFragment();
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
        }
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
        // Inflate the layout for this fragment
        LOGUtil.logE(this,"OnceateView");
        View view=inflater.inflate(R.layout.fragment_station_chosen, container, false);
        initView(view,savedInstanceState);
        return view ;
    }

    @Override
    public void onPause() {
        LOGUtil.logE(this,"onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        LOGUtil.logE(this,"OnResume");
        super.onResume();

    }
    public void flushData(){
//        lineAdapter.setBusLineItem(busLineItem=getActivity().getIntent().getParcelableExtra("BUS_LINE_ITEM"));
        //刷新对应fragment的数据
        tittle.setText(busLineItem.getBusLineName());
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
            LOGUtil.logE(this,"putLine"+busLineItem.getBusLineName());
            fragmentManager.beginTransaction().add(R.id.line_item_window,stationItemFragment,"STATION_ITEM_FRAGMENT").commit();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        void onStationItemClick(BusStationItem busStationItem);
    }
}
