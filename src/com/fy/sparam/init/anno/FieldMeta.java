package com.fy.sparam.init.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对应表的列信息配置注解
 * 
 * @author linjie
 * @since 1.0.1
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMeta {
	
	/**
	 * 数据库中表的字段的名称
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public String name();
	
	/**
	 * 数据库中表的字段的别名, 可以没有
	 * <br/>如果有的话获取字段的完全名称时使用的就是别名而不是原来字段名称
	 *
	 * @author linjie
	 * @since 1.0.1
	 */
	public String alias() default "";
}
