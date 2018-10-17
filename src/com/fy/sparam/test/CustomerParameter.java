package com.fy.sparam.test;

import java.util.Date;

import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.FieldMeta;
import com.fy.sparam.init.anno.InheritMeta;
import com.fy.sparam.init.anno.TableMeta;

/**
 * 客户实体类
 * 
 * @author hongweiquan, linjie
 * @since 4.5.0
 */
@TableMeta(name= "t_customer", alias = "c")
@InheritMeta(fieldName = "user_id", inheritBy = "userID")
public class CustomerParameter extends UserParameter {
	
	/**
	 * 客户创建时间
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	@FieldMeta(name = "create_date")
	public ISearchable<Date> createDate;

//	/**
//	 * 客户在工具商店的别称
//	 * 
//	 * @author linjie
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "alias")
//	public ISearchable<String> alias;
//	
//	/**
//	 * 客户状态标志码
//	 * 
//	 * @author linjie
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "status_flag")
//	public ISearchable<Integer> statusFlag;
//	
//	/**
//	 * 客户客服消息板ID
//	 * 
//	 * @author linjie
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "chatroom_id")
//	public ISearchable<String> customerCareChatroomID;
//	
//	/**
//	 * 客户最后阅读通知的时间
//	 * 
//	 * @author linjie
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "read_inform_last_date")
//	public ISearchable<Date> read_inform_last_date;
}
