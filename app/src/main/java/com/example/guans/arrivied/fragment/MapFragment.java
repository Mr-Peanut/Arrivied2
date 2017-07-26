package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapException;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.core.LatLonPoint;
import com.example.guans.arrivied.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapFragment extends Fragment implements AMap.OnMarkerClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private AMap aMap;
    private MapView mapView;
    private Marker mMarker;
    private BusStationItem targetBustStationItem;
    private MyLocationStyle myLocationStyle;
    private BusLineItem busLineItem;
    private  List<LatLonPoint> lineLatLonPoints;
    private PolylineOptions polyLineOptions;
    private List<Marker> markers;


    private OnFragmentInteractionListener mListener;
    private CameraPosition cameraPosition;

    public MapFragment() {
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        busLineItem=getArguments().getParcelable("LINE_ITEM");
        markers=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_map, container, false);
        mapView=view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        aMap=mapView.getMap();
        aMap.setOnMarkerClickListener(this);
        cameraPosition=aMap.getCameraPosition();
        initLocationPoint();
        initBusLineOnMap();
        return view;
    }
    private void initBusLineOnMap() {
        if(busLineItem!=null){
            markers.clear();
           lineLatLonPoints= busLineItem.getDirectionsCoordinates();
            polyLineOptions=new PolylineOptions();
           for(LatLonPoint point:lineLatLonPoints){
               polyLineOptions.add(new LatLng(point.getLatitude(),point.getLongitude()));
           }
           aMap.addPolyline(polyLineOptions);
            List<BusStationItem> stationItems=busLineItem.getBusStations();
            for(BusStationItem busStationItem:stationItems){
                Marker marker=aMap.addMarker(new MarkerOptions()
                        .position(new LatLng(busStationItem.getLatLonPoint().getLatitude(),busStationItem.getLatLonPoint().getLongitude())));
                marker.setTitle(busStationItem.getBusStationName());
                marker.showInfoWindow();
                marker.setDraggable(false);
                marker.setObject(busStationItem);
                markers.add(marker);
            }
           List<LatLonPoint> latLngBounds=busLineItem.getBounds();
            LatLonPoint leftUp=latLngBounds.get(0);
            LatLonPoint rightDown=latLngBounds.get(1);
            LatLng center=new LatLng((leftUp.getLongitude()+rightDown.getLongitude())/2,(leftUp.getLatitude()+rightDown.getLatitude())/2);
            try {
                LatLngBounds latLngBounds1=new LatLngBounds(new LatLng(leftUp.getLatitude(),leftUp.getLongitude()),new LatLng(rightDown.getLatitude(),rightDown.getLongitude()));
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngBounds(latLngBounds1,450);
                aMap.moveCamera(cameraUpdate);
            } catch (AMapException e) {
                e.printStackTrace();
            }
        }
    }
    private void initLocationPoint() {
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(5000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);//只定位一次。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW) ;//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
//以下三种模式从5.1.0版本开始提供
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
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
    public String toString() {
        return super.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if(!markers.isEmpty()){
            for(Marker marker:markers){
                marker.destroy();
            }
        }
        aMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }
    public void flush(){
        busLineItem=getArguments().getParcelable("LINE_ITEM");
        initBusLineOnMap();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mListener.onStationItemClick((BusStationItem) marker.getObject());
        return true;
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
