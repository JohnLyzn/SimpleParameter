package com.fy.sparam.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.fy.sparam.core.AbsParameter;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.JoinRelationType;
import com.fy.sparam.core.ParameterContext;
import com.fy.sparam.core.ParameterField;
import com.fy.sparam.core.ParameterField.IFieldQueryNameGenerateStrategy;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.init.anno.AnnotationInitializor;
import com.fy.sparam.test.FormatUtils;
import com.fy.sparam.test.StringUtils;

/**
 * 构建Sql语句的搜索参数
 * <br> <strong>非线程安全!!!</strong>
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
				SqlMember.GROUP_BY, SqlMember.ORDER_BY,
				SqlMember.LIMIT),
		/**
		 * 构建获取实体指定字段SQL
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		SELECT_FIELDS(SqlMember.SELECT_FIELDS_HEAD,
				SqlMember.FROM, SqlMember.JOIN, SqlMember.WHERE,
				SqlMember.GROUP_BY, SqlMember.ORDER_BY,
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
				// 获取所有输出的实体对应的搜索参数
				ParameterContext<SqlParameter, SqlPiece, SqlResult> paramContext = param.getParameterContext();
				Collection<SqlParameter> outputParams = paramContext.getAllOutputParameters();
				if(outputParams.isEmpty()) {
					throw new IllegalArgumentException("获取实体的sql构建需要指定输出的实体");
				}
				// 拼接select内容
				StringBuilder selectEntitiesSqlBuilder = new StringBuilder();
				for(SqlParameter outputParam : outputParams) {
					selectEntitiesSqlBuilder.append(outputParam.getQueryAlias()).append(".*,");
				}
				selectEntitiesSqlBuilder.deleteCharAt(selectEntitiesSqlBuilder.length() - 1); /* 删除最后的',' */
				// 添加到搜索内容中
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr("SELECT ",
						selectEntitiesSqlBuilder.toString())));
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
				// 获取所有输出的字段
				ParameterContext<SqlParameter, SqlPiece, SqlResult> paramContext = param.getParameterContext();
				Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> outputParamFields = 
						paramContext.getAllOutputParameterFields();
				if(outputParamFields.isEmpty()) {
					throw new IllegalArgumentException("获取字段的sql构建需要指定输出的字段");
				}
				// 拼接select的字段内容
				int suffixNumber = 0; /* 字段别名的后缀数字, 这样能保证一定不会重复 */
				StringBuilder selectSqlBuilder = new StringBuilder();
				Set<String> appearDbFieldNames = new HashSet<String>(outputParamFields.size());
				List<String[]> outputFieldNames = new ArrayList<String[]>(outputParamFields.size());
				for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : outputParamFields) {
					// 拼接select语句中输出的内容
					selectSqlBuilder.append(param.generateQueryFieldName(paramField));
					// 把输出的列对应的属性名称按顺序记录下来
					List<String> dbTableAliasLocatFieldNames = param.generatePassedLocateFieldNames(paramField);
					outputFieldNames.add(dbTableAliasLocatFieldNames.toArray(new String[dbTableAliasLocatFieldNames.size()]));
					// 如果该字段之前已经出现过了, 那么加上别名
					String dbFieldName = paramField.getQueryFieldName();
					if(appearDbFieldNames.contains(dbFieldName)) {
						selectSqlBuilder.append(" AS ");
						// 如果自己配置了列别名, 那么使用配置的列, 错误不管
						String alias = paramField.getQueryFieldAlias();
						if(alias != null && ! alias.isEmpty()) {
							selectSqlBuilder.append(alias);
						} else {
							selectSqlBuilder.append(paramField.getFieldName());
							selectSqlBuilder.append("_");
							selectSqlBuilder.append(suffixNumber);
							suffixNumber ++;
						}
					} else {
						// 如果之前没出现过, 加上已出现的集合中
						appearDbFieldNames.add(dbFieldName);
					}
					selectSqlBuilder.append(",");
				}
				selectSqlBuilder.deleteCharAt(selectSqlBuilder.length() - 1); /* 删除最后的',' */
				result.setOutputValCorrespondFieldNames(outputFieldNames);
				result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr("SELECT ", selectSqlBuilder.toString())));
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
				if(! param.isIgnoreGroupBy) {
					String groupByFieldSql = SqlParameter.generateGroupBySqlStr(param);
					if(! groupByFieldSql.isEmpty()) {
						countSql = StringUtils.concatAsStr("COUNT(DISTINCT ", groupByFieldSql, ")");
					}
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
						needDelteTableAlias.add(needDeleteParam.getQueryAlias());
					}
				} else {
					for(SqlParameter inheritedFormParam : param.getInheritedFromParameters()) {
						needDelteTableAlias.add(inheritedFormParam.getQueryAlias());
					}
					needDelteTableAlias.add(param.getQueryAlias());
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
				String tableName = param.getQueryName();
				String tableAlias = param.getQueryAlias();
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
						if(udpateContents == null || udpateContents.isEmpty()) {
							throw new IllegalArgumentException("构建update语句没有需要更新的内容!");
						}
						// 构建set子句的sql语句
						StringBuilder setSqlBuilder = new StringBuilder("SET");
						for(ISearchable<?> searchField : udpateContents.keySet()) {
							SqlSearcher<?> fieldSearcher = (SqlSearcher<?>) searchField;
							ParameterField<SqlParameter, SqlPiece, SqlResult> paramField =
									fieldSearcher.getBelongParameterField();
							String fieldName = param.generateQueryFieldName(paramField);
							setSqlBuilder.append(" ").append(fieldName).append(" = ?").append(",");
						}
						setSqlBuilder.deleteCharAt(setSqlBuilder.length() - 1);
						result.addSqlPiece(new SqlPiece(setSqlBuilder.toString(), udpateContents.values()));
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
				String tableName = param.getQueryName();
				String tableAlias = param.getQueryAlias();
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
				List<SqlPiece> whereSqlPieces = param.getSearchEntry(SqlMember.WHERE.name());
				if(! whereSqlPieces.isEmpty()) {
					result.addSqlPiece(new SqlPiece(" WHERE "));
					result.addSqlPieces(whereSqlPieces);
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
				if(param.isIgnoreGroupBy) {
					// 如果有额外添加的GROUP BY语句则拼接上去
					List<SqlPiece> extraGroupBySqlPieces = param.getSearchEntry(SqlMember.GROUP_BY.name());
					if(! extraGroupBySqlPieces.isEmpty()) {
						result.addSqlPieces(extraGroupBySqlPieces);
					}
					return;
				}
				String groupByFieldStr = SqlParameter.generateGroupBySqlStr(param);
				if(! groupByFieldStr.isEmpty()) {
					result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
							"GROUP BY ", groupByFieldStr, " ")));
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
				if(param.isIgnoreOrderBy) {
					// 如果有额外添加的ORDER BY语句则拼接上去
					List<SqlPiece> extraOrderBySqlPieces = param.getSearchEntry(SqlMember.ORDER_BY.name());
					if(! extraOrderBySqlPieces.isEmpty()) {
						result.addSqlPieces(extraOrderBySqlPieces);
					}
					return;
				}
				Map<Integer, String> orderBys = new TreeMap<Integer, String>(INT_COMPARATOR);
				
				Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> allParamFields = 
						param.getParameterContext().getAllParameterFields();
				for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : allParamFields) {
					String fieldName = param.generateQueryFieldName(paramField);
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
								String ableToAddConcatStr = orderByFieldStr.substring(0, orderByFieldStr.length() - 1);
								// 拼上新的列名, 不齐右括号
								fieldName = StringUtils.concatAsStr("", ableToAddConcatStr,", ", fieldName,")");
							}
						}
						String order = paramField.isAsc() ? "ASC" : "DESC";
						orderBys.put(paramField.getOrderByPriority(), StringUtils.concatAsStr(" ", fieldName, " ", order));
					}
				}
				if(! orderBys.isEmpty()) {
					StringBuilder orderBySqlBuilder = new StringBuilder();
					for(Integer key : orderBys.keySet()) {
						orderBySqlBuilder.append(orderBys.get(key));
						orderBySqlBuilder.append(",");
					}
					orderBySqlBuilder.deleteCharAt(orderBySqlBuilder.length() - 1);
					result.addSqlPiece(new SqlPiece(StringUtils.concatAsStr(
							"ORDER BY ", orderBySqlBuilder.toString(), " ")));
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
				if(param.isIgnoreLimit) {
					return;
				}
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
	 * 定义sql成员常量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected enum FieldNameGenerateStrategy {
		
		/**
		 * 数据库查询格式
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		DB_FIELD_NAME(new IFieldQueryNameGenerateStrategy<SqlParameter, SqlPiece, SqlResult>() {

			@Override
			public String generate(SqlParameter param,
					ParameterField<SqlParameter, SqlPiece, SqlResult> paramField) {
				return StringUtils.concatAsStr(param.getQueryAlias(),
						FieldNameGenerateStrategy.PATH_SPERATOR,
						paramField.getQueryFieldName());
			}
		}),
		
		/**
		 * 对象查询格式, 用于HQL和返回字段标记
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		OBJ_FIELD_NAME(new IFieldQueryNameGenerateStrategy<SqlParameter, SqlPiece, SqlResult>() {

			@Override
			public String generate(SqlParameter param,
					ParameterField<SqlParameter, SqlPiece, SqlResult> paramField) {
				return StringUtils.concatAsStr(param.getQueryAlias(),
						FieldNameGenerateStrategy.PATH_SPERATOR,
						paramField.getFieldName().replace("$", ""));
			}
		});
		
		/**
		 * 字段查询名称使用的分隔符
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private static final String PATH_SPERATOR = ".";
		
		/**
		 * 字段查询名称生成策略实例
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private final IFieldQueryNameGenerateStrategy<SqlParameter, SqlPiece, SqlResult> strategy;
		
		/**
		 * 构造一个字段查询名称生成策略实例常量
		 * 
		 * @param strategy 字段查询名称生成策略实例
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		private FieldNameGenerateStrategy(IFieldQueryNameGenerateStrategy<SqlParameter, SqlPiece, SqlResult> strategy) {
			this.strategy = strategy;
		}
		
		/**
		 * 获取当前常量使用的字段查询名称生成策略实例
		 * 
		 * @return 当前常量使用的字段查询名称生成策略实例
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		public IFieldQueryNameGenerateStrategy<SqlParameter, SqlPiece, SqlResult> getStrategy() {
			return strategy;
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
	 * 是否忽略Limit语句
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private boolean isIgnoreLimit;
	
	/**
	 * 是否忽略GroupBy语句
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private boolean isIgnoreGroupBy;
	
	/**
	 * 是否忽略OrderBy语句
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private boolean isIgnoreOrderBy;
	
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
			prototype.setPage(MIN_PAGE);
			prototype.setCount(MAX_COUNT);
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

	/**
	 * 加入自定义的from的sql语句(注意尾部加空格)
	 * 
	 * @param from 自定义的from的sql语句, 不能为null
	 * @throws Exception from为null则抛出异常
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void addExtraFrom(String from) throws Exception {
		if(from == null) {
			throw new IllegalArgumentException("错误的自定义From的Sql语句"); 
		}
		this.addSearchEntry(SqlMember.FROM.name(), new SqlPiece(from));
	}
	
	/**
	 * 加入自定义的where的sql语句(注意尾部加空格)
	 * <br/> 首个条件不需要添加WHERE和AND!
	 * <br/> 与搜索参数中的条件以AND方式连接.
	 * 
	 * @param where 自定义的where的sql语句, 不能为null
	 * @throws Exception where为null则抛出异常
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void addExtraWhere(String where, Object...params) throws Exception {
		if(where == null) {
			throw new IllegalArgumentException("错误的自定义Where的Sql语句"); 
		}
		this.above().and(null);
		this.addSearchEntry(SqlMember.WHERE.name(), new SqlPiece(where, params));
	}
	
	/**
	 * 加入自定义的group by的sql语句(注意尾部加空格)
	 * <br/> 需要以 GROUP BY 开头
	 * <br/> 会清理搜索参数中原来设置的GroupBy
	 * 
	 * @param groupBy 自定义的groupBy的sql语句, 不能为null
	 * @throws Exception groupBy为null则抛出异常
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void addExtraGroupBy(String groupBy) throws Exception {
		if(groupBy == null) {
			throw new IllegalArgumentException("错误的自定义GroupBy的Sql语句"); 
		}
		this.isIgnoreGroupBy = true;
		this.clearSearchEntry(SqlMember.GROUP_BY.name());
		this.addSearchEntry(SqlMember.GROUP_BY.name(), new SqlPiece(groupBy));
	}
	
	/**
	 * 加入自定义的order by的sql语句(注意尾部加空格)
	 * <br/> 需要以 ORDER BY 开头
	 * <br/> 会清理搜索参数中原来设置的OrderBy
	 * 
	 * @param orderBy 自定义的orderBy的sql语句, 不能为null
	 * @throws Exception orderBy为null则抛出异常
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void addExtraOrderBy(String orderBy) throws Exception {
		if(orderBy == null) {
			throw new IllegalArgumentException("错误的自定义OrderBy的Sql语句"); 
		}
		this.isIgnoreOrderBy = true;
		this.clearSearchEntry(SqlMember.ORDER_BY.name());
		this.addSearchEntry(SqlMember.ORDER_BY.name(), new SqlPiece(orderBy));
	}
	
	/**
	 * 设置是否忽略Limit语句
	 * @param isIgnoreLimit 是否忽略Limit语句, <tt>true</tt>表示忽略, <tt>false</tt>是默认情况, 表示不忽略
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void setIgnoreLimit(boolean isIgnoreLimit) {
		this.isIgnoreLimit = isIgnoreLimit;
	}
	
	/**
	 * 设置是否忽略GroupBy语句
	 * @param isIgnoreGroupBy 是否忽略GroupBy语句, <tt>true</tt>表示忽略, <tt>false</tt>是默认情况, 表示不忽略
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void setIgnoreGroupBy(boolean isIgnoreGroupBy) {
		this.isIgnoreGroupBy = isIgnoreGroupBy;
	}
	
	/**
	 * 设置是否忽略OrderBy语句
	 * @param isIgnoreOrderBy 是否忽略OrderBy语句, <tt>true</tt>表示忽略, <tt>false</tt>是默认情况, 表示不忽略
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public void setIgnoreOrderBy(boolean isIgnoreOrderBy) {
		this.isIgnoreOrderBy = isIgnoreOrderBy;
	}
	
	/**
	 * 根据当前上下文环境生成查询字段名称
	 * 
	 * @param paramField 查询的搜索参数字段
	 * @return 查询字段名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	String generateQueryFieldName(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField) {
		return paramField.getContextUniqueFieldName(FieldNameGenerateStrategy.DB_FIELD_NAME.getStrategy());
	}
	
	/**
	 * 根据当前上下文环境生成查询字段关联路径中所有字段的定位名称
	 * 
	 * @param paramField 查询的搜索参数字段
	 * @return 查询字段关联路径中所有字段的定位名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	List<String> generatePassedLocateFieldNames(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField) {
		return paramField.getPassedLocateFieldNames(FieldNameGenerateStrategy.OBJ_FIELD_NAME.getStrategy());
	}
	
	@Override
	protected void onJoin(SqlParameter fromParam, JoinType joinType, JoinRelationType relationType,
			ParameterField<SqlParameter, SqlPiece, SqlResult> fromField,
			ParameterField<SqlParameter, SqlPiece, SqlResult> toField,
			SqlResult extraQuery) throws Exception {
		String mappedFromQueryFieldName = fromParam.generateQueryFieldName(fromField);
		String mappedToDbTableAlias = this.getQueryAlias();
		String mappedToDbTableName = this.getQueryName();
		String mappedToQueryFieldName = this.generateQueryFieldName(toField);
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
			onStr = StringUtils.concatAsStr(mappedFromQueryFieldName,
					" = ", mappedToQueryFieldName);
			break;
		case NOT_EQ:
			onStr = StringUtils.concatAsStr(mappedFromQueryFieldName,
					" <> ", mappedToQueryFieldName);
			break;
		case IN:
			onStr = StringUtils.concatAsStr(mappedFromQueryFieldName,
					" IN (", mappedToQueryFieldName, ")");
			break;
		case NOT_IN:
			onStr = StringUtils.concatAsStr(mappedFromQueryFieldName,
					" NOT IN (", mappedToQueryFieldName, ")");
			break;
		}
		// 如果有额外查询, 加入ON后的sql语句
		if(extraQuery != null) {
			onStr = StringUtils.concatAsStr(onStr, " ", extraQuery.getSql());
		}
		this.addSearchEntry(SqlMember.JOIN.name(), new SqlPiece(StringUtils.concatAsStr(joinTypeStr, 
				" ", mappedToDbTableName, " ", mappedToDbTableAlias, " ON ", onStr, " "),
				extraQuery.getVals()));
	}
	
	@Override
	protected SqlResult onJoinExtra() throws Exception {
		SqlResult result = new SqlResult();
		List<SqlPiece> whereSqlPieces = this.getSearchEntry(SqlMember.WHERE.name());
		if(! whereSqlPieces.isEmpty()) {
			result.addSqlPieces(whereSqlPieces);
		}
		return result;
	}

	@Override
	protected SqlResult onBuild(Object... args) throws Exception {
		// 获取构建的模式
		BuildMode buildMode = BuildMode.SELECT_ENTITIES;
		if(args != null && args.length > 0) {
			if(!(args[0] instanceof BuildMode)) {
				throw new IllegalArgumentException("选择的获取结果类型无效, 必须为SqlParameter.BuildMode常量的一项.");
			}
			buildMode = (BuildMode) args[0];
		}
		SqlResult result = buildMode.build(this, args);
		result.addSqlPiece(new SqlPiece(";"));
		return result;
	}
	
	@Override
	protected void onReset(Object... args) throws Exception {
		this.isIgnoreGroupBy = false;
		this.isIgnoreOrderBy = false;
		this.isIgnoreLimit = false;
	}
	
	/**
	 * 解析并生成GroupBy字段的字符串
	 * <br/> 按照优先级进行排序, 优先级越高在越前面, 多个使用CONCAT进行连接
	 * 
	 * @return GroupBy字段的字符串
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private static String generateGroupBySqlStr(SqlParameter param) throws Exception {
		Map<Integer, String> groupBys = new TreeMap<Integer, String>(INT_COMPARATOR);
		Collection<ParameterField<SqlParameter, SqlPiece, SqlResult>> paramFields = 
				param.getParameterContext().getAllParameterFields();
		for(ParameterField<SqlParameter, SqlPiece, SqlResult> paramField : paramFields) {
			String fieldName = param.generateQueryFieldName(paramField);
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
