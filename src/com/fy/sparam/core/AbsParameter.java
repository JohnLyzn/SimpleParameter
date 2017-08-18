package com.fy.sparam.core;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.SearchContext.IRelationalable;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;

/**
 * 搜索参数基类
 * <br/> 适用于关系型数据库进行搜索操作的定义模型, 且符合以对象表达关系型数据库表关系的特性.
 * <p>定义:<table> 
 * <tr><td>搜索参数(Parameter)</td><td>继承并实现{@link AbsParameter}的实现类.</td></tr>
 * <tr><td>搜索参数字段(ParameterField)</td><td>即{@link ParameterField}类.</td></tr>
 * <tr><td>搜索器(Searcher)</td><td>继承并实现{@link AbsSearcher}的实现类.</td></tr>
 * <tr><td>搜索字段(SearchField)</td><td>一个{@link ISearchable}接口的搜索参数成员属性表现类.</td></tr>
 * <tr><td>入口搜索参数(Entrance Parameter)</td><td>调用了{@link AbsParameter #init(Class, Map)}方法的搜索参数, 是搜索参数关系树的根节点.</td></tr>
 * <tr><td>可达搜索参数范围(Reachable Area)</td><td>以某个搜索参数为根下的树型结构中每一个搜索参数节点以及其包含的搜索字段即该搜索参数的可达搜索参数范围</td></tr>
 * </table></p>
 * <strong>非线程安全</strong>
 * 
 * @param <PT>　搜索参数类类型
 * @param <SCT>　搜索内容类类型
 * @param <RT>　搜索结果类类型
 * 
 * @author linjie
 * @since 1.0.1
 */
