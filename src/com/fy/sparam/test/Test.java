package com.fy.sparam.test;

import com.fy.sparam.core.AbsSearcher;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.product.SqlParameter;
import com.fy.sparam.product.SqlSearcher;

public class Test {

	public static void main(String[] args) throws Exception {
		OrderParameter param = SqlParameter.getParameter(OrderParameter.class);
//		System.out.println(param.customer.userID.getDbTableAliasLocateFieldName());
//		System.out.println(param.createDate.getDbTableAliasLocateFieldName());
		param.test();
		
	}

//	ITransformable<String> a = new ITransformable<String>() {
//
//		@Override
//		public String strToTarget(String str) throws Exception {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public String targetToStr(String obj) throws Exception {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//	};
}
