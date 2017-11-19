package com.fruit.web.controller;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.Banner;
import org.apache.log4j.Logger;

public class BannerController extends BaseController {
	private static Logger log = Logger.getLogger(BannerController.class);

	public void groupItem(String groupKey) {
		renderJson(Banner.dao.findByGroupKey(groupKey, "name,img_url,click_url"));
	}

}
