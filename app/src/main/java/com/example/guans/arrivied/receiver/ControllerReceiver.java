package com.example.guans.arrivied.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shiqian.guan on 2017/7/11.
 */

public class ControllerReceiver extends BroadcastReceiver {
    private ControlReceiveListener mListener;
    private ControllerReceiver(){}
    public ControllerReceiver(Context context){
        if(!(context instanceof ControlReceiveListener)){
            throw new IllegalArgumentException("service must implement ControlReceiveListener ");
        }
        mListener= (ControlReceiveListener) context;
    }

    public void setControlListener(ControlReceiveListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(mListener!=null){
            mListener.onControlBroadcastReceive(intent);
        }
    }
    public interface ControlReceiveListener {
        void onControlBroadcastReceive(Intent intent);
    }
}
