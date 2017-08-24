package com.fy.sparam.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import com.fy.sparam.core.AbsParameter.IInitializor;
import com.fy.sparam.core.AbsParameter.ParameterType;
import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.util.StringUtils;

/**
 * 
 * @param <PT>
 * @param <SCT>
 * @param <RT>
 * 
 * @author linjie
 * @since 1.0.1
 */
public final class ParameterContext<PT extends AbsParameter<PT, SCT, RT>, SCT, RT>
implements Cloneable {
	
	/**
	 * 使用的搜索参数对象路径分隔符
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final String PATH_SPERATOR = ".";
	// 全局使用的搜索参数对象初始化器
	IInitializor<PT, SCT, RT> intializor;
	// 所有搜索字段类型转换器
	Map<Class<?>, ITransformable<?>> fieldTransformer;
	// 所有的有关搜索参数和搜索参数字段和搜索器
	PT rootParam;
	Set<PT> allParams = new HashSet<PT>(); /* 不包括继承和动态关联的 */
	Set<AbsSearcher<PT, SCT, RT, ?>> allSearchers = new HashSet<AbsSearcher<PT, SCT, RT, ?>>(); /* 不包括继承和动态关联的 */
	Set<ParameterField<PT, SCT, RT>> allParamFields = new HashSet<ParameterField<PT, SCT, RT>>(); /* 不包括动态关联的 */
	// 当前拥有的搜索内容
	SearchContext<PT, SCT, RT> usingSearchContext;
	// 分页信息(包括当前搜索参数和后代/关联搜索参数的, 公用一个)
	int page;
	int count;
	// 所有关联搜索参数的处理计数器(防止同类进行关联导致别名冲突)
	int joinCounter;
	// 所有搜索内容逻辑关系检查用的数据
	boolean isAutoAddRelation = false; /* 是否自动为条件追加逻辑关系 */
	boolean isAutoAddAnd = true; /* 自动追加的逻辑关系是否是And, false则是Or */
	boolean relationalCheckFlag = true; /* 用来检查连接是否完整的标志 */
	int delimiterStartCount = 0;
	int delimiterEndCount = 0;
	// 动态关联相关信息, 一个搜素参数上下文统一管理
	Map<PT, ParameterContext<PT, SCT, RT>> dynamicJoinParamContextPool; /* 动态关联起点的搜索参数树已经动态关联的搜索参数信息 */
	PT realDynamicJoinParam; /* 被动态关联的根搜索参数进行过最短关联处理后实际用来关联的搜索参数 */
	
	/**
	 * 
	 * @param param
	 * @param mappedFromParamField
	 * @param inheritJoinedParam
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void registerInheritJoinedParameter(PT fromParam, PT inheritJoinedParam,
			JoinType joinType, RelationType relationType,
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
	 * 
	 * @param param
	 * @param mappedFromParamField
	 * @param inheritJoinedParam
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void registerDefaultJoinedParameter(PT fromParam, PT defaultJoinedParam,
			JoinType joinType, RelationType relationType,
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
	 * 
	 * @param belongParam
	 * @param paramField
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final void registerParameterField(PT belongParam, ParameterField<PT, SCT, RT> paramField, Object...args) throws Exception {
		// 调用初始化器进行初始化(所属搜索参数中不需要额外操作)
		this.intializor.initParameterField(paramField, args);
		// 注册到所属搜索参数中(先注册设置与字段中与搜索参数相关的属性)
		belongParam.registerParameterField(paramField);
		// 注册到上下文中
		this.allParamFields.add(paramField);
	}
	
	/**
	 * 
	 * @param belongParam
	 * @param searcher
	 * @throws Exception
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	@SuppressWarnings("unchecked")
	public final void registerSeacher(PT belongParam,  ParameterField<PT, SCT, RT> belongParamField,
			ISearchable<?> searcher, Object...args) throws Exception {
		AbsSearcher<PT, SCT, RT, ?> searcherReal = (AbsSearcher<PT, SCT, RT, ?>) searcher;
		// 调用初始化器进行初始化(所属搜索参数中不需要额外操作)
		this.intializor.initSearcher(searcherReal, args);
		// 注册到所属搜索参数中
		belongParam.registerSeacher(belongParamField, searcherReal); 
		// 生成路径, 注册到上下文中
		this.allSearchers.add(searcherReal);
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final PT getRootParameter() {
		return this.rootParam;
	}
	
	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getAllParameterFields() {
		List<ParameterField<PT, SCT, RT>> result = 
				new ArrayList<ParameterField<PT, SCT, RT>>(this.recursiveGetAllParameterFieldsCount(0));
		this.recursiveGetAllParameterFields(result);
		return result;
	}
	
	/**
	 * 
	 * @param mappedParam
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public final Collection<ParameterField<PT, SCT, RT>> getReachableParameterFieldsWithStartParam(PT startParam) {
		if(startParam.paramType == ParameterType.ROOT) {
			return this.allParamFields;
		}
		String paramPath = this.getParameterObjPath(startParam);
		List<ParameterField<PT, SCT, RT>> result = new LinkedList<ParameterField<PT, SCT, RT>>();
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			if(paramField.fieldPath.startsWith(paramPath)) {
				result.add(paramField);
			}
		}
		return result;
	}
	
	/**
	 * 构造器: 指定初始化器
	 * @param intializor
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	ParameterContext(IInitializor<PT, SCT, RT> intializor) {
		this.intializor = intializor;
		this.fieldTransformer = intializor.getSearcherFieldTransformers();
	}
	
	/**
	 * 
	 * @param rootParam
	 * 
	 * @author linjie
	 * @since 1.0.1
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
	 * 
	 * <br/> 破坏性的, 不可复原.
	 * 
	 * @param startCount 计数起点, 必须大于0
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void generateGlobalNonConflictTableAlias(int startCount) {
		if(startCount < 0) {
			startCount = 0;
		}
		this.joinCounter = startCount;
		for(PT param : this.allParams) {
			param.setTableAlias(StringUtils.concat(param.getTableAlias(), "_", this.joinCounter));
			this.joinCounter ++;
		}
	}
	
	/**
	 * 
	 * <br/> 破坏性的, 不可复原.
	 * 
	 * @param startCount 计数起点, 必须大于0
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final void generateAllParameterFieldPath() {
		for(ParameterField<PT, SCT, RT> paramField : this.allParamFields) {
			paramField.fieldPath = this.getParameterObjPath(paramField);
		}
	}
	
	/**
	 * 获取对应类型类字节码可用的搜索器类型解析器
	 * 
	 * @param typeClass 类型类字节码
	 * @return translator 可用的搜索器类型解析器, 如果找不到会返回null
	 *
	 * @author linjie
	 * @since 1.0.1
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
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final SearchContext<PT, SCT, RT> getCurrentSearchContext() throws Exception {
		if(this.usingSearchContext == null) {
			this.usingSearchContext = SearchContext.create();
		}
		return this.usingSearchContext;
	}
	
	/**
	 * 获取最短关联信息情况下搜索信息(包括表名, 字段名等等)所属的搜索参数字段
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param paramField 指定的搜索参数字段
	 * @return 搜索信息所属的搜索参数字段
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	final ParameterField<PT, SCT, RT> getIndeedSearchParameterField(ParameterField<PT, SCT, RT> paramField) {
		ParameterField<PT, SCT, RT> tmpParamField = paramField;
		PT param = tmpParamField.belongParameter;
		// 如果所属搜索参数是关联搜索参数且指定字段是该搜素参数关联关系中关联终点字段, 则往上找最少关联信息的搜索参数字段
		while(param.usingJoinWorker != null && paramField.equals(param.usingJoinWorker.mappedField)) {
			tmpParamField = param.usingJoinWorker.mappedFromField;
			param = param.usingJoinWorker.mappedFromParam;
		}
		return tmpParamField;
	}
	
	/**
	 * 在默认关联的后代搜索参数沿着关联链进行查询以指定的搜索字段为代表操作的字段的搜索参数字段
	 * <br/> 功能方法, 与当前上下文无关
	 * 
	 * @param startParamField 指定的搜索字段, 是代表返回字段操作的搜索参数字段, 不能为null
	 * @return 以指定的搜索字段为代表操作的字段的搜索参数字段, 默认关联链中找不到返回null
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final ParameterField<PT, SCT, RT> getIndeedRepresentParamField(ParameterField<PT, SCT, RT> startParamField) {
		PT currentParam = startParamField.belongParameter;
		ParameterField<PT, SCT, RT> currentParamField = startParamField;
		while(currentParamField.isMappedFromField) {
			PT tmp = null;
			if(currentParam.myDefaultJoinedParams != null) {
				tmp = currentParam.myDefaultJoinedParams.get(currentParamField);
			}
			if(tmp == null && currentParam.myInheritJoinedParams != null) {
				tmp = currentParam.myInheritJoinedParams.get(currentParamField);
			}
			if(tmp == null && currentParam.myDynamicJoinedParams != null) {
				tmp = currentParam.myDynamicJoinedParams.get(currentParamField);
			}
			if(tmp != null && tmp.usingJoinWorker != null) {
				currentParamField = tmp.usingJoinWorker.mappedField;
			}
			if(tmp == null || tmp.usingJoinWorker == null || ! tmp.usingJoinWorker.hasJoin) {
				break;
			}
			currentParam = tmp;
		}
		return currentParamField;
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
	 * @since 1.0.1
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
	 * 
	 * @param param
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final boolean isReachableParameter(PT param) {
		if(this.allParams.contains(param)) {
			return true;
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(PT dynamicJoinParam : this.dynamicJoinParamContextPool.keySet()) {
				if(dynamicJoinParam.paramContext.isReachableParameter(param)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param searcher
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final boolean isReachableSeacher(AbsSearcher<PT, SCT, RT, ?> searcher) {
		if(this.allSearchers.contains(searcher)) {
			return true;
		}
		if(this.dynamicJoinParamContextPool != null && ! this.dynamicJoinParamContextPool.isEmpty()) {
			for(PT dynamicJoinParam : this.dynamicJoinParamContextPool.keySet()) {
				if(dynamicJoinParam.paramContext.isReachableSeacher(searcher)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param target
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	@SuppressWarnings("unchecked")
	final <POT> String getParameterObjPath(POT target) {
		boolean isParam = target instanceof AbsParameter;
		boolean isSearcher = target instanceof AbsSearcher;
		boolean isParamField = target instanceof ParameterField;
		if(! isParam && ! isSearcher && ! isParamField) {
			throw new IllegalArgumentException("获取路径的搜索参数对象只能是搜索参数, 搜索器, 搜索参数字段");
		}
		// 对搜索器类型
		PT param = null;
		ParameterField<PT, SCT, RT> paramField = null;
		AbsSearcher<PT, SCT, RT, ?> searcher = null;
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
		// 如果是继承搜索参数, 需要使用最终子类搜索参数作为起点
		while(param.isInheritJoinParameter() && param.usingJoinWorker != null) {
			param = param.usingJoinWorker.mappedFromParam;
		}
		if(param.usingJoinWorker != null) {
			PT currentParam = param;
			StringBuilder resultBuilder = new StringBuilder();
			ParameterField<PT, SCT, RT> currentParamField = param.usingJoinWorker.mappedField;
			if(isParam) { /* 搜索参数应是关联起点搜索参数字段的名称 */
				resultBuilder.append(param.usingJoinWorker.mappedFromField.fieldName);
			}
			if(isParamField || isSearcher) {
				if(paramField.usingSearcher != null) {
					// 如果是非关联起点搜索参数字段情况下, 使用指定搜索参数字段的名称
					currentParamField = paramField;
				}
				resultBuilder.append(currentParamField.fieldName);
			}
			// 只能生成默认关联的路径
			while(currentParam.isDefaultJoinParameter() && currentParam.usingJoinWorker != null) {
				currentParamField = currentParam.usingJoinWorker.mappedFromField;
				currentParam = currentParam.usingJoinWorker.mappedFromParam;
				// 插入到前面
				resultBuilder.insert(0, ".");
				resultBuilder.insert(0, currentParamField.fieldName);
			}
			return resultBuilder.toString();
		}
		if(isSearcher) {
			return searcher.belongParameterField.fieldName; /* 根搜索参数的搜素器 */
		}
		if(isParamField) {
			return paramField.fieldName; /* 根搜索参数的搜素器 */
		}
		return ""; /* 为根搜索参数 */
	}
	
	/**
	 * 
	 * @param startParam
	 * @param targetClass
	 * @param path
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	@SuppressWarnings("unchecked")
	final <POT> POT getParameterObjWithStartParam(PT startParam, Class<POT> targetClass, String targetPath) {
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
		if(targetPath.isEmpty() && needParam) { /* 请求获取搜索参数且目标路径为空(包括为null或this)则直接返回起点搜索参数 */
			return (POT) startParam; 
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
	 * 
	 * @param startParam
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	final Collection<AbsSearcher<PT, SCT, RT, ?>> getAllSearchersWithStartParam(PT startParam) {
		if(startParam.paramType == ParameterType.ROOT) {
			return this.allSearchers;
		}
		String paramPath = this.getParameterObjPath(startParam);
		List<AbsSearcher<PT, SCT, RT, ?>> result = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
		for(AbsSearcher<PT, SCT, RT, ?> searcher : this.allSearchers) {
			if(searcher.belongParameterField.fieldPath.startsWith(paramPath)) {
				result.add(searcher);
			}
		}
		return result;
	}
	
	/**
	 * 克隆一个搜索参数字段, 重置相关引用信息
	 * 
	 * @author linjie
	 * @since 1.0.1
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
		cloneParamContext.usingSearchContext = null;
		cloneParamContext.joinCounter = 0;
		cloneParamContext.delimiterStartCount = 0;
		cloneParamContext.delimiterEndCount = 0;
		cloneParamContext.isAutoAddRelation = false; 
		cloneParamContext.isAutoAddAnd = true;
		cloneParamContext.relationalCheckFlag = true;
		cloneParamContext.dynamicJoinParamContextPool = null;
		cloneParamContext.realDynamicJoinParam = null;
		return cloneParamContext;
	}
	
	/**
	 * 
	 * @param currentCount
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
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
	 * 
	 * @param container
	 * 
	 * @author linjie
	 * @since 1.0.1
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
