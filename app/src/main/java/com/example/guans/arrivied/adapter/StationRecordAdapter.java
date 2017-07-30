package com.example.guans.arrivied.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.StationRecordItem;
import com.example.guans.arrivied.database.StationsRecordHelper;

/**
 * Created by guans on 2017/7/30.
 */

public class StationRecordAdapter extends RecyclerView.Adapter<StationRecordAdapter.RecordHolder> {
    private RecordClickedListener recordClickedListener;
    private Context mContext;
    private StationsRecordHelper stationsRecordHelper;
    private Cursor cursor;

    public StationRecordAdapter(Context mContext) {
        this.mContext = mContext;
        stationsRecordHelper = new StationsRecordHelper(mContext.getApplicationContext(), StationsRecordHelper.DATABASE_NAME, null, 1);
        cursor = stationsRecordHelper.getWritableDatabase().query(StationsRecordHelper.STATIONS_RECORD_TABLE_NAME, null, null, null, null, null, StationsRecordHelper.RECORD_ID + " DESC");
    }

    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_item_layout, parent, false);
        return new RecordHolder(view);
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {
        cursor.moveToPosition(position);
        final StationRecordItem stationRecordItem = new StationRecordItem(cursor);
        holder.textView.setText(stationRecordItem.getStationName() + "/" + stationRecordItem.getLineName());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recordClickedListener != null) {
                    recordClickedListener.OnRecordClicked(stationRecordItem);
                }
                cursor.close();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor != null)
            return cursor.getCount();
        return 0;
    }

    public void flushData() {
        if (cursor != null)
            cursor.close();
        cursor = stationsRecordHelper.getWritableDatabase().query(StationsRecordHelper.STATIONS_RECORD_TABLE_NAME, null, null, null, null, null, StationsRecordHelper.RECORD_ID + " DESC");
        notifyDataSetChanged();
    }

    public void closeCursor() {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public void setRecordClickedListener(RecordClickedListener recordClickedListener) {
        this.recordClickedListener = recordClickedListener;
    }

    public interface RecordClickedListener {
        void OnRecordClicked(StationRecordItem stationRecordItem);
    }

    class RecordHolder extends RecyclerView.ViewHolder {
        TextView textView;
        LinearLayout rootView;

        RecordHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_item);
            rootView = itemView.findViewById(R.id.text_item_root);
        }
    }
}
