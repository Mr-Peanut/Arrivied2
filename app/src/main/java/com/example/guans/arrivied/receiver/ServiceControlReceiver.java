package com.example.guans.arrivied.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by guans on 2017/7/12.
 */

public class ServiceControlReceiver extends BroadcastReceiver {
    private ServiceControlReceiverListener mServiceControlReceiverListener;

    private ServiceControlReceiver() {
    }

    public ServiceControlReceiver(Service serviceContext) {
        if (!(serviceContext instanceof ServiceControlReceiverListener))
            throw new IllegalArgumentException("must immplement ServiceControlReceiver for this service");
        else {
            mServiceControlReceiverListener = (ServiceControlReceiverListener) serviceContext;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mServiceControlReceiverListener.onReceiveBroadcast(context, intent);
    }

    interface ServiceControlReceiverListener {
        void onReceiveBroadcast(Context context, Intent intent);
    }
}
