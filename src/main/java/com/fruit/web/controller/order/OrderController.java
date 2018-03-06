package com.fruit.web.controller.order;

import com.fruit.web.Interceptor.AuthInterceptor;
import com.fruit.web.Interceptor.LoginInterceptor;
import com.fruit.web.base.BaseController;
import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
import com.fruit.web.emum.BusinessShipmentsType;
import com.fruit.web.model.*;
import com.fruit.web.service.pay.WechatPayService;
import com.fruit.web.util.Constant;
import com.fruit.web.util.ConvertUtils;
import com.jfinal.aop.Before;
import com.jfinal.ext2.kit.DateTimeKit;
import com.jfinal.ext2.kit.RandomKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

/**
 * Author ZGC and CCZ
 * Date Created in 15:52 2018/1/3
 */
@Before(LoginInterceptor.class)
public class OrderController extends BaseController {

    private static Logger log = Logger.getLogger(OrderController.class);

    private static final int PAGE_SIZE = 10;

    /**
     * 有序id计数
     */
    private static long COUNT = 10000L;

    /**
     * 购物车生成订单(下单)
     */
    @Before({AuthInterceptor.class, Tx.class})
    public void createOrder() {
        // TODO 使用getMode来获取参数
        System.out.println("------------购物车批量生成订单-----------------");
        try {
            //获取前端传过来的商品规格id
            Integer[] standardIds = getParaValuesToInt("standardIds");
            if (standardIds != null && standardIds.length > 0) {
                Integer uid = getSessionAttr(Constant.SESSION_UID);

                //临时限定100页数的购物车内容的获取
                List<Product> products = Product.dao.listCartProduct(uid, 100, 1);

                if (products != null && products.size() > 0) {
                    BusinessUser user = BusinessUser.dao.findByIdLoadColumns(uid, "name");
                    String name = user.getName();

                    //订单总金额
                    BigDecimal allTotalPay = new BigDecimal(0.00d);
                    //生成唯一有序id
                    String orderId = getOrderId();

                    //创建订单,初始订单的支付状态为0-未支付
                    Order order = new Order();
                    order.setPayStatus(0);
                    order.setOrderStatus(0);
                    order.setUId(uid);
                    order.setOrderId(orderId);
                    order.setUpdateTime(new Date());
                    order.setCreateTime(new Date());

                    Integer[] removeCarProduct = new Integer[products.size()];
                    int count = 0;

                    for (Product product : products) {
                        //获取购物车商品id
                        int standardId = Integer.parseInt(product.get("standard_id").toString());
                        for (Integer id : standardIds) {
                            if (id.equals(standardId)) {
                                //buy_num能被取出来是因为所有取出来的内容都被封装在实体对象中了
                                int buyNum = Integer.parseInt(product.get("buy_num").toString());
                                BigDecimal sellPrice = ConvertUtils.toBigDecimal(product.get("sell_price")).setScale(2, BigDecimal.ROUND_UP);
                                BigDecimal originalPrice = ConvertUtils.toBigDecimal(product.get("original_price")).setScale(2, BigDecimal.ROUND_UP);
                                String remark = product.get("remark") == null ? null : product.get("remark").toString();
                                String standardName = product.get("standard_name").toString();
                                //订单金额,目前只是简单计算,没有加入抵用券等金额修改的操作
                                BigDecimal totalPay = new BigDecimal(buyNum).multiply(sellPrice);
                                //添加到总支付金额中
                                allTotalPay = allTotalPay.add(totalPay);

                                //创建子订单,初始订单状态为0-未支付(已下单),手机和收获地址暂时为空
                                OrderDetail orderDetail = new OrderDetail();
                                orderDetail.setOrderId(orderId);
                                orderDetail.setProductId(product.getId());
                                orderDetail.setProductStandardId(id);
                                orderDetail.setProductName(product.getName());
                                orderDetail.setProductStandardName(standardName);
                                orderDetail.setNum(buyNum);
                                orderDetail.setSellPrice(sellPrice);
                                orderDetail.setTotalPay(totalPay);
                                orderDetail.setFruitType(product.getFruitType());
                                orderDetail.setOriginalPrice(originalPrice);
                                orderDetail.setMeasureUnit(product.getMeasureUnit());
                                orderDetail.setBuyUid(uid);
                                orderDetail.setBuyRemark(remark);
                                orderDetail.setUpdateTime(new Date());
                                orderDetail.setCreateTime(new Date());
                                orderDetail.save();

                                removeCarProduct[count++] = id;

                                break;
                            }
                        }
                    }
                    allTotalPay = allTotalPay.setScale(2, BigDecimal.ROUND_UP);
                    // 待支付金额
                    order.setPayNeedMoney(allTotalPay);
                    // 已支付金额
                    order.setPayTotalMoney(new BigDecimal(0));
                    order.save();

                    //从购物车删除添加到订单的商品
                    CartProduct.dao.removeCartProduct(uid, removeCarProduct);

                    renderText(orderId);
                } else {
                    renderErrorText("购物车为空");
                }
            } else {
                renderErrorText("没有选定商品下单\t请正确下单后重试");
            }
        } catch (Exception e) {
            renderErrorText("后台异常!!!");
            e.printStackTrace();
        }
    }

