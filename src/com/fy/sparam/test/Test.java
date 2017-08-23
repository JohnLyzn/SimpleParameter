package com.fy.sparam.test;

import com.fy.sparam.product.SqlParameter;

public class Test {

	public static void main(String[] args) throws Exception {
		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
		OrderParameter param1 = SqlParameter.getParameter(OrderParameter.class);
		param.join(param1, null, null, param.customer.wtbUserID, param1.orderNumber);
		param1.customer.wtbAccount.setOutput(true);
		System.out.println(param.build().getSql());
		param1.unJoin();
		System.out.println(param.build().getSql());
		System.out.println(param1.build().getSql());
		param.reset();
		System.out.println(param1.build().getSql());
		// TODO 获取动态关联搜索参数
	}
}
