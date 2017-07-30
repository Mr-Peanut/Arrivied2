package com.example.guans.arrivied.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.guans.arrivied.bean.StationRecordItem;

/**
 * Created by guans on 2017/7/30.
 */

public class StationRecordAdapter extends RecyclerView.Adapter<StationRecordAdapter.RecordHolder> {
    @Override
    public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecordHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface RecordClickedListener {
        void OnRecordClicked(StationRecordItem stationRecordItem);
    }

    class RecordHolder extends RecyclerView.ViewHolder {

        public RecordHolder(View itemView) {
            super(itemView);
        }
    }
}
