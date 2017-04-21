package org.huakai.wechat_xposed;

import android.app.Activity;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static android.text.TextUtils.isEmpty;
import static android.widget.Toast.LENGTH_LONG;
import static de.robv.android.xposed.XposedBridge.log;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.findFirstFieldByExactType;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

public class Main implements IXposedHookLoadPackage {

    private static Object requestCaller;

    private static String wechatVersion = "";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(VersionParam.CODOON_PACKAGE_NAME)) {
            if (isEmpty(wechatVersion)) {
                Context context = (Context) callMethod(callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
                String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                log("Found wechat version:" + versionName);
                wechatVersion = versionName;
                VersionParam.init(versionName);
            }

            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                    hookMethods(cl);
                    HideModule.hide(cl);
                }
            });
        }
    }

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

    private void hookLuckyMoney(final ClassLoader cl) {
        findAndHookMethod(VersionParam.getMessageClass, cl, "b", Cursor.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (!PreferencesUtils.open()) {
                    return;
                }

                if (PreferencesUtils.debugObjInfo() &&  ((int) getObjectField(param.thisObject, "field_isSend")==0)) {
                    Class cls = param.thisObject.getClass();
                    log("\n");
                    logObjInfo(param.thisObject);
                    log("\n");

                    System.out.println("CurrentRow:");
                    DatabaseUtils.dumpCurrentRow((Cursor) param.args[0]);
                    System.out.println("CurrentRow ended.\n");
                }

                if (PreferencesUtils.debugStackInfo()) {
                    log("\n");
                    logStackInfo();
                    log("\n");
                }

                int type = (int) getObjectField(param.thisObject, "field_type");
                if((int) getObjectField(param.thisObject, "field_isSend")==0) {
                    log("type="+type+"; talker="+getObjectField(param.thisObject, "field_talker").toString()+"; field_content="+
                            getObjectField(param.thisObject, "field_content").toString());
                }
                //type=1 普通消息
                //49 分享
                //0x19000031 转账
                //0x1a000031 0x1C000031 红包
                if(type==0x1){
                    String talker = getObjectField(param.thisObject, "field_talker").toString();
                    if("big-brave".equals(talker)){

                    }
                }
                else if (type == 0x1A000031 || type == 0x1C000031) {

                    int status = (int) getObjectField(param.thisObject, "field_status");
                    if (status == 4) {
                        return;
                    }

                    int isSend = (int) getObjectField(param.thisObject, "field_isSend");
                    if (PreferencesUtils.notSelf() && isSend != 0) {
                        return;
                    }

                    String talker = getObjectField(param.thisObject, "field_talker").toString();

                    if (!isGroupTalk(talker)) {
                        if (PreferencesUtils.notWhisper()) {
                            return;
                        }
                        if (isSend != 0) {
                            return;
                        }
                    }

                    String blackList = PreferencesUtils.blackList();
                    if (!isEmpty(blackList)) {
                        for (String wechatId : blackList.split(",")) {
                            if (talker.equals(wechatId.trim())) {
                                return;
                            }
                        }
                    }

                    String contentXml = getObjectField(param.thisObject, "field_content").toString();

                    String senderTitle = getFromXml(contentXml, "sendertitle");
                    String notContainsWords = PreferencesUtils.notContains();
                    if (!isEmpty(notContainsWords)) {
                        for (String word : notContainsWords.split(",")) {
                            if (senderTitle.contains(word)) {
                                return;
                            }
                        }
                    }

                    String nativeUrlString = getFromXml(contentXml, "nativeurl");
                    Uri nativeUrl = Uri.parse(nativeUrlString);
                    int msgType = Integer.parseInt(nativeUrl.getQueryParameter("msgtype"));
                    int channelId = Integer.parseInt(nativeUrl.getQueryParameter("channelid"));
                    String sendId = nativeUrl.getQueryParameter("sendid");
                    requestCaller = callStaticMethod(findClass(VersionParam.networkRequest, cl), VersionParam.getNetworkByModelMethod);

                    Object luckyMoneyRequest = newInstance(findClass("com.tencent.mm.plugin.luckymoney.c.ab", cl),
                            msgType, channelId, sendId, nativeUrlString, "", "", talker, "v1.0");
                    callMethod(requestCaller, "a", luckyMoneyRequest, getDelayTime());
                }
            }
        });

        XposedHelpers.findAndHookMethod(VersionParam.luckyMoneyReceiveUI, cl, VersionParam.receiveUIFunctionName, int.class, int.class, String.class, VersionParam.receiveUIParamName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesUtils.quickOpen()) {
                    Button button = (Button) findFirstFieldByExactType(param.thisObject.getClass(), Button.class).get(param.thisObject);
                    if (button.isShown() && button.isClickable()) {
                        button.performClick();
                    }
                }
            }
        });
    }

    private void hookWechatId(final ClassLoader cl) {
        findAndHookMethod("com.tencent.mm.plugin.profile.ui.ContactInfoUI", cl, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesUtils.showWechatId()) {
                    Activity activity = (Activity) param.thisObject;
                    ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    String wechatId = activity.getIntent().getStringExtra("Contact_User");
                    cmb.setText(wechatId);
                    Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                }
            }
        });

        findAndHookMethod("com.tencent.mm.plugin.chatroom.ui.ChatroomInfoUI", cl, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (PreferencesUtils.showWechatId()) {
                    Activity activity = (Activity) param.thisObject;
                    String wechatId = activity.getIntent().getStringExtra("RoomInfo_Id");
                    ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(wechatId);
                    Toast.makeText(activity, "微信ID:" + wechatId + "已复制到剪切板", LENGTH_LONG).show();
                }
            }
        });
    }

    private static Object snsDB;

    private void hookCodoonInfo(final ClassLoader cl) {

        XposedHelpers.findAndHookMethod("com.codoon.gps.util.TokenVerifyUtil", cl, "getGaea",
                Context.class, String.class,String.class,String.class,String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        log("getGaea -> paramString1:"+param.args[1]+",paramString2:"+param.args[2]+", paramString3:"+param.args[3]+", paramString4:"+param.args[4]);
                    }
                });

        XposedHelpers.findAndHookMethod("com.codoon.gps.http.HttpRequestHelper", cl, "postSportsData",
                Context.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object mUrlParameterCollection = getObjectField(param.thisObject, "mUrlParameterCollection");
                        try {
                            Method DoTask =  XposedHelpers.findMethodBestMatch(mUrlParameterCollection.getClass(), "GetByName",String.class);
                            Object localObject1 = DoTask.invoke(mUrlParameterCollection,"param");
                            log("ClubStepSynchronousWithDetailHttp -> "+getObjectField(localObject1, "value").toString());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private void hookMethods(final ClassLoader cl) {
        log("init hookMethods");
        hookCodoonInfo(cl);
//		hookWechatId(cl);
//		hookLuckyMoney(cl);
//		hookSnsInfo(cl);
    }

    private int getRandom(int min, int max) {
        return min + (int) (Math.random() * (max - min + 1));
    }

    private int getDelayTime() {
        int delayTime = 0;
        if (PreferencesUtils.delay()) {
            delayTime = getRandom(PreferencesUtils.delayMin(), PreferencesUtils.delayMax());
        }
        return delayTime;
    }

    private boolean isGroupTalk(String talker) {
        return talker.endsWith("@chatroom");
    }

    private String getFromXml(String xmlmsg, String node) throws XmlPullParserException, IOException {
        String xl = xmlmsg.substring(xmlmsg.indexOf("<msg>"));
        // nativeurl
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser pz = factory.newPullParser();
        pz.setInput(new StringReader(xl));
        int v = pz.getEventType();
        String result = "";
        while (v != XmlPullParser.END_DOCUMENT) {
            if (v == XmlPullParser.START_TAG) {
                if (pz.getName().equals(node)) {
                    pz.nextToken();
                    result = pz.getText();
                    break;
                }
            }
            v = pz.next();
        }
        return result;
    }
}
