package org.huakai.wechat_xposed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Main implements IXposedHookLoadPackage{

    static Context context;
    private static boolean isDug = true;
    private static List<ClassLoader> paramClassLoaders = new ArrayList<>();
    private static Class<?> mModleWorkerProfileClass;
    private static OnGotsmsBroadcastReceiver reciver;
    private static Context myApplication;

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
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable{
        if (lpparam.packageName.indexOf("com.zcbl.bjjj_driving")!=-1) {
            findAndHookMethod("com.stub.StubApp", lpparam.classLoader, "ᵢˋ", Context.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if(param!=null) {
                                context = (Context) param.args[0];
                                addClassLoader(context.getClassLoader());
                            }
                        }
                    });
        }
    }

    private void addClassLoader(ClassLoader cl){
        if(reciver==null) {
            reciver = new OnGotsmsBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("wxRobot.action.onGotEncrypt");
            context.registerReceiver(reciver, filter);
        }
        synchronized (paramClassLoaders) {
            paramClassLoaders.add(cl);
        }
        HideModule.hide(cl);
        XposedHelpers.findClass("com.zcbl.driving_simple.activity.MyApplication",cl);
        hookMyApplication(cl);
    }

    private ClassLoader getTargetClassLoader(String className) {
        for (ClassLoader loader : paramClassLoaders){
            try {
                XposedHelpers.findClass(className, loader);
            } catch (Exception ex) {
                continue;
            }
            return loader;
        }
        return null;
    }

    private void hookMyApplication(ClassLoader cl){
        findAndHookMethod("com.zcbl.driving_simple.activity.FirstPagerAcitivty", cl, "initView", Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logMsg("hookMyApplication afterHookedMethod~~~~");
                        if(param!=null) {
                            myApplication = (Context)param.thisObject;
                            try {
                                Class<?> SecuritySignatureClass = XposedHelpers.findClass("com.alibaba.wireless.security.jaq.SecuritySignature", getTargetClassLoader("com.alibaba.wireless.security.jaq.SecuritySignature"));
                                Object SecuritySignature = XposedHelpers.newInstance(SecuritySignatureClass, new Object[]{myApplication});
                                String result = XposedHelpers.callMethod(SecuritySignature, "atlasSign", new Object[]{"7472018-4-102C9CE212C53D4475AC236AF1CEFCC8A6hb135761802120101199306189516205007492018-04-09 11:26:47", "ab0d12bf-b0ec-4694-bc3e-c67689c36bab"}).toString();
                                logMsg(result);
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        );
//        findAndHookMethod("com.zcbl.driving_simple.util.MyLogUtils", cl, "i", String.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        logMsg(param.args[0].toString());
//                    }
//                }
//        );

    }


    private static  void logMsg(String msg){
        if(isDug)
            System.out.println(msg);
    }

    private void hookMethods(ClassLoader cl){
        logMsg("init bjjj hooker~~");
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Timestamp(System.currentTimeMillis()));
        return time;
    }

    private class OnGotsmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            Class<?> SecuritySignatureClass = XposedHelpers.findClass("com.alibaba.wireless.security.jaq.SecuritySignature", getTargetClassLoader("com.alibaba.wireless.security.jaq.SecuritySignature"));
            Object SecuritySignature =XposedHelpers.newInstance(SecuritySignatureClass, new Object[]{myApplication});
            String result = XposedHelpers.callMethod(SecuritySignature,"atlasSign",new Object[]{msg,"ab0d12bf-b0ec-4694-bc3e-c67689c36bab"}).toString();
            logMsg("input="+msg+"; sign="+result);
            Intent mIntent = new Intent();
            mIntent.setAction("wxRobot.action.onRespond");
            mIntent.putExtra("msg", result);
            context.sendBroadcast(mIntent);
        }
    }

}