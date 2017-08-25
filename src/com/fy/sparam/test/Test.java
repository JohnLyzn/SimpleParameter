package com.fy.sparam.test;

import com.fy.sparam.product.SqlParameter;

public class Test {

	public static void main(String[] args) throws Exception {
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		OrderParameter param1 = SqlParameter.getParameter(OrderParameter.class);
//		param.join(param1, null, null, param.orderNumber, param1.customer.wtbUserID);
//		param1.customer.wtbAccount.setOutput(true);
//		param.build().getSql();
//		param.getDynamicJoinedParameter("customer.wtbUserID");
//		param1.unJoin();
//		param.build().getSql();
//		param1.build().getSql();
//		param.reset();
//		param1.build().getSql();
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		param.setAllFieldOutput(true);
//		System.out.println(param.build().getSql());
//		long start = System.nanoTime();
//		OrderParameter param1 = (OrderParameter) param.clone();
//		System.out.println(param1.build().getSql());
//		System.out.println(param.build().getSql());
//		param.reset();
//		System.out.println(param.build().getSql());
//		System.out.println(System.nanoTime() - start);
		// TODO 所有注释
		
		CustomerParameter cParam = SqlParameter.getParameter(CustomerParameter.class);
//		CustomerParameter cParam1 = (CustomerParameter) cParam.clone();
		cParam.email.eq("1");
		System.out.println(cParam.build().getSql());
	}
}
