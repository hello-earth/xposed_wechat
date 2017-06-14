package org.huakai.wechat_xposed;

import android.view.View;

/**
 * Created by Young on 2017/5/2.
 */

public class MessageLooper implements Runnable {
    private String name;
    private boolean flag = true;
    private View view;
    private int retry=0;

    public MessageLooper(String name, View view) {
        this.name = name;
        this.view = view;
    }

    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        try {
            while (flag && retry<10){
                view.performClick();
                retry++;
                Thread.sleep(15000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}