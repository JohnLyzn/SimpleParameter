package com.fy.sparam.init.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fy.sparam.core.JoinWorker.JoinType;
import com.fy.sparam.core.JoinWorker.RelationType;

/**
 * 关联外键信息的配置注解
 *
 * @author linjie
 * @since 4.1.0
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinParam {
	
	/**
	 * 关联搜索参数的关联字段对应的属性名称
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String mappedBy();
	
	/**
	 * 关联类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public JoinType joinType() default JoinType.INNER_JOIN;
	
	/**
	 * 关联的关联字段之间的关系类型
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public RelationType relationType() default RelationType.EQ;
}
