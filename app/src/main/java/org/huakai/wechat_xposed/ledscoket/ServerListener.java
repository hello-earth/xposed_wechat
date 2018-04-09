package org.huakai.wechat_xposed.ledscoket;

import android.content.Context;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerListener  extends Thread{

    private Context mContext;

    public ServerListener(Context mContext){
        this.mContext = mContext;
    }

    public void run() {
        boolean flag = true;
        try {
            ServerSocket serversocket = new ServerSocket(32039);
            while(flag){
                Socket socket = serversocket.accept();
                ChatSocket cs = new ChatSocket(this.mContext,socket);
                cs.start();
            }
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
    }

}
