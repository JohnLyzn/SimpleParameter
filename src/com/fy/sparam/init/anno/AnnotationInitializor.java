package com.fy.sparam.init.anno;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fy.sparam.core.AbsParameter;
import com.fy.sparam.core.AbsParameter.IInitializor;
import com.fy.sparam.core.AbsSearcher;
import com.fy.sparam.core.ParameterContext;
import com.fy.sparam.core.ParameterField;
import com.fy.sparam.core.SearchContext.ISearchable;
import com.fy.sparam.core.SearchContext.ITransformable;
import com.fy.sparam.util.StringUtils;

/**
 * 注解式搜索参数初始化器
 * <br/> <strong>需要搜索器{@link ISearchable}作为搜索参数实现类中的public的成员参数(不能是static或final的), 并正确配置注解.</strong>
 * 
 * @param <PT> 搜索参数类类型
 * @param <SCT> 搜索内容类类型
 * @param <RT> 搜索结果类类型
 * 
 * @author linjie
 * @since 1.0.1
 */
@SuppressWarnings("unchecked")
public final class AnnotationInitializor<PT extends AbsParameter<PT, SCT, RT>, SCT, RT> 
implements IInitializor<PT, SCT, RT> {

	public final static String PF_EXTRA_FIELD =  "PF_EXTRA_FIELD";
	
	private Map<Class<?>, ITransformable<?>> fieldTransformer;
	
	/**
	 * 
	 * @param fieldTransformer
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public AnnotationInitializor(Map<Class<?>, ITransformable<?>> fieldTransformer) {
		this.fieldTransformer = fieldTransformer;
	}
	
	@Override
	public Map<Class<?>, ITransformable<?>> getSearcherFieldTransformers() {
		return this.fieldTransformer;
	}

	@Override
	public Class<?> getSearcherFieldTypeClass(AbsSearcher<PT, SCT, RT, ?> searcher) {
		Field searcherField = (Field) searcher.getBelongParameterField().getExtra(PF_EXTRA_FIELD);
		if(searcherField != null) {
			Type searcherFieldGenericType = searcherField.getGenericType();
			return (Class<?>) ((ParameterizedType) searcherFieldGenericType).getActualTypeArguments()[0];
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * @param args 依次是[搜索器类字节码(不为null), 搜索参数实现基类字节码(不为null),
	 * 		 当前初始化的搜索参数类字节码(可以为null), 用来设置属性的搜索参数实例(可以为null),
	 * 		存放当前搜索参数树中已初始化化过的搜索参数字节码容器(可以为null)]
	 */
	@Override
	public void initParameter(PT param, ParameterContext<PT, SCT, RT> paramContext,
			Object...args) throws Exception {
		// 需要额外参数: 搜索器实现类类字节码
		if(args == null || args.length != 5) {
			throw new IllegalArgumentException("注解方式初始化搜索参数需要参数[搜索器类字节码(不为null), 搜索参数实现基类字节码(不为null),"
					+ " 当前初始化的搜索参数类字节码(可以为null), 用来设置属性的搜索参数实例(可以为null)]");
		}
		Class<AbsSearcher<PT, SCT, RT, ?>> searcherClass = (Class<AbsSearcher<PT, SCT, RT, ?>>) args[0];
		Class<PT> basicParamClass = (Class<PT>) args[1]; /* 搜索参数实现类的基本定义类 */
		Class<PT> paramClass = (Class<PT>) args[2];
		if(paramClass == null) {
			paramClass = (Class<PT>) param.getClass();
		}
		PT setFieldParam = (PT) args[3];
		if(setFieldParam == null) {
			setFieldParam = param;
		}
		List<Class<PT>> meetBeforeParamClasses = (List<Class<PT>>) args[4];
		if(meetBeforeParamClasses == null) {
			meetBeforeParamClasses = new LinkedList<Class<PT>>(); 
		}
		// 获取父级的搜索参数, 如果父级搜索参数具有@TableMeta且当前搜索参数没有@InheritMeta则抛出异常
		Class<?> superClass = paramClass.getSuperclass();
		boolean thisHasInheriteConfig = paramClass.isAnnotationPresent(InheritMeta.class);
		boolean parentHasTableConfig = superClass.isAnnotationPresent(TableMeta.class);
		if(parentHasTableConfig && !thisHasInheriteConfig) {
			throw new IllegalArgumentException(format("搜索参数%s是继承型的搜索参数(它的父类%s具有@TableMeta注解), 需要添加@InheritMeta注解配置.", 
					paramClass.getName(), superClass.getName()));
		}
		// 如果是继承型搜索参数先对继承的搜索参数进行初始化
		if(thisHasInheriteConfig && parentHasTableConfig) {
			// 验证环关联, 不然死循环
			if(meetBeforeParamClasses.contains(superClass)) {
				throw new IllegalArgumentException(format(
						"搜索参数%s与搜索参数%s存在关联环的关系, 目前不支持此种关系的搜索参数初始化, 请重新设置. PS: 可以考虑使用动态关联.", 
						this.getClass().getName(), paramClass.getName()));
			}
			// 不存在的加入到已经遇过的列表中
			meetBeforeParamClasses.add((Class<PT>) superClass);
			// 继承关联注解配置
			InheritMeta inheritMeta = paramClass.getAnnotation(InheritMeta.class);
			String inheritByName = inheritMeta.inheritBy();
			// 生成子级搜索参数用来被父级搜索参数关联的搜索参数字段, 字段名称为关联终点字段名称加$符号
			String inheritFromFieldName = StringUtils.concat(inheritByName, "$");
			ParameterField<PT, SCT, RT> inheritFromParamField = new ParameterField<PT, SCT, RT>();
			paramContext.registerParameterField(param, inheritFromParamField, 
					inheritFromFieldName, inheritMeta.fieldName(), inheritMeta.fieldAlias());
			// 生成父级关联搜索参数实例, 并进行初始化, 关联起点字段名称和关联终点字段名称一致
			PT inheritJoinedParam = (PT) superClass.newInstance();
			paramContext.registerInheritJoinedParameter(param, inheritJoinedParam,
					inheritMeta.joinType(), inheritMeta.relationType(),
					inheritFromFieldName, inheritByName,
					searcherClass, basicParamClass, superClass, param, meetBeforeParamClasses); 
			/* 用来设置实例字段的值不是父类, 而是最后的子类 */
		}
		// 初始化当前搜索参数中的搜索器或默认搜索参数成员
		if(paramClass.isAnnotationPresent(TableMeta.class)) { /* 初始化表信息 */
			TableMeta tableMeta = paramClass.getAnnotation(TableMeta.class);
			String tableName = tableMeta.name();
			String tableAlias = tableMeta.alias();
			if(tableAlias.isEmpty()) {
				tableAlias = tableName;
			}
			param.setTableName(tableName);
			param.setTableAlias(tableAlias);
		} else {
			throw new IllegalArgumentException(format("搜索参数%s没有添加@TableMeta注解", paramClass.getName()));
		}
		// 根据当前搜索参数包含的类属性(不包括包括继承的类属性)进行识别并初始化为搜索器或默认关联搜索参数
		boolean isSuperClassNotParam = true; /* 用来验证是否是独立搜索参数, 允许继承的父类(不是继承搜索参数)也包含一些属性作为搜索参数字段 */
		while(isSuperClassNotParam && ! paramClass.equals(basicParamClass)) {
			Field[] fields = paramClass.getDeclaredFields();
			for(Field field : fields) {
				int modifiers = field.getModifiers();
				// 只有public的非final非static属性才进行初始化
				if(Modifier.isPublic(modifiers) && ! Modifier.isStatic(modifiers) 
						&& ! Modifier.isFinal(modifiers)) {
					field.setAccessible(true);
					Class<?> typeClass = field.getType();
					String fieldName = field.getName();
					if(ISearchable.class.isAssignableFrom(typeClass)) { /* 生成搜索器实例 */
						// 为搜索操作类型的类属性提供一个搜索器实例
						if(field.isAnnotationPresent(FieldMeta.class)) {
							if(field.isAnnotationPresent(FieldMeta.class)) {
								FieldMeta fieldMeta = field.getAnnotation(FieldMeta.class);
								ParameterField<PT, SCT, RT> paramField = new ParameterField<PT, SCT, RT>();
								paramField.addExtra(PF_EXTRA_FIELD, field);
								paramContext.registerParameterField(param, paramField, 
										fieldName, fieldMeta.name(), fieldMeta.alias());
								AbsSearcher<PT, SCT, RT, ?> searcher = searcherClass.newInstance();
								paramContext.registerSeacher(param, paramField, searcher);
								// 设置搜索器成员属性的值
								field.set(setFieldParam, searcher);
							} else {
								throw new IllegalArgumentException(format(
										"搜索参数%s中属性名为%s的字段搜索参数没有添加@FieldMeta注解.",
										paramClass.getName(), fieldName));
							}
						} else {
							throw new IllegalArgumentException(format(
									"搜索参数%s中属性名为%s的字段搜索参数没有添加@FieldMeta注解.",
									paramClass.getName(), field.getName()));
						}
					} else if(AbsParameter.class.isAssignableFrom(typeClass)) { /* 生成默认关联搜索参数实例 */
						// 为搜索参数类型的类属性初始化为默认关联搜索参数
						if(field.isAnnotationPresent(JoinParam.class) && field.isAnnotationPresent(FieldMeta.class)) {
							// 验证环关联, 不然死循环
							if(meetBeforeParamClasses.contains(paramClass)) {
								throw new IllegalArgumentException(format(
										"搜索参数%s与搜索参数%s存在关联环的关系, 目前不支持此种关系的搜索参数初始化, 请重新设置. PS: 可以考虑使用动态关联.", 
										this.getClass().getName(), paramClass.getName()));
							}
							// 不存在的加入到已经遇过的列表中
							meetBeforeParamClasses.add(paramClass);
							// 初始化默认关联搜索参数
							FieldMeta fieldMeta = field.getAnnotation(FieldMeta.class);
							ParameterField<PT, SCT, RT> paramField = new ParameterField<PT, SCT, RT>(); /* 这个关联字段是数据 */
							paramField.addExtra(PF_EXTRA_FIELD, field);
							paramContext.registerParameterField(param, paramField, 
									fieldName, fieldMeta.name(), fieldMeta.alias(), null);
							JoinParam joinParam = field.getAnnotation(JoinParam.class);
							PT defaultJoinedParam = (PT) typeClass.newInstance();
							paramContext.registerDefaultJoinedParameter(param, defaultJoinedParam,
									joinParam.joinType(), joinParam.relationType(),
									fieldName, joinParam.mappedBy(),
									searcherClass, basicParamClass, typeClass, defaultJoinedParam, meetBeforeParamClasses);
							// 设置搜索参数成员属性的值
							field.set(setFieldParam, defaultJoinedParam);
						} else {
							throw new IllegalArgumentException(format(
									"搜索参数%s中属性名为%s的关联搜索参数没有添加@JoinParam或@FieldMeta注解.", 
									paramClass.getName(), fieldName));
						}
					}
				}
			}
			// 对没有@TableMeta注解的父类进行属性解析, 这允许搜索字段通过类继承进行代码复用
			paramClass = (Class<PT>) paramClass.getSuperclass();
			isSuperClassNotParam = ! paramClass.isAnnotationPresent(TableMeta.class);
		}
	}

	/**
	 * {@inheritDoc}
	 * @param args 依次是[字段名称, 数据库字段名称, 数据库字段别名]
	 */
	@Override
	public void initParameterField(ParameterField<PT, SCT, RT> paramField, Object... args) throws Exception {
		String fieldName = (String) args[0];
		String dbFieldName = (String) args[1];
		String dbFieldAlias = (String) args[2];
		
		paramField.setFieldName(fieldName);
		paramField.setDbFieldName(dbFieldName);
		paramField.setDbFieldAlias(dbFieldAlias);
	}

	@Override
	public void initSearcher(AbsSearcher<PT, SCT, RT, ?> searcher, Object... args) throws Exception {
		
	}
}
