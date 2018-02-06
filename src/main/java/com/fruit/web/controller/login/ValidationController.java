package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.util.Constant;
import com.fruit.web.util.VerifyCodeUtils;
import com.fruit.web.util.factory.VerifyCodeFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 用于获取校验信息的接口
 *
 * @author ZGC
 * @date 2018-02-05 18:22
 **/
public class ValidationController extends BaseController {

    /**
     * 获取图片验证码
     * @return 图片地址
     */
    public void createImagesVerifyCode() {
        renderText(VerifyCodeFactory.getImageType().createVerifyCode(getRequest()));
    }

    /**
     * 获取手机短信验证码
     * @return 无
     */
    public void createPhoneSmsVerifyCode() {
        renderText(VerifyCodeFactory.getPhoneMsgType().createVerifyCode(getRequest()));
    }




}
