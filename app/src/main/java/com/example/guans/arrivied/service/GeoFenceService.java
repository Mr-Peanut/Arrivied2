package com.example.guans.arrivied.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.bean.LocationClient;
import com.example.guans.arrivied.process.LiveActivity;
import com.example.guans.arrivied.process.ScreenChangeReceiver;
import com.example.guans.arrivied.receiver.ControllerReceiver;
import com.example.guans.arrivied.util.LOGUtil;
import com.example.guans.arrivied.view.MainActivity;

import java.util.List;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;

public class GeoFenceService extends Service implements ControllerReceiver.ControlReceiveListener, GeoFenceListener,GeoFenceClientProxy.GenFenceTaskObserver,ScreenChangeReceiver.OnBroadcastReceiveListener {
    public static final int ADD_DPOINT = 1;
    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    public static final String GEOFENCE_CANCLE_ATCITON = "com.example.guans.arrivied.service.GeoFenceService.CANCLE_GEOFENCE";
    public static final String ADD_DPOINT_ACTION="com.example.guans.arrivied.service.GeoFenceService.ADD_GEOFENCE";
    public static final String ACTION_GEOFENCE_SERVICE_BIND = "com.example.guan.arrived.geofenceservice.SERVICE_BIND";
    public static final String ACTION_GEOFENCE_REMOVED = "com.example.guan.arrived.geofenceservice.GEOREMOVED";
    public static final int ADD_GEOFENCE_ID=100;
    public static final String ADD_GEOFENCE_SUCCESS_ACTION="com.example.guan.arrived.geofenceservice.ADD_GEOFENCE_SUCCESS";
    public static final String ARRIVED_ACTION="com.example.guan.arrived.geofenceservice.ARRIVED";
    private GeoFenceClient mGeoFenceClient;
    private BusStationItem stationItem;
    private GeoFenceClientProxy mGeoFenceClientProxy;
    private boolean isWatching=false;
    private ScreenChangeReceiver screenChangeReceiver;
    private ControllerReceiver controller;
    private AlarmManager alarmManager;
    private PendingIntent wakeupPendingIntnet;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private Handler handler;
    private Runnable testRunnable;

    public GeoFenceService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        if (mGeoFenceClient == null) {
            mGeoFenceClient = new GeoFenceClient(getApplicationContext());
            mGeoFenceClient.setActivateAction(GEOFENCE_IN | GEOFENCE_OUT | GEOFENCE_STAYED);
            //创建并设置PendingIntent
            mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
            mGeoFenceClient.setGeoFenceListener(this);
        }
        screenChangeReceiver=new ScreenChangeReceiver(this);
        controller=new ControllerReceiver(this);
        IntentFilter controlIntentFilter=new IntentFilter();
        controlIntentFilter.addAction(GEOFENCE_CANCLE_ATCITON);
        controlIntentFilter.addAction("wakeup");
        controlIntentFilter.addAction(ARRIVED_ACTION);
        registerReceiver(controller,controlIntentFilter);
        handler=new Handler(getMainLooper());
        testRunnable=new Runnable() {
            @Override
            public void run() {

                LOGUtil.logE(this,"test");
                if(isWatching){
                    handler.postDelayed(testRunnable,2000);
                }
            }
        };
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
    }

    private void initReceiver() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            //先实现单点提醒，后续可以实现多点提醒
        mGeoFenceClient.removeGeoFence();
        DPoint dPoint = new DPoint();
        stationItem=intent.getParcelableExtra("LINE_ITEM");
        mGeoFenceClientProxy.setBusStationItem(stationItem);
        dPoint.setLatitude(stationItem.getLatLonPoint().getLatitude());
        dPoint.setLongitude(stationItem.getLatLonPoint().getLongitude());
        mGeoFenceClient.addGeoFence(dPoint, 50f, "BUS_STATION");
        LOGUtil.logE(this,String.valueOf(dPoint.getLatitude())+"/"+String.valueOf(dPoint.getLongitude()));
        LOGUtil.logE(this,String.valueOf(mGeoFenceClient.getAllGeoFence().size()));
        Intent startLaunchActivity=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,100,startLaunchActivity,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification= null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.watching_notification_view);
            Intent cancelIntent=new Intent(GEOFENCE_CANCLE_ATCITON);
            remoteViews.setOnClickPendingIntent(R.id.cancel_watch,PendingIntent.getBroadcast(getApplicationContext(),0,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            remoteViews.setTextViewText(R.id.content_title,"您设置了"+stationItem.getBusStationName());
            Notification.Builder builder=new Notification.Builder(getApplicationContext())
                        .setContentTitle(getPackageName())
                        .setContentText("正在监控"+stationItem.getBusStationName())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setStyle(new Notification.BigPictureStyle())
                        .setCustomBigContentView(remoteViews);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setCustomContentView(remoteViews);
            }else {
                builder.setContent(remoteViews);
            }
            notification=builder.build();

        }
        startForeground(ADD_GEOFENCE_ID,notification);
        alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        wakeupPendingIntnet=PendingIntent.getBroadcast(this,0,new Intent("wakeup"),PendingIntent.FLAG_UPDATE_CURRENT);
