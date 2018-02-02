package com.fruit.web.Interceptor;

import com.fruit.web.model.BusinessUser;
import com.fruit.web.model.User;
import com.fruit.web.util.Constant;
import com.google.common.collect.Sets;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;
import org.apache.shiro.session.Session;

/**
 * 登录拦截器(目前只拦截购物车控制层内容)
 *
 * @Author: ZGC
 * @Date Created in 16:49 2017/12/29
 */
public class LoginInterceptor implements Interceptor {
    private static Logger log = Logger.getLogger(LoginInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        log.info("----------登录拦截器----------");
        Controller controller = inv.getController();
        try {
            // 获取的session的id 和 登录时session的id不一样
            String token = controller.getSessionAttr(Constant.SESSION_TOKEN);

            String sessionToken = controller.getSessionAttr(Constant.SESSION_TOKEN);

            System.out.println("SessionToken:" + sessionToken + ",LocalToken:" + token);

            // token校验
            if (sessionToken == null || token == null || token != sessionToken || !token.equals(sessionToken)) {
                //微信前端的拦截器会拦截401并跳转到login页面的
                controller.renderError(401, "toKen校验失败");
                //清空cookie
                controller.setCookie(null);
                return;
            }

            String sequence = controller.getSessionAttr(Constant.SESSION_SEQUENCE);
            int uid = controller.getSessionAttr(Constant.SESSION_UID);
            BusinessUser dbUser = BusinessUser.dao.findByIdLoadColumns(uid, "sequence");
            String dbSequence = dbUser.getSequence();

            // sequence防止同时登陆验证
            if (sequence == null || dbSequence == null || !dbSequence.equals(sequence)) {
                //微信前端的拦截器会拦截401并跳转到login页面的
                controller.renderError(401, "您的账户已被登录,如果不是您的操作,请尽快修改密码!");
                //清空cookie
                controller.setCookie(null);
                return;
            }

            // 验证ip,可能不是很好.因为用户所在的区域有可能有多台机共用ip(路由、交换机等),这时候就不能了

            inv.invoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
