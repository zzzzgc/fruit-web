package com.fruit.web.controller.order;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Order;
import com.fruit.web.model.OrderDetail;
import com.fruit.web.model.Param;
import com.fruit.web.model.Product;
import com.fruit.web.service.PayService;
import com.fruit.web.util.Constant;
import com.fruit.web.util.ConvertUtils;
import com.jfinal.ext2.kit.DateTimeKit;
import com.jfinal.ext2.kit.RandomKit;
import com.jfinal.kit.HashKit;
import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.util.*;

/**
 * Author ZGC and CCZ
 * Date Created in 15:52 2018/1/3
 */
public class OrderController extends BaseController {

    private static Logger log = Logger.getLogger(OrderController.class);

    private static final int PAGE_SIZE = 10;

    // 有序id计数
    public static long COUNT = 10000L;

    /**
     * 购物车生成订单(下单)
     */
    public void createOrder() {
        System.out.println("------------生成订单-----------------");
        try {
            //获取前端传过来的商品规格id
            Integer[] standardIds = getParaValuesToInt("standardIds");
            if (standardIds != null && standardIds.length > 0) {
                Integer uid = getSessionAttr(Constant.SESSION_UID);
                //临时限定100页数的购物车内容的获取
                List<Product> products = Product.dao.listCartProduct(uid, 100, 1);

                //订单总金额
                BigDecimal allTotalPay = new BigDecimal(0.00d);
                //生成唯一有序id
                String orderId = getOrderId();

                //创建订单,初始订单的支付状态为0-未支付
                Order order = new Order();
                order.setPayStatus(0);
                order.setStatus(0);
                order.setUId(uid);
                order.setOrderId(orderId);
                order.setUpdateTime(new Date());
                order.setCreateTime(new Date());
                order.setBuyAddress("");
                order.setBuyPhone("");

                for (Product product : products) {
                    //获取购物车商品id
                    int standardId = Integer.parseInt(product.get("standard_id").toString());
                    for (Integer id : standardIds) {
                        if (id.equals(standardId)) {
                            //buy_num能被取出来是因为所有取出来的内容都被封装在实体对象中了
                            int buy_num = Integer.parseInt(product.get("buy_num").toString());
                            BigDecimal sell_price = ConvertUtils.toBigDecimal(product.get("sell_price")).setScale(2, BigDecimal.ROUND_UP);
                            BigDecimal original_price = ConvertUtils.toBigDecimal(product.get("original_price")).setScale(2, BigDecimal.ROUND_UP);
                            String remark = product.get("remark").toString();
                            String standard_name = product.get("standard_name").toString();
                            //订单金额,目前只是简单计算,没有加入抵用券等金额修改的操作
                            BigDecimal totalPay = new BigDecimal(buy_num).multiply(sell_price);
                            //添加到总支付金额中
                            allTotalPay = allTotalPay.add(totalPay);

                            //创建子订单,初始订单状态为0-未支付(已下单),手机和收获地址暂时为空
                            OrderDetail orderDetail = new OrderDetail();
                            orderDetail.setOrderId(orderId);
                            orderDetail.setProductId(product.getId());
                            orderDetail.setProductStandardId(id);
                            orderDetail.setProductName(product.getName());
                            orderDetail.setProductStandardName(standard_name);
                            orderDetail.setNum(buy_num);
                            orderDetail.setSellPrice(sell_price);
                            orderDetail.setTotalPay(totalPay);
                            orderDetail.setFruitType(product.getFruitType());
                            orderDetail.setOriginalPrice(original_price);
                            orderDetail.setMeasureUnit(product.getMeasureUnit());
                            orderDetail.setBuyUid(uid);
                            orderDetail.setBuyRemark(remark);
                            orderDetail.setUpdateTime(new Date());
                            orderDetail.setCreateTime(new Date());
                            orderDetail.save();
                        }
                    }
                }

                allTotalPay = allTotalPay.setScale(2, BigDecimal.ROUND_UP);
                // 待支付金额
                order.setPayNeedMoney(allTotalPay);
                // 已支付金额
                order.setPayTotalMoney(new BigDecimal(0));
                order.save();

                renderJson(orderId);
            } else {
                renderErrorText("没有选定商品下单\t请正确下单后重试");
            }
        } catch (Exception e) {
            renderErrorText("后台异常!!!");
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 直接创建订单(下单)
     */
    public void directCreateOrder() {
        //生成唯一有序id
        String orderId;
        orderId = getOrderId();
        Integer uid = getSessionAttr(Constant.SESSION_UID);

        try {
            int Standard_id = getParaToInt("standard_id");
            if (Standard_id != 0) {
                int id = getParaToInt("id");
                int buyNum = getParaToInt("buyNum");
                String name = getPara("name");
                String standard_name = getPara("standard_name");
                // 单价
                BigDecimal sell_price = new BigDecimal(getPara("sell_price"));
                // 水果名
                String fruit_type = getPara("fruit_type");
                // 原价(用于参考)
                BigDecimal original_price = new BigDecimal(getPara("original_price"));
                // 单位
                String measure_unit = getPara("measure_unit");

                Order order = new Order();
                order.setPayStatus(0);
                order.setStatus(0);
                order.setUId(uid);
                order.setCreateTime(new Date());
                order.setUpdateTime(new Date());
                order.setOrderId(orderId);
                order.setBuyAddress("");
                order.setBuyPhone("");
                order.setPayNeedMoney(sell_price);
                order.setPayTotalMoney(new BigDecimal(0));
                order.save();

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderId);
                orderDetail.setProductId(id);
                orderDetail.setProductStandardId(Standard_id);
                orderDetail.setProductName(name);
                orderDetail.setProductStandardName(standard_name);
                orderDetail.setNum(buyNum);
                orderDetail.setSellPrice(sell_price);
                orderDetail.setTotalPay(sell_price);
                orderDetail.setFruitType(fruit_type);
                orderDetail.setOriginalPrice(original_price);
                orderDetail.setMeasureUnit(measure_unit);
                orderDetail.setBuyUid(uid);
                orderDetail.setBuyRemark("");
                orderDetail.setCreateTime(new Date());
                orderDetail.setUpdateTime(new Date());
                orderDetail.save();

                // 返回订单号
                renderText(orderId);
            }
        } catch (Exception e) {
            renderErrorText("后台异常!!!");
            e.printStackTrace();
        } finally {
        }
    }

    private String getOrderId() {
        String orderId;
        synchronized (OrderController.class) {
            orderId = DateTimeKit.formatDateToStyle("yyMMddhhmmss", new Date()) + "-" + COUNT + "-" + RandomKit.random(100000, 999999);
            COUNT++;
        }
        return orderId;
    }

    /**
     * 生成预支付订单
     */
    public void createPayOrder() {
        String orderId = getPara("orderId");
        StringBuffer sb = new StringBuffer().append("SELECT\n" +
                "\tb_order.pay_need_money\n" +
                "FROM\n" +
                "\tb_order\n" +
                "WHERE\n" +
                "\tb_order.order_id = ?");
        List<Order> orders = Order.dao.find(sb.toString(), orderId);

        if (orders != null && orders.size() > 0) {
            long money = orders.get(0).getPayNeedMoney().longValue();
            String prepay_id = new PayService().wechatJsApiPay("嘻果商城", orderId, money);
            if (prepay_id != null) {
                Param paramDao = Param.dao;
                String appId = paramDao.getParam("weChar_appId");
                String timeStamp = System.currentTimeMillis() + "";
                String nonceStr = RandomKit.randomStr();
                String signType = paramDao.getParam("weChar_tradeType");
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

                HashMap<String, String> map = new HashMap<>(6);
                map.put("appId", appId);
                map.put("prepay_id", prepay_id);
                map.put("timeStamp", timeStamp);
                map.put("nonceStr", nonceStr);
                map.put("paySign", paySign);
                map.put("signType", signType);
                renderJson(map);
            }
        }

        // 临时跳过
        renderText("内容");
    }

    /**
     * 获取订单上商品
     */
    public void getOrderProducts() {
        try {
            String orderId = getPara("orderId");
            StringBuffer sb = new StringBuffer("SELECT\n" +
                    "\to.num,\n" +
                    "\to.product_standard_name,\n" +
                    "\to.product_standard_id,\n" +
                    "\to.product_name,\n" +
                    "\tp.img,\n" +
                    "\to.sell_price,\n" +
                    "\tp.country,\n" +
                    "\to.measure_unit\n" +
                    "FROM\n" +
                    "\tb_order_detail AS o\n" +
                    "LEFT JOIN b_product AS p ON o.product_id = p.id\n" +
                    "WHERE\n" +
                    "\to.order_id = ?");
            List<OrderDetail> orderDetails = OrderDetail.dao.find(sb.toString(), orderId);

            BigDecimal price = new BigDecimal(0.00);

            for (OrderDetail orderDetail : orderDetails) {
                BigDecimal sellPrice = orderDetail.getSellPrice();
                price = price.add(sellPrice);
            }
            HashMap<String, Object> responseMap = new HashMap<>(2);
            responseMap.put("products",orderDetails);
            responseMap.put("totalPrice",price);

            renderJson(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    //跳转页面

    public static void main(String[] args) {

    }

    /**
     * 获取到订单列表
     */
    public void getOderList() {
        String order_status = getPara("order_status");
        if ("one".equals(order_status)) {
            // 判断代付款
            order_status = "'0'";
        } else if ("two".equals(order_status)) {
            //判断确认中
            order_status = "'0','5'";
        } else if ("three".equals(order_status)) {
            //判断待发货
            order_status = "'10'";
        } else if ("four".equals(order_status)) {
            //判断我的订单
            order_status = "'0','5','10','20','30','40'";
        }
        // 获取用户ID
        Object uid = getSessionAttr(Constant.SESSION_UID);
        List<Order> orderList = Order.dao.getOrderListByStatus(uid.toString(), order_status);
        LinkedHashMap<String, List<Order>> map = new LinkedHashMap<String, List<Order>>(){
            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }
        };
        for (Order order : orderList) {
            if (map.containsKey(order.getOrderId())) {
                List<Order> orders = map.get(order.getOrderId());
                orders.add(order);
                map.put(order.getOrderId(), orders);
            } else {
                List<Order> orders = new ArrayList<Order>();
                orders.add(order);
                map.put(order.getOrderId(), orders);
            }
        }
        renderJson(map);
    }

    // HashMap的value降序
    public static List<Map.Entry<String,List<Order>>> hashMapperDesc(Map map){
        List<Map.Entry<String,List<Order>>> list = new ArrayList<Map.Entry<String,List<Order>>>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,List<Order>>>() {
            //降序排序
            public int compare(Map.Entry<String, List<Order>> o1,
                               Map.Entry<String, List<Order>> o2) {
                return o2.getValue().get(0).getCreateTime().compareTo(o1.getValue().get(0).getCreateTime());
            }
        });
        Map<String,List<Order>> listMap=new HashMap<>();
        for(Map.Entry<String,List<Order>> mapping:list){
            listMap.put(mapping.getKey(),mapping.getValue());
            System.out.println(mapping.getKey()+":"+mapping.getValue());
        }
        return list;
    }

}
