package org.huakai.wechat_xposed.ledscoket;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;


/**
 * Created by Administrator on 2018/1/11.
 */

public class SocketService extends Service {

    AlarmManager mAlarmManager = null;
    PendingIntent mPendingIntent = null;
    static Context mContext;

    @Override
    public int onStartCommand(Intent mintent, int flags, int startId) {
        if(startId==1) {
            new ServerListener(this).start();
        }
        return super.onStartCommand(mintent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private  void createLivingService(){
        if (Build.VERSION.SDK_INT < 18) {
            startForeground(1200, new Notification());
        } else {
            Intent innerIntent = new Intent(getBaseContext(), SocketServiceInnerService.class);
            startService(innerIntent);
            startForeground(1200, new Notification());
        }
    }

    static class SocketServiceInnerService extends Service {

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(1200, new Notification());
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            // TODO: Return the communication channel to the service.
            throw new UnsupportedOperationException("Not yet implemented");
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createLivingService();
        mContext = this;
        Intent mIntent = new Intent(getApplicationContext(), SocketService.class);
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long now = System.currentTimeMillis()+2000;
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, now, 300000, mPendingIntent);
    }

    @Override
    public void onDestroy() {
        Intent mIntent = new Intent(getApplicationContext(), SocketService.class);
        mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(mPendingIntent);
        super.onDestroy();
    }

}
