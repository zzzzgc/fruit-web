package com.fruit.web.util;

/**
 * 常量类枚举类
 */
public class Constant {

	/*登录相关*/
	public static final String SESSION_UID = "uid";//登录用户的user_id，这个值是通过登录接口设置的
	public static final String SESSION_SEQUENCE = "login_sequence";//登录用户的sequence用来标识用户登录的
	public static final String SESSION_TOKEN = "login_token";//登录用户的token用来代替账户密码的
	public static final String SESSION_IP = "login_ip";//登录用户的ip用来识别用户所用机器

	/*cookie相关*/
	public static final String COOKIE_TOKEN = "cookie_login_token";//登录用户请求过来的token用来代替账户密码的

	/*安全验证相关*/
	public final static String LOGIN_IMAGE_VERIFY_CODE = "login_image_verify_code";// 图片验证码 session的key
	public final static String LOGIN_PHONE_MSGS_VERIFY_CODE = "login_phone_msgs_verify_code";// 短信验证码 session的key
	
	/*ehcache 缓存key值，只允许使用这里定义的，其他的将不生效*/
	public static final String CACHE_KEY_10 = "cache_key_10";// 缓存10秒
	public static final String CACHE_KEY_30 = "cache_key_30";// 缓存30秒
	public static final String CACHE_KEY_60 = "cache_key_60";// 缓存1分钟
	public static final String CACHE_KEY_300 = "cache_key_300";// 缓存5分钟
	public static final String CACHE_KEY_600 = "cache_key_600";// 缓存10分钟
	public static final String CACHE_KEY_1800 = "cache_key_1800";// 缓存30分钟
	public static final String CACHE_KEY_3600 = "cache_key_3600";// 缓存1小时
	public static final String CACHE_KEY_86400 = "cache_key_86400";// 缓存1天
	
	
}