    /**
     * 直接创建订单(下单)
     */
    @Before({AuthInterceptor.class, Tx.class})
    public void directCreateOrder() {
        System.out.println("------------单笔(直接)生成订单-----------------");
        //生成唯一有序id
        String orderId;
        orderId = getOrderId();
        Integer uid = getSessionAttr(Constant.SESSION_UID);
        try {
            int standardId = getParaToInt("standard_id");
            if (standardId != 0) {
                int id = getParaToInt("id");
                int buyNum = getParaToInt("buyNum");
                String name = getPara("name");
                String standardName = getPara("standard_name");
                // 单价
                BigDecimal sellPrice = new BigDecimal(getPara("sell_price"));
                // 水果名
                String fruitType = getPara("fruit_type");
                // 原价(用于参考)
                BigDecimal originalPrice = new BigDecimal(getPara("original_price"));
                // 单位
                String measureUnit = getPara("measure_unit");

                Order order = new Order();
                order.setPayStatus(0);
                order.setOrderStatus(0);
                order.setUId(uid);
                order.setCreateTime(new Date());
                order.setUpdateTime(new Date());
                order.setOrderId(orderId);
                order.setPayNeedMoney(sellPrice);
                order.setPayTotalMoney(new BigDecimal(0));
                order.save();

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(orderId);
                orderDetail.setProductId(id);
                orderDetail.setProductStandardId(standardId);
                orderDetail.setProductName(name);
                orderDetail.setProductStandardName(standardName);
                orderDetail.setNum(buyNum);
                orderDetail.setSellPrice(sellPrice);
                orderDetail.setTotalPay(sellPrice);
                orderDetail.setFruitType(fruitType);
                orderDetail.setOriginalPrice(originalPrice);
                orderDetail.setMeasureUnit(measureUnit);
                orderDetail.setBuyUid(uid);
                orderDetail.setBuyRemark("");
                orderDetail.setCreateTime(new Date());
                orderDetail.setUpdateTime(new Date());
                orderDetail.save();
                // 返回订单号
                renderText(orderId);
            } else {
                renderErrorText("没有选定商品下单\t请正确下单后重试");
            }
        } catch (Exception e) {
            renderErrorText("后台异常!!!");
            e.printStackTrace();
        }
    }

    /**
     * 订单id生成规则
     *
     * @return 新的订单id
     */
    private String getOrderId() {
        String orderId;
        synchronized (OrderController.class) {
            orderId = DateTimeKit.formatDateToStyle("yyMMddhhmmss", new Date()) + "-" + COUNT + RandomKit.random(1000, 9999);
            COUNT++;
        }
        return orderId;
    }

    /**
     * 返回总数和金额
     */
    public void getOrderCount() {
        Integer uid = getSessionAttr(Constant.SESSION_UID);
        Order first = Order.dao.findFirst("SELECT count(1) as order_count,SUM(b_order.pay_need_money) as total_money FROM b_order WHERE u_id = ?", uid);
        HashMap<String, String> map = new HashMap<>(2);
        map.put("order_count", first.get("order_count"));
        map.put("total_money", first.get("total_money"));
        System.out.println(map);
        renderJson(map);
    }

    /**
     * 获取个人信息和物流信息
     */
    public void getBuyInfo() {
        Integer uid = getSessionAttr(Constant.SESSION_UID);
        List<BusinessInfo> businessInfoByUid = BusinessInfo.dao.getBusinessInfoByUid(uid);
        if (businessInfoByUid == null || businessInfoByUid.size() < 1 || businessInfoByUid.get(0) == null) {
            renderErrorText("账户店铺未绑定");
            return;
        }
        BusinessInfo businessInfo = businessInfoByUid.get(0);
        String buyAddress = businessInfo.getAddressProvince() + "省" + businessInfo.getAddressCity() + "区" + businessInfo.getAddressDetail();
        String buyUserName = businessInfo.getBusinessContacts();
        String buyPhone = businessInfo.getPhone();
        String deliveryType = BusinessShipmentsType.getShipmentsTypeName(businessInfo.getShipmentsType());
        Integer deliveryIndex = businessInfo.getShipmentsType();
        String deliveryTime = "次日 8点 至 10点";

        HashMap<String, String> buyInfo = new HashMap<>(5);
        buyInfo.put("buy_address", buyAddress);
        buyInfo.put("buy_user_name", buyUserName);
        buyInfo.put("buy_phone", buyPhone);
        buyInfo.put("delivery_type", deliveryType);
        buyInfo.put("deliver_index", deliveryIndex.toString());
        buyInfo.put("delivery_time", deliveryTime);

        renderJson(buyInfo);
    }

