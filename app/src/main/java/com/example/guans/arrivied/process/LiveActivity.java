package com.example.guans.arrivied.process;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.guans.arrivied.service.GeoFenceService;
import com.example.guans.arrivied.util.LOGUtil;

/**
 * Created by guans on 2017/7/20.
 */

public class LiveActivity extends Activity {
    private GeoFenServiceConnection geoFenceConnection;
    private ScreenOnReceiver screenOnReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LOGUtil.logE(this, "onCreate");
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        bindGeoFenceService();
        registerScreenOnReceiver();
    }

    private void registerScreenOnReceiver() {
        screenOnReceiver = new ScreenOnReceiver();
        IntentFilter screenOnIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenOnReceiver, screenOnIntentFilter);
    }

    private void bindGeoFenceService() {
        geoFenceConnection = new GeoFenServiceConnection();
        Intent geoFenceServiceIntent = new Intent(this, GeoFenceService.class);
        bindService(geoFenceServiceIntent, geoFenceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        unbindService(geoFenceConnection);
        unregisterReceiver(screenOnReceiver);
        LOGUtil.logE(this, "onDestroy");
        super.onDestroy();
    }

    class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                LiveActivity.this.finish();
            }
        }
    }

    private class GeoFenServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
}
