package com.fruit.web.controller.person;

import com.fruit.web.Interceptor.LoginInterceptor;
import com.fruit.web.base.BaseController;
import com.fruit.web.emum.BusinessShipmentsType;
import com.fruit.web.model.BusinessInfo;
import com.fruit.web.util.Constant;
import com.jfinal.aop.Before;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Before(LoginInterceptor.class)
public class BusinessInfoController extends BaseController{
    private static  Logger log = Logger.getLogger(BusinessInfoController.class);

    // 添加商户信息
    public void addBusinessInfo(){
        try {
            BusinessInfo businessInfo=getModel(BusinessInfo.class,"",true);
            Integer shipmentType=getParaToInt("shipmentCode",0);
            Object uid=getSessionAttr(Constant.SESSION_UID);
            businessInfo.setShipmentsType(shipmentType);
            businessInfo.setUId(Integer.parseInt(uid.toString()));
            businessInfo.setCreateTime(new Date());
            businessInfo.setUpdateTime(new Date());
//            BusinessInfo.dao.put(businessInfo);
            businessInfo.save();
            renderText("1");
        } catch (Exception e) {
            renderErrorText("0");
        }
    }

    // 获取商户信息
    public void getBusinessInfo(){
        Object uid=getSessionAttr(Constant.SESSION_UID);
        List<BusinessInfo> businessInfoList=BusinessInfo.dao.getBusinessInfoByUid(Integer.parseInt(uid.toString()));
        renderJson(businessInfoList);
    }

    private Integer getEmum(String shipmentsType){
        //0市场车 1物流 2自提
        Integer shipmentCode = 0;
        if("市场车".equals(shipmentsType)){
            shipmentCode=BusinessShipmentsType.MARKET.getStatus();
        } else if ("物流".equals(shipmentsType)) {
            shipmentCode=BusinessShipmentsType.LOGISTICS.getStatus();
        } else if ("自提".equals(shipmentsType)) {
            shipmentCode=BusinessShipmentsType.TAKE_THEIR.getStatus();
        }
        return shipmentCode;
    }
}
