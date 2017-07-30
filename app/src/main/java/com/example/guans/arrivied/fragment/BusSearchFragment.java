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
import com.example.guans.arrivied.adapter.BusLineSearchSuggestAdapter;
import com.example.guans.arrivied.adapter.BusLinesAdapter;
import com.example.guans.arrivied.util.LOGUtil;
import com.example.guans.arrivied.view.ItemDivider;

import java.util.List;

public class BusSearchFragment extends Fragment implements BusLineSearch.OnBusLineSearchListener,BusLinesAdapter.OnLineItemClickListener{


    private OnFragmentInteractionListener mListener;
    private BusStationQuery busStationQuery;
    private BusStationSearch busStationSearch;
    private BusLineSearch busLineSearch;
    private BusLineQuery busLineQuery;
    private SearchView busSearchView;
    private String city;
    private TextView statueText;
    private RecyclerView suggestList;
    private RecyclerView result_list;
    private BusLineSearchSuggestAdapter suggestAdapter;
    private BusLinesAdapter busLinesAdapter;
    private SearchTask searchTask;
    private ItemDivider itemDivider;
    private Handler mHandler;
    public BusSearchFragment() {
    }

    public static BusSearchFragment newInstance() {
        BusSearchFragment fragment = new BusSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mHandler=new Handler(getActivity().getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_bus_search, container, false);
        initView(rootView);
        return rootView ;
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
    private void initView(View rootView) {
        itemDivider = new ItemDivider();
        busSearchView= rootView.findViewById(R.id.bus_search);
        busSearchView.setSubmitButtonEnabled(true);
        suggestList = rootView.findViewById(R.id.suggest_result);
        suggestList.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestAdapter =new BusLineSearchSuggestAdapter(null,getContext());
        suggestList.addItemDecoration(itemDivider);
        suggestList.setAdapter(suggestAdapter);
        result_list= rootView. findViewById(R.id.search_result_list);
        result_list.setLayoutManager(new LinearLayoutManager(getContext()));
        busLinesAdapter=new BusLinesAdapter(null,getContext());
        busLinesAdapter.setLineItemClickListener(this);
        result_list.setAdapter(busLinesAdapter);
        result_list.addItemDecoration(itemDivider);
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
                statueText.setText("开始搜索"+city);
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
                mHandler.postDelayed(searchTask, 300);
                statueText.setText("搜索建议"+city);
                return true;
            }
        });
        statueText = rootView.findViewById(R.id.statue);
        statueText.setVisibility(View.VISIBLE);
        if(city!=null){
            statueText.setText(city);
        }else {
            statueText.setText("没有获取城市信息,请返回重试");
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
        statueText.setText(city + busLineResult.getQuery().getQueryString() + "的搜索结果：");
        LOGUtil.logE(this,"getLines"+String.valueOf(i));
        if(i==1000){
            List<String> suggestKeyWords=busLineResult.getSearchSuggestionKeywords();
            //没有获取正确的线路线索（关键字输入有误）
            if (suggestKeyWords!=null&&suggestKeyWords.size()!=0){
                suggestAdapter.setSugessutList(suggestKeyWords);
            }else {
                if(busLineResult.getBusLines().size()==0){
                    statueText.setText("没有搜索结果");
                }
                busLinesAdapter.setBusLineItems(busLineResult.getBusLines());
            }
        }
    }

    @Override
    public void onLineItemClick(BusLineItem busLineItem) {
        mListener.onLineItemClicked(busLineItem);
    }
    public interface OnFragmentInteractionListener {
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
