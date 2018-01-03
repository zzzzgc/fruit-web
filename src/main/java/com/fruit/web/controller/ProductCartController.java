package com.fruit.web.controller;

import com.fruit.web.Interceptor.LoginInterceptor;
import com.fruit.web.base.BaseController;
import com.fruit.web.model.CartProduct;
import com.fruit.web.model.Product;
import com.fruit.web.util.Constant;
import com.jfinal.aop.Before;
import org.apache.log4j.Logger;

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


}
