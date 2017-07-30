package com.example.guans.arrivied.bean;

import android.database.Cursor;

import com.example.guans.arrivied.database.StationsRecordHelper;

/**
 * Created by guans on 2017/7/30.
 */

public class StationRecordItem {
    private int recordID;
    private String stationID;
    private String stationName;
    private String lineName;
    private String lineID;
    private String cityCode;

    public StationRecordItem(Cursor cursor) {
        recordID = cursor.getInt(cursor.getColumnIndex(StationsRecordHelper.RECORD_ID));
        stationID = cursor.getString(cursor.getColumnIndex(StationsRecordHelper.STATION_ID));
        stationName = cursor.getString(cursor.getColumnIndex(StationsRecordHelper.STATION_NAME));
        lineID = cursor.getString(cursor.getColumnIndex(StationsRecordHelper.STATION_LINE_ID));
        lineName = cursor.getString(cursor.getColumnIndex(StationsRecordHelper.STATION_LINE_NAME));
        cityCode = cursor.getString(cursor.getColumnIndex(StationsRecordHelper.CITY_CODE));
    }

    public int getRecordID() {
        return recordID;
    }

    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
