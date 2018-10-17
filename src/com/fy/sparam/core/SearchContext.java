package com.fy.sparam.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 搜索上下文
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * 
 * @author linjie
 * @since 1.0.2
 */
public final class SearchContext<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

	/**
	 * 搜索操作接口
	 * 
	 * @param <T> 源字段的类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public interface ISearchable<T> {

		/**
		 * 当前字段等于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> eq(T value) throws Exception;
		
		/**
		 * 当前字段等于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> eq(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段不等于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notEq(T value) throws Exception;
		
		/**
		 * 当前字段不等于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notEq(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值在某个集合中
		 * 
		 * @param values 集合
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> in(Collection<T> values) throws Exception;
		
		/**
		 * 当前字段的值在某个集合中
		 * <br/> 集合的值是另一个当前搜索参数可管理的字段在数据库中的值, 一般是数组字符串.
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> in(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不在某个集合中
		 * 
		 * @param values 集合
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notIn(Collection<T> values) throws Exception;
		
		/**
		 * 当前字段的值在某个集合中
		 * <br/> 集合的值是另一个当前搜索参数可管理的字段在数据库中的值, 一般是数组字符串.
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notIn(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值在某个范围中
		 * 
		 * @param from 范围起点
		 * @param to 范围终点
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> between(T from, T to) throws Exception;
		
		/**
		 * 当前字段的值在某个范围中
		 * 
		 * @param from 范围起点, 是另一个当前搜索参数可管理的字段对应的数据库中的值, 不能为null.
		 * @param to 范围终点, 是另一个当前搜索参数可管理的字段对应的数据库中的值, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> between(ISearchable<?> from, ISearchable<?> to) throws Exception;

		/**
		 * 当前字段的值小于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> lessThan(T value) throws Exception;
		
		/**
		 * 当前字段小于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> lessThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不小于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notLessThan(T value) throws Exception;
		
		/**
		 * 当前字段不小于(大于或等于)另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notLessThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值大于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> greaterThan(T value) throws Exception;

		/**
		 * 当前字段大于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> greaterThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不大于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notGreaterThan(T value) throws Exception;

		/**
		 * 当前字段不大于(小于或等于)另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notGreaterThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值模糊匹配上某值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> like(String value) throws Exception;
		
		/**
		 * 当前字段模糊匹配上另一个当前搜索参数可管理的字段在数据库中的值
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> like(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不模糊匹配上某值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notLike(String value) throws Exception;
		
		/**
		 * 当前字段不模糊匹配上另一个当前搜索参数可管理的字段在数据库中的值
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notLike(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值为Null
		 * 
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> isNull() throws Exception;
		
		/**
		 * 当前字段的值不为null
		 * 
		 * @throws Exception　操作失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> isNotNull() throws Exception;

		/**
		 * 当前字段的值在子查询的结果集合中
		 * 
		 * @param childQuery 子查询的搜索参数
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> inChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception;

		/**
		 * 当前字段的值不在子查询的结果集合中
		 * 
		 * @param childQuery 子查询的搜索参数
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		IRelationalable<T> notInChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception;

		/**
		 * 取消对该字段的所有搜索内容
		 * <br/> 不包括标记型的操作
		 * 
		 * @throws Exception 操作失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void cancelSearch() throws Exception;
		
		/**
		 * 标记当前字段进行分组
		 * 
		 * @param priority 优先级值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		void markGroupBy(int priority) throws Exception;

		/**
		 * 标记当前字段进行排序
		 * 
		 * @param priority 优先级值
		 * @param isAsc 是否是正序, 否为倒序
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		void markOrderBy(int priority, boolean isAsc) throws Exception;
		
		/**
		 * 设置当前搜索器对应的字段进行输出
		 * 
		 * @param isOutput　是否是输出, <tt>true</tt>表示输出, <tt>false</tt>表示不输出.
		 * @throws Exception　操作失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void setOutput(boolean isOutput) throws Exception;
		
		/**
		 * 获取对应的搜索参数字段在搜索参数中的属性名称
		 * @return 对应的搜索参数字段在搜索参数中的属性名称
		 * @throws Exception 获取失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		String getFieldName() throws Exception;
		
		/**
		 * 获取搜索器在搜索参数树中的属性路径
		 * 
		 * @return 搜索器在搜索参数树中的属性路径
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		String getPath() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段配置对应的查询字段名称
		 * @return 对应的搜索参数字段配置对应的查询字段名称
		 * @throws Exception 获取失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		String getQueryFieldName() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段配置对应的查询字段别名
		 * @return 对应的搜索参数字段配置对应的查询字段别名
		 * @throws Exception
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		String getQueryFieldAlias() throws Exception;
		
		/**
		 * 获取搜索字段的类型转换器
		 * 
		 * @return 对应的搜索字段的类型转换器, 如果不存在对应类型的转换器则返回null
		 * @throws Exception 获取失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		ITransformable<T> getTransformer() throws Exception;
		
		/**
		 * 获取搜索字段的类型转换器, 可以指定实际类型转换器的实现类(自定义转换类型)
		 * 
		 * @param realTypeClass 实际的类型转换器的实现类的字节码, 不能为null
		 * @return 对应的搜索字段的类型转换器, 且为传入的类字节码类型, 如果不存在对应类型的转换器则返回null
		 * @throws Exception 获取失败或存在的类型转换器实现类不是指定的类类型则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		<TT extends ITransformable<T>> TT getTransformer(Class<TT> realTypeClass) throws Exception;
	}
	
	/**
	 * 关系连接操作接口
	 * 
	 * @param <T>　源字段类类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public interface IRelationalable<T> {
		
		/**
		 * 连接两个条件, 表示and, 可以根据需要重定向到关联的搜索参数的属性
		 * <br/>传入的搜索参数限定了指定哪个搜索参数进行接下来的操作, 请保持使用同一范围(一个入口, 可能多个关联)的搜索参数, 
		 * 跨入口搜索参数使用可能会导致错误的结果.
		 * 
		 * @param param 要求进行连接的搜索参数实例, 可以为null, 为null返回的结果也是null
		 * @return 传入的搜索参数的实例, 形成链型调用
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		<PT extends AbsParameter<?, ?, ?>> PT and(PT param) throws Exception;
		
		/**
		 * 连接两个条件, 表示or, 可以根据需要重定向到关联的搜索参数的属性
		 * <br/>传入的搜索参数限定了指定哪个搜索参数进行接下来的操作, 请保持使用同一范围(一个入口, 可能多个关联)的搜索参数, 
		 * 跨入口搜索参数使用可能会导致错误的结果.
		 * 
		 * @param param 要求进行连接的搜索参数实例, 可以为null, 为null返回的结果也是null
		 * @return 传入的搜索参数的实例, 形成链型调用
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		 <PT extends AbsParameter<?, ?, ?>> PT or(PT param) throws Exception;
		 
		 /**
		  * 分割符开始, 比如'('等等, 由搜索器实现类自定义
		  * <br/> 裸插入开始分隔符, 用于连续出现的情况, 如: ((( ...
		  * <br/> 必须与结束分隔符成对出现
		  * 
		  * @param args 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 传入的搜索参数的实例, 形成链型调用
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.2
		  */
		 <PT extends AbsParameter<?, ?, ?>> PT ds(PT param, Object... params) throws Exception;
		 
		 /**
		  * 分割符开始, 比如'('等等, 由搜索器实现类自定义
		  * <br/> 以AND连接逻辑开始时插入分隔符, 如: AND ( ...
		  * <br/> 必须与结束分隔符成对出现
		  * 
		  * @param args 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 传入的搜索参数的实例, 形成链型调用
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.2
		  */
		 <PT extends AbsParameter<?, ?, ?>> PT andDs(PT param, Object...args) throws Exception;
		
		 /**
		  * 分割符开始, 比如'('等等, 由搜索器实现类自定义
		  * <br/> 以OR连接逻辑开始时插入分隔符, 如: OR ( ...
		  * <br/> 必须与结束分隔符成对出现
		  * 
		  * @param args 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 传入的搜索参数的实例, 形成链型调用
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.2
		  */
		 <PT extends AbsParameter<?, ?, ?>> PT orDs(PT param, Object...args) throws Exception;
		 
		 /**
		  * 分割符结束, 比如')'等等, 由搜索器实现类自定义
		  * <br/> 必须与开始分隔符成对出现
		  * 
		  * @param args 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 传入的搜索参数的实例, 形成链型调用
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.2
		  */
		 IRelationalable<T> de(Object...args) throws Exception;
	}
	
	/**
	 * 类型转换接口
	 * 
	 * @param <T>　源字段类类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public interface ITransformable<T> {
		
		/**
		 * 将字符串解析成当前类型的值
		 * @param str 代表当前值的字符串
		 * @return 当前类型的值
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		T strToTarget(String str) throws Exception;
		
		/**
		 * 将当前类型的值解析成字符串
		 * @return 当前类型的值代表的字符串
		 *
		 * @author linjie
		 * @since 1.0.2
		 */
		String targetToStr(T obj) throws Exception;
	}
	
	/**
	 * 搜索内容源, 继承它的子类可以添加搜索内容
	 * 
	 * @param <SCT>　搜索内容类类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	static abstract class SearchContentSource<SCT> {
		
		/**
		 * 添加搜索内容键值对
		 * 
		 * @param key 自定义的搜索内容Key, 不能为null
		 * @param searchContent 添加的搜索内容, 不能为null
		 * @throws Exception 如果键或值为null则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		protected abstract void addSearchEntry(String key, SCT searchContent) throws Exception ;
		
		/**
		 * 获取对应键的搜索内容键值对
		 * 
		 * @param key 指定的搜索内容键
		 * @return 对应键的搜索内容键值对, 如果没有则返回空列表
		 * @throws Exception 如果键为null则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		protected abstract List<SCT> getSearchEntry(String key) throws Exception ;
		
		/**
		 * 清除指定键搜索内容键值对
		 * 
		 * @param key 自定义的搜索内容Key, 不能为null
		 * @throws Exception 如果键为null则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		protected abstract void clearSearchEntry(String key) throws Exception ;
	}
	
	// 所有的搜索内容
	private Map<String, List<SCT>> contents = new HashMap<String, List<SCT>>();
	// 描述搜索内容添加者和添加的搜索内容的映射关系, 当进行回滚时要用到
	private Map<SearchContentSource<SCT>, Map<String, List<SCT>>> contentMapper 
		= new HashMap<SearchContentSource<SCT>, Map<String, List<SCT>>>();
	// 所有搜索内容逻辑关系检查用的数据
	private boolean isSkipFirstRelation = true;
	private boolean relationalCheckFlag = true; /* 用来检查连接是否完整的标志 */
	private int delimiterStartCount = 0;
	private int delimiterEndCount = 0;
	
	private boolean isAutoAddRelation = false; /* 是否自动为条件追加逻辑关系 */
	private boolean isAutoAddAnd = true; /* 自动追加的逻辑关系是否是And, false则是Or */
	
	/**
	 * 启用关系自动追加
	 * 
	 * @param isAnd 是否使用AND连接关系, 为false则使用OR连接关系
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void enableAutoRelation(boolean isAnd) {
		// 设置自动关系追加
		this.isAutoAddRelation = true;
		this.isAutoAddAnd = isAnd;
	}
	
	/**
	 * 关闭关系自动追加
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void disableAutoRelation() {
		// 关闭自动关系追加
		this.isAutoAddRelation = false;
		this.isAutoAddAnd = true;
	}
	
	/**
	 * 当前是否为关系自动追加
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isAutoAddRelation() {
		return this.isAutoAddRelation;
	}
	
	/**
	 * 当前关系自动追加是否使用AND连接关系
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isAutoAddAnd() {
		return this.isAutoAddAnd;
	}
	
	/**
	 * 标记遇到开始分割符
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void meetDelimiterStart() {
		this.delimiterStartCount ++;
	}
	
	/**
	 * 标记遇到结束分割符
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void meetDelimiterEnd() {
		this.delimiterEndCount ++;
	}
	
	/**
	 * 判断是否所有分割符成对出现
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isDemiterInPair() {
		return this.delimiterStartCount == this.delimiterEndCount;
	}
	
	/**
	 * 设置第一次添加关系是否忽略
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void setSkipFirstRelation(boolean isSkipFirstRelation) {
		this.isSkipFirstRelation = isSkipFirstRelation;
	}
	
	/**
	 * 第一次添加关系是否忽略
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isSkipFirstRelation() {
		return this.isSkipFirstRelation;
	}
	
	/**
	 * 开始搜索关系检查
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void startSearchRelationCheck() {
		this.relationalCheckFlag = true;
	}
	
	/**
	 * 结束搜索关系检查
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void endSearchRelationCheck() {
		this.relationalCheckFlag = false;
	}
	
	/**
	 * 获取搜索关系检查标志
	 * 
	 * @return true表示已经添加了连接关系, false表示还没有
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean getRelationalCheckFlag() {
		return this.relationalCheckFlag;
	}
	
	/**
	 * 创建一个搜索上下文
	 * 
	 * @return 搜索上下文
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final static<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> SearchContext<PT, SCT, RT> create() throws Exception {
		return new SearchContext<PT, SCT, RT>();
	}
	
	/**
	 * 添加指定源的搜索内容键值对
	 * 
	 * @param source 指定的搜索内容源
	 * @param key 键
	 * @param searchContent 搜索内容
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void addSearchEntry(SearchContentSource<SCT> source, String key, SCT searchContent) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("添加搜索内容不能不指定搜索内容来源");
		}
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("添加搜索内容需要指定键");
		}
		if(searchContent == null) {
			throw new IllegalArgumentException("添加的搜索内容不能为null");
		}
		// 添加到总的搜索内容中
		this.addSearchContentHelper(this.contents, key, searchContent);
		// 添加到搜索内容-源关联集合中
		Map<String, List<SCT>> mappedContents = contentMapper.get(source);
		if(mappedContents == null) {
			mappedContents = new HashMap<String, List<SCT>>();
			contentMapper.put(source, mappedContents);
		}
		this.addSearchContentHelper(mappedContents, key, searchContent);
	}
	
	/**
	 * 根据搜索内容的key获取它对应的值
	 * 
	 * @param key 搜索内容的key, 由parameter实现类自定义
	 * @return key对应的搜索内容值, 如果没有则返回空列表
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	final List<SCT> getSearchEntry(String key) {
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("获取搜索内容需要指定键");
		}
		List<SCT> vals = this.contents.get(key);
		if(vals != null) {
			return vals;
		}
		return Collections.emptyList();
	}
	
	/**
	 * 清空指定键的搜索内容
	 * 
	 * @param key 指定的键
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void clearSearchEntry(String key) throws Exception {
		if(key == null || key.isEmpty()) {
			throw new IllegalArgumentException("移除搜索内容需要指定键");
		}
		if(this.contents.containsKey(key)) {
			List<SCT> vals = this.contents.get(key);
			if(vals != null && ! vals.isEmpty()) {
				vals.clear();
			}
		}
	}
	
	/**
	 * 根据搜索内容源移除其对应的所有的搜索内容键值对
	 * 
	 * @param source 指定的搜索内容源
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void removeSearchEntryBySource(SearchContentSource<SCT> source) throws Exception {
		if(source == null) {
			throw new IllegalArgumentException("移除搜索内容不能不指定搜索内容来源");
		}
		if(! this.contents.isEmpty()) {
			Map<String, List<SCT>> needRemoveContents = this.contentMapper.get(source);
			if(needRemoveContents != null && ! needRemoveContents.isEmpty()) {
				for(String key : needRemoveContents.keySet()) {
					List<SCT> needRemoveVals = needRemoveContents.get(key);
					List<SCT> vals = this.contents.get(key);
					if(needRemoveVals != null && ! needRemoveVals.isEmpty()
							&& vals != null && ! vals.isEmpty()) {
						vals.removeAll(needRemoveVals);
					}
				}
				this.contentMapper.remove(source);
			}
		}
	}
	
	/**
	 * 清除所有搜索内容键值对
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void clear() throws Exception {
		this.contents.clear();
		this.contentMapper.clear();
		
		this.isAutoAddAnd = false;
		this.isAutoAddRelation = false;
		this.relationalCheckFlag = true;
		this.delimiterStartCount = 0;
		this.delimiterEndCount = 0;
	}
	
	/**
	 * 添加搜索内容的辅助方法
	 * 
	 * @param contentsRef 搜索内容容器的引用
	 * @param key 键
	 * @param content 搜索内容
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private void addSearchContentHelper(Map<String, List<SCT>> contentsRef,
			String key, SCT content) {
		List<SCT> vals = contentsRef.get(key);
		if(vals == null) {
			vals = new LinkedList<SCT>();
			contentsRef.put(key, vals);
		}
		vals.add(content);
	}
	
	/**
	 * 禁止直接实例化
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private SearchContext() {};
}
