package com.fruit.web.controller;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.CartProduct;
import com.fruit.web.model.Product;
import com.fruit.web.util.Constant;
import org.apache.log4j.Logger;

public class ProductCartController extends BaseController {
	private static Logger log = Logger.getLogger(ProductCartController.class);
	private static final int PAGE_SIZE = 50;

    /**
     * 获取购物车商品，TODO 需做登录校验
	 */
	public void getProduct() {
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		if(uid == null) {// TODO 添加拦截器
			renderErrorText("未登录");
			return;
		}
		renderJson(Product.dao.listCartProduct(uid, PAGE_SIZE, pageNum));
	}

	/**
	 * 删除购物车商品
	 */
	public void removeProduct() {
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		if(uid == null) {// TODO 添加拦截器
			renderErrorText("未登录");
			return;
		}
		Integer[] productIds = getParaValuesToInt("ids");
		CartProduct.dao.removeCartProduct(uid, productIds);
		renderResult(true);
	}

	/**
	 * 添加购物车商品
	 */
	public void addProduct() {
		Integer standardId = getParaToInt("standard_id");
		if(standardId == null) {
			renderErrorText("参数有误");
			return;
		}
		Integer uid = getSessionAttr(Constant.SESSION_UID);
		if(uid == null) {// TODO 添加拦截器
			renderErrorText("未登录");
			return;
		}
		Integer buyNum = getParaToInt("buy_num", 1);
		String remark = getPara("remark");
		CartProduct.dao.addProduct(uid, standardId, buyNum, remark);
		renderResult(true);
	}
}
