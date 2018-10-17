package com.fy.sparam.test;

import java.util.Arrays;

import com.fy.sparam.product.SqlParameter;
import com.fy.sparam.product.SqlParameter.BuildMode;
import com.fy.sparam.product.SqlResult;

public class Test {

	public static void main(String[] args) throws Exception {
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		OrderParameter param1 = SqlParameter.getParameter(OrderParameter.class);
//		param.join(param1, null, null, param.orderID, param1.orderID);
//		System.out.println(param.orderID.getDbTableAliasLocateFieldName());
//		param1.customer.wtbAccount.setOutput(true);
//		System.out.println(param.build(BuildMode.SELECT_FIELDS).getSql());
//		param.getDynamicJoinedParameters("customer.wtbUserID");
//		param1.unJoin();
//		param.build().getSql();
//		System.out.println(param1.build().getSql());
//		param.reset();
//		param.createDate.isNotNull();
//		System.out.println(param.build().getSql());
		
		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
		OrderParameter param1 = SqlParameter.getParameter(OrderParameter.class);
		param.join(param1, null, null, param.orderID, param1.orderID,
				"{orderNumber}:eq(1000)");
		param1.orderNumber.setOutput(true);
		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
		System.out.println(result.getSql());
		System.out.println(Arrays.asList(result.getVals()));
//		List<String[]> foo = result.getSelectedFieldNames();
		
//		CustomerParameter param = SqlParameter.getParameter(CustomerParameter.class);
//		param.ds(param).createDate.eq(new Date()).andDs(param).name.like("123").de().de();
//		SqlResult result = param.build();
//		System.out.println(result.getSql());
		
//		CustomerParameter param = SqlParameter.getParameter(CustomerParameter.class);
//		param.setAllMyFieldOutput(true);
//		param.userID.eq("1111");
//		param.userID.setOutput(true);
//		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
//		System.out.println(result.getSql());
//		for(String[] fieldNameList : result.getOutputValCorrespondFieldNames()) {
//			for(String fieldName : fieldNameList) {
//				System.out.print(fieldName);
//				System.out.print(", ");
//			}
//			System.out.println();
//		}
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		param.customer.userID.setOutput(true);
//		param.tool.toolID.setOutput(true);
//		param.tool.name.setOutput(true);
//		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
//		System.out.println(result.getSql());
//		for(String[] fieldNameList : result.getOutputValCorrespondFieldNames()) {
//			for(String fieldName : fieldNameList) {
//				System.out.print(fieldName);
//				System.out.print(", ");
//			}
//			System.out.println();
//		}
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		param.enableAutoRelation(true);
//		param.orderID.like("%,100,%");
//		param.orderID.in(Arrays.asList(null, "124"));
//		param.orderID.markGroupBy(1);
//		param.addExtraGroupBy("GROUP BY abc ");
//		SqlResult result = param.build(BuildMode.SELECT_ENTITIES);
//		System.out.println(result.getSql());
//		for(Object obj : result.getVals()) {
//			System.out.println(obj);
//		}
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		System.out.println(param.customer.userID.getDbTableAliasLocateFieldName());
//		System.out.println(param.customer.userID.getWholeDbFieldName());
//		param.customer.userID.setOutput(true);
//		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
//		for(String[] strs : result.getSelectedFieldNames()) {
//			for(String str : strs) {
//				System.out.println(str);
//			}
//		}
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		OrderParameter param1 = SqlParameter.getParameter(OrderParameter.class);
//		param.join(param1, null, null, param.customer.userID, param1.orderID);
//		param1.orderID.setOutput(true);
//		param1.createDate.setOutput(true);
//		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
//		for(String[] strs : result.getSelectedFieldNames()) {
//			for(String str : strs) {
//				System.out.print(" ");
//				System.out.print(str);
//			}
//			System.out.println();
//		}
//		System.out.println(result.getSql());
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		param.createDate.setOutput(true);
//		param.customer.name.eq("111");
//		System.out.println(param.build(BuildMode.SELECT_FIELDS).getSql());
//		param.setAllFieldOutput(false);
//		param.totalCost.cancelSearch();
//		param.createDate.setOutput(true);
//		param.customer.name.cancelSearch();
//		System.out.println(param.build(BuildMode.SELECT_FIELDS).getSql());
		
//		param.customer.name.setOutput(true);
//		ManagerParameter param = SqlParameter.getParameter(ManagerParameter.class);
//		System.out.println(param.managerID.getPath());
//		param.customer.name.eq("123");
//		param.customer.name.setOutput(false);
		
//		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		param.customer.userID.setOutput(true);
//		param.customer.userID.eq("123");
//		System.out.println(param.customer.name.getDbTableAliasLocateFieldName());
//		System.out.println(param.customer.userID.getDbTableAliasLocateFieldName());
//		SqlResult result = param.build(BuildMode.SELECT_FIELDS);
//		System.out.println(result.getSql());
//		System.out.println(result.getSelectedFieldNames());
		
//		param.setAllFieldOutput(false);
//		System.out.println(param.build().getSql());
//		long start = System.nanoTime();
//		OrderParameter param1 = (OrderParameter) param.clone();
//		System.out.println(param1.build().getSql());
//		System.out.println(param.build().getSql());
//		param.reset();
//		System.out.println(param.build().getSql());
//		System.out.println(System.nanoTime() - start);
		
//		CustomerParameter cParam1 = SqlParameter.getParameter(CustomerParameter.class);
//		long start = System.currentTimeMillis();
//		long start1 = System.nanoTime();
//		for(int i = 0; i < 50; i ++) {
//			CustomerParameter cParam = SqlParameter.getParameter(CustomerParameter.class);
//			cParam.email.eq("1");
//			SqlMarker marker = new SqlMarker();
//			marker.markUpdate(cParam.name, "linjie");
//			System.out.println(cParam.build(BuildMode.UPDATE, marker).getSql());
//		}
//		System.out.println(System.currentTimeMillis() - start);
//		System.out.println(System.nanoTime() - start1);
//		System.out.println(cParam1.build().getSql());
	}
}
