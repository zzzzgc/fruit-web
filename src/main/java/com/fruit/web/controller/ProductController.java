package com.fruit.web.controller;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Product;
import com.fruit.web.model.ProductImg;
import com.fruit.web.model.ProductMarket;
import com.fruit.web.model.ProductStandard;
import org.apache.log4j.Logger;

public class ProductController extends BaseController {
	private static Logger log = Logger.getLogger(ProductController.class);

	public void get(int productId) {
		Product product = Product.dao.findById(productId);
		if(product == null) {
			renderError(404);
			return;
		}
		product.put("product_standard", ProductStandard.dao.listProductStandard(productId, "id,name,sub_title,original_price,sell_price,buy_start_time,buy_end_time,stock,is_default,update_time"));
		product.put("product_img", ProductImg.dao.getImgs(productId, ProductImg.TYPE_PRODUCT));
		product.put("market", ProductMarket.dao.getMarket(productId));
		product.put("market_img", ProductImg.dao.getImgs(productId, ProductImg.TYPE_MARKET));
		renderJson(product);
	}

	public void list() {

	}

}
