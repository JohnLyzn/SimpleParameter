package com.fy.sparam.core;

import java.util.HashMap;
import java.util.Map;

import com.fy.sparam.util.StringUtils;

/**
 * 搜索参数字段
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * 
 * @author lyzn
 * @since 1.0.1
 */
public final class ParameterField<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

	/**
	 * 字段名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	String fieldName;
	
	/**
	 * 字段相对于根搜索参数的字段路径
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	String fieldPath;
	
	/**
	 * 数据库属性名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	String dbFieldName;
	
	/**
	 * 数据库属性的别名, 可能没有
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	String dbFieldAlias;

	/**
	 * 是否是排序字段
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isOrderBy;

	/**
	 * 排序字段是否为正序, 否则为倒序
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isAsc;

	/**
	 * 排序的优先级
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	int orderByPriority;

	/**
	 * 是否是分组字段
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isGroupBy;

	/**
	 * 分组优先级
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	int groupByPriority;
	
	/**
	 * 当前字段是否被作为搜索内容
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isSearched;
	
	/**
	 * 是否为输出字段
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isOutput;
	
	/**
	 * 所属的搜索参数
	 * <br/> 字段在哪就是哪个搜索参数的
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	PT belongParameter;
	
	/**
	 * 可以自定义的额外的信息存储容器器
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	Map<String, Object> extraInfo;

	/**
	 * 使用的搜索器
	 * <br/> 如果是被关联字段, 则使用的搜索器必为null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	AbsSearcher<PT, SCT, RT, ?> usingSearcher;

	/**
	 * 是否是被关联字段
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isMappedFromField;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getDbFieldName() {
		return dbFieldName;
	}

	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}

	public String getDbFieldAlias() {
		return dbFieldAlias;
	}

	public void setDbFieldAlias(String dbFieldAlias) {
		this.dbFieldAlias = dbFieldAlias;
	}

	public boolean isOrderBy() {
		return isOrderBy;
	}

	public void setOrderBy(boolean isOrderBy) {
		this.isOrderBy = isOrderBy;
	}

	public boolean isAsc() {
		return isAsc;
	}

	public void setAsc(boolean isAsc) {
		this.isAsc = isAsc;
	}

	public Integer getOrderByPriority() {
		return orderByPriority;
	}

	public void setOrderByPriority(Integer orderByPriority) {
		this.orderByPriority = orderByPriority;
	}

	public boolean isGroupBy() {
		return isGroupBy;
	}

	public void setGroupBy(boolean isGroupBy) {
		this.isGroupBy = isGroupBy;
	}

	public Integer getGroupByPriority() {
		return groupByPriority;
	}

	public void setGroupByPriority(Integer groupByPriority) {
		this.groupByPriority = groupByPriority;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final String getFieldPath() {
		return fieldPath;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isSearched() {
		return isSearched;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isOutput() {
		return isOutput;
	}

	/**
	 * 获取经过表别名前缀处理的完全数据库列名
	 * <br/> 格式为表别名前缀.数据库列名称
	 * 
	 * @return 经过表别名前缀处理的完全数据库列名
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	public final String getWholeDbFieldName() {
		return StringUtils.concat(this.belongParameter.tableAlias, ParameterContext.PATH_SPERATOR, this.dbFieldName);
	}
	
	/**
	 * 获取经过表别名前缀处理的定位属性名(唯一的范围为当前搜索参数树, 包括动态关联的部分)
	 * <br/> 格式为表别名前缀.当前搜索参数字段所在搜索参数中的属性名称
	 * <br/> 被关联的搜索参数字段使用实际代表操作的字段的唯一属性名.
	 * <br/> 继承关联搜索参数的关联搜索参数字段只会使用最终子类搜索参数的表别名作为前缀.
	 * 
	 * @return 经过表别名前缀处理的唯一属性名
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	public final String getDbTableAliasLocateFieldName() {
		AbsParameter<PT, SCT, RT> targetParam = this.belongParameter;
		ParameterField<PT, SCT, RT> targetParamField = this;
		// 如果是被关联字段, 则找到实际代表操作的字段, 使用其所属搜索参数的表别名
		if(this.isMappedFromField) {
			targetParamField = this.belongParameter.paramContext.getIndeedRepresentParamField(this);
			targetParam = targetParamField.belongParameter;
		}
		// 如果是继承搜索参数, 需要使用最终子类搜索参数的表别名
		while(targetParam.isInheritJoinParameter()) {
			targetParam = targetParam.usingJoinWorker.mappedFromParam;
		}
		return StringUtils.concat(targetParam.tableAlias, ParameterContext.PATH_SPERATOR, targetParamField.fieldName);
	}
	
	/**
	 * 添加额外的数据
	 * 
	 * @param key 键, 不能为null
	 * @param value 值
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final void addExtra(String key, Object value) {
		if(this.extraInfo == null) {
			this.extraInfo = new HashMap<String, Object>();
		}
		this.extraInfo.put(key, value);
	}
	
	/**
	 * 获取额外的数据
	 * 
	 * @param key 键
	 * @return 值
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public final Object getExtra(String key) {
		if(this.extraInfo != null) {
			return this.extraInfo.get(key);
		}
		return null;
	}
	
	/**
	 * 重写toString方法
	 * 
	 * @return 所属搜索参数类名称 : 字段名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	@Override
	public String toString() {
		return StringUtils.concat(this.belongParameter.getClass().getSimpleName(), ":", this.fieldName);
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void reset() throws Exception {
		// 重置所有标志位相关的
		this.isOrderBy = false;
		this.isAsc = false;
		this.orderByPriority = 0;
		this.isGroupBy = false;
		this.groupByPriority = 0;
		this.isOutput = false;
		this.isSearched = false;
		// 还原相关的搜索内容, 如果有
		if(this.usingSearcher != null) {
			this.belongParameter.paramContext.getCurrentSearchContext()
				.removeSearchEntryBySource(this.usingSearcher);
		}
	}
}
