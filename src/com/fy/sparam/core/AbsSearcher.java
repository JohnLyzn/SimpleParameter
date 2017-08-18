package com.fy.sparam.core;

import java.util.Collection;

import com.fy.sparam.core.SearchContext.IRelationalable;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.util.StringUtils;

/**
 * 搜索器基类
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <T>　源字段类类型
 * 
 * @author linjie
 * @since 1.0.1
 */
public abstract class AbsSearcher<PT extends AbsParameter<PT, SCT, RT>, SCT, RT, T> 
implements ISearchable<T>, IRelationalable<T>  {

	PT belongParameter;
	ParameterField<PT, SCT, RT> belongParameterField;
	SearchContext<PT, SCT, RT> usingSearchContext;
	
	@Override
	public final <RPT extends AbsParameter<?, ?, ?>> RPT and(RPT param) throws Exception {
		this.onAnd();
		return param;
	}

	@Override
	public final <RPT extends AbsParameter<?, ?, ?>> RPT or(RPT param) throws Exception {
		this.onOr();
		return param;
	}

	@Override
	public final IRelationalable<T> ds(Object... params) throws Exception {
		this.onDelimiterStart(params);
		return this;
	}

	@Override
	public final IRelationalable<T> de(Object... params) throws Exception {
		this.onDelimiterEnd(params);
		return this;
	}

	@Override
	public final IRelationalable<T> eq(T value) throws Exception {
		this.invokeSearchWorker();
		this.onEq(value);
		return this;
	}

	@Override
	public final IRelationalable<T> eq(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onEq(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notEq(T value) throws Exception {
		this.invokeSearchWorker();
		this.onNotEq(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notEq(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onNotEq(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> in(Collection<T> values) throws Exception {
		this.invokeSearchWorker();
		this.onIn(values);
		return this;
	}

	@Override
	public final IRelationalable<T> in(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onIn(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notIn(Collection<T> values) throws Exception {
		this.invokeSearchWorker();
		this.onNotIn(values);
		return this;
	}

	@Override
	public final IRelationalable<T> notIn(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onNotIn(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> between(T from, T to) throws Exception {
		this.invokeSearchWorker();
		this.onBetween(from, to);
		return this;
	}

	@Override
	public final IRelationalable<T> between(ISearchable<?> from, ISearchable<?> to) throws Exception {
		this.invokeSearchWorker();
		this.onBetween(from, to);
		return this;
	}

	@Override
	public final IRelationalable<T> lessThan(T value) throws Exception {
		this.invokeSearchWorker();
		this.onLessThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> lessThan(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onLessThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notLessThan(T value) throws Exception {
		this.invokeSearchWorker();
		this.onNotLessThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notLessThan(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onNotLessThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> greaterThan(T value) throws Exception {
		this.invokeSearchWorker();
		this.onGreaterThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> greaterThan(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onGreaterThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notGreaterThan(T value) throws Exception {
		this.invokeSearchWorker();
		this.onNotGreaterThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notGreaterThan(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onNotGreaterThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> like(String value) throws Exception {
		this.invokeSearchWorker();
		this.onLike(value);
		return this;
	}

	@Override
	public final IRelationalable<T> like(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onLike(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notLike(String value) throws Exception {
		this.invokeSearchWorker();
		this.onNotLike(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notLike(ISearchable<?> searchField) throws Exception {
		this.invokeSearchWorker();
		this.onNotLike(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> isNull() throws Exception {
		this.invokeSearchWorker();
		this.onIsNull();
		return this;
	}

	@Override
	public final IRelationalable<T> isNotNull() throws Exception {
		this.invokeSearchWorker();
		this.onIsNotNull();
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final IRelationalable<T> inChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception {
		this.invokeSearchWorker();
		this.onInChildQuery((PT) childQuery); 
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final IRelationalable<T> notInChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception {
		this.invokeSearchWorker();
		this.onNotInChildQuery((PT) childQuery);
		return this;
	}

	@Override
	public final void markGroupBy(int priority) throws Exception {
		this.invokeSearchWorker();
		ParameterField<PT, SCT, RT> searchParamField = this.getSearchParameterField();
		searchParamField.isGroupBy = true;
		searchParamField.groupByPriority = priority;
	}

	@Override
	public final void markOrderBy(int priority, boolean isAsc) throws Exception {
		this.invokeSearchWorker();
		ParameterField<PT, SCT, RT> searchParamField = this.getSearchParameterField();
		searchParamField.isOrderBy = true;
		searchParamField.orderByPriority = priority;
		searchParamField.isAsc = isAsc;
	}
	
	@Override
	public final void setOutput(boolean isOutput) throws Exception {
		this.belongParameter.setMyFiledOutPut(this, isOutput);
	}
	
	@Override
	public final String getFieldName() throws Exception {
		return this.belongParameterField.fieldName;
	}
	
	@Override
	public final String getFieldPath() throws Exception {
		return this.belongParameterField.fieldPath;
	}
	
	@Override
	public final String getDbTableAliasLocateFieldName() throws Exception {
		return this.belongParameterField.getDbTableAliasLocateFieldName();
	}
	
	@Override
	public final String getDbFieldName() throws Exception {
		return this.belongParameterField.dbFieldName;
	}
	
	@Override
	public final String getDbFieldAlias() throws Exception {
		return this.belongParameterField.dbFieldAlias;
	}
	
	@Override
	public final String getWholeDbFieldName() throws Exception {
		return this.belongParameterField.getWholeDbFieldName();
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public PT getBelongParameter() {
		return belongParameter;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public ParameterField<PT, SCT, RT> getBelongParameterField() {
		return belongParameterField;
	}
	
	/**
	 * 重写toString方法
	 * 
	 * @return SEARCHER-所属搜索参数类名称 : 字段名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	@Override
	public String toString() {
		return StringUtils.concat("SEARCHER-", this.belongParameterField.toString());
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	ParameterField<PT, SCT, RT> getSearchParameterField() {
		return this.belongParameter.paramContext.getSearchParameterField(this.belongParameterField, false);
	}
	
	/**
	 * 发起对字段的查询时进行的处理
	 * <br/> 进行关联冗余减少处理, 即对搜索信息所属的搜索参数触发关联操作
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void invokeSearchWorker() throws Exception {
		// 设置搜索的字段为被搜索
		this.getSearchParameterField().isSearched = true;
		// 如果实际搜索的搜索参数字段所属的搜索参数之前没有进行过关联处理, 这里进行执行
		JoinWorker<PT, SCT, RT> joinWorker = this.getSearchParameterField().belongParameter.usingJoinWorker;
		if(joinWorker != null) {
			joinWorker.doJoinWork();
		}
	} 
	
	/**
	 * 
	 * @param key
	 * @param searchContent
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void addSearchEntry(String key, SCT searchContent) throws Exception {
		// TODO 搜索上下文初始化
		this.usingSearchContext.addSearchEntry(this, key, searchContent);
	}
	
	/**
	 * 
	 * @param key
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void clearSearchEntry(String key) throws Exception {
		// TODO 搜索上下文验证
		this.usingSearchContext.clearSearchEntry(this, key);
	}
	
	/**
	 * 实际的条件Eq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onEq(T value) throws Exception;

	/**
	 * 实际的条件Eq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onEq(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotEq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotEq(T value) throws Exception;
	
	/**
	 * 实际的条件NotEq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotEq(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件In的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onIn(Collection<T> values) throws Exception;
	
	/**
	 * 实际的条件In的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onIn(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotIn的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotIn(Collection<T> values) throws Exception;

	/**
	 * 实际的条件NotIn的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotIn(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件Between的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onBetween(T from, T to) throws Exception;

	/**
	 * 实际的条件Between的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onBetween(ISearchable<?> from, ISearchable<?> to)
			throws Exception;

	/**
	 * 实际的条件LessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onLessThan(T value) throws Exception;

	/**
	 * 实际的条件LessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onLessThan(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotLessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotLessThan(T value) throws Exception;

	/**
	 * 实际的条件NotLessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotLessThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件GreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onGreaterThan(T value) throws Exception;

	/**
	 * 实际的条件GreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onGreaterThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件NotGreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotGreaterThan(T value) throws Exception;

	/**
	 * 实际的条件NotGreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotGreaterThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件Like的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onLike(String value) throws Exception;

	/**
	 * 实际的条件Like的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onLike(ISearchable<?> searchField) throws Exception;
	
	/**
	 * 实际的条件NotLike的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotLike(String value) throws Exception;

	/**
	 * 实际的条件NotLike的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotLike(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件IsNull的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onIsNull() throws Exception;

	/**
	 * 实际的条件IsNotNull的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onIsNotNull() throws Exception;

	/**
	 * 实际的条件InChildQuery的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onInChildQuery(PT childQuery) throws Exception;

	/**
	 * 实际的条件NotInChildQuery的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onNotInChildQuery(PT childQuery) throws Exception;

	/**
	 * 实际的连接And的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onAnd() throws Exception;
	
	/**
	 * 实际的连接Or的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onOr() throws Exception;
	
	/**
	 * 实际的分界符开始实现
	 * 
	 * @param params 可选参数, 表示构建分界符构建方式
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onDelimiterStart(Object... params) throws Exception;
	
	/**
	 * 实际的分界符结束实现
	 * 
	 * @param params 可选参数, 表示构建分界符构建方式
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onDelimiterEnd(Object... params) throws Exception;
	
	/**
	 * 可选重载: 在标记为分组字段时回调
	 * 
	 * @param priority 分组的优先级值
	 * @throws Exception 需要则抛出异常
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onMarkGroupBy(Integer priority) throws Exception {}
	
	/**
	 * 可选重载: 在标记为排序字段时回调
	 * 
	 * @param priority 排序的优先级值
	 * @param isAsc 是否是正向排序, <tt>true</tt>表示升序, <tt>false</tt>表示降序
	 * @throws Exception 需要则抛出异常
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onMarkOrderBy(Integer priority, Boolean isAsc) throws Exception {}
	
	/**
	 * 可选重载: 在设置当前搜索器对应的字段进行输出时回调
	 * 
	 * @param isOutput　是否是输出, <tt>true</tt>表示输出, <tt>false</tt>表示不输出.
	 * @throws Exception　操作失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onSetOutput(boolean isOuput) throws Exception {}
}
