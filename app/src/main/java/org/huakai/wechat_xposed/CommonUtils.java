package org.huakai.wechat_xposed;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.R.id.message;
import static de.robv.android.xposed.XposedBridge.log;


/**
 * Created by TK on 2017-02-13.
 */

public class CommonUtils {
    public final static String LOG_TAG = "javahook";
    public final static String ALER_MSG_ORGIN="后台已被管理员关闭，请稍后再试。";
    public static String ALER_MSG=ALER_MSG_ORGIN;

    public final static int USER_SHARE_PANGE = 0x1;
    public final static int USER_REGET_PANGE = 0x2;
    public final static int USER_EMPTY_PANGE = 0x3;
    public final static int USER_OVERDUE_PANGE = 0x4;
    public final static int USER_SWITCH_JUMP= 0x5;
    public final static int USER_ISSUE_PANGE = 0x6;
    public final static int USER_HISTORY_PANGE= 0x7;
    public final static int  USER_DO_LIFTBAN = 0x8;
    public final static int  USER_SWITCH_GROUP = 0x9;
    public final static int  USER_NEW_VERSION = 0xA;
    public final static int  USER_OLD_VERSION = 0xB;

    public final static int ADMIN_REGISTER_ACCOUNT = 0x10;
    public final static int ADMIN_STATUS_ACCOUNT = 0x11;
    public final static int ADMIN_READD_ACCOUNT = 0x12;
    public final static int ADMIN_CHECK_OVERDUCE = 0x13;
    public final static int ADMIN_SERVICE_RESUME = 0x14;
    public final static int ADMIN_GET_UNTOUCH_USERS = 0x15;

    public final static int USER_SHARE_ZXFOOLSDAY = 0x20;
    public final static int USER_SHAREHELP_ZXFOOLSDAY = 0x21;

    public final static int USER_CMBC_INIT = 0x30;
    public final static int USER_CMBC_GETCODE = 0x31;
    public final static int USER_CMBC_UPDATECODE = 0x32;
    public final static int USER_CMBC_CHECKEXPIRE = 0x33;
    public static String cookie = "";


    public static void httpPost(final String wxid, final String urlPath, final String datas, final Handler mHandler) {
        httpPost(0,wxid,urlPath,datas,mHandler);
    }

    public static void httpPost(final int what, final String wxid, final String urlPath, final String datas, final Handler mHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                Message message = new Message();
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setReadTimeout(30000);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Connection:", "keep-alive");
                    connection.setRequestProperty("Content-Type",  "application/x-www-form-urlencoded");// 设置请求 参数类型
                    connection.setRequestProperty("User-Agent",  "Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_5 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13G36 MicroMessenger/6.3.31 NetType/WIFI Language/zh_CN");
                    connection.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                    connection.setRequestProperty("Cookie",  "JSESSIONID=72DF697FA520BC02CD326C84465542FB; UM_distinctid=162a36643f362-070afa4d6-3727b66-38400-162a36643f58c; CNZZDATA1260761932=1182971563-1523160728-%7C1523256515");
                    connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                    connection.setRequestProperty("Referer", "https://enterbj.zhongchebaolian.com/enterbj/jsp/enterbj/index.html");


                    byte[] sendData = datas.getBytes("UTF-8");// 将请求字符串转成UTF-8格式的字节数组

                    OutputStream outputStream = connection.getOutputStream();// 得到输出流对象
                    outputStream.write(sendData);
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = connection.getInputStream();

                    InputStreamReader inputStreamReader = new InputStreamReader(
                            inputStream);
                    BufferedReader bReader = new BufferedReader(inputStreamReader);

                    String str = "";
                    String temp = "";
                    while ((temp = bReader.readLine()) != null) {
                        str = str + temp + "\n";
                    }
                    message.what = what;
                    if("".equals(str))
                        Log.e(LOG_TAG,datas);
                    message.obj = str;
                }
                catch (java.net.SocketTimeoutException ex){
                    message.obj = "{\"wxid\":\""+wxid+"\",\"status\":\"0\",\"msg\":\"请求超时，请稍后再试。\",\"name\":\"\"}";
                }
                catch (Exception e) {
                    message.what = 1;
                    message.obj = "Unexpected code " + e.toString();
                    Log.e(LOG_TAG,datas);
                }
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private static String request(String urlPath) throws IOException {
        URL url = new URL(urlPath.trim());
        //打开连接
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        if(urlPath.indexOf("m.baidu.com")>0) {
            urlConnection.setDoInput(true);// 允许输入
            urlConnection.setDoOutput(true);// 允许输出
            urlConnection.setUseCaches(false); // 不允许使用缓存
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Connection:", "keep-alive");
            urlConnection.setRequestProperty("Accept-Encoding", "deflate");
            urlConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9");
            urlConnection.setRequestProperty("Cache-Control", "max-age=0");
            urlConnection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 6 Build/LYZ28E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Mobile Safari/537.36");
            urlConnection.setRequestProperty("Cookie", cookie);
        }
        if(200 == urlConnection.getResponseCode()){
            //得到输入流
            InputStream is =urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while(-1 != (len = is.read(buffer))){
                baos.write(buffer,0,len);
                baos.flush();
            }
            if(urlPath.indexOf("m.baidu.com")>0){
                List<String> cookies = urlConnection.getHeaderFields().get("set-cookie");
                for(String cook : cookies){
                    cookie+=cook.split(";")[0]+";";
                }
                cookies.get(0);
            }
            return baos.toString("utf-8");
        }
        return "";
    }

    public static void httpGet(final String wxid, final String urlPath, final Handler mHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                Message message = new Message();
                message.what = 0;
                try {
                    message.obj = request(urlPath);
                    if(urlPath.indexOf("m.baidu.com")>0 && message.obj.toString().indexOf("访问出错了")>0){
                        message.obj = request(urlPath);
                    }
                }
                catch (java.net.SocketTimeoutException ex){
                    message.obj = "{\"wxid\":\""+wxid+"\",\"status\":\"0\",\"msg\":\"请求超时，请稍后再试。\",\"name\":\"\"}";
                }
                catch (Exception e) {
                    message.what = 1;
                    message.obj = "Unexpected code " + e.toString();
                }
                if(mHandler!=null)
                    mHandler.sendMessage(message);
            }
        }).start();
    }

    public static String base64Encode(String str){
        try {
            return Base64.encodeToString(str.getBytes("UTF-8"),Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String base64Decode(String str){
        try {
            return new String(Base64.decode(str.getBytes("UTF-8"),Base64.DEFAULT));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String findSubString(String content, String firststr, String endstr){
        int first = content.indexOf(firststr);
        if(first!=-1)
        {
            int end = content.indexOf(endstr,first+firststr.length());
            if(end!=-1)
            {
                return content.substring(first+firststr.length(),end);
            }
        }
        return "";
    }

}
