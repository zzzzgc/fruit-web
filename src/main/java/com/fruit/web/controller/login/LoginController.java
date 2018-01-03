package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.User;
import com.fruit.web.util.Constant;
import com.fruit.web.util.DataResult;
import com.jfinal.core.Controller;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class LoginController extends BaseController {

    private Logger log = Logger.getLogger(getClass());

    /**
     * 登录操作 // TODO 通过getSessionAttr 设置登录信息
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
            //User user = User.dao.getUser(phone, password);
            //if (user != null) {
            if (true){// 测试数据
//                HttpSession session = getSession(false);
//                session.setAttribute(Constant.SESSION_UID,1);
                //设置登陆信息到session
                setSessionAttr(Constant.SESSION_UID,1);
                log.info("----------session获取jsessionid为"+getSession().getId()+"----------");
                renderNull();
            } else {
                renderLogin("用户名或密码有误");
            }
        } else {
            renderErrorText("请填写用户名、密码");
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

}
