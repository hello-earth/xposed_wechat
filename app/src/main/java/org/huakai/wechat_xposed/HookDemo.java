package org.huakai.wechat_xposed;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by Administrator on 2017/4/28.
 */

public class HookDemo implements IXposedHookLoadPackage {

    String getMessageClass = "com.tencent.mm.e.b.by"; //wechat version 6.5.7

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
            if("6.3.31".equals(versionName)){
                getMessageClass = "com.tencent.mm.e.b.bv";
            }
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods(cl);
                }
            });
        }
    }

    private void hookWechatMessage(final ClassLoader cl) {
        findAndHookMethod(this.getMessageClass, cl, "b", Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int type = (int) getObjectField(param.thisObject, "field_type");
                log("type="+type+"; talker="+getObjectField(param.thisObject, "field_talker").toString()+"; field_content="+
                        getObjectField(param.thisObject, "field_content").toString());

                if(type==0x13000031 && "notifymessage".equals(getObjectField(param.thisObject, "field_talker").toString())){
                    //面对面转账消息
                    //自己处理
                    log("catch message which you want.");
                }
            }
        });
    }

    private void hookMethods(final ClassLoader cl) {
        log("init hookMethods");
        hookWechatMessage(cl);
    }

}