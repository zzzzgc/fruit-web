package com.fruit.web.controller;

import com.fruit.web.base.BaseController;
import com.fruit.web.model.*;
import com.jfinal.kit.Kv;
import org.apache.log4j.Logger;

import java.util.List;

public class FruitTypeController extends BaseController {
	private static Logger log = Logger.getLogger(FruitTypeController.class);

	public void get() {
		List<TypeGroup> groups = TypeGroup.dao.find("SELECT id,name FROM b_type_group WHERE status=? ORDER BY sort DESC ", TypeGroup.STATUS_ENABLE);
		List<TypeGroup> types = TypeGroup.dao.find("SELECT t.id,t.group_id,t.name FROM b_type_group tg JOIN b_type t ON (tg.id=t.group_id) WHERE tg.status=? and t.status=? ORDER BY t.sort DESC ",
				Type.STATUS_ENABLE, TypeGroup.STATUS_ENABLE);
		Kv kv = Kv.by("groups", groups)
				.set("types", types);
		renderJson(kv);
	}

}
