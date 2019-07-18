package org.huakai.wechat_xposed;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class HookCJT implements IXposedHookLoadPackage {

    static ClassLoader cl;
    static Context context;
    private static boolean isDug = true;

    private static void logObjInfo(Object obj) throws Throwable {
        Class cls = obj.getClass();
        logMsg("Object info begin");
        logMsg("Name: " + cls.getName());
        java.lang.reflect.Field[] fields = cls.getDeclaredFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                field.setAccessible(true);
                logMsg(field.getName() +": "+field.get(obj));
            }
        }
        logMsg("Object info ended");
    }

    private static Object findObj(Object obj,String name) throws Throwable {
        Class cls = obj.getClass();
        java.lang.reflect.Field field=cls.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static void setValue(Object instance, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        java.lang.reflect.Field field = instance.getClass().getDeclaredField(fileName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    private static void logStackInfo() {
        logMsg("Stack info begin");
        StackTraceElement[] stackTraceElements = (new Throwable()).getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            logMsg(stackTraceElement.toString());
        }
        logMsg("Stack info ended");
    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        if (lpparam.packageName.indexOf("com.chanjet.chanpay.qianketong")!=-1) {
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    context = (Context) param.args[0];
                    cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods();
                    HideModule.hide(cl);
                }
            });
        }
    }

    private void hookMyMethod1() {
        findAndHookMethod("com.chanjet.chanpay.qianketong.threelib.retrofit.rsa.RSAEncrypt", cl, "decrypt",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logMsg(param.getResult().toString());
                    }
                }
        );
        findAndHookMethod("com.chanjet.chanpay.qianketong.ui.activity.homepage.LifeCircleActivity", cl, "S2cRspBodyWrapPB",
                String.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[0] = "浙江省";
                        param.args[1] = "杭州市";
                    }
                }
        );
    }

    private static  void logMsg(String msg){
        if(isDug)
            System.out.println(msg);
    }

    private void hookMethods(){
        logMsg("init citi hooker~~");
        hookMyMethod1();
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Timestamp(System.currentTimeMillis()));
        return time;
    }
}
