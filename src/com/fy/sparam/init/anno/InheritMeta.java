package com.fy.sparam.init.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;

/**
 * 继承关系的表信息配置注解
 *
 * @author linjie
 * @since 1.0.1
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InheritMeta {

	/**
	 * 继承型的表的父级表中外键关联的字段对应的属性名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String inheritBy();
	
	/**
	 * 当前表使用的用来表示该外键的数据库字段名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String fieldName();
	
	/**
	 * 当前表使用的用来表示该外键的数据库字段别名
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String fieldAlias() default "";
	
	/**
	 * 继承关联类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public JoinType joinType() default JoinType.INNER_JOIN;
	
	/**
	 * 继承关联的关联字段之间的关系类型
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public RelationType relationType() default RelationType.EQ;
}
