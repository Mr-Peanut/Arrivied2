package com.example.guans.arrivied.view;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.example.guans.arrivied.R;

public class NoticeActivity extends AppCompatActivity {
//    private PowerManager powerManager;
//    private KeyguardManager keyguardManager;
    private Button cancelButton;
//    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_notice);
        cancelButton= (Button) findViewById(R.id.cancel_notified);
//        powerManager= (PowerManager) getSystemService(POWER_SERVICE);
//        keyguardManager= (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//        wakeLock=powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,"SCREEN_ON");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        wakeLock.acquire();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            if(keyguardManager.isKeyguardLocked()){
//
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onDestroy();
    }
}
