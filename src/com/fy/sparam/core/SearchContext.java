package com.fy.sparam.core;

import java.util.Collection;
import java.util.Map;

public final class SearchContext<PT extends AbsParameter<PT, SCT, ?>, SCT, RT> {

	/**
	 * 搜索操作接口
	 * 
	 * @param <PT> 搜索参数类型
	 * @param <T> 源字段的类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public interface ISearchable<T> {

		/**
		 * 当前字段等于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> eq(T value) throws Exception;
		
		/**
		 * 当前字段等于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> eq(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段不等于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notEq(T value) throws Exception;
		
		/**
		 * 当前字段不等于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notEq(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值在某个集合中
		 * 
		 * @param values 集合
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
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
		 * @since 1.0.1
		 */
		IRelationalable<T> in(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不在某个集合中
		 * 
		 * @param values 集合
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
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
		 * @since 1.0.1
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
		 * @since 1.0.1
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
		 * @since 1.0.1
		 */
		IRelationalable<T> between(ISearchable<?> from, ISearchable<?> to) throws Exception;

		/**
		 * 当前字段的值小于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> lessThan(T value) throws Exception;
		
		/**
		 * 当前字段小于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> lessThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不小于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notLessThan(T value) throws Exception;
		
		/**
		 * 当前字段不小于(大于或等于)另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notLessThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值大于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> greaterThan(T value) throws Exception;

		/**
		 * 当前字段大于另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> greaterThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不大于某个值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notGreaterThan(T value) throws Exception;

		/**
		 * 当前字段不大于(小于或等于)另一个当前搜索参数可管理的字段
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notGreaterThan(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值模糊匹配上某值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> like(String value) throws Exception;
		
		/**
		 * 当前字段模糊匹配上另一个当前搜索参数可管理的字段在数据库中的值
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> like(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值不模糊匹配上某值
		 * 
		 * @param value 值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notLike(String value) throws Exception;
		
		/**
		 * 当前字段不模糊匹配上另一个当前搜索参数可管理的字段在数据库中的值
		 * 
		 * @param searchField 另一个当前搜索参数可管理的字段, 不能为null.
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notLike(ISearchable<?> searchField) throws Exception;
		
		/**
		 * 当前字段的值为Null
		 * 
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> isNull() throws Exception;
		
		/**
		 * 当前字段的值不为null
		 * 
		 * @throws Exception　操作失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> isNotNull() throws Exception;

		/**
		 * 当前字段的值在子查询的结果集合中
		 * 
		 * @param childQuery 子查询的搜索参数
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> inChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception;

		/**
		 * 当前字段的值不在子查询的结果集合中
		 * 
		 * @param childQuery 子查询的搜索参数
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		IRelationalable<T> notInChildQuery(AbsParameter<?, ?, ?> childQuery) throws Exception;
		
		/**
		 * 标记当前字段进行分组
		 * 
		 * @param priority 优先级值
		 * @throws Exception　操作失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
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
		 * @since 1.0.1
		 */
		void markOrderBy(int priority, boolean isAsc) throws Exception;
		
		/**
		 * 设置当前搜索器对应的字段进行输出
		 * 
		 * @param isOutput　是否是输出, <tt>true</tt>表示输出, <tt>false</tt>表示不输出.
		 * @throws Exception　操作失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		void setOutput(boolean isOutput) throws Exception;
		
		/**
		 * 获取对应的搜索参数字段在搜索参数中的属性名称
		 * @return 对应的搜索参数字段在搜索参数中的属性名称
		 * @throws Exception 获取失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		String getFieldName() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段在入口搜索参数中的属性路径
		 * 
		 * @return 对应的搜索参数字段在入口搜索参数中的属性路径
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		String getFieldPath() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段经过表别名前缀处理的当前搜索范围中唯一属性名
		 * <br/> 格式为表别名前缀.当前搜索器所在搜索参数中的属性名称
		 * <br/> 被关联的搜索参数字段使用实际代表操作的字段的唯一属性名.
		 * <br/> 继承关联搜索参数的关联搜索参数字段只会使用最终子类搜索参数的表别名作为前缀.
		 * 
		 * @return 经过表别名前缀处理的唯一属性名
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		String getDbTableAliasLocateFieldName() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段配置对应的数据库字段名称
		 * @return 对应的搜索参数字段配置对应的数据库字段名称
		 * @throws Exception 获取失败则抛出异常
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		String getDbFieldName() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段配置对应的数据库字段别名
		 * @return 对应的搜索参数字段配置对应的数据库字段别名
		 * @throws Exception
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		String getDbFieldAlias() throws Exception;
		
		/**
		 * 获取对应的搜索参数字段经过表别名前缀处理的完全数据库列名
		 * <br/> 格式为表别名前缀.数据库列名称
		 * 
		 * @return 经过表别名前缀处理的完全数据库列名
		 * 
		 * @see ParameterField #getWholeDbFieldName()
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		String getWholeDbFieldName() throws Exception;
	}
	
	/**
	 * 关系连接操作接口
	 * 
	 * @param <T>　源字段类类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public interface IRelationalable<T> {
		
		/**
		 * 连接两个条件, 表示and, 可以根据需要重定向到关联的搜索参数的属性
		 * <br/>传入的搜索参数限定了指定哪个搜索参数进行接下来的操作, 请保持使用同一范围(一个入口, 可能多个关联)的搜索参数, 
		 * 跨入口搜索参数使用可能会导致错误的结果.
		 * 
		 * @param param 要求进行连接的搜索参数实例
		 * @return 传入的搜索参数的实例, 形成链型调用
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		 <PT extends AbsParameter<?, ?, ?>> PT and(PT param) throws Exception;
		
		/**
		 * 连接两个条件, 表示or, 可以根据需要重定向到关联的搜索参数的属性
		 * <br/>传入的搜索参数限定了指定哪个搜索参数进行接下来的操作, 请保持使用同一范围(一个入口, 可能多个关联)的搜索参数, 
		 * 跨入口搜索参数使用可能会导致错误的结果.
		 * 
		 * @param param 要求进行连接的搜索参数实例
		 * @return 传入的搜索参数的实例, 形成链型调用
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		 <PT extends AbsParameter<?, ?, ?>> PT or(PT param) throws Exception;
		 
		 /**
		  * 分割符开始, 比如'('等等, 由搜索器实现类自定义
		  * <br/> 必须与结束分隔符成对出现
		  * 
		  * @param params 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 当前连接器的实例, 形成链型调用, 后必接连接器的中的内容
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.1
		  */
		 IRelationalable<T> ds(Object...params) throws Exception;
		
		 /**
		  * 分割符结束, 比如')'等等, 由搜索器实现类自定义
		  * <br/> 必须与开始分隔符成对出现
		  * 
		  * @param params 可选参数, 由搜索器实现类自定义决定是什么实现
		  * @return 当前连接器的实例, 形成链型调用, 后必接连接器的中的内容
		  * 
		  * @throws Exception
		  *
		  * @author linjie
		  * @since 1.0.1
		  */
		 IRelationalable<T> de(Object...params) throws Exception;
	}
	
