package com.fruit.web.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseBusinessUser<M extends BaseBusinessUser<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}

	public java.lang.String getName() {
		return getStr("name");
	}

	public void setNickName(java.lang.String nickName) {
		set("nick_name", nickName);
	}

	public java.lang.String getNickName() {
		return getStr("nick_name");
	}

	public void setPass(java.lang.String pass) {
		set("pass", pass);
	}

	public java.lang.String getPass() {
		return getStr("pass");
	}

	public void setPhone(java.lang.String phone) {
		set("phone", phone);
	}

	public java.lang.String getPhone() {
		return getStr("phone");
	}

	public void setWeixinOpenId(java.lang.String weixinOpenId) {
		set("weixin_open_id", weixinOpenId);
	}

	public java.lang.String getWeixinOpenId() {
		return getStr("weixin_open_id");
	}

	public void setSequence(java.lang.String sequence) {
		set("sequence", sequence);
	}

	public java.lang.String getSequence() {
		return getStr("sequence");
	}

	public void setIp(java.lang.String ip) {
		set("ip", ip);
	}

	public java.lang.String getIp() {
		return getStr("ip");
	}

	public void setLastLoginTime(java.util.Date lastLoginTime) {
		set("last_login_time", lastLoginTime);
	}

	public java.util.Date getLastLoginTime() {
		return get("last_login_time");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setUpdateTime(java.util.Date updateTime) {
		set("update_time", updateTime);
	}

	public java.util.Date getUpdateTime() {
		return get("update_time");
	}

}
