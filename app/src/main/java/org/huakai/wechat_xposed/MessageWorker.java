package org.huakai.wechat_xposed;


import android.os.Message;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedBridge.log;


public class MessageWorker extends XC_MethodHook {

    ClassLoader paramClassLoader;
    Class<?> mModleClass;
    Class<?> mModelMultiClassh;

    public MessageWorker(ClassLoader paramClassLoader){
        this.paramClassLoader = paramClassLoader;
        mModleClass = XposedHelpers.findClass(VersionParam.networkRequest, paramClassLoader);
        mModelMultiClassh = XposedHelpers.findClass(VersionParam.textMessageBeanClass, paramClassLoader);
    }

    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramMethodHookParam) {
        Message localObject = (Message)paramMethodHookParam.args[0];
        String talker = localObject.getData().getString("notification.show.talker");
        int type = localObject.getData().getInt("notification.show.message.type");
        String content = localObject.getData().getString("notification.show.message.content");
        log("type="+type+"; talker="+talker+"; field_content="+content);
    }

    public boolean sendText(String toUser,  String content){
        return (boolean)XposedHelpers.callMethod(XposedHelpers.callStaticMethod(this.mModleClass, VersionParam.getNetworkByModelMethod, new Object[0]),
                VersionParam.sendMessageMethod,new Object[] { XposedHelpers.newInstance(this.mModelMultiClassh, new Object[] { toUser, content, 1 }) });
    }
}