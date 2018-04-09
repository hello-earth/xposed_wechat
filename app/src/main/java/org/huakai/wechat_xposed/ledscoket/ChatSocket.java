package org.huakai.wechat_xposed.ledscoket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.huakai.wechat_xposed.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import de.robv.android.xposed.XposedHelpers;

public class ChatSocket extends Thread{
	Socket scoket;
    Context mContext;
    OnRespondBroadcastReceiver reciver;

	public ChatSocket(Context mContext, Socket  s) {
		this.scoket=s;
        this.mContext = mContext;
        if(reciver==null) {
            reciver = new OnRespondBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("wxRobot.action.onRespond");
            mContext.registerReceiver(reciver, filter);
        }
	}

	@Override
	public void run() {
		try {
            InputStream inputStream = scoket.getInputStream();
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;
            // 从InputStream当中读取客户端所发送的数据
            while ((temp = inputStream.read(buffer)) != -1) {
                String msg = new String(buffer, 0, temp);
                System.out.println(scoket.getPort()+"-发来消息:\n"+msg);
//                scoket.getOutputStream().write((msg).getBytes("UTF-8"));
                Intent mIntent = new Intent();
                mIntent.setAction("wxRobot.action.onGotsms");
                mIntent.putExtra("msg", msg);
                mContext.sendBroadcast(mIntent);
            }
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("断开了一个客户端链接");
			e.printStackTrace();
		}
	}

    private class OnRespondBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            System.out.println("OnRespondBroadcastReceiver =>> "+msg);
            try {
                scoket.getOutputStream().write((msg).getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}