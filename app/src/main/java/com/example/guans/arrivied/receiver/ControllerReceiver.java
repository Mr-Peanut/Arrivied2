package com.example.guans.arrivied.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shiqian.guan on 2017/7/11.
 */

public class ControllerReceiver extends BroadcastReceiver {
    private ControlReceiveListener mListener;

    public void setControlListener(ControlReceiveListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener!=null){
            mListener.onBroadcastReceive(intent);
        }
    }
    public interface ControlReceiveListener {
        void onBroadcastReceive(Intent intent);
    }
}
