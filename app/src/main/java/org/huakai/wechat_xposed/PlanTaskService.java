package org.huakai.wechat_xposed;

/**
 * Created by Administrator on 2017/6/15.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class PlanTaskService {

    private static AlarmManager getAlarmManager(Context ctx){
        return (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    }

    public static void sendUpdateBroadcast(Context ctx){
        AlarmManager am = getAlarmManager(ctx);
        Intent i = new Intent(ctx, TaskWorker.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MINUTE, 1);
//        c.set(2017,6,15,16,20,0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 10,60000, pendingIntent);
        Log.d("TaskWorker","on PlanTaskService");
    }
}