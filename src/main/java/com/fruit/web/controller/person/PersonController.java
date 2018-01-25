package com.fruit.web.controller.person;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.BusinessUser;
import com.fruit.web.model.Order;
import com.fruit.web.util.Constant;
import com.jfinal.kit.PropKit;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  LZZ
 */
public class PersonController extends BaseController{
    private static Logger logger= Logger.getLogger(PersonController.class);

    /**
     * 进入"我的" 页面的时候进行用户信息和订单个数的统计
     */
    public void getUserInfo(){
        Object uid=getSessionAttr(Constant.SESSION_UID);
        BusinessUser user= BusinessUser.dao.findById(uid.toString());
        List<Order> orderList=Order.dao.getOrderCountList(uid.toString());
        Map<String,Integer> mapStatusAndCount=new HashMap<>();
        if(orderList!=null && orderList.size()>0){
            Integer waitPay=0;
            Integer verifying=0;
            Integer waitReceiver=0;
            Integer myOrders=0;
            for (int i = 0; i < orderList.size(); i++) {
                Order order=orderList.get(i);
                Integer status=order.get("status");
                Object countDB=order.get("count");
                Integer count=Integer.parseInt(countDB.toString());
                if(status==0){
                    waitPay+=count;
                    verifying+=count;
                    myOrders+=count;
                }else if(status==5){
                    verifying+=count;
                    myOrders+=count;
                }else if(status==10){
                    waitReceiver+=count;
                    myOrders+=count;
                }else{
                    myOrders+=count;
                }
            }
            mapStatusAndCount.put("waitPay",waitPay);
            mapStatusAndCount.put("verifying",verifying);
            mapStatusAndCount.put("waitReceiver",waitReceiver);
            mapStatusAndCount.put("myOrders",myOrders);
        }
        Map<String,Object> map=new HashMap<>();
        user.setPass(null);
        map.put("phone",user.getPhone());
        map.put("mapStatusAndCount",mapStatusAndCount);
        String name = PropKit.get("ziho.name");
        String bank = PropKit.get("ziho.bank");
        String cardNum=PropKit.get("ziho.cardNum");
        map.put("zihoName",name);
        map.put("zihoBank",bank);
        map.put("zihoCardNum",cardNum);
        renderJson(map);
    }
}
