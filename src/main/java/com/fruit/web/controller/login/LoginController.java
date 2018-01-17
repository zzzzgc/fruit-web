package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.BusinessUser;
import com.fruit.web.util.Constant;
import com.fruit.web.util.DataResult;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class LoginController extends BaseController {

    private Logger log = Logger.getLogger(getClass());

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
                setSessionAttr(Constant.SESSION_UID,user.getId());
                log.info("----------session获取jsessionid为"+getSession().getId()+"----------");
                renderNull();
            } else {
                renderErrorText("用户名或密码有误");
            }
        } else {
            renderErrorText("请填写用户名、密码");
        }
    }

    /**
     * 注册操作
     */
    public void register() {

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

}
