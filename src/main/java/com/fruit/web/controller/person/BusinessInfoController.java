package com.fruit.web.controller.person;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.BusinessInfo;
import com.fruit.web.util.Constant;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BusinessInfoController extends BaseController{
    private static  Logger log = Logger.getLogger(BusinessInfoController.class);

    public void addBusinessInfo(){
        try {
            BusinessInfo businessInfo=getModel(BusinessInfo.class,"",true);
            Object uid=getSessionAttr(Constant.SESSION_UID);
            businessInfo.setUId(Integer.parseInt(uid.toString()));
            businessInfo.setCreateTime(new Date());
            businessInfo.setUpdateTime(new Date());
//            BusinessInfo.dao.put(businessInfo);
            businessInfo.save();
            renderText("1");
        } catch (Exception e) {
            e.printStackTrace();
            renderErrorText("0");
        }
    }
}
