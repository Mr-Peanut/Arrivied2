package com.example.guans.arrivied.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.guans.arrivied.util.LOGUtil;

/**
 * Created by shiqian.guan on 2017/7/11.
 */

public class LocationReceiver extends BroadcastReceiver {
    private LocationReceiveListener mListener;

    public void setLocationListener(LocationReceiveListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener!=null){
            mListener.onBroadcastReceive(intent);
        }
    }
    public interface LocationReceiveListener{
        void onBroadcastReceive(Intent intent);
    }
}