//        if(isWatching){
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,0,5000,wakeupPendingIntnet);
            wakeLock.acquire();
            handler.removeCallbacks(testRunnable);
            handler.post(testRunnable);

//        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mGeoFenceClientProxy == null) {
            mGeoFenceClientProxy = new GeoFenceClientProxy(mGeoFenceClient);
            mGeoFenceClientProxy.setGenFenceTaskObserver(this);
        }
        return mGeoFenceClientProxy;
    }

    @Override
    public void onDestroy() {
        if (!mGeoFenceClient.getAllGeoFence().isEmpty())
            mGeoFenceClient.removeGeoFence();
        if(screenChangeReceiver!=null){
            unregisterReceiver(screenChangeReceiver);
        }
        unregisterReceiver(controller);
        super.onDestroy();
    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        LOGUtil.logE(this,"addfinish"+String.valueOf(i)+"/"+s);
        if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
            Toast.makeText(getApplicationContext(),"围栏创建成功"+s,Toast.LENGTH_SHORT).show();
            //发送一个广播通知UI控件更新状态
            //更改该服务到前台通知
            //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
            Intent intent=new Intent(ADD_GEOFENCE_SUCCESS_ACTION);
            sendBroadcast(intent);
            isWatching=true;
        } else {
            Toast.makeText(getApplicationContext(),"围栏创建失败"+s,Toast.LENGTH_SHORT).show();
            //geoFenceList就是已经添加的围栏列表
            LOGUtil.logE(this, "围栏创建失败" + s);
        }
    }

    @Override
    public void onGeoPointAdd(DPoint dPoint, float r, String id) {

    }

    @Override
    public void onGeoPointRemoved() {
        stopForeground(true);
        Intent removeGeoFenceIntent=new Intent(ACTION_GEOFENCE_REMOVED);
        isWatching=false;
        alarmManager.cancel(wakeupPendingIntnet);
        sendBroadcast(removeGeoFenceIntent);
    }

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        LOGUtil.logE(this,intent.getAction());
        if(isWatching){
            switch (intent.getAction()){
                case Intent.ACTION_SCREEN_OFF:
                    Intent startAliveAction=new Intent(this, LiveActivity.class);
                    startActivity(startAliveAction);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    break;
            }
        }
    }

    @Override
    public void onControllBroadcastReceive(Intent intent) {
        LOGUtil.logE(this,intent.getAction());
       switch (intent.getAction()){
           case GEOFENCE_CANCLE_ATCITON:
              mGeoFenceClientProxy.removeDPoint();
               wakeLock.release();
               break;
           case "wakeup":
               LOGUtil.logE(this,"wakeup");
               break;
           case ARRIVED_ACTION:
               mGeoFenceClientProxy.removeDPoint();
               wakeLock.release();
               alarmManager.cancel(wakeupPendingIntnet);
               break;
       }
    }
}