	/**
	 * 类型转换接口
	 * 
	 * @param <T>　源字段类类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public interface ITransformable<T> {
		
		/**
		 * 将字符串解析成当前类型的值
		 * @param str 代表当前值的字符串
		 * @return 当前类型的值
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		T strToTarget(String str) throws Exception;
		
		/**
		 * 将当前类型的值解析成字符串
		 * @return 当前类型的值代表的字符串
		 *
		 * @author linjie
		 * @since 1.0.1
		 */
		String targetToStr(T obj) throws Exception;
	}
	
	/**
	 * 
	 */
	private Map<ISearchable<?>, Map<String, SCT>> contents;
	
	
	SearchContext<PT, SCT, RT> build(PT param)  throws Exception {
		return null;
	}
	
	/**
	 * 
	 * @param key
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void clearAllSearchEntry(String key) throws Exception {
		
	}

	/**
	 * 
	 * @param searcher
	 * @param key
	 * @param searchContent
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void addSearchEntry(ISearchable<?> searcher, String key, SCT searchContent) throws Exception {
		
	}
	
	/**
	 * 
	 * @param searcher
	 * @param key
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void clearSearchEntry(ISearchable<?> searcher, String key) throws Exception {
		
	}
	
	/**
	 * 
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void reset() throws Exception {
		this.contents.clear();
	}
	
	/**
	 * 禁止直接实例化
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private SearchContext() {};
}
