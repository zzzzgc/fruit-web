package com.fruit.web.Interceptor;

import com.fruit.web.util.Constant;
import com.google.common.collect.Sets;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

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
        // 获取的session的id 和 登录时session的id不一样
        Integer uid = controller.getSessionAttr(Constant.SESSION_UID);
        System.out.println("验证用户:"+uid+",已登录");

        if (uid == null) {
            //微信前端的拦截器会拦截401并跳转到login页面的
            controller.renderError(401, "请登录后再执行该操作");
            return;
        }

        inv.invoke();
    }
}
