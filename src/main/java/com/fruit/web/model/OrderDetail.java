package com.fruit.web.model;

import com.fruit.web.model.base.BaseOrderDetail;

import java.util.List;

/*
 * @author ZGC
 * @date Created in 13:58 2018/2/28
 */

@SuppressWarnings("serial")
public class OrderDetail extends BaseOrderDetail<OrderDetail> {
	public static final OrderDetail dao = new OrderDetail().dao();

	public List<OrderDetail> getOrderDetails(String orderId) {
		StringBuffer sb = new StringBuffer("SELECT\n" +
				"\to.num,\n" +
				"\to.product_standard_name,\n" +
				"\to.product_standard_id,\n" +
				"\to.product_id,\n" +
				"\to.product_name,\n" +
				"\tp.img,\n" +
				"\to.sell_price,\n" +
				"\tp.country,\n" +
				"\to.measure_unit\n" +
				"FROM\n" +
				"\tb_order_detail AS o\n" +
				"LEFT JOIN b_product AS p ON o.product_id = p.id\n" +
				"WHERE\n" +
				"\to.order_id = ?");
		List<OrderDetail> orderDetails = OrderDetail.dao.find(sb.toString(), orderId);
		return orderDetails;
	}
}
