package com.example.guans.arrivied.process;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.guans.arrivied.util.LOGUtil;

/**
 * Created by guans on 2017/7/20.
 */

public class ScreenChangeReceiver extends BroadcastReceiver {
    private OnBroadcastReceiveListener mListener;
    @Override
    public void onReceive(Context context, Intent intent) {
    }

    public ScreenChangeReceiver(Service context) {
        super();
        if(!(context instanceof OnBroadcastReceiveListener)){

            throw new IllegalArgumentException("Service must be instance of OnBroadcastReceiveListener");
        }
        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(this,intentFilter);
        mListener= (OnBroadcastReceiveListener) context;
        LOGUtil.logE(this,"create");
    }
    public interface OnBroadcastReceiveListener{
        void onBroadcastReceive(Context  context,Intent intent);
    }
}
