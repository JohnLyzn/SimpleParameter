package com.fy.sparam.product;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.fy.sparam.core.AbsSearcher;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.product.SqlParameter.BuildMode;
import com.fy.sparam.test.StringUtils;

public class SqlSearcher<T> extends AbsSearcher<SqlParameter, SqlPiece, SqlResult, T> {

	@Override
	public void onIn(Collection<T> values) throws Exception {
		if(values == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		if(values.isEmpty()) {
			throw new IllegalArgumentException("in搜索集合不能为空.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" IN (", generatePlaceHolder(values.size()),") "),
				translateEnums(values));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onNotIn(Collection<T> values) throws Exception {
		if(values == null || values.isEmpty()) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		if(values.isEmpty()) {
			throw new IllegalArgumentException("notIn搜索集合不能为空.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" NOT IN (", generatePlaceHolder(values.size()),") "),
				translateEnums(values));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onEq(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName()," = ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotEq(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " <> ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onBetween(T from, T to) throws Exception {
		if(from == null || to == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName()," BETWEEN ? AND ? "),
				translateEnum(from), translateEnum(to));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onLessThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName()," < ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotLessThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName()," >= ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onGreaterThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " > ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotGreaterThan(T value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " <= ? "),
				translateEnum(value));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onLike(String value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " LIKE ? "),
				value);
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotLike(String value) throws Exception {
		if(value == null) {
			throw new IllegalArgumentException("搜索内容不能为null.");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " NOT LIKE ? "),
				value);
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onIsNull() throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " IS NULL "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onIsNotNull() throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), " IS NOT NULL "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onInChildQuery(SqlParameter childQuery) throws Exception {
		if(! (childQuery instanceof SqlParameter)) {
			throw new IllegalArgumentException("子查询必须是SqlParameter的实例");
		}
		SqlResult result = childQuery.build(BuildMode.SELECT_FIELDS);
		String sql = result.getSql();
		if(sql.contains("*")) {
			throw new IllegalArgumentException("子查询的sql语句必须指定输出内容");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" IN (", sql, ") "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onNotInChildQuery(SqlParameter childQuery) throws Exception {
		if(! (childQuery instanceof SqlParameter)) {
			throw new IllegalArgumentException("子查询必须是SqlParameter的实例");
		}
		SqlResult result = childQuery.build(BuildMode.SELECT_FIELDS);
		String sql = result.getSql();
		if(sql.contains("*")) {
			throw new IllegalArgumentException("子查询的sql语句必须指定输出内容");
		}
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" NOT IN (", sql,") "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onIn(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" IN (", this.getWholeDbFieldName(),") "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onNotIn(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" NOT IN (", searchField.getWholeDbFieldName(),") "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onEq(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" = ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotEq(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" <> ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onBetween(ISearchable<?> from, ISearchable<?> to)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" BETWEEN ", from.getWholeDbFieldName(),
						" AND ", to.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onLessThan(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" < ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onNotLessThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" >= ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onGreaterThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" > ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotGreaterThan(ISearchable<?> searchField)
			throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" <= ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onLike(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(),
						" LIKE ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}

	@Override
	public void onNotLike(ISearchable<?> searchField) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(
				StringUtils.concatAsStr(this.getWholeDbFieldName(), 
						" NOT LIKE ", searchField.getWholeDbFieldName(), " "));
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
		
	}
	
	@Override
	public void onDelimiterStart(Object... params) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(" ( ");
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	public void onDelimiterEnd(Object... params) throws Exception {
		SqlPiece sqlPiece = new SqlPiece(" ) ");
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}
	
	@Override
	protected void onAnd() throws Exception {
		SqlPiece sqlPiece = new SqlPiece("AND ");
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}

	@Override
	protected void onOr() throws Exception {
		SqlPiece sqlPiece = new SqlPiece("OR ");
		this.addSearchEntry(SqlParameter.WHERE, sqlPiece);
	}
	
	@Override
	protected void onMarkGroupBy(int priority) throws Exception {
		
	}
	
	@Override
	protected void onMarkOrderBy(int priority, boolean isAsc) throws Exception {
		
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
	 * @param enums 要转换的enum类型的值.
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
				if(result != null) {
					results.add(result);
				}
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
