package com.fy.sparam.product;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fy.sparam.core.SearchContext.ITransformable;

/**
 * 基本的项目运行配置
 * 
 * @author linjie
 * @since 4.5.0
 */
public class BaseConfig {
	
	static {
		PARAM_TRANSLATORS = new ConcurrentHashMap<Class<?>, ITransformable<?>>();
		BaseConfig.initParamSearcherTranslators();
	}
	
	/**
	 * 全局缓存搜索器类型转换器
	 * <br/> 保证线程安全, 使用ConcurrentHashMap
	 * 
	 * @author linjie
	 * @since 1.0.2
	 */
	public static final Map<Class<?>, ITransformable<?>> PARAM_TRANSLATORS;
	
	/**
	 * 初始化需要用到的常量类型的搜索参数搜索器类型转换器到缓存中
	 * <br/> 注意缓存性能, 不要在类型转换器中存储大内容.
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public static void initParamSearcherTranslators() {
		// 注册常量类型的搜索器类型转换器
		PARAM_TRANSLATORS.put(Integer.class, new ITransformable<Integer>() {
			@Override
			public Integer strToTarget(String str) throws Exception {
				return Integer.valueOf(str);
			}
			
			@Override
			public String targetToStr(Integer obj) throws Exception {
				return obj.toString();
			}
		});
		PARAM_TRANSLATORS.put(String.class, new ITransformable<String>() {
			@Override
			public String strToTarget(String str) throws Exception {
				return str;
			}
			
			@Override
			public String targetToStr(String obj) throws Exception {
				return obj;
			}
		});
	}
}
