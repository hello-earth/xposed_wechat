package org.huakai.wechat_xposed;

/**
 * Created by Administrator on 2017/6/15.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class TaskWorker extends BroadcastReceiver{

    Context mContext;
    static View button;

    public static void setButton(View button){
        TaskWorker.button = button;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        execute();
//        PlanTaskService.sendUpdateBroadcast(context);
    }

    private void execute(){
        Log.d("TaskWorker","on exec task");
        try {
            if(TaskWorker.button!=null)
                TaskWorker.button.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}