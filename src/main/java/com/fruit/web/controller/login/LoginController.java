package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.BusinessUser;
import com.fruit.web.util.Constant;
import com.fruit.web.util.DataResult;
import com.fruit.web.util.VerifyCodeUtils;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class LoginController extends BaseController {

    private Logger log = Logger.getLogger(getClass());

    private static int counter = 0;

    /**
     * 登录用的session的key
     */
    private final static String LOGIN_VERIFY_CODE = "login_verify_code";

    /**
     * 登录操作
     */
    public void auth() {
        Object uid = getSessionAttr(Constant.SESSION_UID);
        if (uid != null) {
            renderJson(new DataResult<>(DataResult.CODE_SUCCESS, "登录成功"));
            //return;
        }

        String phone = getPara("phone");
        String password = StringUtils.isNotBlank(getPara("password")) ? HashKit.md5(getPara("password")) : getPara("password");

        if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(password)) {
            BusinessUser user = BusinessUser.dao.getUser(phone, password);
            if (user != null) {
                setSessionAttr(Constant.SESSION_UID, user.getId());
                log.info("----------session获取jsessionid为" + getSession().getId() + "----------");
                renderNull();
            } else {
                renderErrorText("用户名或密码有误");
                return;
            }
        } else {
            renderErrorText("请填写用户名、密码");
            return;
        }
    }

    /**
     * 注册操作
     */
    public void register() {

    }

    /**
     * 验证码校验
     */
    public void validationVerifyCode() {
        String verifyCode = getPara("verifyCode");
        String serviceVerifyCode = getVerifyCode();
        if (verifyCode != null && serviceVerifyCode != null && verifyCode.equals(serviceVerifyCode)) {

        } else {
            renderErrorText("验证码错误!");
        }

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
        renderNull();
    }

    /**
     * 生成验证码
     */
    public void createVerifyCode() {
        try {
            //文件夹
            String folder = "/verify";
            //文件名
            String dirName = System.currentTimeMillis() + ++counter + ".jpg";
            //文件路径
            String realPath = getRequest().getSession().getServletContext().getRealPath(folder);

            File dir = new File(realPath);
            String verify = VerifyCodeUtils.generateVerifyCode(4);
            getSession().setAttribute(LOGIN_VERIFY_CODE, verify);

            File file = new File(dir, dirName);
            VerifyCodeUtils.outputImage(200, 80, file, verify);
            // /verify/imgName.jsp
            renderText(folder + File.separator + dirName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取来验证码
     *
     * @return
     */
    private String getVerifyCode() {
        return getSessionAttr(LOGIN_VERIFY_CODE);
    }


}
