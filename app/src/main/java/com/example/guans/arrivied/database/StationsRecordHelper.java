package com.example.guans.arrivied.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.bean.WatchItem;

/**
 * Created by guans on 2017/7/30.
 */

public class StationsRecordHelper extends SQLiteOpenHelper {
    public static final String STATIONS_RECORD_TABLE_NAME = "STATION_RECORD";
    public static final String STATION_ID = "STATION_ID";
    public static final String RECORD_ID = "RECORD_ID";
    public static final String STATION_NAME = "STATION_NAME";
    public static final String STATION_LINE_ID = "STATION_LINE_ID";
    public static final String STATION_LINE_NAME = "STATION_LINE_NAME";
    public static final String CITY_CODE = "CITY_CODE";
    public static final String DATABASE_NAME = "HISTORY_RECORD";

    public StationsRecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSQL = "create table " + STATIONS_RECORD_TABLE_NAME + "("
                + RECORD_ID + "  integer primary key autoincrement, "
                + STATION_ID + " text, "
                + STATION_NAME + " text, "
                + STATION_LINE_NAME + " text, "
                + STATION_LINE_ID + " text, "
                + CITY_CODE + " text)";
        sqLiteDatabase.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertData(WatchItem watchItem) {
        BusLineItem busLineItem = watchItem.getBusLineItem();
        BusStationItem busStationItem = watchItem.getBusStationItem();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(STATIONS_RECORD_TABLE_NAME, null, STATION_ID + "=? and " + STATION_NAME + "=? and " + STATION_LINE_ID + "=?", new String[]{busStationItem.getBusStationId(), busStationItem.getBusStationName(), busLineItem.getBusLineId()}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(STATION_ID, busStationItem.getBusStationId());
            contentValues.put(STATION_NAME, busStationItem.getBusStationName());
            contentValues.put(STATION_LINE_ID, busLineItem.getBusLineId());
            contentValues.put(STATION_LINE_NAME, busLineItem.getBusLineId());
            contentValues.put(CITY_CODE, busStationItem.getCityCode());
            database.insert(STATIONS_RECORD_TABLE_NAME, null, contentValues);
        }
        cursor.close();
        database.close();
    }
}
