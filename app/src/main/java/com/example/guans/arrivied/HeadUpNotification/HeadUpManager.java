package com.example.guans.arrivied.HeadUpNotification;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by guans on 2017/7/27.
 */

public class HeadUpManager implements View.OnTouchListener {
    private Context mContext;
    private SparseArray<View> notifications;
    private WindowManager windowManager;
    private LinearLayout linearLayout;

    public HeadUpManager(Context context) {
        mContext = context;
        notifications = new SparseArray<>();
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

    }

    public void showHeadUp(View notification, int id) {
        linearLayout = new LinearLayout(mContext);
        LinearLayout.LayoutParams lineLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(notification, lineLayoutParams);
        linearLayout.setOnTouchListener(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        layoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        notifications.put(id, notification);
        windowManager.addView(linearLayout, layoutParams);
    }

    private void dismissHeadUp(int id) {
        if (notifications.valueAt(id) != null) {
            notifications.remove(id);
            windowManager.removeView(linearLayout);
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                windowManager.removeView(linearLayout);
                break;
        }
        return false;
    }
}
