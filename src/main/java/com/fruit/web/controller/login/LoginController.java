package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.emum.VerifyCodeType;
import com.fruit.web.model.BusinessUser;
import com.fruit.web.util.Constant;
import com.fruit.web.util.HttpUtils;
import com.fruit.web.util.factory.VerifyCodeFactory;
import com.jfinal.aop.Before;
import com.jfinal.ext2.kit.RandomKit;
import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

public class LoginController extends BaseController {

    private Logger log = Logger.getLogger(getClass());

    /**
     * 登录操作
     */
    @Before(Tx.class)
    public void auth() {

        /*验证码拦截*/
        boolean verifyCode = validationVerifyCode(getParaToInt("verifyCodeType"), getPara("verifyCode"));
        if (!verifyCode) {
            renderErrorText("验证码错误");
            return;
        }
        boolean rememberPW = getParaToBoolean("rememberPW");
        String phone = getPara("phone");
        String password = StringUtils.isNotBlank(getPara("password")) ? HashKit.md5(getPara("password")) : getPara("password");

        /*口令拦截*/
        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(password)) {
            BusinessUser user = BusinessUser.dao.getUser(phone, password);
            if (user != null) {
                // 保存ip
                HttpServletRequest request = getRequest();

                String ip = HttpUtils.getRequestIp(request);
                setSessionAttr(Constant.SESSION_IP, ip);

                // 保存uId
                setSessionAttr(Constant.SESSION_UID, user.getId());

                // 保存登录序列号
                String sequence = RandomKit.randomMD5Str();
                setSessionAttr(Constant.SESSION_SEQUENCE, sequence);

                //保存token
                String token = RandomKit.randomMD5Str();
                setSessionAttr(Constant.SESSION_TOKEN, token);

                if (rememberPW){
                    // 设置cookie过期时间为7天后
                    System.out.println(getSession().getMaxInactiveInterval());
                    HttpSession session = getSession();
                    session.setMaxInactiveInterval(604800);
                    setCookie("JSESSIONID",session.getId(),604800);
                    Cookie jsessionid = getCookieObject("JSESSIONID");
                    jsessionid.setMaxAge(604800);
                }

                user.setIp(ip);
                user.setSequence(sequence);
                user.setUpdateTime(new Date());
                user.setLastLoginTime(new Date());
                user.update();

                log.info("----------session获取jsessionid为" + getSession().getId() + "----------");
                renderText(token);
            } else {
                renderErrorText("用户名或密码有误");
            }
        } else {
            renderErrorText("请填写用户名或密码");
        }
    }

    /**
     * 校验验证码
     *
     * @param verifyCodeType VerifyCodeType对象
     * @return 校验成功?
     */
    private boolean validationVerifyCode(int verifyCodeType, String verifyCode) {
        VerifyCodeType type = VerifyCodeType.getVerifyCodeType(verifyCodeType);
        boolean result;

        switch (type) {
            case IMGAGES:
                //图片验证码
                result = VerifyCodeFactory.getImageType().validationVerifyCode(getRequest(), Constant.LOGIN_IMAGE_VERIFY_CODE, verifyCode);
                break;
            case PHONE_SMS:
                //手机验证码
                result = VerifyCodeFactory.getPhoneMsgType().validationVerifyCode(getRequest(), Constant.LOGIN_PHONE_MSGS_VERIFY_CODE, verifyCode);
                break;
            default:
                throw new RuntimeException("verifyCode值不存在或为null");
        }
        return result;
    }

    /**
     * 忘记密码
     */
    public void forgetPwd() {
        try {
            boolean result = validationVerifyCode(getParaToInt("verifyCodeType"), getPara("msgValidCode"));
            if (!result) {
                renderErrorText("验证码错误");
                return;
            }

            String phone = getPara("phone");
            String password = HashKit.md5(getPara("password"));
            BusinessUser.dao.updatePassword(phone,password);
            renderNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改密码
     */
    public void updatePwd() {
        try {
            boolean result = validationVerifyCode(1, getPara("imgVerifyCode"));
            if (!result) {
                renderErrorText("图片验证码错误");
                return;
            }

            result = validationVerifyCode(2, getPara("msgVerifyCode"));
            if (!result) {
                renderErrorText("短信验证码错误");
                return;
            }

            String phone = getPara("phone");
            String password = HashKit.md5(getPara("password"));
            BusinessUser.dao.updatePassword(phone,password);
            renderNull();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册操作
     */
    public void register() {
        /*验证码拦截*/
        boolean verifyCode = validationVerifyCode(getParaToInt("msgVerifyCodeType"), getPara("msgVerifyCode"));
        if (!verifyCode) {
            renderErrorText("验证码错误");
            return;
        }

        /*查看电话是否被注册的拦截*/
        String phone = getPara("phone");
        if (BusinessUser.dao.getUserByPhone(phone) != null) {
            renderErrorText("手机号码已存在");
            return;
        }

        String password = getPara("password");
        BusinessUser user = new BusinessUser();
        user.setPhone(phone);
        user.setPass(HashKit.md5(password));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.save();
        renderNull();
    }

    /**
     * 退出登录操作
     */
    public void logout() {
        Object uid = getSessionAttr(Constant.SESSION_UID);
        if (null != uid) {
            log.info("登出系统：uid=" + uid.toString());
        }
        getSession().invalidate();
        getCookieObjects().clone();
        renderNull();
    }


}
