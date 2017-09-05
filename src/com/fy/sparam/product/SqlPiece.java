package com.fy.sparam.product;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 
 * 
 * @author linjie
 * @since 1.0.2
 */
public final class SqlPiece {

	/**
	 * sql语句片段
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private String sqlPart;
	
	/**
	 * sql语句片段中使用占位符'?'对应的值
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private List<Object> vals;

	/**
	 * 构造函数, 指定sql语句以及占位符值(以可变长参数形式)
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public SqlPiece(String sqlPart, Object... vals) {
		if(sqlPart == null || sqlPart.isEmpty()) {
			throw new IllegalArgumentException("sql语句不能为null");
		}
		this.sqlPart = sqlPart;
		if(vals != null && vals.length != 0) {
			this.vals = new LinkedList<Object>();
			this.vals.addAll(Arrays.asList(vals));
		}
	}
	
	/**
	 * 构造函数, 指定sql片段语句以及占位符值(以列表形式)
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public SqlPiece(String sqlPart, Collection<Object> vals) {
		if(sqlPart == null || sqlPart.isEmpty()) {
			throw new IllegalArgumentException("sql语句不能为null");
		}
		this.sqlPart = sqlPart;
		if(this.vals == null) {
			this.vals = new LinkedList<Object>();
		}
		this.vals.addAll(vals);
	}
	
	/**
	 * 获取sql片段语句
	 * 
	 * @return sql片段语句
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public String getSqlPart() {
		return sqlPart;
	}

	/**
	 * 获取sql语句片段中使用占位符'?'对应的值的列表
	 * 
	 * @return sql语句片段中使用占位符'?'对应的值的列表
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public List<Object> getVals() {
		return vals;
	}
}
