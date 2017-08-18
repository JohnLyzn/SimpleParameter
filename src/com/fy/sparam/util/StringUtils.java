package com.fy.sparam.util;

/**
 * 
 * 
 * @author linjie
 * @since 1.0.1
 */
public class StringUtils {

	/**
	 * 把字符串片段对象连接为连续的字符串
	 * 
	 * @param pieces 需要连接的字符串片段(对象类型)
	 * @return 拼接的连续字符串, 不会为null(空参数情况下返回空字符串)
	 * 
	 * @author linjie
	 * @since 1.0.1
	 */
	public static String concat(Object...pieces) {
		if(pieces == null || pieces.length == 0) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		for(Object obj : pieces) {
			result.append(obj);
		}
		return result.toString();
	}
	
	
	private StringUtils() {};
}
