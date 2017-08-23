package com.fy.sparam.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fy.sparam.core.AbsParameter;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.ParameterField;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.AnnotationInitializor;
import com.fy.sparam.test.FormatUtils;
import com.fy.sparam.test.StringUtils;

/**
 * 
 * 
 * @author linjie
 * @since 1.0.1
 */
public class SqlParameter extends AbsParameter<SqlParameter, SqlPiece, SqlResult> {

	/**
	 * 最小的页码
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final int MIN_PAGE = 1;
	
	/**
	 * 最大的搜索数量
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final int MAX_COUNT = 500;
	
	/**
	 * 构建类型, 即构建方式
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public enum BuildMode {
		/**
		 * 获取实体
		 */
		SELECT_FIELDS,
		/**
		 * 获取数量
		 */
		SELECT_COUNT,
		/**
		 * 删除
		 */
		DELETE,
		/**
		 * 更新
		 */
		UPDATE
	}
	
	/**
	 * 搜索内容键值
	 * <br/> 用于addSearchEntry方法注册搜索内容, 起分组作用
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	protected static final String SELECT = "SELECT";
	protected static final String DELETE = "DELETE";
	protected static final String UPDATE = "UPDATE";
	protected static final String FROM = "FROM";
	protected static final String WHERE = "WHERE";
	protected static final String ORDER_BY = "ORDER_BY";
	protected static final String GROUP_BY = "GROUP_BY";
	protected static final String LIMIT = "LIMIT";
	
//	/**
//	 * 搜索参数原型缓存池
//	 * 
//	 * @author linjie
//	 * @since 4.5.0
//	 */
//	private static final Map<Class<? extends SqlParameter>, SqlParameter> paramPrototypePool = new ConcurrentHashMap<Class<? extends SqlParameter>, SqlParameter>();
	
	/**
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private static final AnnotationInitializor<SqlParameter, SqlPiece, SqlResult> annoInitializor
		= new AnnotationInitializor<SqlParameter, SqlPiece, SqlResult>(BaseConfig.PARAM_TRANSLATORS);

	@Override
	public void setCount(int count) {
		if(count < 0 || count > MAX_COUNT) {
			count = MAX_COUNT;
		}
		super.setCount(count);
	}
	
	@Override
	public void setPage(int page) {
		if(page < MIN_PAGE) {
			page = MIN_PAGE;
		}
		super.setPage(page);
	}
	
	/**
	 * 
	 * @param paramClazz
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static <SPT extends SqlParameter> SPT getParameter(Class<SPT> paramClazz) throws Exception {
		SPT param = paramClazz.newInstance();
		param.init(annoInitializor, SqlSearcher.class, SqlParameter.class, null, null, null);
		param.setPage(1);
		param.setCount(500);
		return param;
	}
	
	/**
	 * 通用的Integer排序比较器
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private static final Comparator<Integer> comparator = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2 - o1;
		}
	};
	
	@Override
	protected void onJoin(SqlParameter fromParam, JoinType joinType, RelationType relationType,
			ParameterField<SqlParameter, SqlPiece, SqlResult> fromField,
			ParameterField<SqlParameter, SqlPiece, SqlResult> toField) throws Exception {
		String mappedFromDbTableAlias = fromParam.getTableAlias();
		String mappedFromDbFieldName = fromField.getDbFieldName();
		String mappedToDbTableName = this.getTableName();
		String mappedToDbTableAlias = this.getTableAlias();
		String mappedToDbFieldName = toField.getDbFieldName();
		// 根据关联类型确定关联sql语句
		String joinTypeStr = null;
		switch(joinType) {
		case INNER_JOIN: default:
			joinTypeStr = "INNER JOIN";
			break;
		case LEFT_JOIN:
			joinTypeStr = "LEFT OUTER JOIN";
			break;
		case RIGHT_JOIN:
			joinTypeStr = "RIGHT OUTER JOIN";
			break;
		}
		// 根据关系类型确定ON后的条件
		String onStr = null;
		switch(relationType) {
		case EQ: default:
			onStr = StringUtils.concatAsStr(mappedFromDbTableAlias, ".", mappedFromDbFieldName,
					" = ", mappedToDbTableAlias, ".", mappedToDbFieldName);
			break;
		case NOT_EQ:
			onStr = StringUtils.concatAsStr(mappedFromDbTableAlias, ".", mappedFromDbFieldName,
					" <> ", mappedToDbTableAlias, ".", mappedToDbFieldName);
			break;
		case IN:
			onStr = StringUtils.concatAsStr(mappedFromDbTableAlias, ".", mappedFromDbFieldName,
					" IN (", mappedToDbTableAlias, ".", mappedToDbFieldName, ")");
			break;
		case NOT_IN:
			onStr = StringUtils.concatAsStr(mappedFromDbTableAlias, ".", mappedFromDbFieldName,
					" NOT IN (", mappedToDbTableAlias, ".", mappedToDbFieldName, ")");
			break;
		}
		SqlPiece sqlPiece = new SqlPiece(StringUtils.concatAsStr(joinTypeStr, 
				" ", mappedToDbTableName, " ", mappedToDbTableAlias, " ON ", onStr, " "));
		this.addSearchEntry(FROM, sqlPiece);
	}

	@Override
	protected SqlResult onBuild(Object... args) throws Exception {
		// 获取构建的模式
		BuildMode mode = null;
		if(args != null && args.length > 0) {
			if(!(args[0] instanceof BuildMode)) {
				throw new IllegalArgumentException("选择的获取结果类型无效, 必须为SqlParameter.BuildMode常量的一项.");
			}
			mode = (BuildMode) args[0];
		} else {
			mode = BuildMode.SELECT_FIELDS;
		}
		// 清除掉不一定会需要的搜索内容, 保留from和where语句即可
		this.clearSearchEntry(SELECT);
		// 获取必定会有的内容
		List<SqlPiece> fromEntries = this.getSearchEntry(FROM);
		List<SqlPiece> whereEntries = this.getSearchEntry(WHERE);
		// 根据构建的类型, 构建变化的部分
		StringBuilder sb = new StringBuilder();
		SqlResult buildResult = new SqlResult();
		
		List<SqlPiece> selectEntries = null;
		List<SqlPiece> updateEntries = null;
		List<SqlPiece> groupByEntries = null;
		List<SqlPiece> orderByEntries = null;
		List<SqlPiece> limitEntries = null;
		
		switch(mode) {
		case DELETE:
			// 清除上次构建的DELETE内容
			this.clearSearchEntry(DELETE);
			// 获取需要删除的记录的表的搜索参数
			List<SqlParameter> needDeleteParams = null;
			if(args.length > 1) {
				if(args[1] != null) {
					if(args[1] instanceof SqlParamMarker) { /* 如果没传就仅删除当前搜索参数的表的记录 */
						needDeleteParams = ((SqlParamMarker) args[1]).getDeleteContents();
					}
				}
			}
			// 构建变化的部分
			this.buildDelete(needDeleteParams);
			// 获取需要的部分
			List<SqlPiece> deleteEntries = this.getSearchEntry(DELETE);
			// 构建sql语句
			if(deleteEntries != null) {
				for(SqlPiece delete : deleteEntries) {
					sb.append(delete.getSqlPart());
				}
			}
			if(fromEntries != null) {
				for(SqlPiece from : fromEntries) {
					sb.append(from.getSqlPart());
				}
			}
			if(whereEntries != null) {
				sb.append("WHERE ");
				for(SqlPiece where : whereEntries) {
					sb.append(where.getSqlPart());
					buildResult.addValsInSql(where.getVals());
				}
			}
			break;
		case SELECT_COUNT:
			// 清除上次构建的SELECT内容
			this.clearSearchEntry(SELECT);
			// 构建变化的部分
			this.buildSelectCount();
			// 获取需要的部分
			selectEntries = this.getSearchEntry(SELECT);
			// 构建sql语句
			if(selectEntries != null) {
				for(SqlPiece select : selectEntries) {
					sb.append(select.getSqlPart());
				}
			}
			if(fromEntries != null) {
				for(SqlPiece from : fromEntries) {
					sb.append(from.getSqlPart());
				}
			}
			if(whereEntries != null) {
				sb.append("WHERE ");
				for(SqlPiece where : whereEntries) {
					sb.append(where.getSqlPart());
					buildResult.addValsInSql(where.getVals());
				}
			}
			break;
		case SELECT_FIELDS:
		default:
			// 清除上次构建的SELECT和LIMIT内容
			this.clearSearchEntry(SELECT);
			this.clearSearchEntry(LIMIT);
			this.clearSearchEntry(GROUP_BY);
			this.clearSearchEntry(ORDER_BY);
			// 构建变化的部分
			this.buildSelectFields(buildResult);
			this.buildGroupBy();
			this.buildLimit();
			this.buildOrderBy();
			// 获取需要的部分
			selectEntries = this.getSearchEntry(SELECT);
			groupByEntries = this.getSearchEntry(GROUP_BY);
			orderByEntries = this.getSearchEntry(ORDER_BY);
			limitEntries = this.getSearchEntry(LIMIT);
			
			// 构建sql语句
			if(selectEntries != null) {
				for(SqlPiece select : selectEntries) {
					sb.append(select.getSqlPart());
				}
			}
			if(fromEntries != null) {
				for(SqlPiece from : fromEntries) {
					sb.append(from.getSqlPart());
				}
			}
			if(whereEntries != null) {
				sb.append("WHERE ");
				for(SqlPiece where : whereEntries) {
					sb.append(where.getSqlPart());
					buildResult.addValsInSql(where.getVals());
				}
			}
			if(groupByEntries != null) {
				for(SqlPiece groupBy : groupByEntries) {
					sb.append(groupBy.getSqlPart());
				}
			}
			if(orderByEntries != null) {
				for(SqlPiece orderBy : orderByEntries) {
					sb.append(orderBy.getSqlPart());
				}
			}
			if(limitEntries != null) {
				for(SqlPiece limit : limitEntries) {
					sb.append(limit.getSqlPart());
				}
			}
			break;
		case UPDATE:
			// 清除上次构建的UPDATE内容
			this.clearSearchEntry(UPDATE);
			// 构建变化的部分
			this.buildUpdate();
			// 获取需要的部分
			updateEntries = this.getSearchEntry(UPDATE);
			// 构建sql语句
			if(updateEntries != null) {
				for(SqlPiece update : updateEntries) {
					sb.append(update.getSqlPart());
				}
			}
			if(fromEntries != null) {
				for(SqlPiece from : fromEntries) {
					sb.append(from.getSqlPart());
				}
			}
			// set子句, 参数为第二位传入的Map, 其中key为更新的搜索字段, 值为设置的目标值
			SqlParamMarker helper =  null;
			if(args.length > 1) {
				if(args[1] != null) {
					if(!(args[1] instanceof SqlParamMarker)) {
						throw new IllegalArgumentException("构建update语句传入的第一个额外参数必须是SqlParamUpdateHelper!");
					}
					helper = (SqlParamMarker) args[1];
					Map<ISearchable<?>, Object> udpateContents = helper.getUpdateContents();
					if(udpateContents.isEmpty()) {
						throw new IllegalArgumentException("构建update语句没有需要更新的内容!");
					}
					// 构建set子句的sql语句
					StringBuilder setSql = new StringBuilder("SET ");
					for(ISearchable<?> searchField : udpateContents.keySet()) {
						SqlSearcher<?> fieldSearcher = (SqlSearcher<?>) searchField;
						ParameterField<SqlParameter, SqlPiece, SqlResult> paramField =
								fieldSearcher.getBelongParameterField();
						String fieldName = paramField.getWholeDbFieldName();
						setSql.append(fieldName);
						setSql.append(" = ?,");
					}
					String set = setSql.toString();
					if(set.endsWith(",")) {
						set = set.substring(0, set.length() - 1);
					}
					sb.append(set);
					sb.append(" ");
					// 实际要更新的值加入目标列表
					buildResult.addValsInSql(udpateContents.values());
				}
			} else {
				throw new IllegalAccessException("构建update语句缺失了set字句的额外参数, 无法构建!");
			}
			if(whereEntries != null) {
				sb.append("WHERE ");
				for(SqlPiece where : whereEntries) {
					sb.append(where.getSqlPart());
					buildResult.addValsInSql(where.getVals());
				}
			}
			break;
		}
		String sql = sb.append(";").toString();
		buildResult.setSql(sql);
		return buildResult;
	}
	
	/**
	 * 构建select实体语句
	 * 
	 * @param buildingResult 正在构建的结果的引用, 不能为null
	 * 
	 * @throws Exception
	 * @author linjie
	 * @since 4.5.0
	 */
	private void buildSelectFields(SqlResult buildingResult) throws Exception {
		String tableName = this.getTableName();
		String tableAlias = this.getTableAlias();
		StringBuilder sb = new StringBuilder();
		String selectNames = StringUtils.concatAsStr(tableAlias, ".*");
		Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> paramFields = 
				this.getParameterContext().getAllParameterFields();
		List<String> selectedDbFieldNames = new LinkedList<String>();
		List<String> selectedFieldNames = new LinkedList<String>();
		
		int count = 0;
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : paramFields) {
			if(paramField.isOutput()) {
				// 把需要输出的缓存待用
				String dbTableAliasLocatFieldName = paramField.getDbTableAliasLocateFieldName();
				selectedFieldNames.add(dbTableAliasLocatFieldName);
				// 拼接select语句中输出的内容
				sb.append(paramField.getWholeDbFieldName());
				// 如果该字段之前已经出现过了, 那么加上别名
				String dbFieldName = paramField.getDbFieldName();
				if(selectedDbFieldNames.contains(dbFieldName)) {
					sb.append(" AS ");
					// 如果自己配置了列别名, 那么使用配置的列, 错误不管
					String alias = paramField.getDbFieldAlias();
					if(alias != null && !alias.isEmpty()) {
						sb.append(alias);
					} else {
						sb.append(paramField.getFieldName());
						sb.append("_");
						sb.append(count);
						count ++;
					}
				} else {
					// 如果之前没出现过, 加上已选择字段中
					selectedDbFieldNames.add(dbFieldName);
				}
				sb.append(",");
			}
		}
		if(sb.length() > 0) {
			selectNames = sb.substring(0, sb.length() - 1);
			buildingResult.setSelectedFieldNames(selectedFieldNames);
		}
		SqlPiece searchEntry = new SqlPiece(
				StringUtils.concatAsStr("SELECT ", selectNames, " FROM ", tableName, " ", tableAlias, " "));
		this.addSearchEntry(SELECT, searchEntry);
	}
	
	/**
	 * 构建select数量语句
	 * 
	 * @throws Exception
	 * @author linjie
	 * @since 4.5.0
	 */
	private void buildSelectCount() throws Exception {
		String tableName = this.getTableName();
		String tableAlias = this.getTableAlias();
		String countStr = "COUNT(1)";
		String groupByFieldStr = this.getGroupByFieldStr();
		if(! groupByFieldStr.isEmpty()) {
			countStr = StringUtils.concatAsStr("COUNT(DISTINCT ", groupByFieldStr,")");
		}
		SqlPiece searchEntry = new SqlPiece(
				StringUtils.concatAsStr("SELECT ", countStr, " FROM ", tableName, " ", tableAlias, " "));
		this.addSearchEntry(SELECT, searchEntry);
	}
	
	/**
	 * 构建delete语句
	 * 
	 * @throws Exception
	 * @author linjie
	 * @since 4.5.0
	 */
	private void buildDelete(List<SqlParameter> markDeleteParams) throws Exception {
		String tableName = this.getTableName();
		String tableAlias = this.getTableAlias();
		// 至少会加入当前搜索参数对应的表别名作为删除目标, 所以不能为null
		if(markDeleteParams == null) {
			markDeleteParams = new LinkedList<SqlParameter>();
		}
		// 存储需要删除的记录的表别名列
		List<String> needDelteTableAlias = new ArrayList<String>(markDeleteParams.size() + 1);
		// 如果有指定删除哪些就不加入当前搜索参数对应的表别名, 否则加入当前搜索参数对应的表别名作为删除目标
		if(! markDeleteParams.isEmpty()) {
			for(SqlParameter markDeleteParam : markDeleteParams) {
				needDelteTableAlias.add(markDeleteParam.getTableAlias());
			}
		} else {
			needDelteTableAlias.add(tableAlias);
		}
		SqlPiece searchEntry = new SqlPiece(
				StringUtils.concatAsStr("DELETE ", FormatUtils.formatArrayStr(needDelteTableAlias.toString()),
						" FROM ", tableName, " ", tableAlias, " "));
		this.addSearchEntry(DELETE, searchEntry);
	}
	
	/**
	 * 构建udpate语句
	 * 
	 * @throws Exception
	 * @author linjie
	 * @since 4.5.0
	 */
	private void buildUpdate() throws Exception {
		String tableName = this.getTableName();
		String tableAlias = this.getTableAlias();
		SqlPiece searchEntry = new SqlPiece(
				StringUtils.concatAsStr("UPDATE ", tableName," ", tableAlias, " "));
		this.addSearchEntry(UPDATE, searchEntry);
	}
	
	/**
	 * 构建OrderBy语句
	 * 
	 * @author linjie
	 * @throws Exception 
	 * @since 4.5.0
	 */
	private void buildOrderBy() throws Exception {
		Map<Integer, String> orderBys = new TreeMap<Integer, String>(comparator);
		Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> paramFields = 
				this.getParameterContext().getAllParameterFields();
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : paramFields) {
			String fieldName = paramField.getWholeDbFieldName();
			if(paramField.isOrderBy()) {
				Integer priority = paramField.getOrderByPriority();
				// 相同优先级的作concat处理
				if(orderBys.containsKey(priority)) {
					String orderByFieldStr = orderBys.get(priority);
					// 去掉OrderBy语句一定会加上的 ASC或 DESC(包括前面的空格)
					orderByFieldStr = orderByFieldStr.replaceAll(" ASC| DESC", "");
					if(!orderByFieldStr.contains("CONCAT")) {
						// 再重新构建CONCAT语句
						fieldName = StringUtils.concatAsStr("CONCAT(", orderByFieldStr,", ", fieldName,")");
					} else {
						// 去掉CONCAT(...)最右边的括号
						String ableToAddStr = orderByFieldStr.substring(0, orderByFieldStr.length() - 1);
						// 拼上新的列名, 不齐右括号
						fieldName = StringUtils.concatAsStr("", ableToAddStr,", ", fieldName,")");
					}
				}
				String order = paramField.isAsc() ? "ASC" : "DESC";
				orderBys.put(paramField.getOrderByPriority(), StringUtils.concatAsStr(" ", fieldName, " ", order));
			}
		}
		if(!orderBys.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(Integer key : orderBys.keySet()) {
				sb.append(orderBys.get(key));
				sb.append(",");
			}
			SqlPiece searchEntry = new SqlPiece(
					StringUtils.concatAsStr("ORDER BY ", sb.substring(0, sb.length() - 1)," "));
			this.addSearchEntry(ORDER_BY, searchEntry);
		}
	}
	
	/**
	 * 构建GroupBy语句
	 * 
	 * @throws Exception
	 *  
	 * @author linjie
	 * @since 4.5.0
	 */
	private void buildGroupBy() throws Exception {
		String groupByFieldStr = this.getGroupByFieldStr();
		if(! groupByFieldStr.isEmpty()) {
			SqlPiece searchEntry = new SqlPiece(
					StringUtils.concatAsStr("GROUP BY ", groupByFieldStr, " "));
			this.addSearchEntry(GROUP_BY, searchEntry);
		}
	}
	
	/**
	 * 获取GroupBy字段的字符串
	 * <br/> 按照优先级进行排序, 优先级越高在越前面, 多个使用CONCAT进行连接
	 * 
	 * @return GroupBy字段的字符串
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private String getGroupByFieldStr() throws Exception {
		Map<Integer, String> groupBys = new TreeMap<Integer, String>(comparator);
		Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> paramFields = 
				this.getParameterContext().getAllParameterFields();
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : paramFields) {
			String fieldName = paramField.getWholeDbFieldName();
			if(paramField.isGroupBy()) {
				Integer priority = paramField.getGroupByPriority();
				// 相同优先级的作concat处理
				if(groupBys.containsKey(priority)) {
					String groupByFieldStr = groupBys.get(priority);
					if(!groupByFieldStr.contains("CONCAT")) {
						// 再重新构建CONCAT语句
						fieldName = StringUtils.concatAsStr("CONCAT(", groupByFieldStr,", ", fieldName,")");
					} else {
						// 去掉CONCAT(...)最右边的括号
						String ableToAddStr = groupByFieldStr.substring(0, groupByFieldStr.length() - 1);
						fieldName = StringUtils.concatAsStr("", ableToAddStr,", ", fieldName,")");
					}
				}
				groupBys.put(priority, fieldName);
			}
		}
		if(!groupBys.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for(Integer key : groupBys.keySet()) {
				sb.append(groupBys.get(key));
				sb.append(",");
			}
			return sb.substring(0, sb.length() - 1);
		}
		return "";
	}
	
	/**
	 * 构建Limit语句
	 * 
	 * @author linjie
	 * @param selectMode 
	 * @throws Exception 
	 * @since 4.5.0
	 */
	private void buildLimit() throws Exception {
		int start = (this.getPage() - 1) * this.getCount();
		SqlPiece searchEntry = new SqlPiece(
				StringUtils.concatAsStr("LIMIT ", start, ", ", this.getCount(), " "));
		this.addSearchEntry(LIMIT, searchEntry);
	}
}
