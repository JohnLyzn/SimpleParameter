package com.fy.sparam.product;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 
 * @author linjie
 * @since 1.0.2
 */
public final class SqlResult {

	/**
	 * 构建完成的完整sql语句
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	private String sql;
	
	/**
	 * 完整sql语句中使用占位符'?'对应的值
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	private List<Object> vals;
	
	/**
	 * 选择输出的搜索参数字段名称列表
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private List<String> selectedFieldNames;
	
	/**
	 * 获取构建完成的完整sql语句
	 * 
	 * @return 构建完成的完整sql语句, 如果没有完成构建则返回null
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public String getSql() {
		return sql;
	}
	
	/**
	 * 获取完整sql语句中使用占位符'?'对应的值的数组形式
	 * <br/> 使用数组形式是为了方便直接传入底层dao使用
	 * 
	 * @return 完整sql语句中使用占位符'?'对应的值的数组形式, 如果没有构建完成则返回null
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public Object[] getVals() {
		if(vals != null) {
			return vals.toArray();
		}
		return null;
	}

	/**
	 * 获取选择输出的搜索参数字段唯一名称列表, 顺序与select中的字段顺序一致. 格式为: 表别名.属性名称
	 * <br/> 注意是搜索参数字段的名称(属性名或成员变量名), 不是数据库字段名称.
	 * <br/> 在BaseDaoImpl中getRelatePropertiesByParam方法使用, 
	 * 	由于查询数据库记录输出的List中是Object[], 位置与select中存放的顺序一致, 所以通过这个顺序一致的List来获取对应列的信息.
	 * 
	 * @return 选择输出的搜索参数字段名称列表, 且永远不会返回null.
	 * 
	 * @throws IllegalAccessException 如果不是以SELECT_FIELDS模式构建结果且有设置输出字段的情况下获取选择输出的搜索参数字段唯一名称列表则抛出异常
	 *  
	 * @author linjie
	 * @since 4.5.0
	 */
	public List<String> getSelectedFieldNames() throws IllegalAccessException {
		if(selectedFieldNames == null || selectedFieldNames.isEmpty()) {
			throw new IllegalAccessException("只有搜索参数构建模式为SELECT_FIELDS且有设置输出字段才能获取选择输出的搜索参数字段唯一名称列表");
		}
		return selectedFieldNames;
	}

	/**
	 * 设置构建完成的完整sql语句
	 * <br/>设置完成表示构建完成
	 * 
	 * @param sql
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	protected void setSql(String sql) throws IllegalArgumentException  {
		if(sql == null) {
			throw new IllegalArgumentException("SQL搜索参数构建结果的sql语句不能为null");
		}
		this.sql = sql;
	}
	
	/**
	 * 添加完整sql语句中使用占位符'?'对应的值
	 * <br/>添加值不表示构建完成
	 * 
	 * @param partVals 从{@link SqlParamEntry}中提取的占位符'?'对应的值, 顺序会与存入顺序保持一致
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	protected void addValsInSql(Collection<Object> partVals) {
		if(vals == null) {
			vals = new LinkedList<Object>();
		}
		if(partVals != null) {
			vals.addAll(partVals);
		}
	}

	/**
	 * 设置选择输出的列的唯一名称列表, 格式为: 表别名.属性名
	 * @param selectedFieldNames 选择输出的列的唯一名称列表
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	protected void setSelectedFieldNames(List<String> selectedFieldNames) {
		this.selectedFieldNames = selectedFieldNames;
	}
}
