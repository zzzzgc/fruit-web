package com.fruit.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fruit.web.Interceptor.LoginInterceptor;
import com.fruit.web.base.BaseController;
import com.fruit.web.model.CartProduct;
import com.fruit.web.model.Param;
import com.fruit.web.model.Product;
import com.fruit.web.util.Constant;
import com.jfinal.aop.Before;
import org.apache.log4j.Logger;

/**
 * @author ZGC AND LCC
 * @date Created in 17:45 2018/1/3
 */
@Before(LoginInterceptor.class)
public class ProductCartController extends BaseController {
	private static Logger log = Logger.getLogger(ProductCartController.class);
	private static final int PAGE_SIZE = 50;

	/**
	 * 获取购物车商品
	 */
	public void getProduct() {

		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		renderJson(Product.dao.listCartProduct(uid, PAGE_SIZE, pageNum));
	}

	/**
	 * 删除购物车商品
	 */
	public void removeProduct() {
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		Integer[] productIds = getParaValuesToInt("ids");
		CartProduct.dao.removeCartProduct(uid, productIds);
		renderResult(true);
	}

	/**
	 * 添加购物车商品
	 */
	public void addProduct() {
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		Integer standardId = getParaToInt("standard_id");
		Integer buyNum = getParaToInt("buy_num", 1);
		String remark = getPara("remark");

		if(standardId == null) {
			renderErrorText("参数有误");
			return;
		}
		CartProduct.dao.addProduct(uid, standardId, buyNum, remark);
		renderResult(true);
	}

	/**
	 * 新增或修改购物车商品
	 */
	public void updateProduct() {
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		Integer standardId = getParaToInt("standard_id");
		Integer buyNum = getParaToInt("buy_num", 1);
		String remark = getPara("remark");

		if(standardId == null) {
			renderErrorText("参数有误");
			return;
		}
		CartProduct.dao.saveAndUpdateProduct(uid, standardId, buyNum, remark);
		renderResult(true);
	}

	/**
	 * 批量添加商品,目前用于添加本地购物车到数据库
	 */
	public void saveGoodsData() {
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		String cartProductsByJson = getPara("cartProducts");
		System.out.println("cartProductsByJson"+cartProductsByJson);
		JSONArray arrayJson = JSON.parseArray(cartProductsByJson);
		for (int i = 0; i < arrayJson.size(); i++) {
			JSONObject products = arrayJson.getJSONObject(i);
			String remark = products.getString("remark");
			Integer buy_num = products.getInteger("buy_num");
			Integer standard_id = products.getInteger("standard_id");
			System.out.println("remark:"+remark+",buy_num:"+buy_num+",standard_id:"+standard_id);
			//如果有重复的商品,直接累加到数据库
			CartProduct.dao.addProduct(uid, standard_id, buy_num, remark);
		}
		renderResult(true);
	}


}
