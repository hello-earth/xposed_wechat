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
import android.widget.CheckBox;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
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

    public static void setValue(Object instance, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        java.lang.reflect.Field field = instance.getClass().getDeclaredField(fileName);
        field.setAccessible(true);
        field.set(instance, value);
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
                    HideModule.hide(cl);
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
                        log("//////////////showCountTimeFinish///////////////// @"+getCurrentTime());
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
                        log("//////////////RentHouseDetailFragment///////////////// @"+getCurrentTime());
                        Object button = findObj(param.thisObject,"aT");
                        if(button!=null){
                            if(((Button)button).isEnabled())
                                ((Button)button).performClick();
                        }
                    }
                }
        );
        findAndHookMethod("com.ziroom.ziroomcustomer.signed.SignedCertInfoConfirmActivity", cl, "a",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("//////////////SignedCertInfoConfirmActivity///////////////// @"+getCurrentTime());
                        Object button = findObj(param.thisObject,"cert_info_confirm_btn");
                        if(button!=null)
                            ((TextView)button).performClick();
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.SignerAptitudeActivity", cl, "e",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("//////////////SignerAptitudeActivity///////////////// @"+getCurrentTime());
                        final Object button = findObj(param.thisObject,"signer_btn_next");
                        if(button!=null)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((Button)button).performClick();
                                }
                            },500);
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.SignedLeaseInfoActivity", cl, "a",
                "com.ziroom.ziroomcustomer.model.TenancyInfo",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////SignedLeaseInfoActivity TenancyInfo///////////////// @"+getCurrentTime());
                        Object obj = findObj(param.thisObject,"r");
                        CheckBox cb = (CheckBox)XposedHelpers.callMethod(obj,"getCheckBox");
                        cb.setChecked(true);
                        XposedHelpers.callMethod(param.thisObject, "e");
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.PayTermsActivity", cl, "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////PayTermsActivity///////////////// @"+getCurrentTime());
                        Object obj = findObj(param.thisObject,"g");
                        List<Integer> ints = (List<Integer>)XposedHelpers.callMethod(obj, "getmList");
                        obj = XposedHelpers.callMethod(obj, "getmOnCheck");
                        XposedHelpers.callMethod(obj, "onItemClick", new Object[]{ints.indexOf(3)});
                        final Object button = findObj(param.thisObject,"c");
                        if(button!=null)
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    ((Button)button).performClick();
                                }
                            });
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.ContractTermsActivity", cl, "onCreate",
                Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////SignedLeaseInfoActivity///////////////// @"+getCurrentTime());
                        final Object button = findObj(param.thisObject,"f");
                        if(button!=null) {
                            setValue(param.thisObject,"r",1);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((Button) button).performClick();
                                }
                            }, 300);
                        }
                    }
                }
        );

        findAndHookConstructor("com.ziroom.ziroomcustomer.signed.SignedWebActivity$4", cl,
                "com.ziroom.ziroomcustomer.signed.SignedWebActivity",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////SignedWebActivity$4///////////////// @"+getCurrentTime());
                        XposedHelpers.callMethod(param.thisObject, "onJsLinkCallBack", new Object[]{""});
                    }
                }
        );


        findAndHookMethod("com.ziroom.ziroomcustomer.signed.PayInformationActivity", cl, "a",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////PayInformationActivity///////////////// @"+getCurrentTime());
                        final Object button = findObj(param.thisObject,"D");
                        if(button!=null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((Button) button).performClick();
                                }
                            }, 500);
                        }
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.ConfirmContractActivity", cl, "b",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        log("//////////////ConfirmContractActivity///////////////// @"+getCurrentTime());
                        final Object button = findObj(param.thisObject,"K");
                        if(button!=null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((Button) button).performClick();
                                }
                            }, 300);
                        }
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

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Timestamp(System.currentTimeMillis()));
        return time;
    }
}