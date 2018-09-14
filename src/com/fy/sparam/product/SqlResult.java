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
	private StringBuilder sql;
	
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
	private List<String[]> outputValCorrespondFieldNames;
	
	/**
	 * 获取构建完成的完整sql语句
	 * 
	 * @return 构建完成的完整sql语句, 如果没有完成构建则返回null
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public String getSql() {
		return this.sql.toString();
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
		if(this.vals != null) {
			return this.vals.toArray();
		}
		return null;
	}

	/**
	 * 获取选择输出的列的对应的属性名称列表, 格式为: 表别名.属性名, 顺序与select中的字段顺序一致.
	 * <br/> 注意是搜索参数属性的名称(属性名或成员变量名), 不是数据库字段名称.
	 * <br/> 在BaseDaoImpl中getRelatePropertiesByParam方法使用, 
	 * 	由于查询数据库记录输出的List中是Object[], 位置与select中存放的顺序一致, 所以通过这个顺序一致的List来获取列与实体属性映射关系.
	 * 
	 * @return 选择输出的列的对应的属性名称列表, 不会返回null.
	 * 
	 * @throws IllegalAccessException 如果不是以SELECT_FIELDS模式构建结果且有设置输出字段的情况下获取选择输出的搜索参数字段唯一名称列表则抛出异常
	 *  
	 * @author linjie
	 * @since 4.5.0
	 */
	public List<String[]> getOutputValCorrespondFieldNames() throws IllegalAccessException {
		if(this.outputValCorrespondFieldNames == null || this.outputValCorrespondFieldNames.isEmpty()) {
			throw new IllegalAccessException("只有搜索参数构建模式为SELECT_FIELDS且有设置输出字段才能获取选择输出的搜索参数字段唯一名称列表");
		}
		return this.outputValCorrespondFieldNames;
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
	protected void addSqlPiece(SqlPiece sqlPiece) throws IllegalArgumentException  {
		if(sqlPiece == null) {
			throw new IllegalArgumentException("SQL搜索参数构建结果的sql语句片段不能为null");
		}
		if(this.sql == null) {
			this.sql = new StringBuilder();
		}
		this.sql.append(sqlPiece.getSqlPart());
		this.addPreparedVals(sqlPiece.getVals());
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
	protected void addSqlPieces(Collection<SqlPiece> sqlPieces) throws IllegalArgumentException  {
		if(sqlPieces == null) {
			throw new IllegalArgumentException("SQL搜索参数构建结果的sql语句片段不能为null");
		}
		if(! sqlPieces.isEmpty()) {
			if(this.sql == null) {
				this.sql = new StringBuilder();
			}
			for(SqlPiece sqlPiece : sqlPieces) {
				this.sql.append(sqlPiece.getSqlPart());
				this.addPreparedVals(sqlPiece.getVals());
			}
		}
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
	protected void addPreparedVals(Collection<Object> partVals) {
		if(this.vals == null) {
			this.vals = new LinkedList<Object>();
		}
		if(partVals != null) {
			this.vals.addAll(partVals);
		}
	}

	/**
	 * 设置选择输出的列的对应的属性名称列表, 格式为: 表别名.属性名, 一列可能对应多个属性名称
	 * @param selectedFieldNames 选择输出的列的唯一名称列表
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	protected void setOutputValCorrespondFieldNames(List<String[]> outputValCorrespondFieldNames) {
		this.outputValCorrespondFieldNames = outputValCorrespondFieldNames;
	}
}
