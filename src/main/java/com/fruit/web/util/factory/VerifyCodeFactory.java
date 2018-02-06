package com.fruit.web.util.factory;

import com.fruit.web.service.vaildation.ImageBaseValidationServiceImpl;
import com.fruit.web.service.vaildation.PhoneMsgsBaseValidationServiceImpl;
import com.fruit.web.service.vaildation.BaseValidationService;

/**
 * 用于获取各种验证码服务的工厂
 *
 * @author ZGC
 * @date 2018-02-06 10:40
 **/
public class VerifyCodeFactory {
    /**
     * 图片类型的验证码
     * @return
     */
    public static BaseValidationService getImageType () {
        return new ImageBaseValidationServiceImpl();
    }

    /**
     * 手机短信类型的验证码
     * @return
     */
    public static BaseValidationService getPhoneMsgType () {
        return new PhoneMsgsBaseValidationServiceImpl();
    }
}
