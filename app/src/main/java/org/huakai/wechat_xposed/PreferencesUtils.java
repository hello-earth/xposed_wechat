package org.huakai.wechat_xposed;

import de.robv.android.xposed.XSharedPreferences;


public class PreferencesUtils {
    private static XSharedPreferences instance = null;

    private static XSharedPreferences getInstance() {
        if (instance == null) {
            instance = new XSharedPreferences(PreferencesUtils.class.getPackage().getName());
            instance.makeWorldReadable();
        } else {
            instance.reload();
        }
        return instance;
    }

    public static boolean open() {
        return getInstance().getBoolean("open", true);
    }

    public static boolean notSelf() {
        return getInstance().getBoolean("not_self", false);
    }

    public static boolean notWhisper() {
        return getInstance().getBoolean("not_whisper", false);
    }

    public static String notContains() {
        return getInstance().getString("not_contains", "").replace("，", ",");
    }

    public static boolean delay() {
        return getInstance().getBoolean("delay", false);
    }

    public static int delayMin() {
        return getInstance().getInt("delay_min", 0);
    }

    public static int delayMax() {
        return getInstance().getInt("delay_max", 0);
    }

    public static boolean quickOpen() {
        return getInstance().getBoolean("quick_open", true);
    }

    public static boolean showWechatId() {
        return getInstance().getBoolean("show_wechat_id", true);
    }

    public static String blackList() {
        return getInstance().getString("black_list", "").replace("，", ",");
    }

    public static boolean antiRevoke() {
        return getInstance().getBoolean("antiRevoke", false);
    }

    public static boolean debugObjInfo() {
        return getInstance().getBoolean("debug_objInfo", false);
    }

    public static boolean debugStackInfo() {
        return getInstance().getBoolean("debug_stack", false);
    }

    public static boolean debugSqlInfo() {
        return getInstance().getBoolean("debug_sql", false);
    }

}
