package com.fruit.web.service.pay;

import com.fruit.web.bean.pay.wechar.BaseWechatConfig;
import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
import com.fruit.web.model.Param;
import com.fruit.web.util.ConvertUtils;
import com.fruit.web.util.HttpUtils;
import com.jfinal.ext2.kit.RandomKit;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * 微信支付通用接口
 *
 * @author ZGC
 * @date 2018-02-09 11:42
 **/
public class WechatPayService extends BaseWechatConfig {

    public static final Logger log = Logger.getLogger(WechatPayService.class.toString());

    /**
     * 微信统一支付接口
     */
    private static final String WECHAT_SEND_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String SUCCESS = "SUCCESS";
    public static final String MD5 = "MD5";
    public static final String SHA256 = "HMAC-SHA256";

//    /**
//     *
//     *
//     * @param config
//     * @return H5支付跳转URL, 只要让前端重定向到这里就可以了
//     */

    /**
     * 微信H5支付
     * @param orderId 微信支付订单
     * @param money 微信支付金额
     * @return String
     */
    public String wechatH5Pay(String orderId, long money) {
        WeChatPayConfig config = new WeChatPayConfig(orderId, money);
        config.setTradeType("MWEB");
        String param = config.getPayParam();
        String responseXmlStr = request(param, "JSAPI");
        try {
            //解析xml
            Document doc = null;
            doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();
            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功

            String returnCode = root.elementText("return_code");
            if (SUCCESS.equals(returnCode)) {
                String resultCode = root.elementText("result_code");
                if (SUCCESS.equals(resultCode)) {
                    String webUrl = root.elementText("mweb_url");
                    return webUrl;
                } else {
                    throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("err_code_des"));
                }
            } else {
                throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("return_msg"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    //微信js支付主要是靠前端ajax请求连接的,后台只提供参数

    /**
     * 公众号支付预下单请求
     *
     * @param orderId 商户订单号   调用之前,需要把该支付订单号和订单信息绑定并持久化到数据库
     * @param money   支付金额(单位 分)
     * @return prepay_id 预支付交易会话标识, 用于前端拉起微信支付的标示
     */
    public Map<String, String> wechatJsApiPay(String orderId, long money) {
        try {
            //装载必要参数
            WeChatPayConfig sendPo = new WeChatPayConfig(orderId, money);
            sendPo.setTradeType("JSAPI");

            String sendXmlStr = sendPo.getPayParam();

            /**
             * <xml><mchId>9854354</mchId><totalFee>1</totalFee><appid>wx2421b1c4370ec43b</appid><outTradeNo>1415659990</outTradeNo><sign>e75b3758008b05a60864a658f38206bd</sign><notifyUrl>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notifyUrl><body>JSAPI支付测试</body><nonceStr>1add1a30ac87aa2db72f57a2375d8fec</nonceStr><spbillCreateIp>14.23.150.211</spbillCreateIp><tradeType>JSAPI</tradeType></xml>
             */
            // 发起请求
            String responseXmlStr = request(sendXmlStr, "JsApi");
            /**
             * <xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[mch_id参数格式错误]]></return_msg></xml>
             */


            //解析xml
            Document doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();

            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功
            String success = "SUCCESS";
            String return_code = root.element("return_code").getText();
            if (success.equals(return_code)) {
                String result_code = root.element("result_code").getText();
                if (success.equals(result_code)) {
                    String prepay_id = root.element("prepay_id").getText();
                    if (StringUtils.isNotBlank(prepay_id)) {
                        HashMap<String, String> map = new HashMap<>(6);
                        if (prepay_id != null) {
                            Param paramDao = Param.dao;
                            String appId = paramDao.getParam("wechat_appId");
                            String timeStamp = System.currentTimeMillis() + "";
                            String nonceStr = RandomKit.randomStr();
                            String signType = paramDao.getParam("wechat_tradeType");
                            HashMap<String, String> paramMap = new HashMap<>(6);
                            paramMap.put("appId", appId);
                            paramMap.put("prepay_id", prepay_id);
                            paramMap.put("timeStamp", timeStamp);
                            paramMap.put("nonceStr", nonceStr);
                            paramMap.put("signType", signType);

                            // 转格式
                            String paramStr = ConvertUtils.map2KeyValueString(paramMap);
                            // MD5加密
                            String paySign = HashKit.md5(paramStr).toUpperCase();

                            map.put("appId", appId);
                            map.put("prepay_id", prepay_id);
                            map.put("timeStamp", timeStamp);
                            map.put("nonceStr", nonceStr);
                            map.put("paySign", paySign);
                            map.put("signType", signType);
                        }
                        return map;
                    }
                }
            }
        } catch (Exception e) {
            log.throwing(this.getClass().toString(), "wechatJsApiPay", e);
            e.printStackTrace();
        }
        log.warning("获取支付预支付信息失败,该订单号为:" + orderId);
        throw new RuntimeException("获取支付预支付信息失败,该订单号为:" + orderId);
    }

    /**
     * 发起请求
     *
     * @param sendXmlStr
     * @return
     */
    private String request(String sendXmlStr, String type) {
        //发起请求
        log.info("获取微信" + type + "请求参数:" + sendXmlStr);
        String responseXmlStr = HttpUtils.sendPost(WECHAT_SEND_URL, sendXmlStr);
        log.info("获取响应" + type + "参数:" + responseXmlStr);
        return responseXmlStr;
    }

    public static void main(String[] args) {
//        WeChatPayConfig weChatPayConfig = new WeChatPayConfig("123456789", 12300);
//        WechatPayService wechatPayService = null;
//        try {
//            wechatPayService = new WechatPayService();
//            String s = wechatPayService.wechatH5Pay(weChatPayConfig);
//            System.out.println(s);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        boolean b = wechatPayService.payQuery("123456");
//        System.out.println(b);
        BigDecimal divide = new BigDecimal(123456).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        System.out.println(divide);
    }

    /**
     * 支付查询
     *
     * @return
     */
    public boolean payQuery(String outTradeNo) {
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        HashMap<String, String> map = new HashMap<>(5);
        map.put("appid", getAppIdBySuper());
        map.put("out_trade_no", outTradeNo);
        map.put("nonce_str", UUID.randomUUID().toString());
        map.put("signType", getSignTypeBySuper());
        String sign = getSign(map, getSignTypeBySuper());
        map.put("sign", sign + "2");
        String param = ConvertUtils.map2SimpleXmlStr(map);
        System.out.println("请求的url:" + url + ",获取到的参数:" + param);
        String responseXmlStr = HttpUtils.sendPost(url, param);
        System.out.println("请求的url:" + url + "之后,获取到的响应参数:" + responseXmlStr);
        try {
            //解析xml
            Document doc = null;
            doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();
            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功
            String returnCode = root.elementText("return_code");
            if (SUCCESS.equals(returnCode)) {
                String resultCode = root.elementText("result_code");
                if (SUCCESS.equals(resultCode)) {
                    String tradeState = root.elementText("trade_state");
                    boolean isOk = false;
                    if (tradeState.equals(SUCCESS)) {
                        isOk = true;
                    } else {
                        isOk = false;
                    }
                    return isOk;
                } else {
                    throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("err_code_des"));
                }
            } else {
                throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("return_msg"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 退款接口
     *
     * @param outTradeNo
     * @return
     */
    public boolean payRefund(String outTradeNo, long total_fee) {
        String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        HashMap<String, String> map = new HashMap<>(5);
        map.put("appid", getAppIdBySuper());
        // 退款订单号为 (out_trade_no = orderId) + out
        map.put("out_refund_no", outTradeNo + "-out");
        map.put("out_trade_no", outTradeNo);
        map.put("nonce_str", UUID.randomUUID().toString());
        map.put("signType", getSignTypeBySuper());
        map.put("total_fee", total_fee + "");
        map.put("refund_fee", total_fee + "");
        String sign = getSign(map, getSignTypeBySuper());
        map.put("sign", sign + "2");
        String param = ConvertUtils.map2SimpleXmlStr(map);
        System.out.println("请求的url:" + url + ",获取到的参数:" + param);
        String responseXmlStr = HttpUtils.sendPost(url, param);
        System.out.println("请求的url:" + url + "之后,获取到的响应参数:" + responseXmlStr);
        try {
            //解析xml
            Document doc = null;
            doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();
            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功
            String returnCode = root.elementText("return_code");
            if (SUCCESS.equals(returnCode)) {
                String resultCode = root.elementText("result_code");
                if (SUCCESS.equals(resultCode)) {
                    return true;
                } else {
                    throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("err_code_des"));
                }
            } else {
                throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("return_msg"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean payRefundQuery(String outTradeNo) {
        String url = "https://api.mch.weixin.qq.com/pay/refundquery";
        HashMap<String, String> map = new HashMap<>(5);
        map.put("appid", getAppIdBySuper());
        map.put("out_trade_no", outTradeNo);
        map.put("nonce_str", UUID.randomUUID().toString());
        map.put("signType", getSignTypeBySuper());
        map.put("out_refund_no", outTradeNo + "out");
        String sign = getSign(map, getSignTypeBySuper());
        map.put("sign", sign + "2");
        String param = ConvertUtils.map2SimpleXmlStr(map);
        System.out.println("请求的url:" + url + ",获取到的参数:" + param);
        String responseXmlStr = HttpUtils.sendPost(url, param);
        System.out.println("请求的url:" + url + "之后,获取到的响应参数:" + responseXmlStr);
        try {
            //解析xml
            Document doc = null;
            doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();
            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功
            String returnCode = root.elementText("return_code");
            if (SUCCESS.equals(returnCode)) {
                String resultCode = root.elementText("result_code");
                boolean isOk = false;
                if (SUCCESS.equals(resultCode)) {
                    return true;
                } else {
                    throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("err_code_des"));
                }
            } else {
                throw new RuntimeException("响应通信标识return_code不为SUCCESS,错误信息为:" + root.elementText("return_msg"));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean closeOrder() {
        return false;
    }

    public String getSign(Map<String, String> map, String signType) {
        String param = ConvertUtils.map2KeyValueString(map);
        param += getKEYBySuper();
        if (MD5.equals(signType)) {
            return HashKit.md5(param).toUpperCase();
        } else if (SHA256.equals(signType)) {
            return HashKit.sha256(param).toUpperCase();
        } else {
            throw new RuntimeException("不支持该类型加密:" + signType);
        }
    }
}
