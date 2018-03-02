package com.fruit.web.controller.order;

import com.fruit.web.Interceptor.AuthInterceptor;
import com.fruit.web.Interceptor.LoginInterceptor;
import com.fruit.web.base.BaseController;
import com.fruit.web.bean.pay.wechar.WeChatPayConfig;
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
        System.out.println("------------购物车批量生成订单-----------------");
//        boolean tx = Db.tx(new IAtom() {
//            @Override
//            public boolean run() throws SQLException {
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
    @Before({AuthInterceptor.class,Tx.class})
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
        // TODO 前端设置接收H5支付重定向url
        renderText(url);
    }

    /**
     * 获取多个订单上商品
     */
    public void getOrdersProducts() {
        try {
            List<Map<String, Object>> productsInfo = new ArrayList<>();
            BigDecimal orderTotalPrice = new BigDecimal(0);

            String[] orderIds = getParaValues("orderIds");

            StringBuilder sb = new StringBuilder(100);
            for (String orderId : orderIds) {
                sb.append(orderId +",");
            }
            System.out.println("获取的orderId"+sb);
            System.out.println("------------------------------start--------------------------------------");
            // 遍历出每一个订单
            for (String orderId : orderIds) {
                List<OrderDetail> orderDetails = OrderDetail.dao.getOrderDetails(orderId);
                BigDecimal price = new BigDecimal(0.00);
                System.out.println();
                System.out.println("---------------------------------");
                // 遍历出每一个商品
                for (OrderDetail orderDetail : orderDetails) {
                    BigDecimal sellPrice = orderDetail.getSellPrice();
                    price = price.add(sellPrice.multiply(new BigDecimal(orderDetail.getNum())));
                    System.out.println("orderId:"+orderId+" 的商品数量: "+orderDetail.getNum()+",单价: "+sellPrice);
                }
                System.out.println("---------------------------------");
                System.out.println("orderId总价格: "+price);
                orderTotalPrice = orderTotalPrice.add(price);
                System.out.println("orderTotalPrice累加为: "+orderTotalPrice);

                System.out.println("");

                HashMap<String, Object> productsMap = new HashMap<>(3);
                productsMap.put("products", orderDetails);
                productsMap.put("orderId", orderId);
                productsMap.put("totalPrice", price);
                productsInfo.add(productsMap);
            }
            System.out.println("------------------------------end--------------------------------------");

            HashMap<String, Object> responseMap = new HashMap<>(2);
            responseMap.put("productsInfo", productsInfo);
            responseMap.put("orderTotalPrice", orderTotalPrice);

            System.out.println(orderIds+"商品总价为:" + orderTotalPrice);

            renderJson(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

}
