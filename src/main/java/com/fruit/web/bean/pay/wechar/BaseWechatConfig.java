package com.fruit.web.bean.pay.wechar;

/**
 * 微信通用参数
 * @author ZGC
 * @date 2018-02-09 13:53
 **/
public abstract class BaseWechatConfig {
    private static final String APP_ID;

    private static final String MCH_ID;

    private static final String BODY;

    private static final String KEY;

    private static final String SIGN_TYPE;

    private static final String NOTIFY_URL;

    static {
        APP_ID = "wx2421b1c4370ec43b";
        MCH_ID = "10000100";
        BODY = "嘻果商家商城";
        KEY = "969f2f734243f654643ec4800c279962";
        SIGN_TYPE = "MD5";
        NOTIFY_URL ="www.laksdfjlkajdfljljalsdjflj.com";
//        Param paramDao = Param.dao;
//        BODY = paramDao.getParam("wechat_body");
//        APP_ID = paramDao.getParam("wechat_appId");
//        MCH_ID = paramDao.getParam("wechat_mchId");
//        KEY = paramDao.getParam("wechar_key");
//        SIGN_TYPE = paramDao.getParam("wechat_signType");
//        NOTIFY_URL = paramDao.getParam("wechat_pay_notifyUrl");
    }

    public static String getAppIdBySuper() {
        return APP_ID;
    }

    public static String getMchIdBySuper() {
        return MCH_ID;
    }

    public static String getBODYBySuper() {
        return BODY;
    }

    public static String getKEYBySuper() {
        return KEY;
    }

    public static String getSignTypeBySuper() {
        return SIGN_TYPE;
    }

    public static String getNotifyUrlBySuper() {
        return NOTIFY_URL;
    }
}
