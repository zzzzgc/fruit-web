package com.fruit.web.controller.order;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Order;
import org.apache.log4j.Logger;

import java.sql.Array;
import java.util.*;

public class OrderController extends BaseController {
    private static Logger log = Logger.getLogger(OrderController.class);
    private static final int PAGE_SIZE = 10;

    // 获取到订单列表
    public void getOderList(){
        String order_status=getPara("order_status");
        if("one".equals(order_status)){
            order_status="'0'";
        }else if("two".equals(order_status)){
            order_status="'0','5'";
        }else if("three".equals(order_status)){
            order_status="'10'";
        }else if("four".equals(order_status)){
            order_status="'0','5','10','20','30','40'";
        }

        List<Order> orderList=Order.dao.getOrderListByStatus("1",order_status);
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

    public void getOrderListDetail(){
        String orderId=getPara("orderId");

    }
}
