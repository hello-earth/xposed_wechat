package org.huakai.wechat_xposed;


import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.webkit.SslErrorHandler;
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
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class HookUtil implements IXposedHookLoadPackage{

    static ClassLoader cl;
    static Context context;


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
                        logMsg("on onReceivedSslError~~");
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
                        logMsg("//////////////showCountTimeFinish///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////RentHouseDetailFragment///////////////// @"+getCurrentTime());
                        final Object button = findObj(param.thisObject,"aT");
                        if(button!=null){
                            if(((Button)button).isEnabled()){
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context)findObj(param.thisObject,"bo"));
                                builder.setTitle("提示");
                                builder.setMessage("是否执行抢房操作？");
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ((Button)button).performClick();
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            } else {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"///*////暂不可签约///*////\n"+((Button)button).getText().toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }, 1300);
                            }
                        }
                    }
                }
        );
        findAndHookMethod("com.ziroom.ziroomcustomer.signed.SignedCertInfoConfirmActivity", cl, "a",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        logMsg("//////////////SignedCertInfoConfirmActivity///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////SignerAptitudeActivity///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////SignedLeaseInfoActivity TenancyInfo///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////PayTermsActivity///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////SignedLeaseInfoActivity///////////////// @"+getCurrentTime());
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
                        logMsg("//////////////SignedWebActivity$4///////////////// @"+getCurrentTime());
                        XposedHelpers.callMethod(param.thisObject, "onJsLinkCallBack", new Object[]{""});
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.PayInformationActivity$1", cl, "handleMessage",
                Message.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        logMsg("//////////////PayInformationActivity$1///////////////// @"+getCurrentTime());
                        Message msg = (Message)(param.args[0]);
                        if(msg.what==69664 && ((Boolean) XposedHelpers.callMethod(msg.obj,"getSuccess")).booleanValue()){
                            Object button = findObj(findObj(param.thisObject,"a"),"D");
                            if(button!=null) {
                                ((Button) button).performClick();
                            }
                        }
                    }
                }
        );

        findAndHookMethod("com.ziroom.ziroomcustomer.signed.ConfirmContractActivity$1", cl, "handleMessage",
                Message.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        logMsg("//////////////ConfirmContractActivity$1///////////////// @"+getCurrentTime());
                        Message msg = (Message)(param.args[0]);
                        if(msg.what==69779 && ((Boolean) XposedHelpers.callMethod(msg.obj,"getSuccess")).booleanValue()){
                            Object button = findObj(findObj(param.thisObject,"a"),"K");
                            if(button!=null) {
                                ((Button) button).performClick();
                            }
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

    private static  void logMsg(String msg){
        System.out.println(msg);
    }

    private void hookMethods(){
        logMsg("init ziroom hooker~~");
        hookMyMethod3();
//        hookMyMethod2();

    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Timestamp(System.currentTimeMillis()));
        return time;
    }
}