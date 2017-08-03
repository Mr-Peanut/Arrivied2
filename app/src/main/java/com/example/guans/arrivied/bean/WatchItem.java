package com.example.guans.arrivied.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;

/**
 * Created by guans on 2017/7/25.
 */

public class WatchItem implements Parcelable {
    public static final Creator<WatchItem> CREATOR = new Creator<WatchItem>() {
        @Override
        public WatchItem createFromParcel(Parcel in) {
            return new WatchItem(in);
        }

        @Override
        public WatchItem[] newArray(int size) {
            return new WatchItem[size];
        }
    };
    private BusLineItem busLineItem;
    private BusStationItem busStationItem;

    public WatchItem(BusLineItem busLineItem, BusStationItem busStationItem) {
        this.busLineItem = busLineItem;
        this.busStationItem = busStationItem;
    }

    protected WatchItem(Parcel in) {
        busLineItem = in.readParcelable(BusLineItem.class.getClassLoader());
        busStationItem = in.readParcelable(BusStationItem.class.getClassLoader());
    }

    public BusLineItem getBusLineItem() {
        return busLineItem;
    }

    public void setBusLineItem(BusLineItem busLineItem) {
        this.busLineItem = busLineItem;
    }

    public BusStationItem getBusStationItem() {
        return busStationItem;
    }

    public void setBusStationItem(BusStationItem busStationItem) {
        this.busStationItem = busStationItem;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(busLineItem, flags);
        dest.writeParcelable(busStationItem, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
