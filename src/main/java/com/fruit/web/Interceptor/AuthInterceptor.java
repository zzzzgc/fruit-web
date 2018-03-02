package com.fruit.web.Interceptor;

import com.fruit.web.base.ErrorTextRender;
import com.fruit.web.emum.BusinessAuthStatus;
import com.fruit.web.emum.BusinessInfoType;
import com.fruit.web.emum.ControllerStatusCode;
import com.fruit.web.model.BusinessAuth;
import com.fruit.web.model.BusinessInfo;
import com.fruit.web.util.Constant;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * 用户身份信息拦截
 *
 * @author ZGC
 * @date 2018-03-02 13:53
 **/
public class AuthInterceptor implements Interceptor {
    private static Logger log = Logger.getLogger(AuthInterceptor.class);

    @Override
    public void intercept(Invocation inv) {

        Controller controller = inv.getController();

        /*
         * uid校验拦截
         */
        int uid = controller.getSessionAttr(Constant.SESSION_UID) == null ? 0 : controller.getSessionAttr(Constant.SESSION_UID);
        if (uid == 0) {
            //微信前端的拦截器会拦截401并跳转到login页面的
            errorTextRender(controller,"未登录",ControllerStatusCode.AUTH.getStatus());
            log.info("uid为空");
            //清空cookie中的token
            controller.removeCookie(Constant.COOKIE_TOKEN);
            return;
        }

        /*
         * 店铺信息校验拦截
         */
        List<BusinessInfo> businessInfoByUid = BusinessInfo.dao.getBusinessInfoByUid(uid);
        if (businessInfoByUid == null || businessInfoByUid.size() < 1 || businessInfoByUid.get(0) == null){
            errorTextRender(controller,"账户店铺未绑定");
            return;
        }

        /*
         * 店铺认证校验拦截
         */
        List<BusinessAuth> businessAuthByUid = BusinessAuth.dao.getBusinessAuthByUid(uid, BusinessInfoType.PHYSICAL_STORE_AUTH.getStatus());
        // 实体店认证
        if (businessAuthByUid == null || businessAuthByUid.size() < 1 || businessAuthByUid.get(0) == null) {
            businessAuthByUid = BusinessAuth.dao.getBusinessAuthByUid(uid, BusinessInfoType.ONLINE_STORE_AUTH.getStatus());
            // 网店认证
            if (businessAuthByUid == null || businessAuthByUid.size() < 1 || businessAuthByUid.get(0) == null) {
                errorTextRender(controller,"账户未认证");
                return;
            }
        }

        /*
         * 店铺认证审核校验拦截
         */
        BusinessAuth businessAuth = businessAuthByUid.get(0);
        if (!businessAuth.getAudit().equals(BusinessAuthStatus.OK.getStatus())) {
            errorTextRender(controller,"账户店铺认证信息审核中");
            return;
        }

        inv.invoke();
    }

    /**
     * 只提示报错信息不跳转登录
     * @param controller
     * @param errorText
     */
    private void errorTextRender(Controller controller,String errorText) {
        errorTextRender(controller,errorText, ControllerStatusCode.ERROR.getStatus());
    }

    /**
     * 只提示报错信息不跳转登录
     * @param controller
     * @param errorText
     */
    private void errorTextRender(Controller controller,String errorText,int errorCode) {
        controller.render(new ErrorTextRender(errorCode, errorText));
    }
}
