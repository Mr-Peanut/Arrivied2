package com.example.guans.arrivied.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationSearch;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.adapter.BusLineSearchSugesstAdapter;
import com.example.guans.arrivied.adapter.BusLinesAdapter;
import com.example.guans.arrivied.util.LOGUtil;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BusSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusSearchFragment extends Fragment implements BusLineSearch.OnBusLineSearchListener,BusLinesAdapter.OnLineItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private BusStationQuery busStationQuery;
    private BusStationSearch busStationSearch;
    private BusLineSearch busLineSearch;
    private BusLineQuery busLineQuery;
    private SearchView busSearchView;
    private String city;
    private TextView statuText;
    private RecyclerView sugesstList;
    private RecyclerView result_list;
    private BusLineSearchSugesstAdapter sugesstAdapter;
    private BusLinesAdapter busLinesAdapter;
    private SearchTask searchTask;
    private Handler mHandler;
    public BusSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BusSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BusSearchFragment newInstance(String param1, String param2) {
        BusSearchFragment fragment = new BusSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mHandler=new Handler(getActivity().getMainLooper());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_bus_search, container, false);
        initView(rootView);
        return rootView ;
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
    private void initView(View rootView) {
        busSearchView= rootView.findViewById(R.id.bus_search);
        busSearchView.setSubmitButtonEnabled(true);
        sugesstList= rootView.findViewById(R.id.sugesst_result);
        sugesstList.setLayoutManager(new LinearLayoutManager(getContext()));
        sugesstAdapter=new BusLineSearchSugesstAdapter(null,getContext());
        sugesstList.setAdapter(sugesstAdapter);
        result_list= rootView. findViewById(R.id.search_result_list);
        result_list.setLayoutManager(new LinearLayoutManager(getContext()));
        busLinesAdapter=new BusLinesAdapter(null,getContext());
        busLinesAdapter.setLineItemClickListener(this);
        result_list.setAdapter(busLinesAdapter);
        busSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        busSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(searchTask!=null){
                        mHandler.removeCallbacks(searchTask);
                        searchTask.setTargetString(s);
                        mHandler.post(searchTask);
                }
                statuText.setText("开始搜索"+city);
//                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                String searchTarget= busSearchView.getQuery().toString().trim();
                if(searchTask!=null)
                {
                    mHandler.removeCallbacks(searchTask);
                    searchTask.setTargetString(s);
                }else {
                    searchTask=new SearchTask(s);
                }
                mHandler.postDelayed(searchTask,100);
                statuText.setText("搜索建议"+city);
                return true;
            }
        });
        statuText= rootView.findViewById(R.id.statue);
        statuText.setVisibility(View.VISIBLE);
        if(city!=null){
            statuText.setText(city);
        }else {
            statuText.setText("没有获取城市信息");
            busSearchView.setClickable(false);
        }
    }
    private void searchLines(String s){
        String searchTarget= s.trim();
        if(searchTarget.length()!=0){
            if(busLineQuery==null)
                busLineQuery=new BusLineQuery(searchTarget,BusLineQuery.SearchType.BY_LINE_NAME,city);
            else
                busLineQuery.setQueryString(searchTarget);
            if(busLineSearch==null){
                busLineSearch=new BusLineSearch(getContext(),busLineQuery);
                busLineSearch.setOnBusLineSearchListener(BusSearchFragment.this);
            }
            busLineSearch.searchBusLineAsyn();

        }
    }
    private void initData() {
        Intent startIntent=getActivity().getIntent();
        city=startIntent.getStringExtra("LOCATION_CITY");
    }
    @Override
    public void onBusLineSearched(BusLineResult busLineResult, int i) {
        statuText.setText(busLineResult.getQuery().getQueryString());
        LOGUtil.logE(this,"getLines"+String.valueOf(i));
        if(i==1000){
            List<String> sugesstKeyWords=busLineResult.getSearchSuggestionKeywords();
            //没有获取正确的线路线索（关键字输入有误）
            if (sugesstKeyWords!=null&&sugesstKeyWords.size()!=0){
                sugesstAdapter.setSugessutList(sugesstKeyWords);
                LOGUtil.logE(sugesstAdapter,String.valueOf(sugesstKeyWords.size()));
            }else {
                if(busLineResult.getBusLines().size()==0){
                    statuText.setText("没有搜索结果");
                }
                busLinesAdapter.setBusLineItems(busLineResult.getBusLines());
            }
        }
    }

    @Override
    public void onLineItemClick(BusLineItem busLineItem) {
        mListener.onLineItemClicked(busLineItem);
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
        void onLineItemClicked(BusLineItem targetLineItem);
    }
    private class SearchTask implements Runnable{
        private String targetString;
        SearchTask(String targetString) {
            this.targetString = targetString;
        }

        String getTargetString() {
            return targetString;
        }

        void setTargetString(String targetString) {
            this.targetString = targetString;
        }

        @Override
        public void run() {
            searchLines(targetString);
        }
    }
}