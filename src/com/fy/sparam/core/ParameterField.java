package com.fy.sparam.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fy.sparam.core.ParameterContext.IParameterObj;
import com.fy.sparam.util.StringUtils;

/**
 * 搜索参数字段
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * 
 * @author lyzn
 * @since 1.0.2
 */
public final class ParameterField<PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
implements IParameterObj, Cloneable {

	/**
	 * 字段查询名称生成策略
	 * 
	 * @param <PT>　搜索参数类类型
	 * @param <SCT>　搜索内容类类型
	 * @param <RT>　搜索结果类类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public interface IFieldQueryNameGenerateStrategy<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {
		
		/**
		 * 生成字段查询名称
		 * 
		 * @param param 定位的搜索参数, 不一定是当前字段所属的
		 * @param paramField 目标搜索参数字段
		 * @return 生成的字段查询名称, 用于添加到查询中
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		String generate(PT param, ParameterField<PT, SCT, RT> paramField);
	}
	
	/**
	 * 属性名称, 用于声明path等
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	String fieldName;
	
	/**
	 * 查询字段名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	String queryFieldName;
	
	/**
	 * 查询字段的别名, 可能没有
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	String queryFieldAlias;

	/**
	 * 是否是排序字段
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	boolean isOrderBy;

	/**
	 * 排序字段是否为正序, 否则为倒序
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	boolean isAsc;

	/**
	 * 排序的优先级
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	int orderByPriority;

	/**
	 * 是否是分组字段
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	boolean isGroupBy;

	/**
	 * 分组优先级
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	int groupByPriority;
	
	/**
	 * 当前字段是否被作为搜索内容
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	boolean isSearched;
	
	/**
	 * 是否为输出字段
	 * 
	 * @author linjie
	 * @since 1.0.2
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
	 * @since 1.0.2
	 */
	Map<String, Object> extraInfo;

	/**
	 * 字段相对于根搜索参数的字段路径
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	String path;
	
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
	 * @since 1.0.2
	 */
	boolean isMappedFromField;
	
	/**
	 * 代表操作字段列表
	 * <br/> 与{@link ParameterContext#getIndeedRepresentParamFields}配合, 目前只有设置输出这种代表操作会用到这个.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	Set<ParameterField<PT, SCT, RT>> representOptFields;
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getQueryFieldName() {
		return queryFieldName;
	}

	public void setQueryFieldName(String queryFieldName) {
		this.queryFieldName = queryFieldName;
	}

	public String getQueryFieldAlias() {
		return queryFieldAlias;
	}

	public void setQueryFieldAlias(String queryFieldAlias) {
		this.queryFieldAlias = queryFieldAlias;
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
	 * 获取当前搜索参数字段在搜索参数树中的路径
	 * 
	 * @return 当前搜索参数字段在搜索参数树中的路径
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@Override
	public String getPath() {
		return this.path;
	}
	
	/**
	 * 当前搜索参数字段是否被搜索了
	 * 
	 * @return 当前搜索参数字段是否被搜索了的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isSearched() {
		return isSearched;
	}

	/**
	 * 当前搜索参数字段是否被输出了
	 * 
	 * @return 当前搜索参数字段是否被输出了的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isOutput() {
		return isOutput;
	}

	/**
	 * 获取搜索参数字段所属的搜索参数
	 * @return 所属的搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final PT getBelongParameter() {
		return this.belongParameter;
	}
	
	/**
	 * 获取经过表别名前缀处理的完全数据库列名
	 * <br/> 格式为表别名前缀.数据库列名称, 例如: 表别名为user u, 字段user_id, 则输出u.user_id.
	 * <br/> 不会因为关联链导致可能有多个, 因为(设置输出, 触发关联)总是选择最短关联路径, 输出是绝对的.
	 * <br/> 可能一个对应多个经过表别名前缀处理的当前搜索范围中唯一属性名
	 * 
	 * @return 经过表别名前缀处理的完全数据库列名. 
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	public final String getContextUniqueFieldName(IFieldQueryNameGenerateStrategy<PT, SCT, RT> strategy) {
		// 拼接经过的搜索参数的表别名定位字段名称
		return strategy.generate(this.belongParameter, this);
	}
	
	/**
	 * 获取对应的搜索参数字段经过的关联路径上的当前搜索范围中唯一属性名
	 * <br/> 格式为表别名前缀(如果经过多个字段).当前搜索器所在搜索参数中的属性名称
	 * <br/> 被关联的搜索参数字段使用实际代表操作的字段的唯一属性名, 且把途径的所有搜索参数的表别名列出.
	 * <br/> 继承关联搜索参数的关联搜索参数字段只会使用最终子类搜索参数的表别名作为前缀.
	 * <br/> 原则上即: 该字段属于哪个搜索参数就是对应的表别名.
	 * <br/> 由于关联链导致可能有多个.
	 * <br/> 从关联链的头到尾的顺序返回结果.
	 * 
	 * @return 经过表别名前缀处理的唯一属性名集合, 至少包含一个值.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final List<String> getPassedLocateFieldNames(IFieldQueryNameGenerateStrategy<PT, SCT, RT> strategy) {
		// 如果是关联头端字段, 则找到实际代表操作的字段, 使用其所属搜索参数的表别名
		List<ParameterField<PT, SCT, RT>> passParamFields = new LinkedList<ParameterField<PT, SCT, RT>>();
		if(this.isMappedFromField) {
			this.belongParameter.paramContext.getIndeedRepresentParamFields(this, passParamFields);
		} else {
			this.belongParameter.paramContext.getIndeedSearchParameterField(this, passParamFields);
			// 从被关联到关联起点, 为了和getIndeedRepresentParamField的顺序对应起来, 故进行反转
			Collections.reverse(passParamFields);
		}
		// 加入经过的搜索参数的表别名定位字段名称, 固定顺序且去重
		Set<String> results = new LinkedHashSet<String>(passParamFields.size());
		for(ParameterField<PT, SCT, RT> passParamField : passParamFields) {
			PT aliasParam = this.belongParameter.paramContext
					.getInheritEndParameter(passParamField.belongParameter);
			results.add(strategy.generate(aliasParam, passParamField));
		}
		return new ArrayList<String>(results);
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
	
	@Override
	public String toString() {
		return StringUtils.concat(super.toString(), " WITH PATH ", this.path);
	}
	
	/**
	 * 重置搜索参数字段
	 * 
	 * @throws Exception 重置失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
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
		// 清空被代表字段, 如果有
		if(this.representOptFields != null) {
			this.representOptFields.clear();
			this.representOptFields = null;
		}
	}
	
	/**
	 * 克隆一个搜索参数字段, 重置相关引用信息
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ParameterField<PT, SCT, RT> clone() throws CloneNotSupportedException {
		ParameterField<PT, SCT, RT> cloneParamField = (ParameterField<PT, SCT, RT>) super.clone();
		/* 重用字段信息 */
		cloneParamField.isOrderBy = false;
		cloneParamField.isAsc = false;
		cloneParamField.orderByPriority = 0;
		cloneParamField.isGroupBy = false;
		cloneParamField.groupByPriority = 0;
		cloneParamField.isOutput = false;
		cloneParamField.isSearched = false;
		cloneParamField.usingSearcher = null;
		cloneParamField.belongParameter = null;
		cloneParamField.isMappedFromField = false;
		cloneParamField.representOptFields = null;
		if(this.extraInfo != null && ! this.extraInfo.isEmpty()) {
			cloneParamField.extraInfo = new HashMap<String, Object>();
			cloneParamField.extraInfo.putAll(this.extraInfo);
		}
		return cloneParamField;
	}
}
