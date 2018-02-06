package com.fruit.web.service.vaildation;

import com.fruit.web.util.Constant;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于手机验证码的服务
 *
 * @author ZGC
 * @date 2018-02-06 10:23
 **/
public class PhoneMsgsBaseValidationServiceImpl extends BaseValidationService {
    @Override
    public String createVerifyCode(HttpServletRequest request) {
        // TODO 调用短信接口发送短信并添加验证码到session
        request.getSession().setAttribute(Constant.LOGIN_PHONE_MSGS_VERIFY_CODE,"");
        return null;
    }
}
