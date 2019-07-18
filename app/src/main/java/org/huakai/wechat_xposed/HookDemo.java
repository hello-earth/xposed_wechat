package org.huakai.wechat_xposed;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import java.io.File;
import java.util.Arrays;
import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by Administrator on 2017/4/28.
 */

public class HookDemo implements IXposedHookLoadPackage {

    static ClassLoader cl;
    OnGotsmsBroadcastReceiver reciver;
    static Context context;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.processName.equals("com.umetrip.android.msky.app")) {
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    context = (Context) param.args[0];
                    cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods(cl);
                }
            });
        }
    }

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
        cls = cls.getSuperclass();
        logMsg("Parent Name: " + cls.getName());
        fields = cls.getDeclaredFields();
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

    private void hookMyMethod1() {
        findAndHookMethod("com.umetrip.android.umehttp.S2cRspBodyWrapPB", cl, "S2cRspBodyWrapPB",
                Object.class,String.class,String.class,String.class,String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        logStackInfo();
                        logMsg("================");
                        logObjInfo(param.args[0]);
                        logMsg(param.args[1].toString());
                        logMsg(param.args[2].toString());
                        logMsg(param.args[3].toString());
                        logMsg(param.args[4].toString());
                        byte[] switchData = (byte[])param.getResult();
                        logMsg(Arrays.toString(switchData));
                        logMsg("================");
                    }
                }
        );
    }

    private void hookMyMethod2() {
        XposedHelpers.findAndHookMethod("com.ume.android.lib.common.log.SystemLog",cl, "upload",
                Context.class,int.class,String[].class,
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        return null;
                    }
                });
