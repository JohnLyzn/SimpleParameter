package com.fy.sparam.test;

import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.FieldMeta;
import com.fy.sparam.init.anno.TableMeta;
import com.fy.sparam.product.SqlParameter;

/**
 * 用户搜索参数
 * 
 * @author hongweiquan, linjie
 * @since 4.5.0
 */
@TableMeta(name = "t_user", alias = "u")
public class UserParameter extends SqlParameter {

	/**
	 * 用户ID
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "user_id")
	public ISearchable<String> userID;
	
	/**
	 * 用户名称
	 * 
	 * @author hongweiquan
	 * @since 4.5.0
	 */
	@FieldMeta(name = "name")
	public ISearchable<String> name;
	
//	/**
//	 * 用户邮箱
//	 * 
//	 * @author hongweiquan
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "email")
//	public ISearchable<String> email;
//	
//	/**
//	 * 用户联系电话
//	 * 
//	 * @author hongweiquan
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "tel")
//	public ISearchable<String> tel;
//	
//	/**
//	 * 吾托帮用户账号
//	 * 
//	 * @author hongweiquan
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "wtb_account")
//	public ISearchable<String> wtbAccount;
//	
//	/**
//	 * 吾托帮用户ID
//	 * 
//	 * @author hongweiquan
//	 * @since 4.5.0
//	 */
//	@FieldMeta(name = "wtb_user_id")
//	public ISearchable<String> wtbUserID;
}
