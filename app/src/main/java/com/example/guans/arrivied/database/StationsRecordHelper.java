package com.example.guans.arrivied.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private Context mContext;

    public StationsRecordHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableSQL = "create table if not exists" + STATIONS_RECORD_TABLE_NAME + "("
                + RECORD_ID + "int  NOT NULL auto_increment"
                + STATION_ID + "text"
                + STATION_NAME + "text"
                + STATION_LINE_NAME + "text"
                + STATION_LINE_ID + "text"
                + CITY_CODE + "text)";
        sqLiteDatabase.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