    /**
     * 设置个人信息和物流信息
     */
    public void setBuyInfo() {
        String[] orderIds = getParaValues("orderIds");
        String buyPhone = getPara("buy_phone");
        String buyUserName = getPara("buy_user_name");
        String deliveryTime = getPara("delivery_time");
        String deliveryType = getPara("delivery_type");
        String buyAddress = getPara("buy_address");

        for (String orderId : orderIds) {
            Order order = Order.dao.getOrder(orderId);
            order.setBuyPhone(buyPhone);
            order.setBuyUserName(buyUserName);
            // TODO 发货时间
            order.setDeliveryTime(new Date());
            order.setDeliveryType(BusinessShipmentsType.getStatus(deliveryType));
            order.setBuyAddress(buyAddress);
            order.update();
        }
        renderNull();
    }

    /**
     * 生成Js预支付订单
     */
    public void createPayOrderByJsApi() {
        String orderId = getPara("orderId");
        Order order = Order.dao.getOrder(orderId);
        long money = order.getPayNeedMoney().multiply(new BigDecimal(100)).longValue();
        Map<String, String> map = new WechatPayService().wechatJsApiPay(orderId, money);
        renderJson(map);
    }

    /**
     * 生成H5预支付订单
     */
    public void createPayOrderByH5() {
        String orderId = getPara("orderId");
        Order order = Order.dao.getOrder(orderId);
        long money = order.getPayNeedMoney().multiply(new BigDecimal(100)).longValue();
        String url = new WechatPayService().wechatH5Pay(new WeChatPayConfig(orderId, money));
        renderText(url);
    }

    /**
     * 获取多个订单上商品
     */
    public void getOrdersProducts() {
        try {

            Integer uid = getSessionAttr(Constant.SESSION_UID);

            List<Map<String, Object>> productsInfo = new ArrayList<>();
            BigDecimal orderTotalPrice = new BigDecimal(0);

            String[] orderIds = getParaValues("orderIds");

            // 遍历出每一个订单
            for (String orderId : orderIds) {
                Order order = Order.dao.getOrder(orderId);
                if (order.getUId().equals(uid)) {
                    List<OrderDetail> orderDetails = OrderDetail.dao.getOrderDetails(orderId);
                    BigDecimal price = new BigDecimal(0.00);
                    // 遍历出每一个商品
                    for (OrderDetail orderDetail : orderDetails) {
                        BigDecimal sellPrice = orderDetail.getSellPrice();
                        price = price.add(sellPrice.multiply(new BigDecimal(orderDetail.getNum())));
                    }
                    orderTotalPrice = orderTotalPrice.add(price);

                    HashMap<String, Object> productsMap = new HashMap<>(3);
                    productsMap.put("products", orderDetails);
                    productsMap.put("orderId", orderId);
                    productsMap.put("totalPrice", price);
                    productsInfo.add(productsMap);
                }
            }

            HashMap<String, Object> responseMap = new HashMap<>(2);
            responseMap.put("productsInfo", productsInfo);
            responseMap.put("orderTotalPrice", orderTotalPrice);

            renderJson(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        LinkedHashMap<String, List<Order>> map = new LinkedHashMap<String, List<Order>>() {
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

    /**
     * HashMap的value降序
     */
    public static List<Map.Entry<String, List<Order>>> hashMapperDesc(Map map) {
        List<Map.Entry<String, List<Order>>> list = new ArrayList<Map.Entry<String, List<Order>>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, List<Order>>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, List<Order>> o1,
                               Map.Entry<String, List<Order>> o2) {
                return o2.getValue().get(0).getCreateTime().compareTo(o1.getValue().get(0).getCreateTime());
            }
        });
        Map<String, List<Order>> listMap = new HashMap<>(25);
        for (Map.Entry<String, List<Order>> mapping : list) {
            listMap.put(mapping.getKey(), mapping.getValue());
            System.out.println(mapping.getKey() + ":" + mapping.getValue());
        }
        return list;
    }

    /**
     * 完成订单支付
     */
    public void endOrderPay() {
        // 访问的用户 可能处于 完成状态或未完成状态 并点击了完成支付按钮 或者是 恶意的直接访问,所以必须要确认这支付的一步

        // 支付平当台回调确认,且仅当支付状态被回调修改为已支付,才可以完成订单
        String[] orderIds = getParaValues("orderIds");
        for (String orderId : orderIds) {
            Order order = Order.dao.getOrder(orderId);
            if (order.getPaySuccess().equals(1) && order.getPayTotalMoney().equals(order.getPayNeedMoney())) {
                renderNull();
                return;
            }
        }
        // TODO 添加和修改让前端明白是否支付成功的字段
    }

}
