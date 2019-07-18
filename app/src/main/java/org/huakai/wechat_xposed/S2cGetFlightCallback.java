package org.huakai.wechat_xposed;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class S2cGetFlightCallback extends d<S2cGetFlightStatusOrFlightList> {

    public void S2cGetFlightCallback(Context context){

    }

    public void onRequestError(int paramAnonymousInt, String paramAnonymousString)
    {
        System.out.println("###S2cGetFlightCallback#onRequestError#"+paramAnonymousString);
    }

    public void onRequestSuccess(S2cGetFlightStatusOrFlightList v0, boolean v1)
    {
        String str = new f().c().a(v0);
        save("onRequestSuccess",str);
        System.out.println("###S2cGetFlightCallback#onRequestSuccess#"+str);
    }

    public static void save(final String fileName, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                try {
                    File file = new File(Environment.getExternalStorageDirectory(), fileName + ".txt");
                    // 先清空内容再写入
                    fos = new FileOutputStream(file);
                    byte[] buffer = content.getBytes();
                    fos.write(buffer);
                    fos.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}
