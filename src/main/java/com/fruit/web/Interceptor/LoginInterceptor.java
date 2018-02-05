package com.fruit.web.Interceptor;

import com.fruit.web.base.ErrorTextRender;
import com.fruit.web.model.BusinessUser;
import com.fruit.web.util.Constant;
import com.fruit.web.util.HttpUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 登录拦截器
 *
 * @author ZGC
 * @date Created in 16:49 2017/12/29
 */
public class LoginInterceptor implements Interceptor {
    private static Logger log = Logger.getLogger(LoginInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        log.info("----------登录拦截器----------");
        Controller controller = inv.getController();
        try {
            int uid = controller.getSessionAttr(Constant.SESSION_UID) == null ? 0 : controller.getSessionAttr(Constant.SESSION_UID);

            if (uid != 0) {
                /*
                token校验拦截
                 */
                String token = controller.getCookie(Constant.COOKIE_TOKEN);
                String sessionToken = controller.getSessionAttr(Constant.SESSION_TOKEN);
                System.out.println("SessionToken:" + sessionToken + ",LocalToken:" + token);
                if (!StringUtils.isNotBlank(sessionToken) || !StringUtils.isNotBlank(token) || !token.equals(sessionToken)) {
                    //微信前端的拦截器会拦截401并跳转到login页面的
                    controller.render(new ErrorTextRender(401, "toKen校验失败"));
                    log.info("用户uid: " + uid + " ,token校验失败,用户token: " + sessionToken + " ,当前token: " + token);
                    //清空cookie中的token
                    controller.removeCookie(Constant.SESSION_TOKEN);
                    return;
                }

                /*
                sequence校验拦截
                 */
                String sequence = controller.getSessionAttr(Constant.SESSION_SEQUENCE);
                BusinessUser dbUser = BusinessUser.dao.findByIdLoadColumns(uid, "sequence");
                String dbSequence = dbUser == null ? null : dbUser.getSequence();
                // sequence防止同时登陆验证拦截
                if (!StringUtils.isNotBlank(sequence) || dbSequence == null || !dbSequence.equals(sequence)) {
                    //微信前端的拦截器会拦截401并跳转到login页面的
                    log.info("用户uid: " + uid + " ,sequence校验失败,用户最新登录sequence: " + dbUser + " ,当前sequence: " + sequence + ",不排除为被他人登录的情况.警告用户确认并提醒他修改密码,用手机和邮箱的方式修改");
                    //清空cookie中的token
                    controller.removeCookie(Constant.SESSION_TOKEN);
                    return;
                }

                /*
                ip校验拦截
                 */
                String ip = controller.getSessionAttr(Constant.SESSION_IP);
                String localIp = HttpUtils.getRequestIp(controller.getRequest());
                if (!StringUtils.isNotBlank(ip) || !StringUtils.isNotBlank(localIp) || !ip.equals(localIp)) {
                    //微信前端的拦截器会拦截401并跳转到login页面的
                    controller.render(new ErrorTextRender(401, "ip校验失败"));
                    log.info("用户uid: " + uid + " ,ip校验失败,用户登录ip: " + ip + " ,请求ip: " + localIp);
                    //清空cookie中的token
                    controller.removeCookie(Constant.COOKIE_TOKEN);
                    controller.removeCookie("JSESSIONID");
                    return;
                }

                inv.invoke();
            } else {
                //微信前端的拦截器会拦截401并跳转到login页面的
                controller.render(new ErrorTextRender(401, "未登录"));
                log.info("uid为空");
                //清空cookie中的token
                controller.removeCookie(Constant.COOKIE_TOKEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
