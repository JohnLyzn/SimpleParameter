package com.fy.sparam.init.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表的信息配置主键
 *
 * @author linjie
 * @since 1.0.2
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableMeta {
	
	/**
	 * 表名	
	 *
	 * @author linjie
	 * @since 1.0.2
	 */
	public String name();
	
	/**
	 * 表的别名
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public String alias() default "";
}
