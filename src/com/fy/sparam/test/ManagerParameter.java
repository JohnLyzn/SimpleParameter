package com.fy.sparam.test;

import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.FieldMeta;
import com.fy.sparam.init.anno.JoinParam;
import com.fy.sparam.init.anno.TableMeta;
import com.fy.sparam.product.SqlParameter;

@TableMeta(name = "t_a", alias = "a")
public class ManagerParameter extends SqlParameter {

	@FieldMeta(name = "order_id")
	@JoinParam(mappedBy = "orderID")
	public OrderParameter order;
	
	@FieldMeta(name = "manager_id")
	public ISearchable<String> managerID;
}
