package com.example.guans.arrivied.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guans.arrivied.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guans on 2017/7/14.
 */

public class BusLineSearchSuggestAdapter extends RecyclerView.Adapter <BusLineSearchSuggestAdapter.ItemHolder>{
    private List<String> sugessutList;
    private Context mContext;

    public BusLineSearchSuggestAdapter(ArrayList<String> sugessutList, Context mContext) {
        if (sugessutList==null){
            this.sugessutList=new ArrayList<>();
        }else {
            this.sugessutList = sugessutList;
        }
        this.mContext = mContext;
    }

    public void setSugessutList(List<String> sugessutList) {
        this.sugessutList = sugessutList;
        notifyDataSetChanged();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(mContext).inflate(R.layout.text_item_layout,parent));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.itemView.setText(sugessutList.get(position));
    }

    @Override
    public int getItemCount() {
        if(sugessutList==null)
            return 0;
        return sugessutList.size();
    }
    class ItemHolder extends RecyclerView.ViewHolder{
        LinearLayout itemRootView;
        TextView itemView;

        ItemHolder(View view) {
            super(view);
            itemRootView=view.findViewById(R.id.text_item_root);
            itemView=view.findViewById(R.id.text_item);
        }
    }
}
