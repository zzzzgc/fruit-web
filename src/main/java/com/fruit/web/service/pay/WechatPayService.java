package com.fruit.web.service.pay;

import com.fruit.web.bean.pay.wechar.BaseWechatConfig;
import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
import com.fruit.web.util.ConvertUtils;
import com.fruit.web.util.HttpUtils;
import com.jfinal.kit.HashKit;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信支付通用接口
 *
 * @author ZGC
 * @date 2018-02-09 11:42
 **/
public class WechatPayService extends BaseWechatConfig {

    public static final String SUCCESS = "SUCCESS";
    public static final String MD5 = "MD5";
    public static final String SHA256 = "HMAC-SHA256";

    /**
     * 微信H5支付
     *
     * @param config 微信支付配置实体对象
     * @return H5支付跳转URL, 只要让前端重定向到这里就可以了
     */
    public String wechatH5Pay(WeChatPayConfig config) {
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        config.setTradeType("MWEB");
        String param = config.getPayParam();
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
        map.put("out_refund_no", outTradeNo+"-out");
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
        map.put("out_refund_no", outTradeNo+"out");
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
