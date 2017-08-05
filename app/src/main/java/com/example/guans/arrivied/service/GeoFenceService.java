package com.example.guans.arrivied.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusStationItem;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.guans.arrivied.R;
import com.example.guans.arrivied.bean.GeoFenceClientProxy;
import com.example.guans.arrivied.bean.OfflineLocationClient;
import com.example.guans.arrivied.bean.WatchItem;
import com.example.guans.arrivied.database.StationsRecordHelper;
import com.example.guans.arrivied.process.LiveActivity;
import com.example.guans.arrivied.process.ScreenChangeReceiver;
import com.example.guans.arrivied.receiver.ControllerReceiver;
import com.example.guans.arrivied.util.LOGUtil;
import com.example.guans.arrivied.view.MainActivity;

import java.util.List;

import static com.amap.api.fence.GeoFenceClient.GEOFENCE_IN;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_OUT;
import static com.amap.api.fence.GeoFenceClient.GEOFENCE_STAYED;
import static com.baidu.location.LocationClientOption.LOC_SENSITIVITY_HIGHT;

public class GeoFenceService extends Service implements ControllerReceiver.ControlReceiveListener, GeoFenceListener, GeoFenceClientProxy.GenFenceTaskObserver, ScreenChangeReceiver.OnBroadcastReceiveListener {
    public static final int ADD_DPOINT = 1;
    //定义接收广播的action字符串
    public static final String GEOFENCE_BROADCAST_ACTION = "com.location.apis.geofencedemo.broadcast";
    public static final String GEOFENCE_CANCLE_ATCITON = "com.example.guans.arrivied.service.GeoFenceService.CANCLE_GEOFENCE";
    public static final String ADD_DPOINT_ACTION = "com.example.guans.arrivied.service.GeoFenceService.ADD_GEOFENCE";
    public static final String ACTION_GEOFENCE_SERVICE_BIND = "com.example.guan.arrived.geofenceservice.SERVICE_BIND";
    public static final String ACTION_GEOFENCE_REMOVED = "com.example.guan.arrived.geofenceservice.GEOREMOVED";
    public static final int ADD_GEOFENCE_ID = 100;
    public static final int ARRIVED_NOTIFICATION_ID = 101;
    public static final int MONITOR_NOTIFICATION_ID = 102;
    public static final String ADD_GEOFENCE_SUCCESS_ACTION = "com.example.guan.arrived.geofenceservice.ADD_GEOFENCE_SUCCESS";
    public static final String ARRIVED_ACTION = "com.example.guan.arrived.geofenceservice.ARRIVED";
    public static final String WAKE_UP_ACTION = "com.example.guan.arrived.geofenceservice.WAKE_UP";
    public static final String ARRIVED_PROXIMITY_ACTION = "com.example.guans.arrivied.service.GeoFenceService.ARRIVED_PROXIMITY";
    public static final int PROXIMITY_REQUEST_CODE = 1001;
    private LocationManager locationManager;
    private StationsRecordHelper stationsRecordHelper;
    private GeoFenceClient mGeoFenceClient;
    private BusStationItem stationItem;
    private GeoFenceClientProxy mGeoFenceClientProxy;
    private PowerManager.WakeLock wakeLock;
    private boolean isWatching = false;
    private ScreenChangeReceiver screenChangeReceiver;
    private ControllerReceiver controller;
    private AlarmManager alarmManager;
    private PendingIntent wakeupPendingIntent;
    private Handler handler;
    private ConnectivityManager connectivityManager;
    private WatchItem watchItem;
    private OfflineLocationClient offlineLocationClient;
    private BusLineItem busLineItem;
    private float r = 500f;
    private Runnable tryAddDeoFenceAgainRunnable = new Runnable() {
        @Override
        public void run() {
            addGeoFence(stationItem);
            Toast.makeText(getApplicationContext(), "add again", Toast.LENGTH_SHORT).show();
        }
    };
    private PendingIntent alarmPendingIntent;
    private Intent alarmIntent;
    private PowerManager pm;
    private Intent monitorService;
    private BDNotifyListener notifyListener;
    private LocationClient baiduLocationClient;
    private MonitorConnection monitorServiceConnection;
    private LocationClientOption locationClientOption;
    private NotifyListener bdNotifyListener;

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
        registerReceivers();
        initBDLocationClient();
        alarmIntent = new Intent(ARRIVED_PROXIMITY_ACTION);
        handler = new Handler(getMainLooper());
        offlineLocationClient = new OfflineLocationClient(this);
        prepareAliveAction();
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        stationsRecordHelper = new StationsRecordHelper(this.getApplicationContext(), StationsRecordHelper.DATABASE_NAME, null, 1);
        bindMonitorService();
    }

    private void initBDLocationClient() {
        baiduLocationClient = new LocationClient(getApplicationContext());
        locationClientOption = new LocationClientOption();
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationClientOption.setIsNeedAddress(false);
        locationClientOption.setIsNeedLocationDescribe(false);
        locationClientOption.setLocationNotify(true);
        locationClientOption.setIsNeedAltitude(false);
        locationClientOption.setNeedDeviceDirect(false);
        locationClientOption.setOpenAutoNotifyMode(3 * 60 * 1000, 300, LOC_SENSITIVITY_HIGHT);
        baiduLocationClient.setLocOption(locationClientOption);
    }

    private void bindMonitorService() {
        monitorService = new Intent(this, MonitorService.class);
//         monitorServiceConnection=new MonitorConnection();
//         bindService(monitorService,monitorServiceConnection,BIND_AUTO_CREATE);
    }

    private void registerReceivers() {
        screenChangeReceiver = new ScreenChangeReceiver(this);
        controller = new ControllerReceiver(this);
        IntentFilter controlIntentFilter = new IntentFilter();
        controlIntentFilter.addAction(GEOFENCE_CANCLE_ATCITON);
        controlIntentFilter.addAction(WAKE_UP_ACTION);
        controlIntentFilter.addAction(ARRIVED_ACTION);
        controlIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        controlIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(controller, controlIntentFilter);
    }

    private void prepareAliveAction() {
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        wakeupPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(WAKE_UP_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //先实现单点提醒，后续可以实现多点提醒
        mGeoFenceClient.removeGeoFence();
        addGeoFence(intent);
        startAlarmTask();
        isWatching = true;
        return Service.START_NOT_STICKY;
    }

    /*
     *息屏时保证系统不休眠
     */
    private void startAlarmTask() {
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 60 * 1000, wakeupPendingIntent);
        wakeLock.acquire(3 * 3600 * 1000);
    }

    private void startWatchingNotification() {
        Intent startLaunchActivity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, startLaunchActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        RemoteViews remoteViews;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.watching_notification_view);
            Intent cancelIntent = new Intent(GEOFENCE_CANCLE_ATCITON);
            remoteViews.setOnClickPendingIntent(R.id.cancel_watch, PendingIntent.getBroadcast(getApplicationContext(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT));
            remoteViews.setTextViewText(R.id.station_info, "您设置了" + stationItem.getBusStationName());
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Arrived")
                    .setContentText("正在监控" + stationItem.getBusStationName() + "\n" + watchItem.getBusLineItem().getBusLineName())
                    .setSmallIcon(R.drawable.bus_station)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigPictureStyle())
                    .setOngoing(true)
                    .setCustomBigContentView(remoteViews);
            notification = notificationBuilder.build();
        }
        startForeground(ADD_GEOFENCE_ID, notification);
    }

    private void addGeoFence(Intent intent) {
        watchItem = intent.getParcelableExtra("TARGET_ITEM");
        stationItem = watchItem.getBusStationItem();
        busLineItem = watchItem.getBusLineItem();
        if (busLineItem.getBusLineType().contains("地铁") || busLineItem.getBusLineType().contains("轻轨")) {
            r = 1000.0f;
        } else {
            r = 500f;
        }
        if (bdNotifyListener == null) {
            bdNotifyListener = new NotifyListener();
        }
        bdNotifyListener.SetNotifyLocation(stationItem.getLatLonPoint().getLatitude(), stationItem.getLatLonPoint().getLongitude(), r, "gcj02");
        baiduLocationClient.registerNotify(bdNotifyListener);
        baiduLocationClient.start();
        baiduLocationClient.requestNotifyLocation();
        mGeoFenceClientProxy.setBusStationItem(stationItem);
        mGeoFenceClientProxy.setWatchItem(watchItem);
//        addGeoFence(stationItem);
        alarmIntent.putExtra("TARGET_ITEM", stationItem);
        alarmPendingIntent = PendingIntent.getBroadcast(this, PROXIMITY_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        offlineLocationClient.addProximityAlert(stationItem.getLatLonPoint(), r, 30 * 1000, alarmPendingIntent);
//        handler.postDelayed(tryAddDeoFenceAgainRunnable, 2000);
        startWatch();
    }

    private void addGeoFence(BusStationItem stationItem) {
        DPoint dPoint = new DPoint();
        dPoint.setLatitude(stationItem.getLatLonPoint().getLatitude());
        dPoint.setLongitude(stationItem.getLatLonPoint().getLongitude());
        mGeoFenceClient.addGeoFence(dPoint, r, "BUS_STATION");
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
        if (alarmPendingIntent != null)
            offlineLocationClient.removeProximityAlert(alarmPendingIntent);
        if (screenChangeReceiver != null) {
            unregisterReceiver(screenChangeReceiver);
        }
        unregisterReceiver(controller);
        if (wakeLock.isHeld())
            wakeLock.release();
        LOGUtil.logE(this, "onDestroy");
        mGeoFenceClient.removeGeoFence();
        mGeoFenceClient = null;
        mGeoFenceClientProxy = null;
//        unbindService(monitorServiceConnection);
        super.onDestroy();
    }

    private void startWatch() {
        startWatchingNotification();
//        handler.removeCallbacks(tryAddDeoFenceAgainRunnable);
        stationsRecordHelper.insertData(watchItem);
        //发送一个广播通知UI控件更新状态
        //更改该服务到前台通知
        //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
        //发送一个添加围栏成功的broadcast，更新ui
        Intent intent = new Intent(ADD_GEOFENCE_SUCCESS_ACTION);
        intent.putExtra("ON_WATCH_ITEM", watchItem);
        sendBroadcast(intent);
        isWatching = true;
        monitorService.putExtra("TARGET_ITEM", watchItem);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            monitorService.putExtra("R", r);
            startService(monitorService);

        }

    }

    @Override
    public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
        if (i == GeoFence.ADDGEOFENCE_SUCCESS) {//判断围栏是否创建成功
            Toast.makeText(getApplicationContext(), "围栏创建成功" + s, Toast.LENGTH_SHORT).show();
            startWatchingNotification();
            handler.removeCallbacks(tryAddDeoFenceAgainRunnable);
            stationsRecordHelper.insertData(watchItem);
            //发送一个广播通知UI控件更新状态
            //更改该服务到前台通知
            //geoFenceList就是已经添加的围栏列表，可据此查看创建的围栏
            //发送一个添加围栏成功的broadcast，更新ui
            Intent intent = new Intent(ADD_GEOFENCE_SUCCESS_ACTION);
            intent.putExtra("ON_WATCH_ITEM", watchItem);
            sendBroadcast(intent);
            isWatching = true;
            monitorService.putExtra("TARGET_ITEM", watchItem);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                monitorService.putExtra("R", r);
                startService(monitorService);

            }
        } else {
            Toast.makeText(getApplicationContext(), "围栏创建失败" + s, Toast.LENGTH_SHORT).show();
            LOGUtil.logE(this, "围栏创建失败" + s);
        }
    }

    @Override
    public void onGeoPointAdd(DPoint dPoint, float r, String id) {

    }

    @Override
    public void onGeoPointRemoved() {
        stopForeground(true);
        baiduLocationClient.removeNotifyEvent(bdNotifyListener);
        baiduLocationClient.stop();
        if (alarmPendingIntent != null)
            offlineLocationClient.removeProximityAlert(alarmPendingIntent);
        Intent removeGeoFenceIntent = new Intent(ACTION_GEOFENCE_REMOVED);
        isWatching = false;
        alarmManager.cancel(wakeupPendingIntent);
        if (wakeLock.isHeld()) {
            LOGUtil.logE(this, "releaseLock");
            wakeLock.release();
        }
        sendBroadcast(removeGeoFenceIntent);
        stopSelf();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            stopService(monitorService);
    }

    @Override
    public void onBroadcastReceive(Context context, Intent intent) {
        LOGUtil.logE(this, intent.getAction());
        if (isWatching) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:
                    Intent startAliveAction = new Intent(this, LiveActivity.class);
                    startActivity(startAliveAction);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    break;
            }
        }
    }

    @Override
    public void onControlBroadcastReceive(Intent intent) {
        LOGUtil.logE(this, intent.getAction());
        switch (intent.getAction()) {
            case GEOFENCE_CANCLE_ATCITON:
                watchItem = null;
                mGeoFenceClientProxy.removeDPoint();
                if (wakeLock.isHeld())
                    wakeLock.release();
                stopSelf();
                break;
            case WAKE_UP_ACTION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!pm.isDeviceIdleMode()) {
                        return;
                    }
                }
//                }else {
//                    if(CheckSystemActive.isScreenOn(this)){
//                        return;
//                    }
//                }
                break;
            case ARRIVED_ACTION:
                mGeoFenceClientProxy.removeDPoint();
                if (wakeLock.isHeld())
                    wakeLock.release();
                alarmManager.cancel(wakeupPendingIntent);
                watchItem = null;
                LOGUtil.logE(this, "arrived");
                stopSelf();
                break;
            case ConnectivityManager.CONNECTIVITY_ACTION:
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null) {
                    Toast.makeText(this, networkInfo.getDetailedState().toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case LocationManager.PROVIDERS_CHANGED_ACTION:
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(this, "GPS 不可用，可能会影响定位精度", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private boolean checkNetStatueIsAlive() {
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isAvailable() && netInfo.isConnected();
    }

    class MonitorConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }

    private class NotifyListener extends BDNotifyListener {
        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            if (v <= r) {
                Toast.makeText(getApplicationContext(), "距离目标站点还有" + String.valueOf(v) + "米", Toast.LENGTH_SHORT).show();
                sendBroadcast(alarmIntent);
            }
        }
    }


}
