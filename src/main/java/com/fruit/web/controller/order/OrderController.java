package com.fruit.web.controller.order;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Order;
import com.fruit.web.model.OrderDetail;
import com.fruit.web.model.Product;
import com.fruit.web.util.Constant;
import com.fruit.web.util.ConvertUtils;
import com.jfinal.ext2.kit.DateTimeKit;
import com.jfinal.ext2.kit.RandomKit;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Array;
import java.util.*;

/**
 * @Author: ZGC and CCZ
 * @Date Created in 15:52 2018/1/3
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
            order.save();

            for (Product product : products) {
                //获取购物车商品id
                int standardId = Integer.parseInt(product.get("standard_id").toString());
                for (Integer id : standardIds) {
                    if (id.equals(standardId)) {
                        // TODO 删除购物车中的商品

                        //buy_num能被取出来是因为所有取出来的内容都被封装在实体对象中了
                        int buy_num = Integer.parseInt(product.get("buy_num").toString());
                        BigDecimal sell_price = ConvertUtils.toBigDecimal(product.get("sell_price"));
                        BigDecimal original_price = ConvertUtils.toBigDecimal(product.get("original_price"));
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
                        orderDetail.setBuyAddress("");
                        orderDetail.setBuyPhone("");
                        orderDetail.setBuyUid(uid);
                        orderDetail.setBuyRemark(remark);
                        orderDetail.setUpdateTime(new Date());
                        orderDetail.setCreateTime(new Date());
                        orderDetail.save();
                    }
                }
            }
            renderText(orderId);
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
            int id = getParaToInt("id");
            int Standard_id = getParaToInt("Standard_id");
            int buyNum = getParaToInt("buyNum");
            String name = getPara("name");
            String standard_name = getPara("standard_name");
            String sell_price_int = getPara("sell_price");
            BigDecimal sell_price = new BigDecimal(sell_price_int);
            String fruit_type = getPara("fruit_type");
            String original_price_int = getPara("original_price");
            BigDecimal original_price = new BigDecimal(original_price_int);
            String measure_unit = getPara("measure_unit");

            Order order = new Order();
            order.setPayStatus(0);
            order.setStatus(0);
            order.setUId(uid);
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setOrderId(orderId);
            order.save();

            OrderDetail orderDetail = new OrderDetail();
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
            orderDetail.setBuyAddress("");
            orderDetail.setBuyPhone("");
            orderDetail.setBuyUid(uid);
            orderDetail.setBuyRemark("");
            orderDetail.setCreateTime(new Date());
            orderDetail.setUpdateTime(new Date());
            orderDetail.save();

            // 返回订单号
            renderText(orderId);
        } catch (Exception e) {
            renderErrorText("后台异常!!!");
            e.printStackTrace();
        } finally {
        }
    }

    private String getOrderId() {
        String orderId;
        synchronized (OrderController.class) {
            orderId = DateTimeKit.formatDateToStyle("yyMMddhhmmss", new Date()) + "-" + COUNT + "-" + RandomKit.random(100000,999999);
            COUNT++;
        }
        return orderId;
    }

    //跳转页面

    public static void main(String[] args) {

    }

    /**
     * 获取到订单列表
     */
    public void getOderList(){
        String order_status=getPara("order_status");
        if("one".equals(order_status)){ // 判断代付款
            order_status="'0'";
        }else if("two".equals(order_status)){ //判断确认中
            order_status="'0','5'";
        }else if("three".equals(order_status)){ //判断待发货
            order_status="'10'";
        }else if("four".equals(order_status)){ //判断我的订单
            order_status="'0','5','10','20','30','40'";
        }
        // 获取用户ID
        Object uid=getSessionAttr(Constant.SESSION_UID);
        List<Order> orderList=Order.dao.getOrderListByStatus(uid.toString(),order_status);
        Map<String,List<Order>> map =new HashMap<String,List<Order>>();
        for (Order order : orderList) {
            if(map.containsKey(order.getOrderId())){
                List<Order> orders=map.get(order.getOrderId());
                orders.add(order);
                map.put(order.getOrderId(),orders);
            }else{
                List<Order> orders=new ArrayList<Order>();
                orders.add(order);
                map.put(order.getOrderId(),orders);
            }
        }
        renderJson(map);
    }

    public void getOrderListDetail() {
        String orderId = getPara("orderId");
    }


}
