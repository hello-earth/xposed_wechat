package org.huakai.wechat_xposed;


import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.lang.reflect.Field;
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
        java.lang.reflect.Field[] fields = cls.getFields();
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
                log(field.getName() + ":" + field.get(obj));
            }
        }
        log("Object info ended");
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
        if (lpparam.packageName.indexOf("com.hebao.app")!=-1) {
            context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods();
                }
            });
        }
    }

    private void hookMyMethod1() {
        findAndHookMethod("com.hebao.app.activity.purse.PurseRegularDetailsActivity", cl, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                log("DQ");
//                log("\n");
//                logObjInfo(param.thisObject);
//                log("\n");
                log("\n");
                logStackInfo();
                log("\n");

            }
        });
    }

    private void hookMyMethod2() {
        findAndHookMethod("com.hebao.app.activity.fragment.FragmentHomePage", cl, "onCreateView",
                LayoutInflater.class, ViewGroup.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                View button = (View)getObjectField(param.thisObject, "Z");
                if(button!=null) {
                    log("found the redpack button and click it.");
                    MessageLooper mLooper = new MessageLooper("",button);
                    new Thread(mLooper).start();
                }else{
                    log("oopppps, not found the redpack button.");
                }
            }
        });
    }

    private void hookMethods(){
        log("init hebao hooker~~");
//        hookMyMethod1();
        hookMyMethod2();
    }
}