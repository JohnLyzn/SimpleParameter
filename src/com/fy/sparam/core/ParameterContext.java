package com.fy.sparam.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import com.fy.sparam.core.AbsParameter.IParameterInitializor;
import com.fy.sparam.core.AbsParameter.ParameterType;
import com.fy.sparam.core.JoinWorker.JoinRelationType;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.util.StringUtils;

/**
 * 搜索参数上下文
 * 
 * @param <PT> 搜索参数类类型
 * @param <SCT> 搜索内容类类型
 * @param <RT> 搜索结果类类型
 * 
 * @author linjie
 * @since 1.0.2
 */
public final class ParameterContext<PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
implements Cloneable {
	
	/**
	 * 搜索参数对象接口
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	interface IParameterObj {
		
		/**
		 * 获取当前搜索参数对象在搜索参数树中的路径
		 * 
		 * @return 当前搜索参数对象在搜索参数树中的路径, 如果是根则为空字符串.
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		String getPath() throws Exception;
	}
	
	// 全局使用的搜索参数对象初始化器
	IParameterInitializor<PT, SCT, RT> intializor;
	// 所有搜索字段类型转换器
	Map<Class<?>, ITransformable<?>> fieldTransformer;
	// 克隆相关
	boolean isPrototype;
	// 所有的有关搜索参数和搜索参数字段和搜索器
	PT rootParam;
	Set<PT> allParams = new HashSet<PT>(); /* 不包括继承和动态关联的 */
	Set<AbsSearcher<PT, SCT, RT, ?>> allSearchers = new HashSet<AbsSearcher<PT, SCT, RT, ?>>(); /* 不包括继承和动态关联的 */
	Set<ParameterField<PT, SCT, RT>> allParamFields = new HashSet<ParameterField<PT, SCT, RT>>(); /* 不包括动态关联的 */
	// 当前拥有的搜索内容
	static final String DEFAULT_SEARCH_CONTEXT_NAME = "#DEFAULT#";
	String usingSearchContextName = DEFAULT_SEARCH_CONTEXT_NAME;
	Map<String, SearchContext<PT, SCT, RT>> usingSearchContextMap = new HashMap<String, SearchContext<PT, SCT, RT>>();
	// 分页信息(包括当前搜索参数和后代/关联搜索参数的, 公用一个)
	int page;
	int count;
	// 所有关联搜索参数的处理计数器(防止同类进行关联导致别名冲突)
	int joinCounter;
	// 动态关联相关信息, 一个搜素参数上下文统一管理
	Map<PT, ParameterContext<PT, SCT, RT>> dynamicJoinParamContextPool; /* 动态关联起点的搜索参数树已经动态关联的搜索参数信息 */
	PT realDynamicJoinParam; /* 被动态关联的根搜索参数进行过最短关联处理后实际用来关联的搜索参数 */
	
	/**
	 * 注册继承关联搜索参数
	 * 
	 * @param fromParam 关联起点搜索参数
	 * @param inheritJoinedParam 继承关联的关联终点搜索参数 
	 * @param joinType 关联类型
	 * @param relationType 关联关系类型
	 * @param fromFieldName 关联起点字段名称, 必须是关联起点搜索参数拥有的字段
	 * @param toFieldName 关联终点字段名称, 必须是关联终点搜索参数拥有的字段
	 * @param args 初始化默认关联搜索参数需要的参数, 可能没有
	 * @throws Exception 注册失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void registerInheritJoinedParameter(PT fromParam, PT inheritJoinedParam,
			JoinType joinType, JoinRelationType relationType,
			String fromFieldName, String toFieldName,
			Object...args) throws Exception {
		if(fromParam == null) {
			throw new IllegalArgumentException("注册的继承关联起点搜索参数不能为null");
		}
		if(inheritJoinedParam == null || inheritJoinedParam.hasInit) {
			throw new IllegalArgumentException("注册的继承关联终点搜索参数不能为null或已初始化");
		}
		// 进行继承关联搜索参数的初始化
		inheritJoinedParam.init(ParameterType.INHERIT_JOIN, fromParam, joinType,
				relationType, fromFieldName, toFieldName, args);
		// 注册为关联起点搜索参数的关联搜索参数
		fromParam.registerInheritJoinedParameter(inheritJoinedParam);
		// 注册到上下文
		this.allParams.add(inheritJoinedParam);
	}
	
	/**
	 * 注册默认关联搜索参数
	 * 
	 * @param fromParam 关联起点搜索参数
	 * @param defaultJoinedParam 默认关联的关联终点搜索参数 
	 * @param joinType 关联类型
	 * @param relationType 关联关系类型
	 * @param fromFieldName 关联起点字段名称, 必须是关联起点搜索参数拥有的字段
	 * @param toFieldName 关联终点字段名称, 必须是关联终点搜索参数拥有的字段
	 * @param args 初始化默认关联搜索参数需要的参数, 可能没有
	 * @throws Exception 注册失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void registerDefaultJoinedParameter(PT fromParam, PT defaultJoinedParam,
			JoinType joinType, JoinRelationType relationType,
			String fromFieldName, String toFieldName,
			Object...args) throws Exception {
		if(fromParam == null) {
			throw new IllegalArgumentException("注册的默认关联起点搜索参数不能为null");
		}
		if(defaultJoinedParam == null || defaultJoinedParam.hasInit) {
			throw new IllegalArgumentException("注册的默认关联终点搜索参数不能为null或已初始化化");
		}
		// 进行默认关联搜索参数的初始化
		defaultJoinedParam.init(ParameterType.DEFAULT_JOIN, fromParam, joinType,
				relationType, fromFieldName, toFieldName, args);
		// 注册为源搜索参数的关联搜索参数
		fromParam.registerDefaultJoinedParameter(defaultJoinedParam);
		// 注册到上下文
		this.allParams.add(defaultJoinedParam);
	}
	
	/**
	 * 注册搜索参数字段
	 * 
	 * @param belongParam 字段所属的搜索参数
	 * @param paramField 搜索参数字段
	 * @throws Exception 注册失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void registerParameterField(PT belongParam, ParameterField<PT, SCT, RT> paramField, Object...args) throws Exception {
		// 调用初始化器进行初始化(所属搜索参数中不需要额外操作)
		this.intializor.onInitParameterField(paramField, args);
		// 注册到所属搜索参数中(先注册设置与字段中与搜索参数相关的属性)
		belongParam.registerParameterField(paramField);
		// 注册到上下文中
		this.allParamFields.add(paramField);
	}
	
	/**
	 * 注册搜索器
	 * 
	 * @param belongParam 搜索器所属的搜索参数
	 * @param searcher 搜索器
	 * @throws Exception 注册失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@SuppressWarnings("unchecked")
	public final void registerSeacher(PT belongParam,  ParameterField<PT, SCT, RT> belongParamField,
			ISearchable<?> searcher, Object...args) throws Exception {
		AbsSearcher<PT, SCT, RT, ?> searcherReal = (AbsSearcher<PT, SCT, RT, ?>) searcher;
		// 调用初始化器进行初始化(所属搜索参数中不需要额外操作)
		this.intializor.onInitSearcher(searcherReal, args);
		// 注册到所属搜索参数中
		belongParam.registerSeacher(belongParamField, searcherReal); 
		// 生成路径, 注册到上下文中
		this.allSearchers.add(searcherReal);
	}

	/**
	 * 获取根搜索参数
	 * 
	 * @return 根搜搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final PT getRootParameter() {
		return this.rootParam;
	}
	
	/**
	 * 获取当前搜索参数树中所有搜索参数字段
	 * 
	 * @return 当前搜索参数树中所有搜索参数字段
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getAllParameterFields() {
		List<ParameterField<PT, SCT, RT>> result = 
				new ArrayList<ParameterField<PT, SCT, RT>>(this.recursiveGetAllParameterFieldsCount(0));
		this.recursiveGetAllParameterFields(result);
		return result;
	}
	
	/**
	 * 以指定的搜索参数为起点获取其下可管理的所有搜索参数字段
	 * 
	 * @param startParam 指定的搜索参数
	 * @return 其下可管理的所有搜索参数字段
	 * 
	 * @throws Exception 
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getReachableParameterFieldsWithStartParam(PT startParam) throws Exception {
		if(startParam.paramType == ParameterType.ROOT) {
			return this.allParamFields;
		}
		String paramPath = this.getParameterObjPath(startParam);
		List<ParameterField<PT, SCT, RT>> result = new LinkedList<ParameterField<PT, SCT, RT>>();
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			if(paramField.path.startsWith(paramPath)) {
				result.add(paramField);
			}
		}
		return result;
	}
	
	/**
	 * 获取所有输出所有字段的搜索参数
	 * <br/> 包括默认关联, 继承关联, 动态关联的.
	 * 
	 * @return 所有输出所有字段的搜索参数集合, 如果没有则返回空列表.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<PT> getAllOutputParameters() {
		List<PT> result = new LinkedList<PT>(); 
		for(PT param : this.allParams) {
			/* 稳妥起见使用有字段输出就作为输出的处理, 继承字段在设置所有输出时就被设置输出了, 故也会加入 */
			if(param.hasFieldOutput) {
				result.add(param);
			}
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamContext : this.dynamicJoinParamContextPool.values()) {
				result.addAll(dynamicJoinParamContext.getAllOutputParameters());
			}
		}
		return result;
	}
	
	/**
	 * 获取所有输出的搜索参数字段
	 * <br/> 包括默认关联, 继承关联, 动态关联的.
	 * 
	 * @return 所有输出的搜索参数字段集合, 如果没有则返回空列表.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getAllOutputParameterFields() {
		List<ParameterField<PT, SCT, RT>> result = new LinkedList<ParameterField<PT, SCT, RT>>(); 
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			// 如果字段所属的搜索参数未被触发关联, 则跳过
			if(paramField.belongParameter.usingJoinWorker != null 
					&& ! paramField.belongParameter.usingJoinWorker.hasJoin) {
				continue;
			}
			if(paramField.belongParameter.isAllMyFieldOutput || paramField.isOutput) {
				result.add(paramField);
			}
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamContext : this.dynamicJoinParamContextPool.values()) {
				result.addAll(dynamicJoinParamContext.getAllOutputParameterFields());
			}
		}
		return result;
	}
	
	/**
	 * 获取所有参与分组的搜索参数字段
	 * <br/> 包括默认关联, 继承关联, 动态关联的.
	 * 
	 * @return 所有参与分组的搜索参数字段集合, 如果没有则返回空列表.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getAllGroupByParameterFields() {
		List<ParameterField<PT, SCT, RT>> result = new LinkedList<ParameterField<PT, SCT, RT>>(); 
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			if(paramField.isGroupBy) {
				result.add(paramField);
			}
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamContext : this.dynamicJoinParamContextPool.values()) {
				result.addAll(dynamicJoinParamContext.getAllGroupByParameterFields());
			}
		}
		return result;
	}
	
	/**
	 * 获取所有参与排序的搜索参数字段
	 * <br/> 包括默认关联, 继承关联, 动态关联的.
	 * 
	 * @return 所有参与排序的搜索参数字段集合, 如果没有则返回空列表.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getAllOrderByParameterFields() {
		List<ParameterField<PT, SCT, RT>> result = new LinkedList<ParameterField<PT, SCT, RT>>(); 
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			if(paramField.isOrderBy) {
				result.add(paramField);
			}
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamContext : this.dynamicJoinParamContextPool.values()) {
				result.addAll(dynamicJoinParamContext.getAllOrderByParameterFields());
			}
		}
		return result;
	}
	
	/**
	 * 构造器: 指定初始化器
	 * 
	 * @param intializor 指定的全局初始化器
	 * @throws Exception 初始化失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	ParameterContext(IParameterInitializor<PT, SCT, RT> intializor) throws Exception {
		this.intializor = intializor;
		this.fieldTransformer = intializor.onGetSearcherFieldTransformers();
	}
	
	/**
	 * 注册为根搜索参数
	 * 
	 * @param rootParam 根搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerRootParameter(PT rootParam) {
		if(rootParam.paramType != ParameterType.ROOT) {
			throw new IllegalArgumentException("指定的搜索参数不是根搜索参数");
		}
		if(this.rootParam != null) {
			throw new IllegalArgumentException("已存在根搜索参数");
		}
		this.rootParam = rootParam;
		this.allParams.add(rootParam);
	}
	
	/**
	 * 生成全局唯一的表别名, 在表别名后接数字
	 * <br/> 破坏性的, 不可复原.
	 * 
	 * @param setValContext 用来存储结果值的搜索参数上下文, 可以为null, 如果为null则为当前搜索参数上下文
	 * @param startCount 计数起点, 必须大于0
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void generateGlobalNonConflictQueryAlias(ParameterContext<PT, SCT, RT> setValContext, int startCount) {
		if(startCount < 0) {
			startCount = 0;
		}
		if(setValContext == null) {
			setValContext = this;
		}
		setValContext.joinCounter = startCount;
		for(PT param : this.allParams) {
			param.setQueryAlias(StringUtils.concat(param.getQueryAlias(), "_", setValContext.joinCounter));
			setValContext.joinCounter ++;
		}
	}
	
	/**
	 * 生成所有搜素参数字段的搜索参数对象路径
	 * 
	 * @throws Exception 生成失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void generateAllParameterFieldPath() throws Exception {
		for(PT param : this.allParams) {
			param.path = this.getParameterObjPath(param);
		}
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.allSearchers) {
			searcher.path = this.getParameterObjPath(searcher);
		}
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			paramField.path = this.getParameterObjPath(paramField);
		}
	}
	
	/**
	 * 获取对应类型类字节码可用的搜索器类型解析器
	 * 
	 * @param typeClass 类型类字节码
	 * @return translator 可用的搜索器类型解析器, 如果找不到会返回null
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	@SuppressWarnings("unchecked")
	final <T> ITransformable<T> getFieldTransformer(Class<T> typeClass) {
		if(this.fieldTransformer == null) {
			return null;
		}
		for(Class<?> clazz : this.fieldTransformer.keySet()) {
			if(clazz.equals(typeClass) || clazz.isAssignableFrom(typeClass)) {
				return (ITransformable<T>) this.fieldTransformer.get(clazz);
			}
		}
		return null;
	}
	
	/**
	 * 获取当前搜索上下文
	 * 
	 * @return 当前搜索上下文, 如果原来没有会自动建立一个
	 * 
	 * @throws Exception 创建默认搜索上下文失败则抛出异常
	 *  
	 * @author linjie
	 * @since 1.0.2
	 */
	final SearchContext<PT, SCT, RT> getCurrentSearchContext() {
		if(! this.usingSearchContextMap.containsKey(DEFAULT_SEARCH_CONTEXT_NAME)) {
			try {
				this.registerSearchContext(DEFAULT_SEARCH_CONTEXT_NAME, SearchContext.create());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.usingSearchContextMap.get(this.usingSearchContextName);
	}
	
	/**
	 * 注册一个搜索上下文
	 * 
	 * @param name 搜索上下文的名称
	 * @param searchContext 搜索上下文
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerSearchContext(String name, SearchContext<PT, SCT, RT> searchContext) {
		this.usingSearchContextMap.put(name, searchContext);
	}
	
	/**
	 * 移除搜索上下文
	 * 
	 * @param name 搜索上下文的名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void removeSearchContext(String name) {
		if(name == null || DEFAULT_SEARCH_CONTEXT_NAME.equals(name)) {
			return;
		}
		this.usingSearchContextMap.remove(name);
	}
	
	/**
	 * 切换当前使用的搜索上下文
	 * 
	 * @param name 目标搜索上下文的名称, 如果为null则切换到默认
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void switchUsingSearchContext(String name) {
		if(name == null) {
			this.usingSearchContextName = DEFAULT_SEARCH_CONTEXT_NAME;
			return;
		}
		this.usingSearchContextName = name;
	}
	
	/**
	 * 获取最短关联信息情况下搜索信息(包括表名, 字段名等等)所属的搜索参数字段
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param startParamField 指定的搜索参数字段, 不能为null
	 * @param passParams 在寻找目标字段的过程中路过的所有搜索参数, 可以为null
	 * @param passParamFields 在寻找目标字段的过程中路过的所有搜索参数字段, 可以为null
	 * @return 搜索信息所属的搜索参数字段, 如果没有关联关系则返回传入的搜索参数字段, 不会返回null
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	final ParameterField<PT, SCT, RT> getIndeedSearchParameterField(ParameterField<PT, SCT, RT> startParamField,
			Collection<ParameterField<PT, SCT, RT>> passParamFields) {
		ParameterField<PT, SCT, RT> paramField = startParamField;
		PT param = paramField.belongParameter;
		// 默认加入起点
		if(passParamFields != null) {
			passParamFields.add(startParamField);
		}
		// 如果所属搜索参数是关联搜索参数且指定字段是该搜素参数关联关系中关联终点字段, 则往上找最少关联信息的搜索参数字段
		while(param.usingJoinWorker != null && startParamField.equals(param.usingJoinWorker.mappedField)) {
			// 获取关联头端的关联信息
			paramField = param.usingJoinWorker.mappedFromField;
			param = param.usingJoinWorker.mappedFromParam;
			// 如果需要获取路径, 则加入容器中
			if(passParamFields != null) {
				passParamFields.add(paramField);
			}
		}
		return paramField;
	}
	
	/**
	 * 在默认关联的后代搜索参数沿着关联链进行查询以指定的搜索字段为代表操作的字段的搜索参数字段
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param startParamField 指定的搜索字段, 必须是被代表操作的搜索参数字段, 不能为null
	 * @param passParams 在寻找目标字段的过程中路过的所有搜索参数, 可以为null
	 * @param passParamFields 在寻找目标字段的过程中路过的所有搜索参数字段, 可以为null
	 * @return 以指定的搜索字段为代表操作的字段的搜索参数字段, 如果不是关联头端或没有找到目标则返回传入的搜索参数字段, 不会返回null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final List<ParameterField<PT, SCT, RT>> getIndeedRepresentParamFields(
			ParameterField<PT, SCT, RT> startParamField,
			Collection<ParameterField<PT, SCT, RT>> passParamFields) {
		Set<ParameterField<PT, SCT, RT>> representFields = startParamField.representOptFields;
		List<ParameterField<PT, SCT, RT>> results = new LinkedList<ParameterField<PT, SCT, RT>>();
		if(representFields != null && ! representFields.isEmpty()) {
			for(ParameterField<PT, SCT, RT> representField : representFields) {
				ParameterField<PT, SCT, RT> result = this.getIndeedSearchParameterField(
						representField, passParamFields);
				results.add(result);
			}
		} else if(passParamFields != null) {
			passParamFields.add(startParamField);
		}
		return results;
	}
	
	/**
	 * 获取某个继承关联的搜索参数的最终子类搜索参数, 如果不是则返回自己
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param param 搜索参数
	 * @return 最终子类搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final PT getInheritEndParameter(PT param) {
		if(param == null) {
			return null;
		}
		PT current = param;
		// 如果该字段是继承的, 则获取关联头搜索参数, 直到不再是继承关联为止
		while(current.isInheritJoinParameter()) {
			current = current.usingJoinWorker.mappedFromParam;
		}
		return current;
	}
	
	/**
	 * 反转指定关联路径上的搜索参数关联关系
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param start 开始搜索参数
	 * @param end 结束搜索参数
	 * @param endSetJoinWorker 结束时设置最后一个搜索参数的关联处理器
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	final void reverseParametersJoinDirection(PT start, PT end, JoinWorker<PT, SCT, RT> endSetJoinWorker) {
		// 如果开始等于结尾或开始的关联处理器为null, 则不用处理
		if(! start.equals(end) && start.usingJoinWorker != null) {
			// 利用栈, 先压后出使得能够获取相反顺序, 不包括end(独立处理)
			Stack<PT> stack = new Stack<PT>();
			stack.push(start);
			PT current = start;
			while(! current.equals(end)) {
				if(current.usingJoinWorker == null) {
					throw new IllegalArgumentException("关联路径上存在非关联搜索参数");
				}
				current = current.usingJoinWorker.mappedFromParam;
				stack.push(current);
			}
			// 出栈倒置关联关系
			current = stack.pop();
			while(! stack.isEmpty()) {
				PT currentLast = stack.pop();
				if(currentLast.usingJoinWorker != null) {
					// 反转关联方向, 并改变拥有关联处理器的搜索参数
					JoinWorker<PT, SCT, RT> tmpJoinWorker = currentLast.usingJoinWorker;
					tmpJoinWorker.reverseJoinRelation();
					currentLast.usingJoinWorker = current.usingJoinWorker;
					current.usingJoinWorker = tmpJoinWorker;
				}
			}
		}
		// 反转后原来链表的start即现在关联链表的end, 对现在关联链表的end(即start)设置指定的关联处理器
		start.usingJoinWorker = endSetJoinWorker;
	}
	
	/**
	 * 判断某个搜索参数是否是当前搜索上下文可管理的搜索参数
	 * <br> 包括默认和动态关联的搜索参数, 继承的部分不判断(不应该被暴露).
	 * 
	 * @param param 需要判断的搜索参数
	 * @return 判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isReachableParameter(PT param) {
		/* 继承的不会直接暴露出来 */
		if(this.allParams.contains(param)) {
			return true;
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamSrcContext : this.dynamicJoinParamContextPool.values()) {
				if(dynamicJoinParamSrcContext.isReachableParameter(param)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 判断某个搜索参数是否是当前搜索上下文可管理的搜索器
	 * <br> 包括默认和动态关联的搜索参数的搜索器, 继承的部分不判断(不应该被暴露).
	 * 
	 * @param searcher 需要判断的搜索器
	 * @return 判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isReachableSeacher(AbsSearcher<PT, SCT, RT, ?> searcher) {
		/* 继承的不会直接暴露出来 */
		if(this.allSearchers.contains(searcher)) {
			return true;
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> dynamicJoinParamSrcContext : this.dynamicJoinParamContextPool.values()) {
				if(dynamicJoinParamSrcContext.isReachableSeacher(searcher)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取搜索参数对象的路径
	 * <br/> 不能处理动态关联的对象字段, 每课搜索参数对象树都是独立的.
	 * 
	 * @param target 目标搜索参数对象, 只能是搜索参数或搜索器
	 * @return 该搜索参数对象的路径
	 * 
	 * @throws Exception 构造失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@SuppressWarnings("unchecked")
	final String getParameterObjPath(IParameterObj target) throws Exception {
		String path = target.getPath();
		if(path != null) {
			return path;
		}
		// 判断类型
		boolean isParam = target instanceof AbsParameter;
		boolean isSearcher = target instanceof AbsSearcher;
		boolean isParamField = target instanceof ParameterField;
		// 准备强制转换类型
		PT param = null;
		ParameterField<PT, SCT, RT> paramField = null;
		AbsSearcher<PT, SCT, RT, ?> searcher = null;
		// 对搜索器类型
		if(isParam) {
			param = (PT) target;
		}
		if(isSearcher) {
			searcher = (AbsSearcher<PT, SCT, RT, ?>) target;
			paramField = searcher.belongParameterField;
			param = searcher.belongParameter;
		}
		if(isParamField) {
			paramField = (ParameterField<PT, SCT, RT>) target;
			param = paramField.belongParameter;
		}
		// 如果是继承搜索参数, 需要使用最终子类搜索参数作为起点, 这样保证了下面进行的处理的搜索参数要不是根搜索参数要不默认关联搜索参数
		while(param.isInheritJoinParameter()) {
			param = param.usingJoinWorker.mappedFromParam;
		}
		// 默认关联的搜索参数对象
		if(param.usingJoinWorker != null) {
			if(isParam) {
				param.path = this.getParameterObjPath(param.usingJoinWorker.mappedFromField);
				return param.path;
			}
			if(isSearcher) {
				searcher.path = StringUtils.concat(
						this.getParameterObjPath(param.usingJoinWorker.mappedFromField),
						".", paramField.fieldName);
				return searcher.path;
			}
			if(isParamField) {
				paramField.path = StringUtils.concat(
						this.getParameterObjPath(param.usingJoinWorker.mappedFromField),
						".", paramField.fieldName);
				return paramField.path;
			}
		}
		// 根搜索参数对象
		if(isSearcher) {
			return searcher.belongParameterField.fieldName; /* 根搜索参数的搜素器 */
		}
		if(isParamField) {
			return paramField.fieldName; /* 根搜索参数的搜素器 */
		}
		return ""; /* 为根搜索参数 */
	}
	
	/**
	 * 以指定的搜索参数为起点, 往下找路径对应的搜索参数对象
	 * 
	 * @param startParam 起点搜索参数
	 * @param targetClass 目标搜索参数对象的类字节码
	 * @param path 搜索参数对象路径
	 * @return 对应路径的搜索参数对象, 找不到则返回null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@SuppressWarnings("unchecked")
	final <POT extends IParameterObj> POT getParameterObjWithStartParam(PT startParam,
			Class<POT> targetClass, String targetPath) {
		if(targetClass == null) {
			throw new IllegalArgumentException("根据路径获取搜索参数对象时结果类类型不能为null");
		}
		if(targetPath == null) { /* 为null当做空字符串处理 */
			targetPath = ""; 
		}
		if(targetPath.toLowerCase().equals("this")) { /* 为this也当做空字符串处理 */
			targetPath = "";
		}
		String path = targetPath;
		if(path.toLowerCase().startsWith("this.")) { /* 如果有this.开头, 去掉 */
			path = path.substring(4, path.length());
		}
		boolean needParam = AbsParameter.class.isAssignableFrom(targetClass);
		boolean needSearcher = AbsSearcher.class.isAssignableFrom(targetClass);
		if(! needParam && ! needSearcher) {
			throw new IllegalArgumentException("获取的搜索参数对象的类型只能是搜索参数, 搜索器");
		}
		if(targetPath.isEmpty()) { /* 请求获取搜索参数且目标路径为空(包括为null或this)则直接返回起点搜索参数 */
			if(needParam) {
				return (POT) startParam; 
			}
			return null;
		}
		Object result = null;
		PT currentParam = startParam;
		// 如果包含了., 说明是多级路径, 前面都是搜索参数类型的字段
		if(path.contains(".")) {
			String[] fieldNames = path.split("\\.");
			int len = fieldNames.length;
			for(int i = 0; i < len - 1; i ++) { /* 只需要遍历到倒数第二个即可, 最后一个独立处理 */
				String fieldName = fieldNames[i];
				if(! fieldName.trim().isEmpty()) {
					PT foundParam = null; /* 临时变量, 来辅助判断是否找到了搜索参数 */
					if(currentParam.myDefaultJoinedParams != null) { /* 只认默认关联搜索参数 */
						for(Entry<ParameterField<PT, SCT, RT>, PT> entry 
								: currentParam.myDefaultJoinedParams.entrySet()) {
							ParameterField<PT, SCT, RT> paramField = entry.getKey();
							if(paramField.getFieldName().equals(fieldName)) {
								foundParam = entry.getValue();
							}
						}
					}
					if(foundParam == null) {
						return null;
					}
					currentParam = foundParam;
				}
			}
			path = fieldNames[len - 1];
		}
		// 最后一个片段(如果前面有'.'分隔运行到这里也只剩一个)就是目标对象的属性名称
		if(needParam) { /* 要返回搜索参数类型 */
			if(currentParam.myDefaultJoinedParams != null) {
				for(Entry<ParameterField<PT, SCT, RT>, PT> entry 
						: currentParam.myDefaultJoinedParams.entrySet()) {
					ParameterField<PT, SCT, RT> paramField = entry.getKey();
					if(paramField.getFieldName().equals(path)) {
						result = entry.getValue();
						break;
					}
				}
			}
		}
		if(needSearcher) { /* 要返回搜索器类型 */
			// 找到对应名字的字段, 包括继承的搜索器(继承的是不可能重复名字的, 编译报错)
			for(AbsSearcher<PT, SCT, RT, ?> searcher  : currentParam.myOwnedSearchers) {
				ParameterField<PT, SCT, RT> paramField = searcher.getBelongParameterField();
				if(paramField.getFieldName().equals(path)) {
					result = searcher;
					break;
				}
			}
		}
		return (POT) result;
	}
	
	/**
	 * 以指定的搜索参数为起点, 往下找所有可找到的搜索器
	 * 
	 * @param startParam 指定的搜索参数
	 * @return 所有可找到的搜索器
	 * 
	 * @throws Exception 
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final Collection<AbsSearcher<PT, SCT, RT, ?>> getAllSearchersWithStartParam(PT startParam) throws Exception {
		if(startParam.paramType == ParameterType.ROOT) {
			return this.allSearchers;
		}
		String paramPath = this.getParameterObjPath(startParam);
		List<AbsSearcher<PT, SCT, RT, ?>> result = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.allSearchers) {
			if(searcher.belongParameterField.path.startsWith(paramPath)) {
				result.add(searcher);
			}
		}
		return result;
	}
	
	/**
	 * 克隆一个搜索参数上下文, 重置相关引用信息
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ParameterContext<PT, SCT, RT> clone() throws CloneNotSupportedException {
		ParameterContext<PT, SCT, RT> cloneParamContext = (ParameterContext<PT, SCT, RT>) super.clone();
		/* 重用初始化器实例和字段类型转换器缓存, page和count */
		cloneParamContext.rootParam = null;
		cloneParamContext.allParams = new HashSet<PT>(); /* 不包括继承和动态关联的 */
		cloneParamContext.allSearchers = new HashSet<AbsSearcher<PT, SCT, RT, ?>>(); /* 不包括继承和动态关联的 */
		cloneParamContext.allParamFields = new HashSet<ParameterField<PT, SCT, RT>>(); /* 不包括动态关联的 */
		cloneParamContext.usingSearchContextName = DEFAULT_SEARCH_CONTEXT_NAME;
		cloneParamContext.usingSearchContextMap = new HashMap<String, SearchContext<PT, SCT, RT>>();
		cloneParamContext.joinCounter = 0;
		cloneParamContext.dynamicJoinParamContextPool = null;
		cloneParamContext.realDynamicJoinParam = null;
		return cloneParamContext;
	}
	
	/**
	 * 辅助方法: 递归获取所有的搜索参数字段数量, 包括动态关联的部分
	 * 
	 * @param currentCount 已计算的数量
	 * @return 新计算的数量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private int recursiveGetAllParameterFieldsCount(int currentCount) {
		currentCount += this.allParamFields.size();
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> paramContext : this.dynamicJoinParamContextPool.values()) {
				currentCount = paramContext.recursiveGetAllParameterFieldsCount(currentCount);
			}
		}
		return currentCount;
	}
	
	/**
	 * 辅助方法: 递归获取所有搜索参数字段, 包括动态关联的部分
	 * 
	 * @param container 存放结果的容器
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	private void recursiveGetAllParameterFields(Collection<ParameterField<PT, SCT, RT>> container) {
		container.addAll(this.allParamFields);
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(ParameterContext<PT, SCT, RT> paramContext : this.dynamicJoinParamContextPool.values()) {
				paramContext.recursiveGetAllParameterFields(container);
			}
		}
	}
}
