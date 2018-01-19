package com.fruit.web.service;

import com.fruit.web.bean.pay.wechar.WeChatJsPay;
import com.fruit.web.util.HttpUtils;
import com.jfinal.kit.HashKit;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.logging.Logger;

/**
 * 支付接口服务类
 * @Author: ZGC
 * @Date Created in 16:23 2017/12/25
 */
public class PayService {

    public static final Logger log = Logger.getLogger(PayService.class.toString());
    /**
     *  微信统一支付接口
     */
    private static final String WECHAR_SEND_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 公众号支付预下单请求
     * @param title 订单标题
     * @param orderId 商户订单号   调用之前,需要把该支付订单号和订单信息绑定并持久化到数据库
     * @param money  支付金额
     * @return 预支付交易会话标识,用于前端拉起微信支付的标示
     */
    public String wechatJsApiPay(String title, String orderId, long money) {
        try {
            //装载必要参数
            WeChatJsPay sendPo = new WeChatJsPay();
            sendPo.setBody(title);
            sendPo.setTotalFee(money + "");
            sendPo.setOutTradeNo(orderId);
            String sendXmlStr = sendPo.getSendStr();

            /**
             * <xml><mchId>9854354</mchId><totalFee>1</totalFee><appid>wx2421b1c4370ec43b</appid><outTradeNo>1415659990</outTradeNo><sign>e75b3758008b05a60864a658f38206bd</sign><notifyUrl>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notifyUrl><body>JSAPI支付测试</body><nonceStr>1add1a30ac87aa2db72f57a2375d8fec</nonceStr><spbillCreateIp>14.23.150.211</spbillCreateIp><tradeType>JSAPI</tradeType></xml>
             */
            // 发起请求
            String responseXmlStr = request(sendXmlStr);
            /**
             * <xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[mch_id参数格式错误]]></return_msg></xml>
             */


            //解析xml
            Document doc = DocumentHelper.parseText(responseXmlStr);
            Element root = doc.getRootElement();

            //获取特定内容,在return_code 和result_code都为SUCCESS的时候有返回
            //获取<xml>
            //接收和请求是否成功
            String success="SUCCESS";
            String return_code = root.element("return_code").getText();
            if (success.equals(return_code)){
                String result_code = root.element("result_code").getText();
                if (success.equals(result_code)){
                    String prepay_id = root.element("prepay_id").getText();
                    return prepay_id;
                }
            }
        } catch (Exception e) {
            log.throwing(this.getClass().toString(),"wechatJsApiPay",e);
            e.printStackTrace();
        }
        log.warning("获取支付预支付信息失败,该订单号为:"+orderId);
        return null;
    }

    /**
     * 发起请求
     * @param sendXmlStr
     * @return
     */
    private String request(String sendXmlStr) {
        //发起请求
        log.info("获取预支付请求参数:"+sendXmlStr);
        String responseXmlStr = HttpUtils.sendPost(WECHAR_SEND_URL, sendXmlStr);
        log.info("获取预支付响应参数:"+responseXmlStr);
        return responseXmlStr;
    }

    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        String prepay_id = new PayService().wechatJsApiPay("JSAPI支付测试", "1415659990", 1);
        System.out.println(prepay_id);
    }

}
