package org.huakai.wechat_xposed;

/**
 * Created by Administrator on 2017/4/18.
 */

public class VersionParam {
    public static final String WECHAT_PACKAGE_NAME = "com.ziroom.ziroomcustomer";
    public static String receiveUIFunctionName = "S2cRspBodyWrapPB";
    public static String receiveUIParamName = WECHAT_PACKAGE_NAME+".u.k";
    public static String networkRequest = WECHAT_PACKAGE_NAME+".model.ah";
    public static String getNetworkByModelMethod = "vS";
    public static String getMessageClass = WECHAT_PACKAGE_NAME+".booter.notification.f$1";
    public static String sendMessageMethod = "S2cRspBodyWrapPB";
    public static String textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.h";
    public static String luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
    public static boolean hasTimingIdentifier = false;

    public static void init(String version) {
        switch (version) {
            case "6.3.30":
            case "6.3.31":
                receiveUIFunctionName = "S2cRspBodyWrapPB";
                receiveUIParamName = WECHAT_PACKAGE_NAME+".v.k";
                networkRequest = WECHAT_PACKAGE_NAME+".model.ah";
                getNetworkByModelMethod = "vS";
                textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.h";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                break;
            case "6.3.32":
                receiveUIFunctionName = "S2cRspBodyWrapPB";
                receiveUIParamName = WECHAT_PACKAGE_NAME+".v.k";
                networkRequest = WECHAT_PACKAGE_NAME+".model.ak";

                getNetworkByModelMethod = "vw";
                textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.h";

                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                break;
            case "6.5.3":
            case "6.5.4":
                receiveUIFunctionName = "S2cRspBodyWrapPB";
                receiveUIParamName = WECHAT_PACKAGE_NAME+".u.k";
                networkRequest = WECHAT_PACKAGE_NAME+".model.ak";

                getNetworkByModelMethod = "vy";
                textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.i";

                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.LuckyMoneyReceiveUI";
                hasTimingIdentifier = true;
                break;
            case "6.5.6":
            case "6.5.7":
                receiveUIFunctionName = "S2cRspBodyWrapPB";
                receiveUIParamName = WECHAT_PACKAGE_NAME+".u.k";
                networkRequest = WECHAT_PACKAGE_NAME+".model.al";
                getNetworkByModelMethod = "vM";
                textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.j";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                hasTimingIdentifier = true;
                break;
            default:
                receiveUIFunctionName = "S2cRspBodyWrapPB";
                receiveUIParamName = WECHAT_PACKAGE_NAME+".u.k";
                networkRequest = WECHAT_PACKAGE_NAME+".model.al";
                getNetworkByModelMethod = "vM";
                textMessageBeanClass = WECHAT_PACKAGE_NAME+".modelmulti.j";
                luckyMoneyReceiveUI = WECHAT_PACKAGE_NAME + ".plugin.luckymoney.ui.En_fba4b94f";
                hasTimingIdentifier = true;
                break;
        }
    }
}

