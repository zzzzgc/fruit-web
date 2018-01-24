package com.fruit.web.bean.pay.wechar;

import com.fruit.web.model.Param;
import com.fruit.web.util.ConvertUtils;
import com.jfinal.kit.HashKit;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

/**
 * 微信公众号支付请求实体
 *
 * @author ZGC
 * @date Created in 15:08 2017/12/25
 */
public class WeChatJsPay {
    /**
     * 公众账号ID
     */
    private String appid;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * 设备号
     */
    private String deviceInfo;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String sign;
    /**
     * 签名类型
     */
    private String signType;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 商品详情
     */
    private String detail;
    /**
     * 附加数据
     */
    private String attach;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 标价币种
     */
    private String feeType;
    /**
     * 标价金额
     */
    private String totalFee;
    /**
     * 终端IP
     */
    private String spbillCreateIp;
    /**
     * 交易起始时间
     */
    private String timeStart;
    /**
     * 交易结束时间
     */
    private String timeExpire;
    /**
     * 订单优惠标记
     */
    private String goodsTag;
    /**
     * 通知地址
     */
    private String notifyUrl;
    /**
     * 交易类型
     */
    private String tradeType;
    /**
     * 商品ID
     */
    private String productId;
    /**
     * 指定支付方式
     */
    private String limitPay;
    /**
     * 用户标识
     */
    private String openid;
    /**
     * 场景信息
     */
    private String sceneInfo;

    public WeChatJsPay(String body, String outTradeNo, String totalFee) {
        this.body = body;
        this.outTradeNo = outTradeNo;
        this.totalFee = totalFee;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSceneInfo() {
        return sceneInfo;
    }

    public void setSceneInfo(String sceneInfo) {
        this.sceneInfo = sceneInfo;
    }

    @Override
    public String toString() {
        return "WeCharHtml5Pay{" +
                "appid='" + appid + '\'' +
                ", mchId='" + mchId + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", sign='" + sign + '\'' +
                ", signType='" + signType + '\'' +
                ", body='" + body + '\'' +
                ", detail='" + detail + '\'' +
                ", attach='" + attach + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", feeType='" + feeType + '\'' +
                ", totalFee='" + totalFee + '\'' +
                ", spbillCreateIp='" + spbillCreateIp + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeExpire='" + timeExpire + '\'' +
                ", goodsTag='" + goodsTag + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", productId='" + productId + '\'' +
                ", limitPay='" + limitPay + '\'' +
                ", openid='" + openid + '\'' +
                ", sceneInfo='" + sceneInfo + '\'' +
                '}';
    }

    /**
     * 获取请求参数,需要确保body\totalFee\outTradeNo\openId不为空
     *
     * @return
     */
    public String getSendStr() throws Exception {
        Param paramDao = Param.dao;
        this.appid = paramDao.getParam("weChar_appId");
        this.mchId = paramDao.getParam("weChar_mchId");
        this.nonceStr = UUID.randomUUID().toString();
        this.sign = null;
        //this.body            =null;
        //this.totalFee        =null;
        //this.outTradeNo      =null;
        this.body = "JSAPI支付测试";
        this.spbillCreateIp = paramDao.getParam("weChar_spbillCreateIp");
        this.notifyUrl = paramDao.getParam("weChar_JsApiPay_notifyUrl");
        this.tradeType = paramDao.getParam("weChar_tradeType");


//        this.goodsTag        =null;
//        this.productId       =null;
//        this.limitPay        =null;
//        this.openid          =null;
//        this.sceneInfo       =null;
//        this.object          =null;
//        this.feeType         =null;
//        this.attach          =null;
//        this.detail          =null;
//        this.deviceInfo      =null;
        this.signType = paramDao.getParam("weChar_signType");
//        this.timeStart       =null;
//        this.timeExpire      =null;

        HashMap<String, String> map = new HashMap<>(30);
        Class<? extends WeChatJsPay> thisClass = this.getClass();
        Field[] fields = thisClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj != null) {
                map.put(field.getName(), obj.toString());
            }
        }
        String param = ConvertUtils.map2KeyValueString(map);

        String key = "969f2f734243f654643ec4800c279962";
        param += "&key=" + key;

        String sign = HashKit.md5(param).toUpperCase();
        map.put("sign", sign);

        String xmlStr = ConvertUtils.map2SimpleXmlStr(map);
        return xmlStr;
    }

    public void getStr(Class zlass) {
        Field[] fields = zlass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            System.out.println("this." + name);
        }
    }


}