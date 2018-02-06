package com.fruit.web.service.vaildation;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于生成获取图片验证码
 *
 * @author ZGC
 * @date 2018-02-06 18:13
 **/
public abstract class BaseValidationService {

    /**
     * 图片生成验证码
     * @param request http请求对象
     * @return 验证码信息,可能为空可能为地址
     */
    public abstract String createVerifyCode(HttpServletRequest request);

    /**
     * 校验验证码
     * @param request http请求对象
     * @param sessionKey session 的 key
     * @param localVerifyCode 用于校验的验证码字段
     * @return 校验成功?
     */
    public boolean validationVerifyCode(HttpServletRequest request,String sessionKey,String localVerifyCode){
        String serviceVerifyCode = getVerifyCode(request,sessionKey);
        if (localVerifyCode != null && serviceVerifyCode != null && localVerifyCode.toUpperCase().equals(serviceVerifyCode)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取验证码
     * @param request http请求对象
     * @param sessionKey session 的 key
     * @return 获取保存在session中的验证码
     */
    public String getVerifyCode(HttpServletRequest request,String sessionKey){
        return (String) request.getSession().getAttribute(sessionKey);
    }


}
