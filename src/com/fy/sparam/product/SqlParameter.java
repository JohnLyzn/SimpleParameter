package com.fy.sparam.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.fy.sparam.core.AbsParameter;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.ParameterField;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.AnnotationInitializor;
import com.fy.sparam.test.FormatUtils;
import com.fy.sparam.test.StringUtils;

/**
 * 构建Sql语句的搜索参数
 * 
 * @author linjie
 * @since 1.0.2
 */
public class SqlParameter extends AbsParameter<SqlParameter, SqlPiece, SqlResult> {

	/**
	 * 最小的页码
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static final int MIN_PAGE = 1;
	
	/**
	 * 最大的搜索数量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static final int MAX_COUNT = 500;
	
	/**
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static final String WHERE = SqlMember.WHERE.name();
	
	/**
	 * 构建类型, 即构建方式
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public enum BuildMode {
		
		/**
		 * 构建获取实体SQL
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_ENTITIES(SqlMember.SELECT_ENTITIES_HEAD,
				SqlMember.FROM, SqlMember.JOIN, SqlMember.WHERE,
				SqlMember.ORDER_BY, SqlMember.GROUP_BY,
				SqlMember.LIMIT),
		/**
		 * 构建获取实体指定字段SQL
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_FIELDS(SqlMember.SELECT_FIELDS_HEAD,
				SqlMember.FROM, SqlMember.JOIN, SqlMember.WHERE,
				SqlMember.ORDER_BY, SqlMember.GROUP_BY,
				SqlMember.LIMIT),
		/**
		 * 构建获取数量SQL
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_COUNT(SqlMember.SELECT_COUNT_HEAD,
				SqlMember.FROM, SqlMember.JOIN, SqlMember.WHERE),
		/**
		 * 构建删除DML
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		DELETE(SqlMember.DELETE_HEAD, SqlMember.FROM, SqlMember.JOIN,
				SqlMember.WHERE),
		/**
		 * 构建更新DML
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		UPDATE(SqlMember.UPDATE_HEAD, SqlMember.JOIN,
				SqlMember.UPDATE_SET_CONTENT, SqlMember.WHERE);
		
		/**
		 * 用来构建结果的成员SqlMember列表, 由构造器定义, 按顺序进行调用
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private SqlMember[] sqlMembers;
		
		/**
		 * 构造器: 指定用来构建结果的成员SqlMember
		 * 
		 * @param members 指定用来构建结果的成员SqlMember
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private BuildMode(SqlMember...members) {
			this.sqlMembers = members;
		}
		
		/**
		 * 使用SqlMember进行构建SqlResult结果
		 * 
		 * @param args 搜索参数{@link SqlParameter#build(Object...)}方法传入的可选参数
		 * @return 构建的SqlResult结果
		 * @throws Exception 根据需要抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		public SqlResult build(SqlParameter param, Object...args) throws Exception {
			SqlResult result = new SqlResult();
			for(SqlMember sqlMember : sqlMembers) {
				sqlMember.getBuilder().build(param, result, args);
			}
			return result;
		}
	}
	
	/**
	 * 定义sql成员常量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected enum SqlMember {
		
		/**
		 * 查询实体使用的sql语句的SELECT语句开头
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_ENTITIES_HEAD(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				param.setAllMyFieldOutput(true);
				String tableAlias = param.getTableAlias();
				StringBuilder tableAliasesBuilder = new StringBuilder(tableAlias).append(".*");
				for(SqlParameter inheritedFromParam : param.getInheritedFromParameters()) {
					tableAliasesBuilder.append(",").append(inheritedFromParam.getTableAlias()).append(".*");
				}
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
						"SELECT ", tableAliasesBuilder.toString())));
			}
		}),
		
		/**
		 * 查询某些字段使用的sql语句的SELECT语句开头
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_FIELDS_HEAD(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				StringBuilder dbFieldNamesBuilder = new StringBuilder();
				
				Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> allParamFields = 
						param.getParameterContext().getAllParameterFields();
				
				List<String> selectedDbFieldNames = new LinkedList<String>();
				List<String> selectedDbTableAliasLocateFieldNames = new LinkedList<String>();
				
				int count = 0;
				for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : allParamFields) {
					if(paramField.isOutput()) {
						// 把需要输出的缓存待用
						String dbTableAliasLocatFieldName = paramField.getDbTableAliasLocateFieldName();
						selectedDbTableAliasLocateFieldNames.add(dbTableAliasLocatFieldName);
						// 拼接select语句中输出的内容
						dbFieldNamesBuilder.append(paramField.getWholeDbFieldName());
						// 如果该字段之前已经出现过了, 那么加上别名
						String dbFieldName = paramField.getDbFieldName();
						if(selectedDbFieldNames.contains(dbFieldName)) {
							dbFieldNamesBuilder.append(" AS ");
							// 如果自己配置了列别名, 那么使用配置的列, 错误不管
							String alias = paramField.getDbFieldAlias();
							if(alias != null && !alias.isEmpty()) {
								dbFieldNamesBuilder.append(alias);
							} else {
								dbFieldNamesBuilder.append(paramField.getFieldName());
								dbFieldNamesBuilder.append("_");
								dbFieldNamesBuilder.append(count);
								count ++;
							}
						} else {
							// 如果之前没出现过, 加上已选择字段中
							selectedDbFieldNames.add(dbFieldName);
						}
						dbFieldNamesBuilder.append(",");
					}
				}
				if(dbFieldNamesBuilder.length() == 0) {
					throw new IllegalArgumentException("获取字段的sql构建需要指定输出的字段");
				}
				String selectedDbFieldNamesStr = dbFieldNamesBuilder.substring(0, dbFieldNamesBuilder.length() - 1);
				result.setSelectedDbTableAliasLocateFieldNames(selectedDbTableAliasLocateFieldNames);
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr("SELECT ", selectedDbFieldNamesStr)));
			}
		}),
		
		/**
		 * 查询记录数量使用的sql语句的SELECT语句开头
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_COUNT_HEAD(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				param.setAllMyFieldOutput(true);
				String countSql = "COUNT(1)";
				String groupByFieldSql = param.getGroupBySqlStr();
				if(! groupByFieldSql.isEmpty()) {
					countSql = StringUtils.concatAsStr("COUNT(DISTINCT ", groupByFieldSql, ")");
				}
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr("SELECT ", countSql)));
			}
		}),
		
		/**
		 * 删除实体使用的sql语句的DELETE语句开头
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		DELETE_HEAD(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				param.setAllMyFieldOutput(true);
				// 获取需要删除的记录的表的搜索参数
				List<SqlParameter> needDeleteParams = Collections.emptyList();
				if(args.length > 1) {
					if(args[1] != null) {
						if(args[1] instanceof SqlMarker) { /* 如果没传就仅删除当前搜索参数的表的记录 */
							needDeleteParams = ((SqlMarker) args[1]).getDeleteContents();
						}
					}
				}
				// 存储需要删除的记录的表别名列, 至少会加入当前搜索参数对应的表别名
				List<String> needDelteTableAlias = new ArrayList<String>(needDeleteParams.size() + 1);
				// 如果有指定删除哪些就不加入当前搜索参数对应的表别名, 否则加入当前搜索参数对应的表别名作为删除目标
				if(needDeleteParams != null && ! needDeleteParams.isEmpty()) {
					for(SqlParameter needDeleteParam : needDeleteParams) {
						needDelteTableAlias.add(needDeleteParam.getTableAlias());
					}
				} else {
					for(SqlParameter inheritedFormParam : param.getInheritedFromParameters()) {
						needDelteTableAlias.add(inheritedFormParam.getTableAlias());
					}
					needDelteTableAlias.add(param.getTableAlias());
				}
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
						"DELETE ", FormatUtils.formatArrayStr(needDelteTableAlias.toString()))));
			}
		}),
		
		/**
		 * 更新实体使用的sql语句的UPDATE语句开头
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		UPDATE_HEAD(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				// 构建变化的部分
				String tableName = param.getTableName();
				String tableAlias = param.getTableAlias();
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
						"UPDATE ", tableName," ", tableAlias, " ")));
			}
		}),
		
		/**
		 * 更新实体使用的sql语句的SET子句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		UPDATE_SET_CONTENT(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				// SET子句, 参数为第二位传入的搜索参数标志辅助类
				SqlMarker marker =  null;
				if(args.length > 1) {
					if(args[1] != null) {
						if(! (args[1] instanceof SqlMarker)) {
							throw new IllegalArgumentException("构建update语句传入的第一个额外参数必须是SqlParamMarker!");
						}
						marker = (SqlMarker) args[1];
						Map<ISearchable<?>, Object> udpateContents = marker.getUpdateContents();
						if(udpateContents.isEmpty()) {
							throw new IllegalArgumentException("构建update语句没有需要更新的内容!");
						}
						// 构建set子句的sql语句
						StringBuilder setSqlBuilder = new StringBuilder("SET");
						for(ISearchable<?> searchField : udpateContents.keySet()) {
							SqlSearcher<?> fieldSearcher = (SqlSearcher<?>) searchField;
							ParameterField<SqlParameter, SqlPiece, SqlResult> paramField =
									fieldSearcher.getBelongParameterField();
							String fieldName = paramField.getWholeDbFieldName();
							setSqlBuilder.append(", ");
							setSqlBuilder.append(fieldName);
							setSqlBuilder.append(" = ?");
						}
						// "SET "
						String setSql = setSqlBuilder.toString().replaceFirst(",", "");
						result.addSqlPiece(new SqlPiece(setSql, udpateContents.values()));
					}
				} else {
					throw new IllegalAccessException("构建update语句缺失了set字句的额外参数, 无法构建!");
				}
			}
		}),
		
		/**
		 * 要关联的表连接sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		FROM(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				String tableName = param.getTableName();
				String tableAlias = param.getTableAlias();
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
						" FROM ", tableName, " ", tableAlias, " ")));
			}
		}),
		
		/**
		 * 要关联的表连接sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		JOIN(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				List<SqlPiece> joinSqlPieces = param.getSearchEntry(SqlMember.JOIN.name());
				if(! joinSqlPieces.isEmpty()) {
					result.addSqlPieces(joinSqlPieces);
				}
			}
		}),
		
		/**
		 * 表示条件的sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		WHERE(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				List<SqlPiece> whereSqlPiece = param.getSearchEntry(SqlMember.WHERE.name());
				if(! whereSqlPiece.isEmpty()) {
					result.addSqlPiece(new SqlPiece(" WHERE "));
					result.addSqlPieces(whereSqlPiece);
				}
			}
		}),
		
		/**
		 * 表示排序的sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		ORDER_BY(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				Map<Integer, String> orderBys = new TreeMap<Integer, String>(INT_COMPARATOR);
				
				Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> allParamFields = 
						param.getParameterContext().getAllParameterFields();
				for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : allParamFields) {
					String fieldName = paramField.getWholeDbFieldName();
					if(paramField.isOrderBy()) {
						Integer priority = paramField.getOrderByPriority();
						// 相同优先级的作concat处理
						if(orderBys.containsKey(priority)) {
							String orderByFieldStr = orderBys.get(priority);
							// 去掉OrderBy语句一定会加上的 ASC或 DESC(包括前面的空格)
							orderByFieldStr = orderByFieldStr.replaceAll(" ASC| DESC", "");
							if(! orderByFieldStr.contains("CONCAT")) {
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
				if(! orderBys.isEmpty()) {
					StringBuilder sb = new StringBuilder();
					for(Integer key : orderBys.keySet()) {
						sb.append(orderBys.get(key));
						sb.append(",");
					}
					result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
							"ORDER BY ", sb.substring(0, sb.length() - 1), " ")));
				}
			}
		}),
		
		/**
		 * 表示分组的sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		GROUP_BY(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				String groupByFieldStr = param.getGroupBySqlStr();
				if(! groupByFieldStr.isEmpty()) {
					result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
							"GROUP BY ", groupByFieldStr, " ")));
				}
			}
		}),
		
		/**
		 * 表示分页的sql语句
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		LIMIT(new ISqlBuilder() {
	
			@Override
			public void build(SqlParameter param, SqlResult result, Object... args) throws Exception {
				int page = param.getPage();
				int count = param.getCount();
				int start = (page - 1) * count;
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
						"LIMIT ", start, ", ", count, " ")));
			}
		});
	
		/**
		 * 使用sql构建器
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private ISqlBuilder builder;
		
		/**
		 * 构造器: 指定当前sql语句成员使用的sql构建器
		 * 
		 * @param builder 当前sql语句成员使用的sql构建器
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private SqlMember(ISqlBuilder builder) {
			this.builder = builder;
		}
		
		/**
		 * 获取当前sql语句成员使用的sql构建器
		 * 
		 * @return 当前sql语句成员使用的sql构建器
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		public ISqlBuilder getBuilder() {
			return builder;
		}
	}


	/**
	 * 定义sql语句成员使用的sql构建器接口
	 * 
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected static interface ISqlBuilder {
		
		/**
		 * 构建当前类型的sql语句作为输出结果的片段
		 * 
		 * @param param 当前构建结果的根搜索参数实例
		 * @param result 当前构建中的sql结果表示类
		 * @param args 搜索参数{@link SqlParameter#build(Object...)}方法传入的可选参数
		 * @throws Exception 根据需要抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void build(SqlParameter param, SqlResult result, Object...args) throws Exception;
	}
	
	/**
	 * 搜索参数原型缓存池
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private static final Map<Class<? extends SqlParameter>, SqlParameter> paramPrototypePool 
		= new ConcurrentHashMap<Class<? extends SqlParameter>, SqlParameter>();
	
	/**
	 * 注解式搜索参数初始化器
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private static final AnnotationInitializor<SqlParameter, SqlPiece, SqlResult> annoInitializor
		= new AnnotationInitializor<SqlParameter, SqlPiece, SqlResult>(BaseConfig.PARAM_TRANSLATORS);

	/**
	 * 辅助的Integer排序比较器
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private static final Comparator<Integer> INT_COMPARATOR = new Comparator<Integer>() {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2 - o1;
		}
	};

	/**
	 * 根据搜索参数具体实现类字节码获取对应的搜索参数实例
	 * 
	 * @param paramClazz 指定的搜索参数具体实现类字节码
	 * @return 对应的搜索参数实例
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@SuppressWarnings("unchecked")
	public static <SPT extends SqlParameter> SPT getParameter(Class<SPT> paramClazz) throws Exception {
		SPT prototype = (SPT) paramPrototypePool.get(paramClazz);
		if(prototype == null) {
			prototype = paramClazz.newInstance();
			prototype.init(annoInitializor, SqlSearcher.class, SqlParameter.class, null, null, null);
			prototype.setPage(1);
			prototype.setCount(500);
			paramPrototypePool.put(paramClazz, prototype);
		}
		return (SPT) prototype.clone();
	}
	
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
		this.addSearchEntry(SqlMember.JOIN.name(), new SqlPiece(StringUtils.concatAsStr(joinTypeStr, 
				" ", mappedToDbTableName, " ", mappedToDbTableAlias, " ON ", onStr, " ")));
	}

	@Override
	protected SqlResult onBuild(Object... args) throws Exception {
		// 获取构建的模式
		BuildMode buildMode = null;
		if(args != null && args.length > 0) {
			if(!(args[0] instanceof BuildMode)) {
				throw new IllegalArgumentException("选择的获取结果类型无效, 必须为SqlParameter.BuildMode常量的一项.");
			}
			buildMode = (BuildMode) args[0];
		} else {
			buildMode = BuildMode.SELECT_ENTITIES;
		}
		SqlResult result = buildMode.build(this, args);
		result.addSqlPiece(new SqlPiece(";"));
		return result;
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
	private String getGroupBySqlStr() throws Exception {
		Map<Integer, String> groupBys = new TreeMap<Integer, String>(INT_COMPARATOR);
		Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> paramFields = 
				this.getParameterContext().getAllParameterFields();
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : paramFields) {
			String fieldName = paramField.getWholeDbFieldName();
			if(paramField.isGroupBy()) {
				Integer priority = paramField.getGroupByPriority();
				// 相同优先级的作concat处理
				if(groupBys.containsKey(priority)) {
					String groupByFieldStr = groupBys.get(priority);
					if(! groupByFieldStr.contains("CONCAT")) {
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
}
