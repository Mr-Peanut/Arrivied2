package com.example.guans.arrivied.util;

import android.content.Context;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.core.AMapException;
import com.example.guans.arrivied.bean.StationRecordItem;
import com.example.guans.arrivied.bean.WatchItem;

/**
 * Created by guans on 2017/7/30.
 */

public class Record2WatchItem {
    public static WatchItem getWatchItemFromRecord(Context context, StationRecordItem stationRecordItem) {
        String stationKeyWords = stationRecordItem.getStationName()
                + "|" + stationRecordItem.getStationID()
                + "|" + stationRecordItem.getLineName();
        BusStationQuery busStationQuery = new BusStationQuery(stationKeyWords, stationRecordItem.getLineID());
        BusStationSearch busStationSearch = new BusStationSearch(context, busStationQuery);
        BusStationResult stationResult = null;
        BusLineItem targetLine = null;
        BusStationItem targetStation = null;
        try {
            stationResult = busStationSearch.searchBusStation();
        } catch (AMapException e) {
            e.printStackTrace();
        }
        if (stationResult != null) {
            for (BusStationItem stationItem : stationResult.getBusStations()) {
                for (BusLineItem busLineItem : stationItem.getBusLineItems()) {
                    if (busLineItem.getBusLineId().equals(stationRecordItem.getLineID())) {
                        targetLine = busLineItem;
                        targetStation = stationItem;
                    }
                }
            }

        }
        if (targetLine != null) {
            return new WatchItem(targetLine, targetStation);
        }
        return null;
    }

    public static WatchItem getWatchItemFromRecord2(Context context, StationRecordItem stationRecordItem, BusLineSearch.OnBusLineSearchListener busLineSearchListener) {
        BusLineItem targetLine = null;
        BusStationItem targetStation = null;
        BusLineQuery busLineQuery = new BusLineQuery(stationRecordItem.getLineID(), BusLineQuery.SearchType.BY_LINE_ID, stationRecordItem.getLineID());
        BusLineSearch busLineSearch = new BusLineSearch(context, busLineQuery);
        busLineSearch.setOnBusLineSearchListener(busLineSearchListener);
        return null;
    }


}
