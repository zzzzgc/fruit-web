package com.fruit.web.controller;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Product;
import com.fruit.web.model.ProductImg;
import com.fruit.web.model.ProductMarket;
import com.fruit.web.model.ProductStandard;
import com.fruit.web.util.Constant;
import com.jfinal.kit.Kv;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import java.util.ArrayList;

public class ProductController extends BaseController {
	private static Logger log = Logger.getLogger(ProductController.class);
	private static final int PAGE_SIZE = 10;

	public void get() {
		Integer productId = getParaToInt("productId");
		Product product = Product.dao.findById(productId);
		if(product == null) {
			renderError(404);
			return;
		}
		Kv kv = Kv.by("product", product)
				.set("standards", ProductStandard.dao.listProductStandard(productId, "id,name,sub_title,original_price,sell_price,buy_start_time,buy_end_time,stock,is_default,update_time"))
				.set("product_img", ProductImg.dao.getImgs(productId, ProductImg.TYPE_PRODUCT))
				.set("market", ProductMarket.dao.getMarket(productId))
				.set("market_img", ProductImg.dao.getImgs(productId, ProductImg.TYPE_MARKET));
		renderJson(kv);
	}

	public void listBuy() {
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listBuyNum(PAGE_SIZE, pageNum));
	}

	public void listNew() {
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listUpdate(PAGE_SIZE, pageNum));
	}

	/**
	 * 按照类型查找商品
	 */
	public void listType() {
		Integer typeId = getParaToInt("typeId");
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listType(typeId, PAGE_SIZE, pageNum));
	}

	/**
	 * 按照商品类型查找
	 */
	public void listGroupType() {
		Integer groupTypeId = getParaToInt("groupTypeId");
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listGroupType(groupTypeId, PAGE_SIZE, pageNum));
	}

	/**
	 * 按照推荐类型查找商品
	 */
	public void listRecommendType() {
		Integer recommendTypeId = getParaToInt("recommendTypeId");
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listRecommend(recommendTypeId, PAGE_SIZE, pageNum));
	}

	/**
	 * 按照关键字搜索商品
	 */
	public void listKeyword() {
		String keyword = getPara("keyword");
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.listSearch(keyword, PAGE_SIZE, pageNum));
	}

	/**
	 * 无条件搜索商品，默认按照商品sort排序
	 */
	public void list() {
		Integer pageNum = getParaToInt("pageNum", 1);
		if(pageNum == null) {
			pageNum = 1;
		}
		renderJson(Product.dao.list(PAGE_SIZE, pageNum));
	}

	/**
	 * 按照商品规格ID获取购物车商品
	 */
	public void listStandardIds() {
		Integer[] standardIds = getParaValuesToInt("ids");
		if(standardIds != null && standardIds.length > 0) {
			renderJson(Product.dao.listByStandardIds(standardIds));
		} else {
			renderJson(new ArrayList<Product>());
		}
	}

}