@SuppressWarnings("unchecked")
public abstract class AbsParameter<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

	/**
	 * 搜索参数初始化接口
	 * 
	 * @param <PT>　搜索参数类类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public interface IInitializor<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

		/**
		 * 获取字段类型转换器集合
		 * 
		 * @return 字段类型转换器的Map, key是字段类型字节码, value是字段类型转换器实现类实例
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		Map<Class<?>, ITransformable<?>> getFieldTransformers();
		
		/**
		 * 初始化搜索参数
		 * 
		 * @param emptyParam 未初始化的搜索参数实例，不能为null
		 * @param paramContext 使用的搜索参数上下文， 不能为null
		 * @param args 可选的自定义额外参数， 可以没有
		 * @throws Exception 初始化失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		void initParameter(PT emptyParam, ParameterContext<PT, SCT, RT> paramContext, Object...args) throws Exception;
		
		/**
		 * 初始化搜索参数字段
		 * 
		 * @param emptyParamField 未初始化的搜索参数字段实例，不能为null
		 * @param args 可选的自定义额外参数， 可以没有
		 * @throws Exception 初始化失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		void initParameterField(ParameterField<PT, SCT, RT> emptyParamField, Object...args) throws Exception;

		/**
		 * 初始化搜索器
		 * 
		 * @param emptySearcher 未初始化的搜索参数器实例，不能为null
		 * @param args 可选的自定义额外参数， 可以没有
		 * @throws Exception 初始化失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		void initSearcher(AbsSearcher<PT, SCT, RT, ?> emptySearcher, Object...args) throws Exception;
	}
	
	/**
	 * 搜索参数类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	static enum ParameterType {
		
		/**
		 * 根类型搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		ROOT,
		
		/**
		 * 默认关联搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		DEFAULT_JOIN,
		
		/**
		 * 继承关联搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		INHERIT_JOIN,
		
		/**
		 * 动态关联搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		DYNAMIC_JOIN;
	}
	
	// 搜索参数上下文
	ParameterContext<PT, SCT, RT> paramContext; /* 整个搜索参数树使用的  */
	// 使用中的关联处理器
	JoinWorker<PT, SCT, RT> usingJoinWorker; /* 如果当前搜索参数不是关联的搜索参数则为null */
	// 当前搜索参数的标志信息
	ParameterType paramType; /* 搜索参数类型 */
	boolean hasInit; /* 当前搜索参数是否完成了初始化 */
	boolean hasSearched; /* 当前搜素参数是否有搜索器发起搜索操作 */
	boolean hasFieldOutput; /* 当前搜索参数是否有搜索参数字段被设置为输出 */
	boolean isAllMyFieldOutput; /* 当前搜索参数是否所有搜索参数字段都被设置为输出 */
	// 搜索参数对应的表信息
	String tableName;
	String tableAlias;
	// 搜索参数的成员对象, 有进行注册时再初始化
	Map<String, ParameterField<PT, SCT, RT>> myParameterFields;
	Map<ParameterField<PT, SCT, RT>, AbsSearcher<PT, SCT, RT, ?>> mySearchers; /* key是搜索器使用的搜索参数字段 */
	Map<ParameterField<PT, SCT, RT>, PT> myDefaultJoinedParams; /* key是关联起点搜索参数字段(属于当前搜索参数的) */
	Map<ParameterField<PT, SCT, RT>, PT> myInheritJoinedParams;
	List<ParameterField<PT, SCT, RT>> ownedParameterFields; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	List<AbsSearcher<PT, SCT, RT, ?>> ownedSearchers; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	Map<ParameterField<PT, SCT, RT>, PT> myDynamicJoinedParams;
	// 随机选一个搜索器引用作为搜索上下文的连接器
	private AbsSearcher<PT, SCT, RT, ?> aboveSearcherRef;
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String getTableName() {
		this.assertHasInit();
		return tableName;
	}

	/**
	 * 
	 * @param tableName
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * 
	 * @param tableAlias
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public Integer getPage() {
		this.assertParameterContextNotNull();
		return this.paramContext.page;
	}

	/**
	 * 
	 * @param page
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public void setPage(int page) {
		this.assertParameterContextNotNull();
		this.paramContext.page = page;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public Integer getCount() {
		this.assertParameterContextNotNull();
		return this.paramContext.count;
	}

	/**
	 * 
	 * @param page
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public void setCount(int count) {
		this.assertParameterContextNotNull();
		this.paramContext.count = count;
	}
	
	/**
	 * 初始化为根搜索参数实例
	 * <br/> 只能进行一次, 已初始化的再次调用无效果.
	 * 
	 * @param intializor 使用搜索参数对象初始化器， 不能为null
	 * @param args 可选的自定义额外参数， 可以没有
	 * @throws Exception 初始化失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void init(IInitializor<PT, SCT, RT> intializor, Object...args) throws Exception {
		if(! this.hasInit) {
			if(intializor == null) {
				throw new IllegalArgumentException("传入的初始化器不能为null");
			}
			PT thisParam = (PT) this;
			// 初始化搜索参数上下文， 并向相关搜索参数传递引用
			this.paramContext = new ParameterContext<PT, SCT, RT>(intializor);
			this.paramType = ParameterType.ROOT;
			this.paramContext.registerRootParameter(thisParam);
			// 初始化搜索参数对象
			this.paramContext.intializor.initParameter(thisParam, paramContext, args);
			this.onInit(args);
			// 设置相关属性
			this.hasInit = true;
			// 生成全局不会冲突的表别名(包括继承, 默认关联的搜索参数的表别名)
			this.paramContext.generateGlobalNonConflictTableAlias(0);
		}
	}

	/**
	 * 
	 * @param param
	 * @param joinType
	 * @param relationType
	 * @param from
	 * @param to
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void join(PT param, JoinType joinType, RelationType relationType,
			ISearchable<?> from, ISearchable<?> to) throws Exception {
		this.assertHasInit();
		if(this.paramType != ParameterType.ROOT) {
			throw new IllegalArgumentException("111");
		}
		if(param == null) {
			throw new IllegalArgumentException("动态关联的搜索参数不能为null");
		}
		if(! param.hasInit) {
			throw new IllegalArgumentException("动态关联的搜索参数不能没完成初始化");
		}
		if(param.paramType != ParameterType.ROOT) {
			throw new IllegalArgumentException("222");
		}
		// 默认关联关系和连接类型
		if(joinType == null) {
			joinType = JoinType.INNER_JOIN;
		}
		if(relationType == null) {
			relationType = RelationType.EQ;
		}
		// 选择最短关联路径的搜索参数, 起终点关联字段
		PT fromParam = (PT) this;
		PT toParam = param;
		if(from == null) {
			throw new IllegalArgumentException("动态关联的起点搜索参数字段不能为null");
		}
		AbsSearcher<PT, SCT, RT, ?> fromSearcher = (AbsSearcher<PT, SCT, RT, ?>) from;
		if(to == null) {
			throw new IllegalArgumentException("动态关联的终点搜索参数字段不能为null");
		}
		AbsSearcher<PT, SCT, RT, ?> toSearcher = (AbsSearcher<PT, SCT, RT, ?>) to;
		// 缓存原来的搜索参数上下文
		
		// 生成新的关联信息
		fromParam.paramType = ParameterType.DYNAMIC_JOIN; /* 一定是动态关联类型 */
		JoinWorker<PT, SCT, RT> joinWorker = JoinWorker.build(fromParam, toParam, joinType, relationType,
				fromSearcher.belongParameterField, toSearcher.belongParameterField);
		toParam.usingJoinWorker = joinWorker;
		// 注册为源关联字段所属的搜索参数的动态关联的搜索参数
		fromParam.registerDynamicJoinedParameter(fromSearcher.belongParameterField, toParam);
		// 反转最短关联路径的关联关系
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final IRelationalable<Object> above() {
		this.assertHasInit();
		if(this.aboveSearcherRef == null) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		return (IRelationalable<Object>) this.aboveSearcherRef;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void ds(Object... params) throws Exception {
		this.assertHasInit();
		if(this.aboveSearcherRef == null) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.aboveSearcherRef.ds(params);
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void de(Object... params) throws Exception {
		this.assertHasInit();
		if(this.aboveSearcherRef == null) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.aboveSearcherRef.de(params);
	}
	
	/**
	 * 
	 * @param joinType
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void changeMappedJoinType(JoinType joinType) {
		this.assertHasInit();
		if(this.paramType == ParameterType.ROOT) {
			throw new IllegalArgumentException("不能改变非关联搜索参数的关联类型");
		}
		if(this.paramType == ParameterType.INHERIT_JOIN) {
			throw new IllegalArgumentException("继承关联搜索参数的关联类型不允许修改");
		}
		this.usingJoinWorker.mappedJoinType = joinType;
	}
	
	/**
	 * 
	 * @param relationType
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void changeMappedRelationType(RelationType relationType) {
		this.assertHasInit();
		if(this.paramType == ParameterType.ROOT) {
			throw new IllegalArgumentException("不能改变非关联搜索参数的关联连接条件类型");
		}
		if(this.paramType == ParameterType.INHERIT_JOIN) {
			throw new IllegalArgumentException("继承关联搜索参数的关联连接条件类型不允许修改");
		}
		this.usingJoinWorker.mappedRelationType = relationType;
	}
	
	/**
	 * 
	 * @param paramPath
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final PT getParameter(String paramPath) {
		this.assertParameterContextNotNull();
		return (PT) this.paramContext.getParameterObjWithStartParam((PT) this, AbsParameter.class, paramPath);
	}
	
	/**
	 * 
	 * @param searcherPath
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final ISearchable<?> getSearcher(String searcherPath) {
		this.assertParameterContextNotNull();
		return this.paramContext.getParameterObjWithStartParam((PT) this, AbsSearcher.class, searcherPath);
	}
	
	/**
	 * 
	 * @param checkParameter
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isMyParameter(PT checkParameter) {
		if(this.equals(checkParameter)) {
			return true;
		}
		if(this.myDefaultJoinedParams == null) {
			return false;
		}
		/* 不包括动态关联(语义错误)和继承关联(不可见) */
		return this.myDefaultJoinedParams.containsValue(checkParameter);
	}
	
	/**
	 * 
	 * @param checkSearcher
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isMySearcher(ISearchable<?> checkSearcher) {
		if(this.mySearchers == null) {
			return false;
		}
		return this.mySearchers.containsValue(checkSearcher);
	}
	
	/**
	 * 
	 * @param checkParameter
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isReachableParameter(PT checkParameter) {
		this.assertParameterContextNotNull();
		return this.paramContext.allParams.containsValue(checkParameter);
	}
	
	/**
	 * 
	 * @param checkSearcher
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean isReachableSearcher(ISearchable<?> checkSearcher) {
		this.assertParameterContextNotNull();
		return this.paramContext.allSearchers.containsValue(checkSearcher);
	}

	/**
	 * 
	 * @param isOupout
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void setAllFieldOutput(boolean isOutput) throws Exception {
		this.assertHasInit();
		// 设置所有字段输出
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.paramContext.getAllSearchersWithStartParam((PT) this)) {
			searcher.belongParameter.isAllMyFieldOutput = isOutput;
			searcher.belongParameter.hasFieldOutput = isOutput;
			searcher.setOutput(isOutput);
		}
	}
	
	/**
	 * 
	 * @param isOutput
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void setAllMyFieldOutput(boolean isOutput) throws Exception {
		this.assertHasInit();
		// 设置当前搜索参数拥有字段(包括继承的)输出
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.ownedSearchers) {
			searcher.belongParameter.isAllMyFieldOutput = isOutput;
			searcher.belongParameter.hasFieldOutput = isOutput;
			searcher.setOutput(isOutput);
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final boolean hasSearched() {
		this.assertHasInit();
		return this.hasSearched;
	}
	
	/**
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final RT build(Object...args) throws Exception {
		this.assertHasInit();
		return null;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void reset(Object...args) throws Exception {
		this.assertHasInit();
	}
	
	/**
	 * 初始化为关联搜索参数(只可能是默认关联和继承关联)
	 * 
	 * @param paramType
	 * @param fromParam
	 * @param joinType
	 * @param relationType
	 * @param fromFieldName
	 * @param toFieldName
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void init(ParameterType paramType, PT fromParam, JoinType joinType, RelationType relationType,
			String fromFieldName, String toFieldName, Object...args) throws Exception {
		if(paramType == null || paramType == ParameterType.ROOT || paramType == ParameterType.DYNAMIC_JOIN) {
			throw new IllegalArgumentException("关联搜索参数类型不能为null或不能为ROOT或DYNAMIC_JOIN");
		}
		// 获取关联起点字段
		ParameterField<PT, SCT, RT> fromParamField = null;
		for(ParameterField<PT, SCT, RT> ownedParameterField : fromParam.ownedParameterFields) {
			if(ownedParameterField.fieldName.equals(fromFieldName) && ! ownedParameterField.isMappedFromField) {
				fromParamField = ownedParameterField;
			}
		}
		if(fromParamField == null) {
			throw new IllegalArgumentException(format(
					"关联起点搜索参数%s中不包含名称为%s的搜索参数字段",
					fromParam.getClass().getName(), fromFieldName));
		}
		if(fromParam.paramContext == null) {
			throw new IllegalArgumentException("传入的搜索参数上下文不能为null");
		}
		this.paramContext = fromParam.paramContext; /* 沿用源搜索参数的搜索参数上下文 */
		this.paramType = paramType; /* 设置关联搜索参数的类型 */
		// 初始化关联搜索参数对象
		this.paramContext.intializor.initParameter((PT) this, paramContext, args);
		this.onInit(args);
		// 完成后初始化后获取关联终点字段
		ParameterField<PT, SCT, RT> toParamField = null;
		for(ParameterField<PT, SCT, RT> ownedParameterField : this.ownedParameterFields) {
			if(ownedParameterField.fieldName.equals(toFieldName) && ! ownedParameterField.isMappedFromField) {
				toParamField = ownedParameterField;
			}
		}
		if(toParamField == null) {
			throw new IllegalArgumentException(format(
					"注册的关联搜索参数%s中不包含名称为%s的搜索参数字段",
					this.getClass().getName(), toFieldName));
		}
		// 设置初始化完成标记
		this.hasInit = true;
		// 建立关联关系
		PT toParam = (PT) this;
		JoinWorker<PT, SCT, RT> joinWorker = JoinWorker.build(fromParam, toParam,
				joinType, relationType,
				fromParamField, toParamField);
		this.usingJoinWorker = joinWorker; /* 设置关联搜索参数的关联处理器*/
		// 设置关联起点字段的属性
		fromParamField.isMappedFromField = true;
		fromParamField.usingSearcher = null;
		// 设置关联终点字段的属性
		toParamField.usingJoinWorker = joinWorker;
		// 如果当前搜索参数是继承关联搜索参数(某个搜索参数的父类), 则需要向其所有关联起点搜索参数添加包含的搜索器以及搜索参数字段到已拥有列表中(父类的搜索器子类都应该有)
		PT currentParam = toParam;
		while(currentParam.paramType == ParameterType.INHERIT_JOIN) {
			PT mappedFromParam = currentParam.usingJoinWorker.mappedFromParam;
			if(mappedFromParam.ownedSearchers == null) {
				mappedFromParam.ownedSearchers = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
			}
			if(mappedFromParam.ownedParameterFields == null) {
				mappedFromParam.ownedParameterFields = new LinkedList<ParameterField<PT, SCT, RT>>();
			}
			mappedFromParam.ownedSearchers.addAll(currentParam.mySearchers.values());
			mappedFromParam.ownedParameterFields.addAll(currentParam.myParameterFields.values());
			currentParam = mappedFromParam;
		}
	}

	/**
	 * 
	 * @param inheritJoinedParam
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerInheritJoinedParameter(PT inheritJoinedParam) throws Exception {
		if(inheritJoinedParam == null  || inheritJoinedParam.paramType != ParameterType.INHERIT_JOIN) {
			throw new IllegalArgumentException("注册的继承关联搜索参数不能为null或不为继承关联类型搜索参数.");
		}
		if(this.myInheritJoinedParams == null) {
			this.myInheritJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		this.myInheritJoinedParams.put(inheritJoinedParam.usingJoinWorker.mappedFromField, inheritJoinedParam);
	}

	/**
	 * 
	 * @param defaultJoinedParam
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerDefaultJoinedParameter(PT defaultJoinedParam) throws Exception {
		if(defaultJoinedParam == null  || defaultJoinedParam.paramType != ParameterType.DEFAULT_JOIN) {
			throw new IllegalArgumentException("注册的默认关联搜索参数不能为null或不为默认关联类型搜索参数.");
		}
		if(this.myDefaultJoinedParams == null) {
			this.myDefaultJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		this.myDefaultJoinedParams.put(defaultJoinedParam.usingJoinWorker.mappedFromField, defaultJoinedParam);
	}

	/**
	 * 
	 * @param mappedFromParamField
	 * @param dynamicJoinedParam
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerDynamicJoinedParameter(ParameterField<PT, SCT, RT> mappedFromParamField,
			PT dynamicJoinedParam) throws Exception {
		this.assertHasInit(); /* 动态关联需要当前搜索参数已经完成初始化 */
		if(mappedFromParamField == null || dynamicJoinedParam == null
				|| ! this.isMyParameterField(mappedFromParamField)) {
			throw new IllegalArgumentException("注册的动态关联搜索参数不能为null, 或其对应"
					+ "的关联起点搜索参数字段不能为null或不属于当前搜索参数");
		}
		if(this.myDynamicJoinedParams == null) {
			this.myDynamicJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		this.myDynamicJoinedParams.put(mappedFromParamField, dynamicJoinedParam);
	}

	/**
	 * 
	 * @param paramField
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerParameterField(ParameterField<PT, SCT, RT> paramField) throws Exception {
		if(paramField == null) {
			throw new IllegalArgumentException("注册的搜索参数字段不能为null");
		}
		if(this.myParameterFields == null) {
			this.myParameterFields = new HashMap<String, ParameterField<PT, SCT, RT>>();
		}
		String fieldName = paramField.getFieldName();
		if(fieldName == null) {
			throw new IllegalArgumentException("注册的搜索参数字段的字段名称不能为null");
		}
		if(this.isMyParameterField(paramField)) {
			throw new IllegalArgumentException(format("当前搜索参数已存在字段名称为%s的搜索参数字段", fieldName));
		}
		this.myParameterFields.put(fieldName, paramField);
		// 把自己的搜索参数字段加入到已拥有搜索参数字段中
		if(this.ownedParameterFields == null) {
			this.ownedParameterFields = new LinkedList<ParameterField<PT, SCT, RT>>();
		}
		this.ownedParameterFields.add(paramField);
		// 设置搜索参数与搜索参数字段的关系
		paramField.belongParameter = (PT) this;
	}

	/**
	 * 
	 * @param searcher
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerSeacher(ParameterField<PT, SCT, RT> belongParamField, 
			AbsSearcher<PT, SCT, RT, ?> searcher) throws Exception {
		if(searcher == null) {
			throw new IllegalArgumentException("注册的搜索器不能为null");
		}
		if(belongParamField == null) {
			throw new IllegalArgumentException("注册搜索器所属的搜索参数字段不能为null");
		}
		if(! this.isMyParameterField(belongParamField)) {
			throw new IllegalArgumentException("搜索器所属的搜索参数字段必须属于当前搜索参数");
		}
		if(this.isMySearcher(searcher)) {
			throw new IllegalArgumentException("当前搜索参数已存在对应搜索参数字段的搜索器");
		}
		if(this.mySearchers == null) {
			this.mySearchers = new HashMap<ParameterField<PT, SCT, RT>, AbsSearcher<PT, SCT, RT, ?>>();
		}
		this.mySearchers.put(belongParamField, searcher);
		// 把自己的搜索器加入到已拥有搜索器中
		if(this.ownedSearchers == null) {
			this.ownedSearchers = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
		}
		this.ownedSearchers.add(searcher);
		// 设置搜索参数&搜索参数字段与搜索器的关系
		searcher.belongParameter = (PT) this;
		searcher.belongParameterField = belongParamField;
		// 同时设置该搜索参数字段使用的搜索器
		belongParamField.usingSearcher = searcher;
		// 随便设置一个搜索器作为above的搜索器引用
		if(this.aboveSearcherRef == null) {
			this.aboveSearcherRef = searcher;
		}
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isRootParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.ROOT;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isDefaultJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.DEFAULT_JOIN;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isInheritJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.INHERIT_JOIN;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	boolean isDynamicJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.DYNAMIC_JOIN;
	}
	
	/**
	 * 
	 * @param searcher
	 * @param isOutput
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void setMyFiledOutPut(AbsSearcher<PT, SCT, RT, ?> searcher, boolean isOutput) throws Exception {
		this.assertHasInit();
		if(! this.ownedSearchers.contains(searcher)) {
			throw new IllegalArgumentException("指定的搜索器必须属于当前搜索参数");
		}
		ParameterField<PT, SCT, RT> searchParamField = searcher.getSearchParameterField();
		PT searchParam = searchParamField.belongParameter;
		// 搜索字段设置为指定的输出类型
		searchParamField.isOutput = isOutput;
		if(isOutput) {
			// 触发关联处理, 添加关联的搜索内容(如果需要的话)
			if(searchParam.usingJoinWorker != null) {
				searchParam.usingJoinWorker.doJoinWork();
			}
			searchParam.hasFieldOutput = true;
			// 继承类型父类字段设置输出等于子类字段设置输出
			PT currentParam = searchParam;
			while(currentParam.paramType == ParameterType.INHERIT_JOIN) {
				currentParam.hasFieldOutput = true;
				if(currentParam.usingJoinWorker == null) {
					break;
				}
				currentParam = currentParam.usingJoinWorker.mappedFromParam;
			}
		} else {
			// 回滚关联的搜索内容(如果需要的话)
			if(searchParam.usingJoinWorker != null) {
				searchParam.usingJoinWorker.rollBackJoinWork();
			}
		}
		// 与全局设置设置输出不符合的, 检查是否所有已拥有字段都已经与传入一致了, 否则重置搜索参数的全局输出标志与传入一致
		if(searchParam.isAllMyFieldOutput != isOutput) {
			boolean isAllSetAsInput = true;
			for(ParameterField<PT, SCT, RT> ownedParamField : searchParam.ownedParameterFields) {
				if(ownedParamField.isOutput != isOutput) {
					isAllSetAsInput = false;
					break;
				}
			}
			if(isAllSetAsInput) {
				this.isAllMyFieldOutput = isOutput;
				if(isOutput == false) {
					searchParam.hasFieldOutput = false;
					// 继承类型父类字段设置不输出等于子类字段设置不输出
					PT currentParam = searchParam;
					while(currentParam.paramType == ParameterType.INHERIT_JOIN) {
						currentParam.hasFieldOutput = false;
						if(currentParam.usingJoinWorker == null) {
							break;
						}
						currentParam = currentParam.usingJoinWorker.mappedFromParam;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected boolean hasInit() {
		return this.hasInit;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected ParameterContext<PT, SCT, RT> getParameterContext() {
		assertHasInit();
		return this.paramContext;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected boolean isAllMyFieldOutput() {
		return isAllMyFieldOutput;
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onInit(Object...args) throws Exception {};
	
	/**
	 * 
	 * @param joinParam
	 * @param joinType
	 * @param relationType
	 * @param from
	 * @param to
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onJoin(PT joinParam, JoinType joinType, RelationType relationType,
			ParameterField<PT, SCT, RT> from, ParameterField<PT, SCT, RT> to) throws Exception;
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onBeforeBuild(Object...args) throws Exception {};
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onBuild(Object...args) throws Exception;
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected void onReset(Object...args) throws Exception {};
	
	/**
	 * 断言搜索参数已完成初始化否则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private void assertParameterContextNotNull() {
		if(this.paramContext == null) {
			throw new IllegalArgumentException("未绑定搜索参数上下文对象");
		}
	}
	
	/**
	 * 断言搜索参数已完成初始化否则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private void assertHasInit() {
		if(! this.hasInit) {
			throw new IllegalArgumentException("搜索参数还没有进行初始化, 请先调用init方法");
		}
	}
	/**
	 * 断言搜索参数已完成初始化否则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private void assertParameterTypeNotNull() {
		if(this.paramType == null) {
			throw new IllegalArgumentException("搜索参数类型未确定");
		}
	}
	
	/**
	 * 
	 * @param checkParamField
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	private boolean isMyParameterField(ParameterField<PT, SCT, RT> checkParamField) {
		if(this.myParameterFields == null) {
			return false;
		}
		return this.myParameterFields.containsValue(checkParamField);
	}
}
