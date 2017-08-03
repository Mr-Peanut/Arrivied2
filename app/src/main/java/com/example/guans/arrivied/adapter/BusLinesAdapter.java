package com.example.guans.arrivied.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.example.guans.arrivied.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guans on 2017/7/15.
 */

public class BusLinesAdapter extends RecyclerView.Adapter<BusLinesAdapter.BusLineItemHolder> {
    private List<BusLineItem> busLineItems;
    private Context mContext;
    private OnLineItemClickListener lineItemClickListener;

    public BusLinesAdapter(List<BusLineItem> busLineItems, Context mContext) {
        if (busLineItems == null) {
            this.busLineItems = new ArrayList<>();
        } else {
            this.busLineItems = busLineItems;
        }
        this.mContext = mContext;
    }

    public void setLineItemClickListener(OnLineItemClickListener lineItemClickListener) {
        this.lineItemClickListener = lineItemClickListener;
    }

    public void setBusLineItems(List<BusLineItem> busLineItems) {
        this.busLineItems = busLineItems;
        notifyDataSetChanged();
    }

    @Override
    public BusLineItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.text_item_layout, null);
        final BusLineItemHolder busLineItemHolder = new BusLineItemHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineItemClickListener.onLineItemClick(busLineItems.get(busLineItemHolder.getAdapterPosition()));
            }
        });
        return busLineItemHolder;
    }

    @Override
    public void onBindViewHolder(BusLineItemHolder holder, int position) {
        BusLineItem busLineItem = busLineItems.get(position);
        holder.itemView.setText(busLineItem.getBusLineName() + " " + busLineItem.getBusLineName());
    }

    @Override
    public int getItemCount() {
        if (busLineItems == null)
            return 0;
        return busLineItems.size();
    }

    public interface OnLineItemClickListener {
        void onLineItemClick(BusLineItem busLineItem);
    }

    class BusLineItemHolder extends RecyclerView.ViewHolder {
        LinearLayout itemRootView;
        TextView itemView;

        BusLineItemHolder(View view) {
            super(view);
            itemRootView = view.findViewById(R.id.text_item_root);
            itemView = view.findViewById(R.id.text_item);
        }
    }
}
