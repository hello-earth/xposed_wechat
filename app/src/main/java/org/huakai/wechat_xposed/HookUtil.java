package org.huakai.wechat_xposed;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class HookUtil implements IXposedHookLoadPackage{

    static ClassLoader cl;
    static Context context;


    private static void logObjInfo(Object obj) throws Throwable {
        Class cls = obj.getClass();
        log("Object info begin");
        log("Name: " + cls.getName());
        java.lang.reflect.Field[] fields = cls.getDeclaredFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                log(field.getName() );
            }
        }
        log("Object info ended");
    }

    private static Object findObj(Object obj,String name) throws Throwable {
        Class cls = obj.getClass();
        java.lang.reflect.Field field=cls.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(obj);
    }

    private static void logStackInfo() {
        log("Stack info begin");
        StackTraceElement[] stackTraceElements = (new Throwable()).getStackTrace();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            log(stackTraceElement.toString());
        }
        log("Stack info ended");
    }


    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable{
        if (lpparam.packageName.indexOf("com.ziroom.ziroomcustomer")!=-1) {
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    context = (Context) param.args[0];
                    cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods();
                }
            });
        }
    }

    private void hookMyMethod1() {
        findAndHookMethod("android.webkit.WebViewClient", cl, "onReceivedSslError",
                WebView.class, SslErrorHandler.class, SslError.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        log("on onReceivedSslError~~");
                    }
                }
        );
    }

    private void hookMyMethod2() {
        findAndHookMethod("com.ziroom.commonlibrary.a.a", cl, "onSuccess",
                int.class, Object.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logObjInfo(param.args[1]);
                    }
                }
        );
    }

    public void hookMyMethod3(){
        findAndHookMethod("com.ziroom.ziroomcustomer.findhouse.view.RentHouseDetailFragment", cl, "showCountTimeFinish",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("//////////////showCountTimeFinish/////////////////");
                        Object button = findObj(param.thisObject,"aT");
                        if(button!=null)
                            ((Button)button).performClick();
                    }
                }
        );
        findAndHookMethod("com.ziroom.ziroomcustomer.findhouse.view.RentHouseDetailFragment", cl, "showBottom",
                boolean.class, String.class, boolean.class, boolean.class, String.class, boolean.class, long.class, boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("//////////////showBottom/////////////////");
                        Object button = findObj(param.thisObject,"aT");
                        if(button!=null)
                            ((Button)button).performClick();
                    }
                }
        );

//        Class<?> mModleWorkerProfileClass = findClass("", cl);
////        callStaticMethod(mModleWorkerProfileClass, "getRentHouseDetail", new Object[]{context,"61033245","60166155",null});
////        Class<?> mModelMultiClassh = XposedHelpers.findClass(VersionParam.textMessageBeanClass, cl);
//        Object aaa = XposedHelpers.newInstance(mModleWorkerProfileClass, new Object[]{context, "60380232","6006164",null,null});
//        try{
//            XposedHelpers.callMethod(aaa,"initData");
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
    }

    private void hookMethods(){
        log("init ziroom hooker~~");
        hookMyMethod3();
//        hookMyMethod2();

    }
}