//        Class<?> xlog = findClass("com.tencent.mars.xlog.Log", cl);
//        callStaticMethod(xlog,"setLevel",new Object[]{1,true});
    }

    private void hookMyMethod3(){
        findAndHookMethod("com.umetrip.android.umehttp.d", cl, "processResponse",
                "com.lzy.okgo.model.Response",boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logMsg("com.umetrip.android.umehttp.d");
                        logMsg(param.thisObject.getClass().getGenericSuperclass().toString());
                        if(param.thisObject.getClass().getGenericSuperclass().toString().indexOf("S2cGetFlightStatusOrFlightList")==-1) {
                            return;
                        }
                        Object v0 = XposedHelpers.callMethod(param.args[0],"body",new Object[]{});
                        v0 = XposedHelpers.callMethod(v0,"getS2cRspBodyWrapPB",new Object[]{});
                        v0 = XposedHelpers.callMethod(v0,"getResponsebody",new Object[]{});
                        Class<?> xlog = findClass("com.umetrip.S2cRspBodyWrapPB.S2cRspBodyWrapPB.S2cRspBodyWrapPB.b.S2cRspBodyWrapPB", cl);
                        v0 = callStaticMethod(xlog,"S2cRspBodyWrapPB",new Object[]{(byte[])v0,findClass("com.ume.android.lib.common.s2c.S2cGetFlightStatusOrFlightList", cl)});
                        Class<?> gson = findClass("com.google.gson.f", cl);
                        Object gsonObject = XposedHelpers.newInstance(gson, new Object[]{});
                        Object v1 = XposedHelpers.callMethod(gsonObject,"CommonResponse",new Object[]{});
                        v1 = XposedHelpers.callMethod(v1,"S2cRspBodyWrapPB",new Object[]{v0});
                        logMsg("##hookMyMethod3#"+v1.toString());
                    }
                }
        );
    }

    private void hookMyMethod4(){
        findAndHookMethod("com.umetrip.android.umehttp.a", cl, "convertResponse",
                "okhttp3.Response",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logMsg(param.thisObject.getClass().getGenericSuperclass().toString());
                        logObjInfo(param.thisObject);
                        logStackInfo();
                    }
                }
        );

    }

    private void hookMethods(final ClassLoader cl) {
        if(reciver==null) {
            log("init hookMethods");
//            hookMyMethod1();
//            hookMyMethod4();
            reciver = new OnGotsmsBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("wxRobot.action.on_ume_test");
            context.registerReceiver(reciver, filter);
            log("registerReceiver");
            hookMyMethod2();
        }
    }

    private static void logMsg(String msg){
        System.out.println(msg);
    }

    private Object[] getFlightStatusByCodeData(String flithno,String date,String rpid,String vername){
        Class<?> UmeSystem = findClass("com.ume.android.lib.common.config.UmeSystem", cl);
        String key = (String)callStaticMethod(UmeSystem,"getKey",new Object[]{4,context,new String[]{flithno,"","",date}});
        logMsg("flithno="+flithno+" date="+date+" UmeSystem get key="+key);
        Class<?> C2sGetFlightStatusByCode = findClass("com.ume.android.lib.common.c2s.C2sGetFlightStatusByCode", cl);
        Object C2sGetFlightStatusByCodeObject = XposedHelpers.newInstance(C2sGetFlightStatusByCode, new Object[]{});
        XposedHelpers.callMethod(C2sGetFlightStatusByCodeObject,"setFlightNo",new Object[]{flithno});
        XposedHelpers.callMethod(C2sGetFlightStatusByCodeObject,"setDeptFlightDate",new Object[]{date});
        Object[] digst = new Object[2];
        digst[0] = C2sGetFlightStatusByCodeObject;
        digst[1] = key;
        return digst;
    }

    private Object[] getC2sSearchFlyByAreaData(String rstartcity,String rendcity,String date,String rpid,String vername){
        Class<?> UmeSystem = findClass("com.ume.android.lib.common.config.UmeSystem", cl);
        String key = (String)callStaticMethod(UmeSystem,"getKey",new Object[]{3,context,new String[]{rstartcity,rendcity,date}});
        logMsg("rstartcity="+rstartcity+" date="+date+" UmeSystem get key="+key);
        Class<?> C2sSearchFlyByArea = findClass("com.ume.android.lib.common.c2s.C2sSearchFlyByArea", cl);
        Object C2sSearchFlyByAreaObject = XposedHelpers.newInstance(C2sSearchFlyByArea, new Object[]{});
        XposedHelpers.callMethod(C2sSearchFlyByAreaObject,"setRdate",new Object[]{date});
        XposedHelpers.callMethod(C2sSearchFlyByAreaObject,"setRendcity",new Object[]{rendcity});
        XposedHelpers.callMethod(C2sSearchFlyByAreaObject,"setRstartcity",new Object[]{rstartcity});
        Object[] digst = new Object[2];
        digst[0] = C2sSearchFlyByAreaObject;
        digst[1] = key;
        return digst;
    }

    private Object[] getC2sGetPreFlightList(String deptAirportCode,String destAirportCode,String deptFlightDate,String flightNo, String regNo, String std,String rpid,String vername){
        Class<?> UmeSystem = findClass("com.ume.android.lib.common.config.UmeSystem", cl);
        String key = (String)callStaticMethod(UmeSystem,"getKey",new Object[]{5,context,new String[]{deptAirportCode,flightNo, regNo,std,deptFlightDate}});
        logMsg("rstartcity="+deptAirportCode+" date="+deptFlightDate+" UmeSystem get key="+key);
        Class<?> C2sGetPreFlightList = findClass("com.umetrip.android.msky.app.flight.c2s.C2sGetPreFlightList", cl);
        Object C2sGetPreFlightListObject = XposedHelpers.newInstance(C2sGetPreFlightList, new Object[]{});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setDeptAirportCode",new Object[]{deptAirportCode});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setDestAirportCode",new Object[]{destAirportCode});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setDeptFlightDate",new Object[]{deptFlightDate});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setFlightNo",new Object[]{flightNo});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setRegNo",new Object[]{regNo});
        XposedHelpers.callMethod(C2sGetPreFlightListObject,"setStd",new Object[]{std});
        Object[] digst = new Object[2];
        digst[0] = C2sGetPreFlightListObject;
        digst[1] = key;
        return digst;
    }

    private void upBytes(final int what, String rpid,String vername,Object[] digst){
        if(digst[0]!=null && digst[1]!=null){
            try{
                Class<?> umehttp_e = findClass("com.umetrip.android.umehttp.e", cl);
                Object ParamBuilderObject = callStaticMethod(umehttp_e,"b",new Object[]{3});
                XposedHelpers.callMethod(ParamBuilderObject,"pid",new Object[]{rpid});
                XposedHelpers.callMethod(ParamBuilderObject,"version",new Object[]{vername});
                XposedHelpers.callMethod(ParamBuilderObject,"key",new Object[]{digst[1]});
                XposedHelpers.callMethod(ParamBuilderObject,"data",new Object[]{digst[0]});
                Object callbacko;
                if(what==1){
                    Class callback = createClass("com.umetrip.android.umehttp.S2cGetFlightCallback");
                    callbacko = XposedHelpers.newInstance(callback, new Object[]{context});
                }else {
                    Class callback = createClass("com.lzy.okgo.callback.MyStringCallback");
                    callbacko = XposedHelpers.newInstance(callback, new Object[]{});
                }
                if(callbacko!=null){
                    XposedHelpers.callMethod(ParamBuilderObject,"request", new Object[]{callbacko});
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private void feedback(int what, String response){
        Intent mIntent = new Intent();
        mIntent.setAction("wxRobot.action.on_ume_test_Respond");
        mIntent.putExtra("what", what);
        mIntent.putExtra("response", response);
        context.sendBroadcast(mIntent);
    }

    private class OnGotsmsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int what = intent.getIntExtra("what", -1);
            String flight_no,date,rpid,vername,rstartcity,rendcity,regNo,std;
            Object[] digst;
            switch (what) {
                case 1:
                    flight_no = intent.getStringExtra("flight_no");
                    date = intent.getStringExtra("date");
                    rpid = intent.getStringExtra("rpid");
                    vername = intent.getStringExtra("vername");
                    digst = getFlightStatusByCodeData(flight_no, date, rpid, vername);
                    upBytes(what, rpid, vername, digst);
                    break;
                case 2:
                    date = intent.getStringExtra("date");
                    rpid = intent.getStringExtra("rpid");
                    vername = intent.getStringExtra("vername");
                    rstartcity = intent.getStringExtra("rstartcity");
                    rendcity = intent.getStringExtra("rendcity");
                    digst = getC2sSearchFlyByAreaData(rstartcity,rendcity, date, rpid, vername);
                    upBytes(what, rpid, vername, digst);
                    break;
                case 3:
                    rpid = intent.getStringExtra("rpid");
                    vername = intent.getStringExtra("vername");
                    rstartcity = intent.getStringExtra("destAirportCode");
                    rendcity = intent.getStringExtra("deptAirportCode");
                    flight_no = intent.getStringExtra("flight_no");
                    date = intent.getStringExtra("date");
                    regNo = intent.getStringExtra("regNo");
                    std = intent.getStringExtra("std");
                    digst = getC2sGetPreFlightList(rstartcity,rendcity,date, flight_no, regNo, std, rpid, vername);
                    upBytes(what, rpid, vername, digst);
                    break;
                default:
                    break;
            }
        }
    }


    private Class createClass(String clzn) throws Exception {
        File dexOutputDir = context.getDir("code_cache", 0);
        DexClassLoader loader =
                new DexClassLoader(Environment.getExternalStorageDirectory().toString() + "/other_dex.dex", dexOutputDir.getAbsolutePath(), null, context.getClassLoader());
        try {
            Class clz = loader.loadClass(clzn);
            return clz;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}