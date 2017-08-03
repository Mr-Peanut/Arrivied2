package com.example.guans.arrivied.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;

/**
 * Created by guans on 2017/7/15.
 */

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.StationItemHolder> {
    private Context mContext;
    private BusLineItem busLineItem;
    private OnStationItemClickListener stationItemClickListener;

    public LineAdapter(Context mContext, BusLineItem busLineItem) {
        this.mContext = mContext;
        this.busLineItem = busLineItem;
    }

    public void setBusLineItem(BusLineItem busLineItem) {
        this.busLineItem = busLineItem;
        notifyDataSetChanged();
    }

    public void setStationItemClickListener(OnStationItemClickListener staionItemClickListener) {
        this.stationItemClickListener = staionItemClickListener;
    }

    @Override
    public StationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_item_layout, parent, false);
        final StationItemHolder stationItemHolder = new StationItemHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stationItemClickListener.onStationItemClick(busLineItem.getBusStations().get(stationItemHolder.getAdapterPosition()));
            }
        });
        return stationItemHolder;
    }

    @Override
    public void onBindViewHolder(StationItemHolder holder, int position) {
        holder.itemView.setText(busLineItem.getBusStations().get(position).getBusStationName());
    }

    @Override
    public int getItemCount() {
        if (busLineItem != null)
            return busLineItem.getBusStations().size();
        return 0;
    }

    public interface OnStationItemClickListener {
        void onStationItemClick(BusStationItem busStationItem);
    }

    class StationItemHolder extends RecyclerView.ViewHolder {
        LinearLayout itemRootView;
        TextView itemView;

        StationItemHolder(View view) {
            super(view);
            itemRootView = view.findViewById(R.id.text_item_root);
            itemView = view.findViewById(R.id.text_item);
        }
    }
}
