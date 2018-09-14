package com.fy.sparam.core;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.ParameterContext.IParameterObj;
import com.fy.sparam.core.SearchContext.IRelationalable;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.core.SearchContext.SearchContentSource;
import com.fy.sparam.util.StringUtils;

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
 * @since 1.0.2
 */
@SuppressWarnings("unchecked")
public abstract class AbsParameter<PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
extends SearchContentSource<SCT>
implements IParameterObj, Cloneable {

	/**
	 * 搜索参数初始化接口
	 * <br/> 初始化顺序为: 搜索参数(继承关联 > 默认关联) -> 搜索参数字段 -> 搜索器
	 * 
	 * @param <PT>　搜索参数类类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public interface IInitializor<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {

		/**
		 * 获取搜索字段类型转换器集合
		 * 
		 * @return 字段类型转换器的Map, key是字段类型字节码, value是字段类型转换器实现类实例
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		Map<Class<?>, ITransformable<?>> getSearcherFieldTransformers();
		
		/**
		 * 获取搜索字段的类型字节码
		 * 
		 * @return 如果无法确定类型将返回null
		 * 
		 * @author linjie
		 * @since 1.0.2
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
		 * @since 1.0.2
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
		 * @since 1.0.2
		 */
		void initParameterField(ParameterField<PT, SCT, RT> emptyParamField, Object...args) throws Exception;

		/**
		 * 初始化搜索器
		 * 
		 * @param emptySearcher 未初始化的搜索参数器实例, 不能为null
		 * @param args 可选的自定义额外参数， 可以没有
		 * @throws Exception 初始化失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void initSearcher(AbsSearcher<PT, SCT, RT, ?> emptySearcher, Object...args) throws Exception;
		
		/**
		 * 在克隆搜索参数完成对关联搜索参数克隆后的回调
		 * 
		 * @param ownerParameter 拥有该关联搜索参数的搜索参数(继承关联是最终子类), 不能为null
		 * @param fromParamField 关联起点搜索参数字段, 不能为null
		 * @param clonedJoinParam 克隆的关联搜索参数, 不能为null
		 * @throws Exception 处理失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void onDoneCloneJoinedParameter(PT ownerParameter, ParameterField<PT, SCT, RT> fromParamField,
				PT clonedJoinParam) throws Exception;
		
		/**
		 * 在克隆搜索参数完成对包含搜索器克隆后的回调
		 * 
		 * @param ownerParameter 拥有该搜索器的搜索参数(继承关联是最终子类), 不能为null
		 * @param belongParamField 搜索器所属的搜索参数字段, 不能为null
		 * @param clonedSearcher 克隆的搜索器, 不能为null
		 * @throws Exception 处理失败则抛出异常
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		void onDoneCloneSearcher(PT ownerParameter, ParameterField<PT, SCT, RT> belongParamField,
				AbsSearcher<PT, SCT, RT, ?> clonedSearcher) throws Exception;
	}
	
	/**
	 * 搜索参数类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	static enum ParameterType {
		
		/**
		 * 根类型搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		ROOT,
		
		/**
		 * 默认关联搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		DEFAULT_JOIN,
		
		/**
		 * 继承关联搜索参数
		 * 
		 * @author linjie
		 * @since 1.0.2
		 */
		INHERIT_JOIN,
	}
	
	// 搜索参数上下文
	ParameterContext<PT, SCT, RT> paramContext; /* 整个搜索参数树使用的  */
	// 使用中的关联处理器
	JoinWorker<PT, SCT, RT> usingJoinWorker; /* 如果当前搜索参数不是关联的搜索参数则为null */
	// 当前搜索参数的标志信息
	ParameterType paramType; /* 搜索参数类型 */
	String path;
	boolean hasInit; /* 当前搜索参数是否完成了初始化 */
	boolean hasFieldSearched; /* 当前搜素参数是否有搜索器发起搜索操作 */
	boolean hasFieldOutput; /* 当前搜索参数是否有搜索参数字段被设置为输出 */
	boolean isAllMyFieldOutput; /* 当前搜索参数是否所有搜索参数字段都被设置为输出 */
	// 搜索参数对应的表信息
	String tableName;
	String tableAlias;
	// 搜索参数的私有成员对象, 有进行注册时再初始化
	Map<String, ParameterField<PT, SCT, RT>> myParameterFields;
	Map<ParameterField<PT, SCT, RT>, AbsSearcher<PT, SCT, RT, ?>> mySearchers; 
	Map<ParameterField<PT, SCT, RT>, PT> myDefaultJoinedParams;
	Map<ParameterField<PT, SCT, RT>, PT> myInheritJoinedParams;
	Map<ParameterField<PT, SCT, RT>, PT> myDynamicJoinedParams;
	// 搜索参数的包含成员对象(针对继承的对象的缓存),  有进行注册时再初始化
	Set<PT> myOwnedInheritedFromParameters; /* 包括当前搜索参数继承的所有父级搜索参数 */
	Set<PT> myOwnedDefaultJoinedParameters; /* 包括当前搜索参数的和所有继承的父级搜索参数的默认关联搜索参数 */
	Set<ParameterField<PT, SCT, RT>> myOwnedParameterFields; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	Set<AbsSearcher<PT, SCT, RT, ?>> myOwnedSearchers; /* 包括当前搜索参数子级的和所有继承的父级搜索参数的搜索器 */
	
	/**
	 * 获取表名称
	 * 
	 * @return 表名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String getTableName() {
		this.assertHasInit();
		return tableName;
	}

	/**
	 * 设置表名称
	 * 
	 * @param tableName 表名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 获取表别名
	 * 
	 * @return 表别名
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String getTableAlias() {
		return tableAlias;
	}

	/**
	 * 设置表别名
	 * 
	 * @param tableAlias 表别名
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	/**
	 * 获取当前分页的页码
	 * 
	 * @return 当前分页的页码
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public Integer getPage() {
		this.assertParameterContextNotNull();
		return this.paramContext.page;
	}

	/**
	 * 设置当前分页的页码
	 * 
	 * @param page 当前分页的页码
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public void setPage(int page) {
		this.assertParameterContextNotNull();
		this.paramContext.page = page;
	}
	
	/**
	 * 获取当前分页的单页包含记录数量
	 * 
	 * @return 分页的单页包含记录数量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public Integer getCount() {
		this.assertParameterContextNotNull();
		return this.paramContext.count;
	}

	/**
	 * 设置分页的单页包含记录数量
	 * 
	 * @param page 分页的单页包含记录数量
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public void setCount(int count) {
		this.assertParameterContextNotNull();
		this.paramContext.count = count;
	}
	
	/**
	 * 初始化为根搜索参数实例, 同时对指定搜索参数树关系中的搜索参数进行初始化
	 * <br/> 只能进行一次, 已初始化的再次调用无效果.
	 * 
	 * @param intializor 使用搜索参数对象初始化器， 不能为null
	 * @param args 可选的自定义额外参数， 可以没有, 一般参考初始化器实现类的定义
	 * @throws Exception 初始化失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void init(IInitializor<PT, SCT, RT> intializor, Object...args) throws Exception {
		if(! this.hasInit) {
			if(intializor == null) {
				throw new IllegalArgumentException("传入的初始化器不能为null");
			}
			PT thisParam = (PT) this;
			// 初始化搜索参数上下文， 并向相关搜索参数传递引用
			this.paramContext = new ParameterContext<PT, SCT, RT>(intializor);
			// 这是个原型
			this.paramContext.isPrototype = true;
			// 当前搜索参数作为根搜索参数
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
			this.paramContext.generateGlobalNonConflictTableAlias(null, 0);
		}
	}

	/**
	 * 动态关联其它搜索参数
	 * <br/> 如果当前搜索参数/指定关联的搜索参数不是根搜索参数会自动找到根搜索参数并进行关联.
	 * 
	 * @param param 要动态关联的搜索参数实例, 不能为null.
	 * @param joinType 关联类型, 如果为null则默认为{@link JoinType.INNER_JOIN}
	 * @param relationType 关联关系类型, 如果为null默认为{@link RelationType.EQ}
	 * @param from 属于当前搜索参数管理范围内的搜索器, 作为关联起点, 不能为null
	 * @param to 属于动态关联的搜索参数管理范围内的搜索器, 作为关联终点, 不能为null
	 * @throws Exception 动态关联失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void join(PT param, JoinType joinType, RelationType relationType,
			ISearchable<?> from, ISearchable<?> to) throws Exception {
		this.assertHasInit();
		if(param == null || ! param.hasInit) {
			throw new IllegalArgumentException("动态关联的搜索参数为null, 或没没完成初始化");
		}
		if(from == null || to == null) {
			throw new IllegalArgumentException("动态关联的起点搜索器或终点搜索器为null");
		}
		PT fromRootParam = this.paramContext.rootParam;
		PT toRootParam = param.paramContext.rootParam;
		if(param.paramContext.dynamicJoinParamContextPool != null
				&& ! param.paramContext.dynamicJoinParamContextPool.isEmpty()) {
			throw new IllegalArgumentException("发起动态关联方法调用的搜索参数已动态关联到其它搜索参数, 不能重复关联");
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
		if(! fromRootParam.paramContext.isReachableSeacher(fromSearcher)) {
			throw new IllegalArgumentException("动态关联的起点搜索器必须可以被当前搜索参数管理");
		}
		AbsSearcher<PT, SCT, RT, ?> toSearcher = (AbsSearcher<PT, SCT, RT, ?>) to;
		if(! toRootParam.paramContext.isReachableSeacher(toSearcher)) {
			throw new IllegalArgumentException("动态关联的终点搜索器必须可以被动态关联的搜索参数管理");
		}
		// 选择最短关联路径的搜索参数, 起终点关联字段
		ParameterField<PT, SCT, RT> fromParamField = fromRootParam.paramContext
				.getIndeedSearchParameterField(fromSearcher.belongParameterField, null);
		ParameterField<PT, SCT, RT> toParamField = toRootParam.paramContext
				.getIndeedSearchParameterField(toSearcher.belongParameterField, null);
		PT fromParam = fromParamField.belongParameter;
		PT toParam = toParamField.belongParameter;
		// 缓存动态关联的搜索参数和原来的搜索参数上下文(包括了搜索上下文)
		if(fromParam.paramContext.dynamicJoinParamContextPool == null) {
			fromParam.paramContext.dynamicJoinParamContextPool = new HashMap<PT, ParameterContext<PT,SCT,RT>>();
		}
		fromParam.paramContext.dynamicJoinParamContextPool.put(toRootParam, toParam.paramContext);
		// 注册为关联起点的搜索参数的动态关联的搜索参数
		if(fromParam.myDynamicJoinedParams == null) {
			fromParam.myDynamicJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		fromParam.myDynamicJoinedParams.put(fromParamField, toParam);
		// 重新动态关联的搜索参数的表别名, 使用被关联的搜索参数中的计数器
		toParam.paramContext.generateGlobalNonConflictTableAlias(fromParam.paramContext,
				fromParam.paramContext.joinCounter);
		// 在被动态关联的搜索参数树的搜索参数上下文中缓存实际被动态关联的搜索参数(不一定是根)
		toParam.paramContext.realDynamicJoinParam = toParam;
		// 重置动态关联搜索参数方的搜索参数上下文
		for(PT toIncludeParam : toParam.paramContext.allParams) {
			toIncludeParam.paramContext = fromRootParam.paramContext;
		}
		// 反转最短关联路径的关联关系
		JoinWorker<PT, SCT, RT> joinWorker = JoinWorker.build(fromParam, toParam, joinType, relationType,
				fromParamField, toParamField);
		toParam.paramContext.reverseParametersJoinDirection(toParam, toRootParam, joinWorker);
	}

	/**
	 * 根据关联起点搜索参数字段获取动态关联的搜索参数, 可能有多个
	 * 
	 * @return 动态关联的搜索参数实例列表, 不会为null, 如果没有返回空列表
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<PT> getDynamicJoinedParameters(String fromFieldPath) {
		this.assertHasInit();
		if(this.paramContext.dynamicJoinParamContextPool == null
				|| this.paramContext.dynamicJoinParamContextPool.isEmpty()) {
			return Collections.emptyList();
		}
		List<PT> result = new ArrayList<PT>(this.paramContext.dynamicJoinParamContextPool.size());
		for(Entry<PT, ParameterContext<PT, SCT, RT>> dynamicJoinedParamEntry : 
			this.paramContext.dynamicJoinParamContextPool.entrySet()) {
			if(dynamicJoinedParamEntry.getValue().realDynamicJoinParam
				.usingJoinWorker.mappedFromField.path.equals(fromFieldPath)) {
				result.add(dynamicJoinedParamEntry.getKey());
			}
		}
		return result;
	}
	
	/**
	 * 获取当前搜索参数树所有动态关联的搜索参数
	 * 
	 * @return 所有动态关联的搜索参数列表, 不会为null, 如果没有返回空列表
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final Collection<PT> getAllDynamicJoinedParameters() {
		this.assertHasInit();
		if(this.paramContext.dynamicJoinParamContextPool == null
				|| this.paramContext.dynamicJoinParamContextPool.isEmpty()) {
			return Collections.emptyList();
		}
		return this.paramContext.dynamicJoinParamContextPool.keySet();
	}
	
	/**
	 * 被动态关联的搜索参数解绑动态关联
	 * <br/> 会重置被动态关联的搜索参数在关联期间进行添加的搜索内容
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void unJoin() throws Exception {
		this.unJoin(true);
	}
	
	/**
	 * 代表上文搜索内容, 方便用来跨段的添加搜索内容
	 * 
	 * @return 关系连接操作接口实现类, 不会为null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final IRelationalable<Object> above() {
		this.assertHasInit();
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		return (IRelationalable<Object>) this.myOwnedSearchers.iterator().next();
	}
	
	/**
	 * 分割符开始, 同{@link IRelationalable#ds(Object...)}, 方便用来跨段的添加开始分割符
	 * 
	 * @param args 进行添加开始分割符需要的自定义参数, 可以没有
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void ds(Object... args) throws Exception {
		this.assertHasInit();
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.myOwnedSearchers.iterator().next().ds(args);
	}
	
	/**
	 * 分割符结束, 同{@link IRelationalable#de(Object...)}, 方便用来跨段的添加结束分割符
	 * 
	 * @param args 进行添加结束分割符需要的自定义参数, 可以没有
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void de(Object... args) throws Exception {
		this.assertHasInit();
		if(this.myOwnedSearchers == null || this.myOwnedSearchers.isEmpty()) {
			throw new IllegalArgumentException(format("初始化的搜索参数%s不包含任何搜索器",
					this.getClass().getName()));
		}
		this.myOwnedSearchers.iterator().next().de(args);
	}
	
	/**
	 * 改变关联时的关联类型
	 * 
	 * @param joinType 新的关联类型, 不能为null, 为null调用无效
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void changeMappedJoinType(JoinType joinType) {
		if(joinType != null) {
			this.assertHasInit();
			if(this.usingJoinWorker == null) {
				throw new IllegalArgumentException("不能改变非关联搜索参数的关联类型");
			}
			this.usingJoinWorker.mappedJoinType = joinType;
		}
	}
	
	/**
	 * 改变关联时的关联关系类型
	 * 
	 * @param relationType 新的关联关系类型, 不能为null, 为null调用无效
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void changeMappedRelationType(RelationType relationType) {
		if(relationType != null) {
			this.assertHasInit();
			if(this.usingJoinWorker == null) {
				throw new IllegalArgumentException("不能改变非关联搜索参数的关联连接条件类型");
			}
			this.usingJoinWorker.mappedRelationType = relationType;
		}
	}
	
	/**
	 * 获取当前搜索参数在搜索参数树中的路径
	 * 
	 * @return 当前搜索参数在搜索参数树中的路径, 如果是根则为空字符串.
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	@Override
	public final String getPath() {
		return this.path;
	}
	
	/**
	 * 根据搜索参数对象路径获取对应路径的默认关联搜索参数或当前搜索参数
	 * <br/> 不包括动态关联的搜索参数包含的搜索参数对象
	 * 
	 * @param paramPath 指定的搜索参数对象路径, 空路径或null都将返回当前搜索参数
	 * @return 对应该路径的搜索参数实例, 找不到返回null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final PT getDefaultJoinedParameter(String paramPath) {
		this.assertParameterContextNotNull();
		return (PT) this.paramContext.getParameterObjWithStartParam((PT) this, AbsParameter.class, paramPath);
	}
	
	/**
	 * 根据搜索参数对象路径获取对应路径的当前搜索参数可管理的搜索器
	 * <br/> 不包括动态关联的搜索参数包含的搜索参数对象
	 * 
	 * @param searcherPath 指定的搜索参数对象路径, 空路径或null将返回null
	 * @return 对应该路径的搜索器实例, 找不到返回null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final ISearchable<?> getSearcher(String searcherPath) {
		this.assertParameterContextNotNull();
		return this.paramContext.getParameterObjWithStartParam((PT) this, AbsSearcher.class, searcherPath);
	}
	
	/**
	 * 判断一个搜索参数是否是当前搜索参数的默认关联搜索参数
	 * <br/> 如果传入参数是当前搜索参数实例则会返回true
	 * 
	 * @param checkParameter 需要判断的搜索参数, 不能为null, 如果为null则一定返回false
	 * @return 指定搜索参数是否是当前搜索参数的默认关联搜索参数的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isMyDefaultJoinedParameter(PT checkParameter) {
		if(this.equals(checkParameter)) {
			return true;
		}
		if(checkParameter == null || this.myDefaultJoinedParams == null) {
			return false;
		}
		return this.myDefaultJoinedParams.containsValue(checkParameter);
	}
	
	/**
	 * 判断一个搜索器是否是当前搜索参数的搜索器
	 * 
	 * @param checkSearcher 需要判断的搜索器, 不能为null, 如果为null则一定返回false
	 * @return 指定搜索器是否是当前搜索参数的搜索器的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isMySearcher(ISearchable<?> checkSearcher) {
		if(checkSearcher == null || this.mySearchers == null) {
			return false;
		}
		return this.mySearchers.containsValue(checkSearcher);
	}

	/**
	 * 判断一个搜索参数是否是当前搜索参数可以管理的默认关联搜索参数
	 * <br/> 如果传入参数是当前搜索参数实例则会返回true
	 * 
	 * @param checkParameter 需要判断的搜索参数, 不能为null, 如果为null则一定返回false
	 * @return 指定搜索参数是否是当前搜索参数可以管理的默认关联搜索参数的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isReachableDefaultJoinedParameter(PT checkParameter) {
		if(this.equals(checkParameter)) {
			return true;
		}
		if(checkParameter == null) {
			return false;
		}
		this.assertParameterContextNotNull();
		return this.paramContext.isReachableParameter(checkParameter);
	}
	
	/**
	 * 判断一个搜索器是否是当前搜索参数可以管理的搜索器
	 * 
	 * @param checkSearcher 需要判断的搜索器, 不能为null, 如果为null则一定返回false
	 * @return 指定搜索器是否是当前搜索参数可以管理的搜索器的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final boolean isReachableSearcher(ISearchable<?> checkSearcher) {
		if(checkSearcher == null) {
			return false;
		}
		this.assertParameterContextNotNull();
		return this.paramContext.isReachableSeacher((AbsSearcher<PT, SCT, RT, ?>) checkSearcher);
	}
	
	/**
	 * 启用搜索内容间的自动关系连接
	 * 
	 * @param isAnd 是否是使用AND连接类型, 为<code>true</code>则时AND, 否则为OR
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void enableAutoRelation(boolean isAnd) {
		this.assertHasInit();
		// 设置自动关系追加
		this.paramContext.isAutoAddRelation = true;
		this.paramContext.isAutoAddAnd = isAnd;
	}
	
	/**
	 * 关闭搜索内容间的自动关系连接
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void disableAutoRelation() {
		this.assertHasInit();
		// 关闭自动关系追加
		this.paramContext.isAutoAddRelation = false;
		this.paramContext.isAutoAddAnd = true;
	}
	
	/**
	 * 设置以当前搜索参数开始可以管理的所有搜索参数字段进行输出
	 * 
	 * @param isOupout 是否输出, true表示输出, false表示不输出
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void setAllFieldOutput(boolean isOutput) throws Exception {
		this.assertHasInit();
		// 设置所有字段输出
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.paramContext.getAllSearchersWithStartParam((PT) this)) {
			searcher.setOutput(isOutput);
		}
	}
	
	/**
	 * 设置当前搜索参数包含的所有搜索参数字段进行输出
	 * <br/> 包括继承的
	 * 
	 * @param isOutput 是否输出, true表示输出, false表示不输出
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final void setAllMyFieldOutput(boolean isOutput) throws Exception {
		this.assertHasInit();
		// 设置当前搜索参数拥有字段(包括继承的)输出
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.myOwnedSearchers) {
			searcher.setOutput(isOutput);
		}
	}

	/**
	 * 构建结果
	 * 
	 * @param args 进行构建所需要的参数, 可能没有
	 * @return 构建完成的结果
	 * @throws Exception 构建结果失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public final RT build(Object...args) throws Exception {
		this.assertHasInit();
		// 根搜索参数&非动态搜索参数才能构建结果
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
	 * 重置搜索参数
	 * 
	 * @param args 进行重置需要的参数, 可能没有
	 * @throws Exception 重置失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
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
		// 重置条件连接, 分隔符验证标志
		this.paramContext.isAutoAddAnd = false;
		this.paramContext.isAutoAddRelation = false;
		this.paramContext.relationalCheckFlag = true;
		this.paramContext.delimiterStartCount = 0;
		this.paramContext.delimiterEndCount = 0;
		// 重置搜素参数字段(重置搜索内容发现全没了就不会处理)
		for(ParameterField<PT, SCT, RT> paramField : this.paramContext.allParamFields) {
			paramField.reset();
		}
		// 重置动态关联搜索内容
		if(this.paramContext.dynamicJoinParamContextPool != null && ! this.paramContext.dynamicJoinParamContextPool.isEmpty()) {
			for(PT param : this.paramContext.dynamicJoinParamContextPool.keySet()) {
				param.unJoin(false);
			}
		}
		// 重置关联搜索内容(关联搜索内容已经没有了)
		for(PT param : this.paramContext.allParams) {
			if(param.usingJoinWorker != null) {
				param.usingJoinWorker.hasJoin = false;
			}
		}
	}
	
	@Override
	public String toString() {
		if(this.usingJoinWorker != null) {
			return StringUtils.concat(super.toString(), 
					" WITH PATH [", this.usingJoinWorker.mappedField.path, "]",
					" MAPPED FROM [", this.usingJoinWorker.mappedFromParam.toString(), "]",
					" PATH [", this.usingJoinWorker.mappedFromField.path, "]");
		}
		return super.toString();
	}
	
	@Override
	public PT clone() throws CloneNotSupportedException {
		this.assertHasInit();
		if(this.paramType != ParameterType.ROOT || ! this.paramContext.isPrototype) {
			throw new CloneNotSupportedException("根搜索参数且为原型才能进行克隆");
		}
		PT cloneParam = (PT) super.clone();
		// 克隆搜索参数上下文
		ParameterContext<PT, SCT, RT> cloneParamContex = this.paramContext.clone();
		cloneParamContex.rootParam = cloneParam;
		// 这就不是原型了
		cloneParamContex.isPrototype = false;
		try {
			// 克隆包含内容, 递归克隆关联搜索参数
			this.cloneImpl(cloneParam, cloneParamContex);
			// 调用克隆回调
			cloneParam.callCloneDoneCallBack(cloneParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cloneParam;
	}
	
	/**
	 * 初始化为当前搜索参数的关联搜索参数(只可能是默认关联和继承关联)
	 * 
	 * @param paramType 关联搜索参数类型, 必须为{@link ParameterType#DEFAULT_JOIN}或{@link ParameterType#INHERIT_JOIN}
	 * @param fromParam 关联起点搜索参数
	 * @param joinType 关联类型
	 * @param relationType 关联关系类型
	 * @param fromFieldName 关联起点搜索参数字段名称, 必须是当前搜索参数拥有的字段
	 * @param toFieldName 关联终点搜索参数字段名称, 必须是关联的搜索参数拥有的字段
	 * @param args 初始化需要的参数, 可能没有
	 * @throws Exception 初始失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void init(ParameterType paramType, PT fromParam, JoinType joinType, RelationType relationType,
			String fromFieldName, String toFieldName, Object...args) throws Exception {
		if(paramType == null || paramType == ParameterType.ROOT) {
			throw new IllegalArgumentException("关联搜索参数类型不能为null或不能为ROOT");
		}
		// 获取关联起点字段
		ParameterField<PT, SCT, RT> fromParamField = null;
		for(ParameterField<PT, SCT, RT> ownedParameterField : fromParam.myOwnedParameterFields) {
			if(ownedParameterField.fieldName.equals(fromFieldName)) {
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
			if(ownedParameterField.fieldName.equals(toFieldName)) {
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
		if(toParam.isDefaultJoinParameter()) {
			fromParamField.usingSearcher = null; /* 作为非动态关联的默认关联起点字段的搜索器总为null */
		}
		// 如果当前搜索参数是继承关联搜索参数(某个搜索参数的父类), 则需要向其所有关联起点搜索参数添加包含的搜索参数对象到其已拥有搜索参数对象列表中(父类的搜索器子类都应该有)
		toParam.syncInheritJoinParamOwnedParameterObjs();
	}

	/**
	 * 同步当前继承搜索参数包含的默认关联搜索参数和拥有字段以及搜索器的引用到所有子级搜索参数中(包括多重)
	 * <br/> 如果当前搜索参数不是继承关联的搜索参数则无处理
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void syncInheritJoinParamOwnedParameterObjs() {
		// 如果当前搜索参数是继承关联搜索参数(某个搜索参数的父类), 则需要向其所有关联起点搜索参数添加包含的搜索器以及搜索参数字段到已拥有列表中(父类的搜索器子类都应该有)
		PT thisParam =  (PT) this;
		PT currentParam = thisParam;
		while(currentParam.isInheritJoinParameter()) {
			PT mappedFromParam = currentParam.usingJoinWorker.mappedFromParam;
			// 继承的搜索参数
			if(mappedFromParam.myOwnedInheritedFromParameters == null) {
				mappedFromParam.myOwnedInheritedFromParameters = new HashSet<PT>();
			}
			mappedFromParam.myOwnedInheritedFromParameters.add(thisParam);
			// 搜索器
			if(mappedFromParam.myOwnedSearchers == null) {
				mappedFromParam.myOwnedSearchers = new HashSet<AbsSearcher<PT, SCT, RT, ?>>();
			}
			mappedFromParam.myOwnedSearchers.addAll(currentParam.mySearchers.values());
			// 搜索参数字段
			if(mappedFromParam.myOwnedParameterFields == null) {
				mappedFromParam.myOwnedParameterFields = new HashSet<ParameterField<PT, SCT, RT>>();
			}
			mappedFromParam.myOwnedParameterFields.addAll(currentParam.myParameterFields.values());
			// 默认关联搜索参数
			if(currentParam.myDefaultJoinedParams != null && ! currentParam.myDefaultJoinedParams.isEmpty()) {
				if(mappedFromParam.myOwnedDefaultJoinedParameters == null) {
					mappedFromParam.myOwnedDefaultJoinedParameters = new HashSet<PT>();
				}
				mappedFromParam.myOwnedDefaultJoinedParameters.addAll(currentParam.myDefaultJoinedParams.values());
			}
			currentParam = mappedFromParam;
		}
	}

	/**
	 * 注册为当前搜索参数的继承关联搜索参数
	 * 
	 * @param inheritJoinedParam 要注册的继承关联搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerInheritJoinedParameter(PT inheritJoinedParam) {
		if(inheritJoinedParam == null  || inheritJoinedParam.paramType != ParameterType.INHERIT_JOIN) {
			throw new IllegalArgumentException("注册的继承关联搜索参数不能为null或不为继承关联类型搜索参数.");
		}
		if(this.myInheritJoinedParams == null) {
			this.myInheritJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		this.myInheritJoinedParams.put(inheritJoinedParam.usingJoinWorker.mappedFromField, inheritJoinedParam);
	}

	/**
	 * 注册为当前搜索参数的默认关联搜索参数
	 * 
	 * @param defaultJoinedParam 要注册的默认关联搜索参数
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerDefaultJoinedParameter(PT defaultJoinedParam) {
		if(defaultJoinedParam == null  || defaultJoinedParam.paramType != ParameterType.DEFAULT_JOIN) {
			throw new IllegalArgumentException("注册的默认关联搜索参数不能为null或不为默认关联类型搜索参数.");
		}
		if(this.myDefaultJoinedParams == null) {
			this.myDefaultJoinedParams = new HashMap<ParameterField<PT, SCT, RT>, PT>();
		}
		this.myDefaultJoinedParams.put(defaultJoinedParam.usingJoinWorker.mappedFromField, defaultJoinedParam);
		// 把自己的搜索参数字段加入到已拥有搜索参数字段中
		if(this.myOwnedDefaultJoinedParameters == null) {
			this.myOwnedDefaultJoinedParameters = new HashSet<PT>();
		}
		this.myOwnedDefaultJoinedParameters.add(defaultJoinedParam);
	}

	/**
	 * 注册为当前搜索参数的搜索参数字段
	 * 
	 * @param paramField 要注册的搜索参数字段
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerParameterField(ParameterField<PT, SCT, RT> paramField) {
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
		this.myParameterFields.put(fieldName, paramField);
		// 把自己的搜索参数字段加入到已拥有搜索参数字段中
		if(this.myOwnedParameterFields == null) {
			this.myOwnedParameterFields = new HashSet<ParameterField<PT, SCT, RT>>();
		}
		this.myOwnedParameterFields.add(paramField);
		// 设置搜索参数与搜索参数字段的关系
		paramField.belongParameter = (PT) this;
	}

	/**
	 * 注册为当前搜索参数的搜索器
	 * 
	 * @param searcher 要注册的搜索器
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void registerSeacher(ParameterField<PT, SCT, RT> belongParamField, 
			AbsSearcher<PT, SCT, RT, ?> searcher) {
		if(searcher == null) {
			throw new IllegalArgumentException("注册的搜索器不能为null");
		}
		if(belongParamField == null) {
			throw new IllegalArgumentException("注册搜索器所属的搜索参数字段不能为null");
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
			this.myOwnedSearchers = new HashSet<AbsSearcher<PT, SCT, RT, ?>>();
		}
		this.myOwnedSearchers.add(searcher);
		// 设置搜索参数&搜索参数字段与搜索器的关系
		searcher.belongParameter = (PT) this;
		searcher.belongParameterField = belongParamField;
		// 同时设置该搜索参数字段使用的搜索器
		belongParamField.usingSearcher = searcher;
	}

	/**
	 * 当前搜索参数是否是根搜索参数
	 * 
	 * @return 当前搜索参数是否是根搜索参数的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isRootParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.ROOT;
	}
	
	/**
	 * 当前搜索参数是否是默认关联搜索参数
	 * 
	 * @return 当前搜索参数是否是默认关联搜索参数的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isDefaultJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.DEFAULT_JOIN;
	}
	
	/**
	 * 当前搜索参数是否是继承关联搜索参数
	 * 
	 * @return 当前搜索参数是否是继承关联搜索参数的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final boolean isInheritJoinParameter() {
		this.assertParameterTypeNotNull();
		return this.paramType == ParameterType.INHERIT_JOIN;
	}
	
	/**
	 * 通过搜索器设置当前搜索参数包含的搜索参数字段输出
	 * 
	 * @param searcher 设置输出的字段所属的搜索器
	 * @param isOutput 是否输出, true表示输出, false表示不输出
	 * @param isRaw 是否对字段直接操作, true则跳过获取最少关联的逻辑, false则不跳过
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void setMyFiledOutPut(ParameterField<PT, SCT, RT> targetParamField, 
			boolean isOutput, boolean isRaw) throws Exception {
		this.assertHasInit();
		if(! this.myOwnedParameterFields.contains(targetParamField)) {
			throw new IllegalArgumentException("指定的搜索参数字段必须属于当前搜索参数");
		}
		ParameterField<PT, SCT, RT> searchParamField = targetParamField;
		if(! isRaw) {
			// 如果是被继承搜索参数的字段的则被关联字段要先设置输出
			if(targetParamField.belongParameter.isInheritJoinParameter()) {
				this.setMyFiledOutPut(targetParamField, isOutput, true);
			}
			// 获取符合最少关联的字段来设置输出, 
			searchParamField = this.paramContext.getIndeedSearchParameterField(targetParamField, null);
		}
		// 如果是继承搜索参数, 需要使用继承终点搜索参数来判断字段
		PT searchParam = searchParamField.belongParameter;
		// 第一次设置输出, 设置代表操作的字段
		if(! searchParamField.equals(targetParamField) && isOutput) {
			if(searchParamField.representOptFields == null) {
				searchParamField.representOptFields = new LinkedList<ParameterField<PT, SCT, RT>>();
			}
			if(! searchParamField.representOptFields.contains(targetParamField)) {
				searchParamField.representOptFields.add(targetParamField);
			}
		}
		// 搜索字段设置为指定的输出类型
		searchParamField.isOutput = isOutput;
		// 添加关联或回滚关联
		for(;;) {
			boolean isAllFieldOutput = true; // 全部字段都输出
			boolean isAllFieldNotOutput = true; // 全部字段都不输出
			PT checkParam = this.paramContext.getInheritEndParameter(searchParam);
			for(ParameterField<PT, SCT, RT> paramField : checkParam.myOwnedParameterFields) {
				if(! paramField.isOutput) {
					isAllFieldOutput = false;
				}
				isAllFieldOutput &= paramField.isOutput;
				isAllFieldNotOutput &= ! paramField.isOutput;
			}
			// 所有字段输出或所有字段不输出作处理, 两者互斥
			if(isAllFieldOutput) {
				checkParam.isAllMyFieldOutput = true;
			} else if(isAllFieldNotOutput) {
				checkParam.hasFieldOutput = false;
				checkParam.isAllMyFieldOutput = false;
				// 如果是关联搜索参数, 回滚关联信息
				if(searchParam.usingJoinWorker != null) {
					searchParam.usingJoinWorker.cancelJoinWork(false, true);
				}
			}
			// 如果是设置为输出, 则包括该字段的继承子类搜索参数都设置为输出
			if(isOutput) {
				searchParam.hasFieldOutput = true;
				// 如果是关联搜索参数, 触发关联关系
				if(searchParam.usingJoinWorker != null) {
					searchParam.usingJoinWorker.doJoinWork();
				}
			}
			if(! searchParam.isInheritJoinParameter()) {
				break;
			}
			searchParam = searchParam.usingJoinWorker.mappedFromParam;
		}
	}
	
	/**
	 * 被动态关联的搜索参数解绑动态关联, 通过isSingle决定是否从动态关联缓存池中删除对应的键值对.
	 * <br/> 会重置被动态关联的搜索参数在关联期间进行添加的搜索内容
	 * 
	 * @param isSingle 是否是进行单个搜索参数来解绑, 一般在循环中解绑传入<code>false</code>, 否则传入<code>true</code>. 
	 * 
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void unJoin(boolean isSingle) throws Exception {
		// 未解绑前当前搜索参数使用的搜索参数上下文是包含动态关联搜索参数信息的(是关联起点搜索参数使用的上下文)
		if(! this.paramContext.dynamicJoinParamContextPool.containsKey(this)) {
			throw new IllegalArgumentException("当前搜索参数不是动态关联搜索参数");
		}
		ParameterContext<PT, SCT, RT> originalParamContext = this.paramContext.dynamicJoinParamContextPool.get(this);
		if(isSingle) {
			this.paramContext.dynamicJoinParamContextPool.remove(this);
		}
		// 重置动态关联搜索参数树包含的搜索参数上下文和搜索内容
		for(PT param : originalParamContext.allParams) {
			// 强制重置关联搜索内容(包括重置搜索内容)
			if(param.usingJoinWorker != null) {
				param.usingJoinWorker.cancelJoinWork(true, false); /* 被动态关联的搜索参数不会再动态关联 */
			}
			// 重置使用的搜索参数上下文
			param.paramContext = originalParamContext;
		}
		// 恢复最短关联路径的关联关系
		originalParamContext.reverseParametersJoinDirection(originalParamContext.rootParam,
				originalParamContext.realDynamicJoinParam, null);
		originalParamContext.realDynamicJoinParam = null;
	}
	
	/**
	 * 克隆实现, 可以用来递归克隆包含的关联搜索参数
	 * 
	 * @param cloneParam 克隆的搜索参数引用, 如果为null会自动创建实例(关联搜索参数逻辑)
	 * @param cloneParamContext 克隆出来的新搜索参数上下文
	 * @return 完成克隆完成填充信息的搜索参数
	 * @throws Exception 克隆失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final PT cloneImpl(PT cloneParam, ParameterContext<PT, SCT, RT> cloneParamContext) throws Exception {
		if(cloneParam == null) {
			cloneParam = (PT) super.clone();
		}
		cloneParamContext.allParams.add(cloneParam);
		/* 重用搜索参数类型, 表信息 */
		cloneParam.paramContext = cloneParamContext;
		cloneParam.usingJoinWorker = null;
		cloneParam.hasInit = true;
		cloneParam.hasFieldOutput = false;
		cloneParam.hasFieldSearched = false;
		cloneParam.isAllMyFieldOutput = false;
		cloneParam.myDefaultJoinedParams = null;
		cloneParam.myInheritJoinedParams = null;
		cloneParam.myDynamicJoinedParams = null;
		cloneParam.myParameterFields = null;
		cloneParam.mySearchers = null;
		cloneParam.myOwnedParameterFields = null;
		cloneParam.myOwnedSearchers = null;
		cloneParam.myOwnedInheritedFromParameters = null;
		cloneParam.myOwnedDefaultJoinedParameters = null;
		// 克隆搜索参数字段
		for(ParameterField<PT, SCT, RT> paramField : this.myParameterFields.values()) {
			ParameterField<PT, SCT, RT> cloneParamField = paramField.clone();
			cloneParam.registerParameterField(cloneParamField);
			cloneParamContext.allParamFields.add(cloneParamField);
			// 克隆搜索器
			if(paramField.usingSearcher != null) {
				AbsSearcher<PT, SCT, RT, ?> cloneSearcher = paramField.usingSearcher.clone();
				cloneParam.registerSeacher(cloneParamField, cloneSearcher);
				cloneParamContext.allSearchers.add(cloneSearcher);
			}
		}
		// 克隆关联搜索参数, 把继承和默认关联的放一起处理, 搜索参数类型是重用的
		boolean hasInheritJoinParam = this.myInheritJoinedParams != null && ! this.myInheritJoinedParams.isEmpty();
		boolean hasDefaultJoinParam = this.myDefaultJoinedParams != null && ! this.myDefaultJoinedParams.isEmpty();
		int needCloneJoinParamCount = (hasInheritJoinParam ? this.myInheritJoinedParams.size() : 0)
					+ (hasDefaultJoinParam ? this.myDefaultJoinedParams.size() : 0);
		List<PT> needCloneJoinParams = new ArrayList<PT>(needCloneJoinParamCount);
		if(hasInheritJoinParam) {
			needCloneJoinParams.addAll(this.myInheritJoinedParams.values());
		}
		if(hasDefaultJoinParam) {
			needCloneJoinParams.addAll(this.myDefaultJoinedParams.values());
		}
		for(PT joinParam : needCloneJoinParams) {
			// 克隆关联搜索参数, 已经克隆好搜索参数字段和搜索器了
			PT cloneJoinParam = joinParam.cloneImpl(null, cloneParamContext);
			// 获取关联起点相关信息
			String fromFieldName = joinParam.usingJoinWorker.mappedFromField.fieldName;
			ParameterField<PT, SCT, RT> fromCloneParamField = null;
			for(ParameterField<PT, SCT, RT> ownedParameterField : cloneParam.myOwnedParameterFields) {
				if(ownedParameterField.fieldName.equals(fromFieldName)) {
					fromCloneParamField = ownedParameterField;
					break;
				}
			}
			// 获取关联终点相关信息
			String toFieldName = joinParam.usingJoinWorker.mappedField.fieldName;
			ParameterField<PT, SCT, RT> toCloneParamField = null;
			for(ParameterField<PT, SCT, RT> ownedParameterField : cloneJoinParam.myOwnedParameterFields) {
				if(ownedParameterField.fieldName.equals(toFieldName)) {
					toCloneParamField = ownedParameterField;
					break;
				}
			}
			// 新的关联处理器
			JoinWorker<PT, SCT, RT> newJoinWorker = JoinWorker.build(cloneParam, cloneJoinParam,
					joinParam.usingJoinWorker.mappedJoinType, 
					joinParam.usingJoinWorker.mappedRelationType,
					fromCloneParamField,
					toCloneParamField);
			cloneJoinParam.usingJoinWorker = newJoinWorker;
			// 设置关联起点字段的属性, 以及注册关联搜索参数
			fromCloneParamField.isMappedFromField = true;
			if(cloneJoinParam.isDefaultJoinParameter()) {
				fromCloneParamField.usingSearcher = null; /* 同init中的设置 */
				cloneParam.registerDefaultJoinedParameter(cloneJoinParam);
			} else { /* 不是默认关联就是继承关联 */
				cloneParam.registerInheritJoinedParameter(cloneJoinParam);
			}
			// 同步继承搜索参数的包含的搜索参数对象到子类搜索参数中
			cloneJoinParam.syncInheritJoinParamOwnedParameterObjs();
		}
		/* 不用克隆动态关联搜索参数 */
		return cloneParam;
	}

	/**
	 * 调用克隆完成在初始化器中定义的回调方法
	 * 
	 * @throws Exception 根据需要抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	final void callCloneDoneCallBack(PT clonedParam) throws Exception {
		// 分别对拥有的搜索器和搜索参数调用初始化器的克隆回调
		if(clonedParam.myOwnedSearchers != null) {
			for(AbsSearcher<PT, SCT, RT, ?> searcher : clonedParam.myOwnedSearchers) {
				clonedParam.paramContext.intializor.onDoneCloneSearcher(clonedParam,
						searcher.getBelongParameterField(), searcher);
			}
		}
		if(clonedParam.myOwnedDefaultJoinedParameters != null) {
			for(PT defaultJoinedParam : clonedParam.myOwnedDefaultJoinedParameters) {
				defaultJoinedParam.callCloneDoneCallBack(defaultJoinedParam);
				clonedParam.paramContext.intializor.onDoneCloneJoinedParameter(clonedParam,
						defaultJoinedParam.usingJoinWorker.mappedFromField, defaultJoinedParam);
			}
		}
	}
	
	/**
	 * 当前搜索参数是否完成初始化
	 * 
	 * @return 当前搜索参数是否完成初始化的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final boolean hasInit() {
		return this.hasInit;
	}

	/**
	 * 当前搜索参数是否有字段输出
	 * 
	 * @return 当前搜索参数是否有字段输出的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final boolean hasFieldOutput() {
		this.assertHasInit();
		return this.hasFieldOutput;
	}
	
	/**
	 * 当前搜索参数是否所有字段都输出
	 * 
	 * @return 当前搜索参数是否所有字段都输出的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final boolean isAllMyFieldOutput() {
		return isAllMyFieldOutput;
	}
	
	/**
	 * 当前搜索参数是否有字段被搜索
	 * 
	 * @return 当前搜索参数是否有字段被搜索的判断结果
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final boolean hasFieldSearched() {
		this.assertHasInit();
		return this.hasFieldSearched;
	}
	
	/**
	 * 获取搜索参数上下文
	 * 
	 * @return 搜索参数上下文
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final ParameterContext<PT, SCT, RT> getParameterContext() {
		assertHasInit();
		return this.paramContext;
	}
	
	/**
	 * 获取当前搜索参数继承的所有父级搜索参数
	 * @return 当前搜索参数继承的所有父级搜索参数, 没有则返回空列表, 不会为null
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected final Collection<PT> getInheritedFromParameters()  {
		this.assertHasInit();
		if(this.myOwnedInheritedFromParameters != null && ! this.myOwnedInheritedFromParameters.isEmpty()) {
			return Collections.unmodifiableCollection(this.myOwnedInheritedFromParameters);
		}
		return Collections.emptyList();
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
	 * 初始化时进行的回调
	 * 
	 * @param args 初始化需要的参数, 可能没有
	 * @throws Exception 初始化失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onInit(Object...args) throws Exception {};
	
	/**
	 * 触发关联操作时进行的回调, 关联终点是当前搜索参数
	 * 
	 * @param fromParam 关联起点搜索参数
	 * @param joinType 关联类型
	 * @param relationType 关联关系类型
	 * @param fromField 关联起点搜索参数字段
	 * @param toField 关联终点搜索参数字段
	 * @throws Exception 关联操作失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract void onJoin(PT fromParam, JoinType joinType, RelationType relationType,
			ParameterField<PT, SCT, RT> fromField, ParameterField<PT, SCT, RT> toField) throws Exception;
	
	/**
	 * 在构建前进行的回调
	 * 
	 * @param args 需要的参数, 可能没有
	 * @throws Exception 处理失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onBeforeBuild(Object...args) throws Exception {};
	
	/**
	 * 构建结果时进行的回调, 构建结果的实际实现
	 * 
	 * @param args  构建结果需要的参数, 可能没有
	 * @throws Exception 构建失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected abstract RT onBuild(Object...args) throws Exception;
	
	/**
	 * 重置时进行的回调
	 * 
	 * @param args 重置需要的参数, 可能没有
	 * @throws Exception 重置失败则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	protected void onReset(Object...args) throws Exception {};
	
	/**
	 * 断言搜索参数已完成初始化否则抛出异常
	 * 
	 * @author linjie
	 * @since 1.0.2
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
	 * @since 1.0.2
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
	 * @since 1.0.2
	 */
	private void assertParameterTypeNotNull() {
		if(this.paramType == null) {
			throw new IllegalArgumentException("搜索参数类型未确定");
		}
	}
}
