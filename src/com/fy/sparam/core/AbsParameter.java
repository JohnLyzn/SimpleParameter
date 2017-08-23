package com.fy.sparam.core;

import static java.lang.String.format;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.SearchContext.IRelationalable;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.core.SearchContext.SearchContentSource;

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
public abstract class AbsParameter<PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
extends SearchContentSource<SCT> {

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
		Map<Class<?>, ITransformable<?>> getSearcherFieldTransformers();
		
		/**
		 * 
		 * @return 如果无法确定类型将返回null
		 * 
		 * @author linjie
		 * @since 1.0.1
		 */
		Class<?> getSearcherFieldTypeClass(AbsSearcher<PT, SCT, RT, ?> searcher);
		
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
	}
	
	// 搜索参数上下文
	ParameterContext<PT, SCT, RT> paramContext; /* 整个搜索参数树使用的  */
	// 使用中的关联处理器
	JoinWorker<PT, SCT, RT> usingJoinWorker; /* 如果当前搜索参数不是关联的搜索参数则为null */
	// 当前搜索参数的标志信息
	ParameterType paramType; /* 搜索参数类型 */
	boolean hasInit; /* 当前搜索参数是否完成了初始化 */
	boolean hasFieldSearched; /* 当前搜素参数是否有搜索器发起搜索操作 */
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
	Map<ParameterField<PT, SCT, RT>, PT> myDynamicJoinedParams;
	List<ParameterField<PT, SCT, RT>> myOwnedParameterFields; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	List<AbsSearcher<PT, SCT, RT, ?>> myOwnedSearchers; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	
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
	 * @param args 可选的自定义额外参数， 可以没有, 一般参考初始化器实现类的定义
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
			// 生成搜索参数字段相对与根搜索参数的路径
			this.paramContext.generateAllParameterFieldPath();
			// 生成全局不会冲突的表别名(包括继承, 默认关联的搜索参数的表别名)
			this.paramContext.generateGlobalNonConflictTableAlias(0);
		}
	}

	/**
	 * 动态关联其它搜索参数
	 * <br/> 当前搜索参数必须是根搜索参数.
	 * 
	 * @param param 要动态关联的搜索参数实例, 必须是根搜索参数, 不能为null.
	 * @param joinType 关联类型, 如果为null则默认为{@link JoinType.INNER_JOIN}
	 * @param relationType 关联关系类型, 如果为null默认为{@link RelationType.EQ}
	 * @param from 属于当前搜索参数管理范围内的搜索器, 作为关联起点, 不能为null
	 * @param to 属于动态关联的搜索参数管理范围内的搜索器, 作为关联终点, 不能为null
	 * @throws Exception 动态关联失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void join(PT param, JoinType joinType, RelationType relationType,
			ISearchable<?> from, ISearchable<?> to) throws Exception {
		this.assertHasInit();
		if(param == null || ! param.hasInit) {
			throw new IllegalArgumentException("动态关联的搜索参数为null, 或没没完成初始化");
		}
		if(this.paramType != ParameterType.ROOT || param.paramType != ParameterType.ROOT) {
			throw new IllegalArgumentException("发起动态关联方法调用和动态关联的搜索参数不为根搜索参数");
		}
		if(this.usingJoinWorker != null || param.usingJoinWorker != null) {
			throw new IllegalArgumentException("发起动态关联方法调用的搜索参数已动态关联到其它搜索参数, 或指定动态关联的搜索参数已经进行过关联了");
		}
		if(from == null || to == null) {
			throw new IllegalArgumentException("动态关联的起点搜索器或终点搜索器为null");
		}
		// 默认关联关系和连接类型
		if(joinType == null) {
			joinType = JoinType.INNER_JOIN;
		}
		if(relationType == null) {
			relationType = RelationType.EQ;
		}
		// 转换成实际类型
		AbsSearcher<PT, SCT, RT, ?> fromSearcher = (AbsSearcher<PT, SCT, RT, ?>) from;
		if(! this.paramContext.isReachableSeacher(fromSearcher)) {
			throw new IllegalArgumentException("动态关联的起点搜索器必须可以被当前搜索参数管理");
		}
		AbsSearcher<PT, SCT, RT, ?> toSearcher = (AbsSearcher<PT, SCT, RT, ?>) to;
		if(! param.paramContext.isReachableSeacher(toSearcher)) {
			throw new IllegalArgumentException("动态关联的终点搜索器必须可以被动态关联的搜索参数管理");
		}
		// 选择最短关联路径的搜索参数, 起终点关联字段
		ParameterField<PT, SCT, RT> fromParamField = this.paramContext
				.getIndeedSearchParameterField(fromSearcher.belongParameterField);
		ParameterField<PT, SCT, RT> toParamField = param.paramContext
				.getIndeedSearchParameterField(toSearcher.belongParameterField);
		PT fromParam = fromParamField.belongParameter;
		PT toParam = toParamField.belongParameter;
		// 重新动态关联的搜索参数的表别名
		toParam.paramContext.generateGlobalNonConflictTableAlias(this.paramContext.joinCounter);
		// 缓存动态关联的搜索参数和原来的搜索参数上下文(包括了搜索上下文)
		if(this.paramContext.dynamicJoinParamContextPool == null) {
			this.paramContext.dynamicJoinParamContextPool = new HashMap<PT, ParameterContext<PT,SCT,RT>>();
		}
		this.paramContext.dynamicJoinParamContextPool.put(toParam, toParam.paramContext);
		// 注册为关联起点的搜索参数的动态关联的搜索参数
		if(fromParam.myDynamicJoinedParams == null) {
			fromParam.myDynamicJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		fromParam.myDynamicJoinedParams.put(fromParamField, toParam);
		// 重置动态关联搜索参数方的搜索参数上下文
		toParam.paramContext.realDynamicJoinParam = toParam;
		for(PT toIncludeParam : toParam.paramContext.allParams) {
			toIncludeParam.paramContext = this.paramContext;
		}
		// 反转最短关联路径的关联关系
		JoinWorker<PT, SCT, RT> joinWorker = JoinWorker.build(fromParam, toParam, joinType, relationType,
				fromParamField, toParamField);
		toParam.paramContext.reverseParametersJoinDirection(toParam, param, joinWorker);
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final Collection<PT> getDynamicJoinedParameter(String fromFieldPath) {
		this.assertHasInit();
		return null;
	}
	
	/**
	 * 被动态关联的搜索参数解绑动态关联
	 * <br/> 会重置被动态关联的搜索参数在关联期间进行添加的搜索内容
	 * 
	 * @param args 重置搜索参数可选自定义参数, 可以没有
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void unJoin(Object...args) throws Exception {
		if(! this.paramContext.dynamicJoinParamContextPool.containsKey(this)) {
			throw new IllegalArgumentException("当前搜索参数不是动态关联搜索参数");
		}
		ParameterContext<PT, SCT, RT> originalParamContext = this.paramContext.dynamicJoinParamContextPool.get(this);
		this.paramContext.dynamicJoinParamContextPool.remove(this);
		// 重置动态关联搜索参数树包含的搜索参数上下文和搜索内容
		for(PT param : originalParamContext.allParams) {
			// 回调自定义实现方法
			param.onReset(args);
			// 重置搜素参数字段(包括重置搜索内容)
			for(ParameterField<PT, SCT, RT> paramField : param.myParameterFields.values()) {
				paramField.reset();
			}
			// 重置关联搜索内容
			if(param.usingJoinWorker != null) {
				param.usingJoinWorker.cancelJoinWork(false); /* 被动态关联的搜索参数不会再动态关联 */
			}
			// 重置使用的搜索参数上下文
			param.paramContext = originalParamContext;
		}
		// 恢复最短关联路径的关联关系
		originalParamContext.reverseParametersJoinDirection(originalParamContext.realDynamicJoinParam,
				originalParamContext.rootParam, null);
		originalParamContext.realDynamicJoinParam = null;
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
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		return (IRelationalable<Object>) this.myOwnedSearchers.get(0);
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
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.myOwnedSearchers.get(0).ds(params);
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
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.myOwnedSearchers.get(0).de(params);
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
	 * <br/> 不包括动态关联的搜索参数包含的
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
	 * <br/> 不包括动态关联的搜索参数包含的
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
		if(checkSearcher == null || this.mySearchers == null) {
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
		if(checkParameter == null) {
			return false;
		}
		return this.paramContext.isReachableParameter(checkParameter);
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
		if(checkSearcher == null) {
			return false;
		}
		return this.paramContext.isReachableSeacher((AbsSearcher<PT, SCT, RT, ?>) checkSearcher);
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
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.myOwnedSearchers) {
			searcher.belongParameter.isAllMyFieldOutput = isOutput;
			searcher.belongParameter.hasFieldOutput = isOutput;
			searcher.setOutput(isOutput);
		}
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
		// 根搜索参数/非动态搜索参数才能构建结果
		if(this.paramType != ParameterType.ROOT || this.usingJoinWorker != null) {
			throw new IllegalArgumentException("非根搜索参数或被动态关联的搜索参数不能进行结果构建!");
		}
		// 检查分界符有没有成对出现
		if(this.paramContext.delimiterEndCount != this.paramContext.delimiterStartCount) {
			throw new IllegalArgumentException("分界符没有成对出现!");
		}
		// 调用自定义的构建前操作实现
		this.onBeforeBuild(args);
		// 调用构建的实现
		return onBuild(args);
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
		if(this.paramType != ParameterType.ROOT || this.usingJoinWorker != null) {
			throw new IllegalArgumentException("非根搜索参数或被动态关联的搜索参数不能进行重置!");
		}
		// 回调自定义实现方法
		this.onReset(args);
		// 清空搜索内容
		this.paramContext.getCurrentSearchContext().clear();
		// 重置搜素参数字段(重置搜索内容发现全没了就不会处理)
		for(ParameterField<PT, SCT, RT> paramField : this.paramContext.allParamFields) {
			paramField.reset();
		}
		// 重置动态关联搜索内容
		for(PT param : this.paramContext.dynamicJoinParamContextPool.keySet()) {
			param.unJoin(args);
		}
		// 重置关联搜索内容(关联搜索内容已经没有了)
		for(PT param : this.paramContext.allParams) {
			if(param.usingJoinWorker != null) {
				param.usingJoinWorker.hasJoin = false;
			}
		}
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
	final void init(ParameterType paramType, PT fromParam, JoinType joinType, RelationType relationType,
			String fromFieldName, String toFieldName, Object...args) throws Exception {
		if(paramType == null || paramType == ParameterType.ROOT) {
			throw new IllegalArgumentException("关联搜索参数类型不能为null或不能为ROOT");
		}
		// 获取关联起点字段
		ParameterField<PT, SCT, RT> fromParamField = null;
		for(ParameterField<PT, SCT, RT> ownedParameterField : fromParam.myOwnedParameterFields) {
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
		for(ParameterField<PT, SCT, RT> ownedParameterField : this.myOwnedParameterFields) {
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
		fromParamField.usingSearcher = null; /* 作为非动态关联的关联起点字段的搜索器总为null */
		// 如果当前搜索参数是继承关联搜索参数(某个搜索参数的父类), 则需要向其所有关联起点搜索参数添加包含的搜索器以及搜索参数字段到已拥有列表中(父类的搜索器子类都应该有)
		PT currentParam = toParam;
		while(currentParam.paramType == ParameterType.INHERIT_JOIN) {
			PT mappedFromParam = currentParam.usingJoinWorker.mappedFromParam;
			if(mappedFromParam.myOwnedSearchers == null) {
				mappedFromParam.myOwnedSearchers = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
			}
			if(mappedFromParam.myOwnedParameterFields == null) {
				mappedFromParam.myOwnedParameterFields = new LinkedList<ParameterField<PT, SCT, RT>>();
			}
			mappedFromParam.myOwnedSearchers.addAll(currentParam.mySearchers.values());
			mappedFromParam.myOwnedParameterFields.addAll(currentParam.myParameterFields.values());
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
	final void registerInheritJoinedParameter(PT inheritJoinedParam) throws Exception {
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
	final void registerDefaultJoinedParameter(PT defaultJoinedParam) throws Exception {
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
	 * @param paramField
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void registerParameterField(ParameterField<PT, SCT, RT> paramField) throws Exception {
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
		if(this.myOwnedParameterFields == null) {
			this.myOwnedParameterFields = new LinkedList<ParameterField<PT, SCT, RT>>();
		}
		this.myOwnedParameterFields.add(paramField);
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
	final void registerSeacher(ParameterField<PT, SCT, RT> belongParamField, 
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
		if(this.myOwnedSearchers == null) {
			this.myOwnedSearchers = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
		}
		this.myOwnedSearchers.add(searcher);
		// 设置搜索参数&搜索参数字段与搜索器的关系
		searcher.belongParameter = (PT) this;
		searcher.belongParameterField = belongParamField;
		// 同时设置该搜索参数字段使用的搜索器
		belongParamField.usingSearcher = searcher;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final boolean isRootParameter() {
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
	final boolean isDefaultJoinParameter() {
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
	final boolean isInheritJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.INHERIT_JOIN;
	}
	
	/**
	 * 
	 * @param searcher
	 * @param isOutput
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void setMyFiledOutPut(AbsSearcher<PT, SCT, RT, ?> searcher, boolean isOutput) throws Exception {
		this.assertHasInit();
		if(! this.myOwnedSearchers.contains(searcher)) {
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
				searchParam.usingJoinWorker.cancelJoinWork(true);
			}
		}
		// 与全局设置设置输出不符合的, 检查是否所有已拥有字段都已经与传入一致了, 否则重置搜索参数的全局输出标志与传入一致
		if(searchParam.isAllMyFieldOutput != isOutput) {
			boolean isAllSetAsInput = true;
			for(ParameterField<PT, SCT, RT> ownedParamField : searchParam.myOwnedParameterFields) {
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
	protected final boolean hasInit() {
		return this.hasInit;
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected final boolean hasFieldOutput() {
		this.assertHasInit();
		return this.hasFieldOutput;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected final boolean hasFieldSearched() {
		this.assertHasInit();
		return this.hasFieldSearched;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected final ParameterContext<PT, SCT, RT> getParameterContext() {
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
	protected final boolean isAllMyFieldOutput() {
		return isAllMyFieldOutput;
	}
	
	@Override
	protected final void addSearchEntry(String key, SCT searchContent) throws Exception {
		this.assertParameterContextNotNull();
		SearchContext<PT, SCT, RT> usingSearchContext = this.paramContext.getCurrentSearchContext();
		usingSearchContext.addSearchEntry(this, key, searchContent);
	}
	
	@Override
	protected List<SCT> getSearchEntry(String key) throws Exception {
		this.assertParameterContextNotNull();
		SearchContext<PT, SCT, RT> usingSearchContext = this.paramContext.getCurrentSearchContext();
		return usingSearchContext.getSearchEntry(key);
	}
	
	@Override
	protected final void clearSearchEntry(String key) throws Exception {
		this.assertParameterContextNotNull();
		SearchContext<PT, SCT, RT> usingSearchContext = this.paramContext.getCurrentSearchContext();
		usingSearchContext.clearSearchEntry(key);
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
	 * @param fromParam
	 * @param joinType
	 * @param relationType
	 * @param fromField
	 * @param toField
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	protected abstract void onJoin(PT fromParam, JoinType joinType, RelationType relationType,
			ParameterField<PT, SCT, RT> fromField, ParameterField<PT, SCT, RT> toField) throws Exception;
	
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
	protected abstract RT onBuild(Object...args) throws Exception;
	
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
