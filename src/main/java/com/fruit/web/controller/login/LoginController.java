package com.fruit.web.controller.login;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.User;
import com.fruit.web.util.Constant;
import com.fruit.web.util.DataResult;
import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class LoginController extends BaseController {
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 登录操作
	 */
	public void auth(){
		Object uid = getSessionAttr(Constant.SESSION_UID);
		if(uid != null){
			renderJson(new DataResult<>(DataResult.CODE_SUCCESS, "登录成功"));
		}
		String phone = getPara("phone");
		String password = StringUtils.isNotBlank(getPara("password")) ? HashKit.md5(getPara("password")) : getPara("password");

		Subject subject=SecurityUtils.getSubject();
		UsernamePasswordToken token=new UsernamePasswordToken(phone, password);
		try{
			subject.login(token);
			Session session = subject.getSession();
			session.setAttribute(Constant.SESSION_UID, User.dao.getUser(phone).getId());
			renderNull();
		}catch(Exception e){
			if(StringUtils.isAllBlank(phone, password)){
				renderLogin("身份认证失败");
			}else{
				renderErrorText("用户名或密码错误");
			}
		}
	}
	
	/**
	 * 退出登录操作
	 */
	public void logout(){
		Object uid = getSessionAttr(Constant.SESSION_UID);
		if(null != uid){
			logger.info("登出系统：uid="+uid.toString());
		}
		getSession().invalidate();
		// shiro登出
		try {
			SecurityUtils.getSubject().logout();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderNull();
	}

}
