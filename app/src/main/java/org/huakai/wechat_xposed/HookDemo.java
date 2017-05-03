package org.huakai.wechat_xposed;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.os.Message;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
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

    String iwechatVersion = "";
    static ClassLoader cl;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("com.tencent.mm")) {
            Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
            String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
            iwechatVersion = versionName;
            VersionParam.init(versionName);

            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods(cl);
                }
            });
        }
    }

    private void hookWechatMessage(final ClassLoader cl) {
        findAndHookMethod(VersionParam.getMessageClass, cl, "b", Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                int type = (int) getObjectField(param.thisObject, "field_type");
                String talker = getObjectField(param.thisObject, "field_talker").toString();
                String content = getObjectField(param.thisObject, "field_content").toString();
                int isSend = (int) getObjectField(param.thisObject, "field_isSend");
                if(type==1 && "big-brave".equals(talker) && isSend==0){
                    //面对面转账消息
                    //自己处理
                    log("type="+type+"; talker="+talker+"; field_content="+content+"; isSend="+isSend);
                }
            }
        });
    }

    protected void hookNotification(ClassLoader paramClassLoader) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.booter.notification.b$1", paramClassLoader, "handleMessage",
                new Object[] { Message.class, new MessageWorker(paramClassLoader) });
    }


    private void hookMethods(final ClassLoader cl) {
        log("init hookMethods");
        hookNotification(cl);
    }


}