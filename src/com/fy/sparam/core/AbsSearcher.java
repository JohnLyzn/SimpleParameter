package com.fy.sparam.core;

import static java.lang.String.format;

import java.util.Collection;
import java.util.List;

import com.fy.sparam.core.ParameterContext.IParameterObj;
import com.fy.sparam.core.SearchContext.IRelationalable;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.core.SearchContext.SearchContentSource;
import com.fy.sparam.util.StringUtils;

/**
 * 搜索器基类
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * @param <T>　源字段类类型
 * 
 * @author linjie
 * @since 1.0.2
 */
public abstract class AbsSearcher<PT extends AbsParameter<PT, SCT, RT>, SCT, RT, T> 
extends SearchContentSource<SCT>
implements IParameterObj, ISearchable<T>, IRelationalable<T>, Cloneable {

	PT belongParameter;
	ParameterField<PT, SCT, RT> belongParameterField;
	boolean isNeedRelationCheck = true;
	String path;
	
	@Override
	public final <RPT extends AbsParameter<?, ?, ?>> RPT and(RPT param) throws Exception {
		if(! this.belongParameter.paramContext.relationalCheckFlag) {
			this.belongParameter.paramContext.relationalCheckFlag = true;
			this.isNeedRelationCheck = false;
			this.onAnd();
			this.isNeedRelationCheck = true;
		}
		return param;
	}

	@Override
	public final <RPT extends AbsParameter<?, ?, ?>> RPT or(RPT param) throws Exception {
		if(! this.belongParameter.paramContext.relationalCheckFlag) {
			this.belongParameter.paramContext.relationalCheckFlag = true;
			this.isNeedRelationCheck = false;
			this.onOr();
			this.isNeedRelationCheck = true;
		}
		return param;
	}

	@Override
	public final IRelationalable<T> ds(Object... params) throws Exception {
		this.isNeedRelationCheck = false;
		this.onDelimiterStart(params);
		this.isNeedRelationCheck = true;
		this.belongParameter.paramContext.delimiterStartCount ++;
		return this;
	}

	@Override
	public final IRelationalable<T> de(Object... params) throws Exception {
		this.isNeedRelationCheck = false;
		this.onDelimiterEnd(params);
		this.isNeedRelationCheck = true;
		this.belongParameter.paramContext.delimiterEndCount ++;
		return this;
	}

	@Override
	public final IRelationalable<T> eq(T value) throws Exception {
		this.preparingDoSearch();
		this.onEq(value);
		return this;
	}

	@Override
	public final IRelationalable<T> eq(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onEq(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notEq(T value) throws Exception {
		this.preparingDoSearch();
		this.onNotEq(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notEq(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onNotEq(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> in(Collection<T> values) throws Exception {
		this.preparingDoSearch();
		this.onIn(values);
		return this;
	}

	@Override
	public final IRelationalable<T> in(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onIn(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notIn(Collection<T> values) throws Exception {
		this.preparingDoSearch();
		this.onNotIn(values);
		return this;
	}

	@Override
	public final IRelationalable<T> notIn(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onNotIn(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> between(T from, T to) throws Exception {
		this.preparingDoSearch();
		this.onBetween(from, to);
		return this;
	}

	@Override
	public final IRelationalable<T> between(ISearchable<?> from, ISearchable<?> to) throws Exception {
		this.preparingDoSearch();
		this.onBetween(from, to);
		return this;
	}

	@Override
	public final IRelationalable<T> lessThan(T value) throws Exception {
		this.preparingDoSearch();
		this.onLessThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> lessThan(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onLessThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notLessThan(T value) throws Exception {
		this.preparingDoSearch();
		this.onNotLessThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notLessThan(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onNotLessThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> greaterThan(T value) throws Exception {
		this.preparingDoSearch();
		this.onGreaterThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> greaterThan(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onGreaterThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notGreaterThan(T value) throws Exception {
		this.preparingDoSearch();
		this.onNotGreaterThan(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notGreaterThan(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onNotGreaterThan(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> like(String value) throws Exception {
		this.preparingDoSearch();
		this.onLike(value);
		return this;
	}

	@Override
	public final IRelationalable<T> like(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onLike(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> notLike(String value) throws Exception {
		this.preparingDoSearch();
		this.onNotLike(value);
		return this;
	}

	@Override
	public final IRelationalable<T> notLike(ISearchable<?> searchField) throws Exception {
		this.preparingDoSearch();
		this.onNotLike(searchField);
		return this;
	}

	@Override
	public final IRelationalable<T> isNull() throws Exception {
		this.preparingDoSearch();
		this.onIsNull();
		return this;
	}

	@Override
	public final IRelationalable<T> isNotNull() throws Exception {
		this.preparingDoSearch();
		this.onIsNotNull();
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final IRelationalable<T> inChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception {
		this.preparingDoSearch();
		this.onInChildQuery((PT) childQuery); 
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final IRelationalable<T> notInChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception {
		this.preparingDoSearch();
		this.onNotInChildQuery((PT) childQuery);
		return this;
	}

	@Override
	public final void cancelSearch() throws Exception {
		// 删除相关的搜索内容
		SearchContext<PT, SCT, RT> usingSearchContext = this.belongParameter.paramContext.getCurrentSearchContext();
		usingSearchContext.removeSearchEntryBySource(this);
		this.belongParameterField.isSearched = false;
		// 判断当前搜索参数包含的搜索参数字段是否都有输出, 如果没有则设置其是否有字段搜索表示
		boolean isAllFieldNotSearched = true;
		for(ParameterField<PT, SCT, RT> paramField : this.belongParameter.myOwnedParameterFields) {
			if(paramField.isSearched) {
				isAllFieldNotSearched = false;
				break;
			}
		}
		// 如果是, 则设置所属搜索参数没有被搜索
		if(isAllFieldNotSearched) {
			this.belongParameter.hasFieldSearched = false;
		}
		// 尝试回滚, 减少多余关联搜索内容
		if(this.belongParameter.usingJoinWorker != null) {
			this.belongParameter.usingJoinWorker.cancelJoinWork(false, true);
		}
	}
	
	@Override
	public final void markGroupBy(int priority) throws Exception {
		ParameterField<PT, SCT, RT> searchParamField = this.preparingDoSearch();
		searchParamField.isGroupBy = true;
		searchParamField.groupByPriority = priority;
	}

	@Override
	public final void markOrderBy(int priority, boolean isAsc) throws Exception {
		ParameterField<PT, SCT, RT> searchParamField = this.preparingDoSearch();
		searchParamField.isOrderBy = true;
		searchParamField.orderByPriority = priority;
		searchParamField.isAsc = isAsc;
	}
	
	@Override
	public final void setOutput(boolean isOutput) throws Exception {
		this.belongParameter.setMyFiledOutPut(this.belongParameterField, isOutput, false);
	}
	
	@Override
	public final String getFieldName() throws Exception {
		return this.belongParameterField.fieldName;
	}
	
	@Override
	public final String getPath() throws Exception {
		return this.path;
	}
	
	@Override
	public final String getDbTableAliasLocateFieldName() throws Exception {
		List<String> results = this.belongParameterField.getDbTableAliasLocateFieldNames();
		return results.get(results.size() - 1);
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
	
	@Override
	@SuppressWarnings("unchecked")
	public final ITransformable<T> getTransformer() throws Exception {
		// 通过搜索器的泛型类型找到对应的搜索字段类型转换器
		ParameterContext<PT, SCT, RT> paramContext = this.belongParameter.paramContext;
		Class<T> searcherTypeclazz = (Class<T>) paramContext.intializor.getSearcherFieldTypeClass(this);
		ITransformable<T> transformer = (ITransformable<T>) paramContext.getFieldTransformer(searcherTypeclazz);
		if(transformer == null) {
			throw new IllegalArgumentException(format(
					"找不到类型为%s对应的类型转换器, 请注册对应类型的转换器(ITransformable)后再获取该类型的搜索器的转换器", 
					searcherTypeclazz));
		}
		return transformer;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final <TT extends ITransformable<T>> TT getTransformer(Class<TT> realTypeClass) throws Exception {
		if(realTypeClass == null) {
			throw new IllegalArgumentException("指定的转换实现类类字节码不能为null");
		}
		// 通过搜索器的泛型类型找到对应的搜索字段类型转换器
		ParameterContext<PT, SCT, RT> paramContext = this.belongParameter.paramContext;
		Class<T> searcherTypeclass = (Class<T>) paramContext.intializor.getSearcherFieldTypeClass(this);
		ITransformable<T> transformer = (ITransformable<T>) paramContext.getFieldTransformer(searcherTypeclass);
		if(transformer == null) {
			throw new IllegalArgumentException(format(
					"找不到类型为%s对应的类型转换器, 请注册对应类型的转换器(ITransformable)后再获取该类型的搜索器的转换器", 
					searcherTypeclass));
		}
		if(! realTypeClass.isInstance(transformer)) {
			throw new IllegalArgumentException(String.format(
					"类型为%s对应的类型转换器的最终实现类不是%s类型或者它的子类", 
					searcherTypeclass));
		}
		return (TT) transformer;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final PT getBelongParameter() {
		return belongParameter;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final ParameterField<PT, SCT, RT> getBelongParameterField() {
		return belongParameterField;
	}
	
	@Override
	public String toString() {
		return StringUtils.concat(super.toString(), " WITH PATH ", this.belongParameterField.path);
	}
	
	/**
	 * 发起对字段的查询时进行的处理
	 * <br/> 进行关联冗余减少处理, 即对搜索信息所属的搜索参数触发关联操作
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final ParameterField<PT, SCT, RT> preparingDoSearch() throws Exception {
		ParameterField<PT, SCT, RT> searchParamField = this.belongParameter.paramContext
				.getIndeedSearchParameterField(this.belongParameterField, null);
		// 设置搜索的字段为被搜索
		searchParamField.isSearched = true;
		searchParamField.belongParameter.hasFieldSearched = true;
		// 如果实际搜索的搜索参数字段所属的搜索参数之前没有进行过关联处理, 这里进行执行
		JoinWorker<PT, SCT, RT> joinWorker = searchParamField.belongParameter.usingJoinWorker;
		if(joinWorker != null) {
			joinWorker.doJoinWork();
		}
		return searchParamField;
	} 
	
	@Override
	protected final void addSearchEntry(String key, SCT searchContent) throws Exception {
		if(this.isNeedRelationCheck) {
			if(! this.belongParameter.paramContext.relationalCheckFlag) { /* 前面添加了搜索器的搜索内容 */
				// 如果没有开启自动追加逻辑关系, 在没有确定搜索内容间逻辑关系情况下则报错
				if(! this.belongParameter.paramContext.isAutoAddRelation) {
					throw new IllegalArgumentException("上一个条件和当前条件没有确定逻辑关系, 请在上一个条件的末尾调用and或or方法,"
							+ " 或使用搜索参数的above.and或or方法确定条件间的逻辑关系!");
				}
				// 开启自动追加逻辑关系
				if(this.belongParameter.paramContext.isAutoAddAnd) {
					this.and(null);
				} else {
					this.or(null);
				}
			}
			// 设置逻辑关系标记为false, 连续两次调用当前方法且无自动追加逻辑关系情况下会报错
			this.belongParameter.paramContext.relationalCheckFlag = false;
		}
		// 添加搜索内容到搜索上下文
		SearchContext<PT, SCT, RT> usingSearchContext = this.belongParameter.paramContext.getCurrentSearchContext();
		usingSearchContext.addSearchEntry(this, key, searchContent);
	}

	@Override
	protected final List<SCT> getSearchEntry(String key) throws Exception {
		SearchContext<PT, SCT, RT> usingSearchContext = this.belongParameter.paramContext.getCurrentSearchContext();
		return usingSearchContext.getSearchEntry(key);
	}
	
	@Override
	protected final void clearSearchEntry(String key) throws Exception {
		SearchContext<PT, SCT, RT> usingSearchContext = this.belongParameter.paramContext.getCurrentSearchContext();
		usingSearchContext.clearSearchEntry(key);
	}
	
	/**
	 * 克隆一个搜索器, 重置相关引用信息
	 * 
	 * @throws CloneNotSupportedException
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected final AbsSearcher<PT, SCT, RT, T> clone() throws CloneNotSupportedException {
		AbsSearcher<PT, SCT, RT, T> cloneSearcher = (AbsSearcher<PT, SCT, RT, T>) super.clone();
		cloneSearcher.belongParameter = null;
		cloneSearcher.belongParameterField = null;
		return cloneSearcher;
	}
	
	/**
	 * 实际的条件Eq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onEq(T value) throws Exception;

	/**
	 * 实际的条件Eq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onEq(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotEq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotEq(T value) throws Exception;
	
	/**
	 * 实际的条件NotEq的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotEq(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件In的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onIn(Collection<T> values) throws Exception;
	
	/**
	 * 实际的条件In的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onIn(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotIn的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotIn(Collection<T> values) throws Exception;

	/**
	 * 实际的条件NotIn的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotIn(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件Between的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onBetween(T from, T to) throws Exception;

	/**
	 * 实际的条件Between的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onBetween(ISearchable<?> from, ISearchable<?> to)
			throws Exception;

	/**
	 * 实际的条件LessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onLessThan(T value) throws Exception;

	/**
	 * 实际的条件LessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onLessThan(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件NotLessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotLessThan(T value) throws Exception;

	/**
	 * 实际的条件NotLessThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotLessThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件GreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onGreaterThan(T value) throws Exception;

	/**
	 * 实际的条件GreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onGreaterThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件NotGreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotGreaterThan(T value) throws Exception;

	/**
	 * 实际的条件NotGreaterThan的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotGreaterThan(ISearchable<?> searchField)
			throws Exception;

	/**
	 * 实际的条件Like的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onLike(String value) throws Exception;

	/**
	 * 实际的条件Like的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onLike(ISearchable<?> searchField) throws Exception;
	
	/**
	 * 实际的条件NotLike的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotLike(String value) throws Exception;

	/**
	 * 实际的条件NotLike的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotLike(ISearchable<?> searchField) throws Exception;

	/**
	 * 实际的条件IsNull的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onIsNull() throws Exception;

	/**
	 * 实际的条件IsNotNull的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onIsNotNull() throws Exception;

	/**
	 * 实际的条件InChildQuery的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onInChildQuery(PT childQuery) throws Exception;

	/**
	 * 实际的条件NotInChildQuery的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onNotInChildQuery(PT childQuery) throws Exception;

	/**
	 * 实际的连接And的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onAnd() throws Exception;
	
	/**
	 * 实际的连接Or的实现
	 * 
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onOr() throws Exception;
	
	/**
	 * 实际的分界符开始实现
	 * 
	 * @param params 可选参数, 表示构建分界符构建方式
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onDelimiterStart(Object... params) throws Exception;
	
	/**
	 * 实际的分界符结束实现
	 * 
	 * @param params 可选参数, 表示构建分界符构建方式
	 * @throws Exception
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onDelimiterEnd(Object... params) throws Exception;
	
	/**
	 * 可选重载: 在标记为分组字段时回调
	 * 
	 * @param priority 分组的优先级值
	 * @throws Exception 需要则抛出异常
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onMarkGroupBy(int priority) throws Exception {}
	
	/**
	 * 可选重载: 在标记为排序字段时回调
	 * 
	 * @param priority 排序的优先级值
	 * @param isAsc 是否是正向排序, <tt>true</tt>表示升序, <tt>false</tt>表示降序
	 * @throws Exception 需要则抛出异常
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onMarkOrderBy(int priority, boolean isAsc) throws Exception {}
	
	/**
	 * 可选重载: 在设置当前搜索器对应的字段进行输出时回调
	 * 
	 * @param isOutput　是否是输出, <tt>true</tt>表示输出, <tt>false</tt>表示不输出.
	 * @throws Exception　操作失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onSetOutput(boolean isOuput) throws Exception {}
}
