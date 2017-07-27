package com.example.guans.arrivied.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.services.busline.BusStationItem;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.bean.WatchItem;
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
    public static final int ARRIVED_NOTIFICATION_ID=101;
    public static final String ADD_GEOFENCE_SUCCESS_ACTION="com.example.guan.arrived.geofenceservice.ADD_GEOFENCE_SUCCESS";
    public static final String ARRIVED_ACTION="com.example.guan.arrived.geofenceservice.ARRIVED";
    public static final String WAKE_UP_ACTION="com.example.guan.arrived.geofenceservice.WAKE_UP";
    private GeoFenceClient mGeoFenceClient;
    private BusStationItem stationItem;
    private GeoFenceClientProxy mGeoFenceClientProxy;
    private PowerManager.WakeLock wakeLock;
    private boolean isWatching=false;
    private ScreenChangeReceiver screenChangeReceiver;
    private ControllerReceiver controller;
//    private AlarmManager alarmManager;
//    private PendingIntent wakeupPendingIntent;
    private PowerManager pm;
    private Handler handler;
    private WatchItem watchItem;
    private Runnable tryAddDeoFenceAgainRunnable =new Runnable() {
        @Override
        public void run() {
            addGeoFence(stationItem);
            Toast.makeText(getApplicationContext(),"add again",Toast.LENGTH_SHORT).show();
        }
    };
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
        controlIntentFilter.addAction(WAKE_UP_ACTION);
        controlIntentFilter.addAction(ARRIVED_ACTION);
        registerReceiver(controller,controlIntentFilter);
        handler=new Handler(getMainLooper());
        prepareAliveAction();
    }

    private void prepareAliveAction() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
//        alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
//        wakeupPendingIntent =PendingIntent.getBroadcast(this,0,new Intent(WAKE_UP_ACTION),PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            //先实现单点提醒，后续可以实现多点提醒
        mGeoFenceClient.removeGeoFence();
        addGeoFence(intent);
        startAlarmTask();
//        }
        return Service.START_NOT_STICKY;
    }
    /*
     *息屏时保证系统不休眠
     */

    private void startAlarmTask() {
//        if(isWatching){
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,0,10000, wakeupPendingIntent);
        wakeLock.acquire();
    }

    private void startWatchingNotification() {
        Intent startLaunchActivity=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,100,startLaunchActivity,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification= null;
        RemoteViews remoteViews=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
               remoteViews=new RemoteViews(getPackageName(),R.layout.watching_notification_view);
//            }else {
//                remoteViews=new RemoteViews(getPackageName(),R.layout.watching_notification_view_small);
//            }
            Intent cancelIntent=new Intent(GEOFENCE_CANCLE_ATCITON);
            remoteViews.setOnClickPendingIntent(R.id.cancel_watch,PendingIntent.getBroadcast(getApplicationContext(),0,cancelIntent,PendingIntent.FLAG_UPDATE_CURRENT));
            remoteViews.setTextViewText(R.id.station_info,"您设置了"+stationItem.getBusStationName());
            NotificationCompat.Builder notificationBuilder=new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Arrived")
                    .setContentText("正在监控"+stationItem.getBusStationName()+"\n"+watchItem.getBusLineItem().getBusLineName())
                    .setSmallIcon(R.drawable.bus_station)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle())
                    .setCustomBigContentView(remoteViews);
//            Notification.Builder builder=new Notification.Builder(getApplicationContext())
//                    .setContentTitle("Arrived")
//                    .setContentText("正在监控"+stationItem.getBusStationName()+"\n"+watchItem.getBusLineItem().getBusLineName())
//                    .setSmallIcon(R.drawable.bus_station)
//                    .setWhen(System.currentTimeMillis())
//                    .setContentIntent(pendingIntent);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                builder.setStyle(new Notification.BigPictureStyle());
//                builder.setCustomBigContentView(remoteViews);
////                builder.setCustomContentView(remoteViews);
//            }else {
//                builder.setContent(remoteViews);
//            }
            notification=notificationBuilder.build();
        }
        startForeground(ADD_GEOFENCE_ID,notification);
    }

    private void addGeoFence(Intent intent) {
        watchItem=intent.getParcelableExtra("TARGET_ITEM");
        stationItem=watchItem.getBusStationItem();
        mGeoFenceClientProxy.setBusStationItem(stationItem);
        mGeoFenceClientProxy.setWatchItem(watchItem);
        addGeoFence(stationItem);
        handler.postDelayed(tryAddDeoFenceAgainRunnable,2000);
    }
    private void addGeoFence(BusStationItem stationItem){
        DPoint dPoint = new DPoint();
        dPoint.setLatitude(stationItem.getLatLonPoint().getLatitude());
        dPoint.setLongitude(stationItem.getLatLonPoint().getLongitude());
        mGeoFenceClient.addGeoFence(dPoint,80f, "BUS_STATION");
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
        if(wakeLock.isHeld())
            wakeLock.release();
        LOGUtil.logE(this,"onDestory");
        super.onDestroy();
    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
            Toast.makeText(getApplicationContext(),"围栏创建成功"+s,Toast.LENGTH_SHORT).show();
            startWatchingNotification();
            handler.removeCallbacks(tryAddDeoFenceAgainRunnable);
            //发送一个广播通知UI控件更新状态
            //更改该服务到前台通知
            //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
            //发送一个添加围栏成功的broadcast，更新ui
            Intent intent=new Intent(ADD_GEOFENCE_SUCCESS_ACTION);
//            intent.putExtra("ON_WATCH_STATION",stationItem);
            intent.putExtra("ON_WATCH_ITEM",watchItem);
            sendBroadcast(intent);
            isWatching=true;
        } else {
            Toast.makeText(getApplicationContext(),"围栏创建失败"+s,Toast.LENGTH_SHORT).show();
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
//        alarmManager.cancel(wakeupPendingIntent);
        if(wakeLock.isHeld()){
            LOGUtil.logE(this,"releaseLock");
            wakeLock.release();
        }
        sendBroadcast(removeGeoFenceIntent);
        stopSelf();
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
    public void onControlBroadcastReceive(Intent intent) {
        LOGUtil.logE(this,intent.getAction());
       switch (intent.getAction()){
           case GEOFENCE_CANCLE_ATCITON:
              mGeoFenceClientProxy.removeDPoint();
               if(wakeLock.isHeld())
               wakeLock.release();
               stopSelf();
               break;
           case WAKE_UP_ACTION:
               LOGUtil.logE(this,"wakeup");
               break;
           case ARRIVED_ACTION:
               mGeoFenceClientProxy.removeDPoint();
               if(wakeLock.isHeld())
               wakeLock.release();
//               alarmManager.cancel(wakeupPendingIntent);
               LOGUtil.logE(this,"arrived");
               stopSelf();
               break;
       }
    }
}
