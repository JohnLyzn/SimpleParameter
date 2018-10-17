package com.fy.sparam.test;

import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.FieldMeta;
import com.fy.sparam.init.anno.TableMeta;
import com.fy.sparam.product.SqlParameter;

/**
 * 工具搜索参数
 * 
 * @author wangpeng, linjie
 * @since 4.5.0
 */
@TableMeta(name="t_tool", alias="t")
public class ToolParameter extends SqlParameter {

	/**
	 * 工具ID
	 * 
	 * @author wangpeng
	 * @since 4.5.0
	 */
	@FieldMeta(name="tool_id")
	public ISearchable<Long> toolID;
	
	
	/**
	 * 工具名称
	 * 
	 * @author wangpeng
	 * @since 4.5.0
	 */
	@FieldMeta(name = "name")
	public ISearchable<String> name;
	
	/**
	 * 工具描述
	 * 
	 * @author wangpeng
	 * @since 4.5.0
	 */
	@FieldMeta(name = "description")
	public ISearchable<String> description;
}
