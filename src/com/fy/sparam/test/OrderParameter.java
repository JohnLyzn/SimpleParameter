package com.fy.sparam.test;


import java.math.BigDecimal;
import java.util.Date;

import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.FieldMeta;
import com.fy.sparam.init.anno.JoinParam;
import com.fy.sparam.init.anno.TableMeta;
import com.fy.sparam.product.SqlParameter;

/**
 * 订单搜索参数
 * 
 * @author hongweiquan
 * @since 4.5.0
 */
@TableMeta(name = "t_order", alias = "o")
public class OrderParameter extends SqlParameter {

	/**
	 * 订单ID
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "order_id")
	public ISearchable<String> orderID;
	
	/**
	 * 订单编号
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "order_number")
	public ISearchable<String> orderNumber;
	
	/**
	 * 总花费
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "total_cost")
	public ISearchable<BigDecimal> totalCost;
	
	/**
	 * 订单所属的客户的信息
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "customer_id")
	@JoinParam(mappedBy = "userID")
	public CustomerParameter customer;
 	
	/**
	 * 订单信息创建时间
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "create_date")
	public ISearchable<Date> createDate;
	
	/**
	 * 订单信息更新时间
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "update_date")
	public ISearchable<Date> updateDate;
	
}
