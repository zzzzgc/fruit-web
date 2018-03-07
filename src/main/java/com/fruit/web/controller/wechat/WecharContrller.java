package com.fruit.web.controller.wechat;

import com.fruit.web.base.BaseController;
import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
import com.fruit.web.emum.PayStatus;
import com.fruit.web.model.Order;
import com.fruit.web.service.pay.WechatPayService;
import com.fruit.web.util.ConvertUtils;
import com.sun.xml.internal.xsom.XmlString;
import com.sun.xml.internal.xsom.parser.XMLParser;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信的回调查询控制层
 *
 * @author ZGC
 * @date 2018-02-09 15:09
 **/
public class WecharContrller extends BaseController {

    /**
     * H5支付
     */
    public void h5Pay() {
        String orderId = getPara("orderId");
        //获取订单信息,计算订单金额
        BigDecimal money = Order.dao.getOrderPayNeedMoney(orderId);
        // 以分为单位
        long total = money.setScale(BigDecimal.ROUND_HALF_UP, 2).longValue();
        String url = new WechatPayService().wechatH5Pay(orderId, total);
        //前端获取到该值以后需要重定向到该url
        renderText(url);
    }

    /**
     * 查询是否支付成功
     */
    public void queryIsPay() {
        String orderId = getPara("orderId");
        //先去数据库查,成功就直接返回
        Order order = Order.dao.getOrder(orderId);
        Integer payStatus = order.getPayStatus();
        if (payStatus.equals(PayStatus.PAY_OK.getStatus())) {
            renderNull();
            return;
        }

        //再去请求微信接口查,成功就直接返回
        boolean isOk = new WechatPayService().payQuery(orderId);
        if (isOk) {
            renderNull();
        }
    }

    /**
     * 微信支付回调
     */
    public void callbackPayInfo(String xmlResponse) {
        System.out.println("进入支付回调,回调内容:" + xmlResponse);

        // 确认订单sign正确性
        Map<String, String> stringStringMap = ConvertUtils.simpleXmlStr2map(xmlResponse);
        String requestSign = stringStringMap.get("sign");
        String newSign = new WechatPayService().getSign(stringStringMap, "MD5");
        if (!requestSign.equals(newSign)) {
            System.out.println("sign校验失败");
        }

        // 确认数据是否匹配到订单
        String outTradeNo = stringStringMap.get("out_trade_no");
        Order order = Order.dao.getOrder(outTradeNo);
        if (order != null){
            //获取信息并保存到数据库
            String totalFee = stringStringMap.get("total_fee");
            //分转元
            BigDecimal payTotalMoney = new BigDecimal(totalFee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            order.setPayTotalMoney(payTotalMoney);
            order.setPayStatus(PayStatus.PAY_OK.getStatus());
            //保存修改
            order.save();
        }
        //测试看看,不知道可不可以
        renderText("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
    }


}
