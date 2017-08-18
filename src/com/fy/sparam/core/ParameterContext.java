package com.fy.sparam.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
public final class ParameterContext<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> {
	
	/**
	 * 使用的搜索参数对象路径分隔符
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static final String PATH_SPERATOR = ".";
	
	// 所有搜索连接器检查用的数据
	boolean isAutoAppend = false; /*是否自动追加连接器*/
	boolean isAutoAppendAnd = true; /*是否自动追加And连接器, 否则是Or*/
	boolean appendCheckFlag = true; /*检查连接是否完整, 所有搜索器共用一个, 所以放搜索参数*/
	int delimiterStartCount = 0;
	int delimiterEndCount = 0;
	// 分页信息(包括当前搜索参数和后代/关联搜索参数的, 公用一个)
	int page;
	int count;
	// 所有关联搜索参数的处理计数器(防止同类进行关联导致别名冲突)
	int joinCounter;
	// 全局使用的搜索参数对象初始化器
	IInitializor<PT, SCT, RT> intializor;
	// 所有搜索字段类型转换器
	private Map<Class<?>, ITransformable<?>> fieldTransformer;
	// 所有的有关搜索参数和搜索参数字段和搜索器, 它们的Key都是相对于根搜索参数的搜索参数对象路径(如a.b.c)
	final static String KEY_ROOT_PARAM = "ROOT_PARAM"; /* 根搜索参数的Key */
	Map<String, PT> allParams = new HashMap<String, PT>(); /* 不包括继承和动态关联的 */
	Map<String, AbsSearcher<PT, SCT, RT, ?>> allSearchers = new HashMap<String, AbsSearcher<PT, SCT, RT, ?>>(); /* 不包括继承和动态关联的 */
	Set<ParameterField<PT, SCT, RT>> allParamFields = new HashSet<ParameterField<PT, SCT, RT>>(); /* 不包括动态关联的 */
	
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
	public void registerInheritJoinedParameter(PT fromParam, PT inheritJoinedParam,
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
		/* 继承关联不需要注册到上下文中 */
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
	public void registerDefaultJoinedParameter(PT fromParam, PT defaultJoinedParam,
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
		// 生成路径, 注册到上下文中
		String path = this.getParameterObjPath(defaultJoinedParam);
		this.allParams.put(path, defaultJoinedParam);
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
	public void registerParameterField(PT belongParam, ParameterField<PT, SCT, RT> paramField, Object...args) throws Exception {
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
	public void registerSeacher(PT belongParam,  ParameterField<PT, SCT, RT> belongParamField,
			ISearchable<?> searcher, Object...args) throws Exception {
		AbsSearcher<PT, SCT, RT, ?> searcherReal = (AbsSearcher<PT, SCT, RT, ?>) searcher;
		// 调用初始化器进行初始化(所属搜索参数中不需要额外操作)
		this.intializor.initSearcher(searcherReal, args);
		// 注册到所属搜索参数中
		belongParam.registerSeacher(belongParamField, searcherReal); 
		// 生成路径, 注册到上下文中
		String path = this.getParameterObjPath(searcherReal);
		this.allSearchers.put(path, searcherReal);
	}

	/**
	 * 
	 * @return
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public Collection<ParameterField<PT, SCT, RT>> getAllParameterFields() {
		return this.allParamFields;
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
		this.fieldTransformer = intializor.getFieldTransformers();
	}
	
	/**
	 * 
	 * @param rootParam
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	void registerRootParameter(PT rootParam) {
		if(rootParam.paramType != ParameterType.ROOT) {
			throw new IllegalArgumentException("指定的搜索参数不是根搜索参数");
		}
		if(this.allParams.containsKey(KEY_ROOT_PARAM)) {
			throw new IllegalArgumentException("已存在根搜索参数");
		}
		this.allParams.put(KEY_ROOT_PARAM, rootParam);
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
	void generateGlobalNonConflictTableAlias(int startCount) {
		if(startCount < 0) {
			startCount = 0;
		}
		this.joinCounter = startCount;
		for(PT param : this.allParams.values()) {
			param.setTableAlias(StringUtils.concat(param.getTableAlias(), "_", this.joinCounter));
			this.joinCounter ++;
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
	<T> ITransformable<T> getFieldTranslator(Class<T> typeClass) {
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
	 * 获取搜索信息(包括表名, 字段名等等)所属的搜索参数字段
	 * <br/> 作为关联字段的搜索器时, 该搜索参数字段为关联起点搜索参数的字段
	 * 
	 * @param isIgnoreDynamic 是否忽略动态关联的搜索参数部分
	 * @return 搜索信息所属的搜索参数字段
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	ParameterField<PT, SCT, RT> getSearchParameterField(ParameterField<PT, SCT, RT> paramField,
			boolean isIgnoreDynamic) {
		ParameterField<PT, SCT, RT> tmpParamField = paramField;
		PT param = tmpParamField.belongParameter;
		while(param.usingJoinWorker != null) {
			if(isIgnoreDynamic && param.isDynamicJoinParameter()) {
				break;
			}
			tmpParamField = param.usingJoinWorker.mappedFromField;
			param = param.usingJoinWorker.mappedFromParam;
		}
		return tmpParamField;
	}
	
	/**
	 * 在默认关联的后代搜索参数沿着关联链进行查询以指定的搜索字段为代表操作的字段的搜索参数字段
	 * 
	 * @param target 指定的搜索字段, 是代表返回字段操作的搜索参数字段, 不能为null
	 * @param currentMappedFrom 当前遍历到哪个关联头端搜索参数字段, 不能为null
	 * @return 以指定的搜索字段为代表操作的字段的搜索参数字段, 默认关联链中找不到返回null
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	ParameterField<PT, SCT, RT> getIndeedRepresentByParamField(ParameterField<PT, SCT, RT> startParamField) {
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
	 * 反转指定路径上的搜索参数关联关系
	 * 
	 * @param start 开始搜索参数
	 * @param end 结束搜索参数
	 * @param lastMappedFrom 结束时设置最后一个搜索参数从哪个搜索参数关联
	 * @param lastMappedFromField 结束时设置最后一个搜索参数从哪个字段关联
	 * @param lastMappedField 结束时设置最后一个搜索参数的关联字段
	 * @param lastJoinType 结束时设置最后一个搜索参数的关联类型
	 * @param lastRelationType 结束时设置最后一个搜索参数的关联关系类型
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	void reverseParameterJoinDirection(PT start, PT end, JoinWorker<PT, SCT, RT> endSetJoinWorker) {
//		if(! start.equals(end) && start.mappedFromParam != null) {
//			// 利用栈, 先压后出使得能够获取相反顺序, 不包括end(独立处理)
//			Stack<AbsParameter<SCT, RT>> stack = new Stack<AbsParameter<SCT, RT>>();
//			stack.push(start);
//			AbsParameter<SCT, RT> current = start;
//			while(! current.equals(end)) {
//				current = current.mappedFromParam;
//				stack.push(current);
//			}
//			// 出栈倒序, 一二交换
//			current = stack.pop();
//			while(! stack.isEmpty()) {
//				AbsParameter<SCT, RT> currentNext = stack.pop();
//				if(currentNext != null) {
//					ParameterField tmpMappedFromField = currentNext.mappedFromField;
//					ParameterField tmpMappedField = currentNext.mappedField;
//					JoinType tmpJoinType = currentNext.mappedJoinType;
//					RelationType tmpRelationType = currentNext.mappedRelationType;
//					// 原来的被关联字段和关联字段 变为 当前的关联字段和被关联字段(交换位置)
//					currentNext.mappedFromField = current.mappedField;
//					currentNext.mappedField = current.mappedFromField;
//					currentNext.mappedJoinType = current.mappedJoinType;
//					currentNext.mappedRelationType = current.mappedRelationType;
//					
//					current.mappedField = tmpMappedFromField;
//					current.mappedFromField = tmpMappedField;
//					current.mappedJoinType = tmpJoinType;
//					current.mappedRelationType = tmpRelationType;
//					
//					current.mappedFromParam = currentNext;
//				}
//				current = currentNext;
//			}
//		}
//		// 反转后原来链表的start即现在链表的end, 对end设置实际关联的搜索参数关联到实际被关联的搜索参数以及设置好对应关联字段
//		start.mappedFromParam = lastMappedFrom;
//		start.mappedFromField = lastMappedFromField;
//		start.mappedField = lastMappedField;
//		start.mappedJoinType = lastJoinType;
//		start.mappedRelationType = lastRelationType;
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
	<POT> String getParameterObjPath(POT target) {
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
			if(isParamField || isSearcher) { /* 搜索参数字段和搜索器即关联终点搜索参数字段的名称(已经确定是关联搜索参数情况下) */
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
	<POT> POT getParameterObjWithStartParam(PT startParam, Class<POT> targetClass, String targetPath) {
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
			for(AbsSearcher<PT, SCT, RT, ?> searcher  : currentParam.ownedSearchers) {
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
	Collection<AbsSearcher<PT, SCT, RT, ?>> getAllSearchersWithStartParam(PT startParam) {
		if(startParam.paramType == ParameterType.ROOT) {
			return this.allSearchers.values();
		}
		String paramPath = this.getParameterObjPath(startParam);
		List<AbsSearcher<PT, SCT, RT, ?>> result = new LinkedList<AbsSearcher<PT, SCT, RT, ?>>();
		for(Entry<String, AbsSearcher<PT, SCT, RT, ?>> entry : this.allSearchers.entrySet()) {
			if(entry.getKey().startsWith(paramPath)) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
