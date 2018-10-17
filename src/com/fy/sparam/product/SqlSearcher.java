package com.fy.sparam.product;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fy.sparam.core.AbsSearcher;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.product.SqlParameter.BuildMode;
import com.fy.sparam.product.SqlParameter.SqlMember;
import com.fy.sparam.test.StringUtils;

/**
 * SQL搜索器实现
 * 
 * @author linjie
 *
 * @param <T> 字段类类型
 * 
 * @author linjie
 * @since 4.5.0
 */
public class SqlSearcher<T> extends AbsSearcher<SqlParameter, SqlPiece, SqlResult, T> {

	/**
	 * 获取字段查询名称, 与当前查询环境相关
	 * 
	 * @return 查询属性名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static String toQueryFieldName(ISearchable<?> searcher) {
		return ((SqlSearcher<?>) searcher).toQueryFieldName();
	}
	
	/**
	 * 获取字段定位名称, 与当前查询环境相关
	 * 
	 * @return 查询属性名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static String toLocateFieldName(ISearchable<?> searcher) {
		return ((SqlSearcher<?>) searcher).toLocateFieldName();
	}
	
	/**
	 * 获取字段查询名称, 与当前查询环境相关
	 * 
	 * @return 查询属性名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String toQueryFieldName() {
		return this.getBelongParameter().generateQueryFieldName(this.getSearchParameterField());
	}
	
	/**
	 * 获取字段定位名称, 与当前查询环境相关
	 * 
	 * @return 查询属性名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String toLocateFieldName() {
		List<String> locateFileNames = this.getBelongParameter()
				.generatePassedLocateFieldNames(this.getSearchParameterField());
		if(locateFileNames.isEmpty()) {
			return "";
		}
		return locateFileNames.get(locateFileNames.size() - 1);
	}
	
	@Override
	protected void onIn(Collection<T> values) throws Exception {
		if(values == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		if(values.isEmpty()) {
			throw new IllegalArgumentException("in搜索集合不能为空.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" IN (", this.generatePlaceHolder(values.size()),") "),
						this.translateEnums(values));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onNotIn(Collection<T> values) throws Exception {
		if(values == null || values.isEmpty()) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		if(values.isEmpty()) {
			throw new IllegalArgumentException("notIn搜索集合不能为空.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" NOT IN (", this.generatePlaceHolder(values.size()),") "),
						this.translateEnums(values));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onEq(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName()," = ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotEq(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " <> ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onBetween(T from, T to) throws Exception {
		if(from == null || to == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName()," BETWEEN ? AND ? "),
				this.translateEnum(from), this.translateEnum(to));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onLessThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName()," < ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotLessThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName()," >= ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onGreaterThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " > ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotGreaterThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " <= ? "),
				this.translateEnum(value));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onLike(String value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " LIKE ? "),
				value);
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotLike(String value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " NOT LIKE ? "),
				value);
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onIsNull() throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " IS NULL "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onIsNotNull() throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), " IS NOT NULL "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}
	
	@Override
	protected void onInChildQuery(SqlParameter childQuery) throws Exception {
		if(! (childQuery instanceof SqlParameter)) {
			throw new IllegalArgumentException("子查询必须是SqlParameter的实例");
		}
		SqlResult result = childQuery.build(BuildMode.SELECT_FIELDS);
		String sql = result.getSql();
		if(sql.contains("*")) {
			throw new IllegalArgumentException("子查询的sql语句必须指定输出内容");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" IN (", sql, ") "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onNotInChildQuery(SqlParameter childQuery) throws Exception {
		if(! (childQuery instanceof SqlParameter)) {
			throw new IllegalArgumentException("子查询必须是SqlParameter的实例");
		}
		SqlResult result = childQuery.build(BuildMode.SELECT_FIELDS);
		String sql = result.getSql();
		if(sql.contains("*")) {
			throw new IllegalArgumentException("子查询的sql语句必须指定输出内容");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" NOT IN (", sql,") "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onIn(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" IN (", ((SqlSearcher<?>) searchField).toQueryFieldName(),") "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onNotIn(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" NOT IN (", ((SqlSearcher<?>) searchField).toQueryFieldName(),") "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onEq(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" = ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotEq(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" <> ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onBetween(ISearchable<?> from, ISearchable<?> to)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" BETWEEN ", ((SqlSearcher<?>) from).toQueryFieldName(),
						" AND ", ((SqlSearcher<?>) to).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onLessThan(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" < ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onNotLessThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" >= ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onGreaterThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" > ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotGreaterThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" <= ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onLike(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(),
						" LIKE ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}

	@Override
	protected void onNotLike(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.toQueryFieldName(), 
						" NOT LIKE ", ((SqlSearcher<?>) searchField).toQueryFieldName(), " "));
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
		
	}
	
	@Override
	protected void onDelimiterStart(Object... params) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(" ( ");
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onDelimiterEnd(Object... params) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(" ) ");
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}
	
	@Override
	protected void onAnd() throws Exception {
		SqlPiece sqlPiece = new SqlPiece("AND ");
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}

	@Override
	protected void onOr() throws Exception {
		SqlPiece sqlPiece = new SqlPiece("OR ");
		this.addSearchEntry(SqlMember.WHERE.name(), sqlPiece);
	}
	
	@Override
	protected void onMarkGroupBy(int priority) throws Exception {
		// TODO 目前没有需求
	}
	
	@Override
	protected void onMarkOrderBy(int priority, boolean isAsc) throws Exception {
		// TODO 目前没有需求
	}
	
	/**
	 * 构建指定数量的占位符字符串, 即: ?,?,?,...
	 * 
	 * @param count 需要多少个
	 * @return 指定数量的占位符字符串
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	private String generatePlaceHolder(int count) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < count; i ++) {
			sb.append("?");
			if(i != count - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 把是常量类的对象转换为对应的值, 要求所有使用的常量类提供类型转换器({@link ITranslator}), 并使objToStr方法返回该常量的值(value).
	 * 常量类要转为数字类型的数据存到数据库, 所以需要转换而不能直接作为占位符值.
	 * 
	 * <br/> 对集合类型的进行处理
	 * <br/> 转换的实现是调用translateEnum()方法, 参考其实现细节.
	 * 
	 * @param enumVals 要转换的enum类型的值的集合.
	 * @return 转换或未转换的对象列表, 即使不是enum类型也会构建新的列表然后返回原来的值.
	 * @throws Exception 转换则抛出异常
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	private List<Object> translateEnums(Collection<T> enumVals) throws Exception {
		if(enumVals != null && !enumVals.isEmpty()) {
			List<Object> results = new LinkedList<Object>();
			for(T enumVal : enumVals) {
				Object result = translateEnum(enumVal);
				results.add(result);
			}
			return results;
		}
		return Collections.emptyList();
	}
	
	/**
	 * 把是常量类的对象转换为对应的值, 要求所有使用的常量类提供类型转换器({@link ITranslator}), 并使objToStr方法返回该常量的值(value).
	 * 常量类要转为数字类型的数据存到数据库, 所以需要转换而不能直接作为占位符值.
	 * 
	 * @param enumVal 常量类的对象
	 * @return 常量类的对应的值, 如果不是enum类型返回原来的值.
	 * @throws Exception 转换则抛出异常
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	private Object translateEnum(T enumVal) throws Exception {
		if(enumVal != null && enumVal instanceof Enum) {
			return this.getTransformer().targetToStr(enumVal);
		}
		return enumVal;
	}
}
