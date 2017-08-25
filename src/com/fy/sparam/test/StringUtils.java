package com.fy.sparam.test;

/**
 * 字符串工具类
 * 
 * @author linjie
 * @since 4.5.0
 */
public class StringUtils {
	
	/**
	 * 按传入对象从左到右顺序转换并连接成连续的字符串
	 * <br/> 调用obj.toString方法转换
	 * 
	 * @param objs 要连接的字符串
	 * @return 顺序连接好的字符串
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public static String concatAsStr(Object...objs) {
		StringBuilder sb = new StringBuilder();
		for(Object str : objs) {
			sb.append(str.toString());
		}
		return sb.toString();
	}
	
	/**
	 * 判断URL是否符合某个Pattern
	 * <br/> 只做了'*'号的匹配.
	 *  
	 * @param url 要检验的URL
	 * @param pattern 要求匹配的模式
	 * @return 是否匹配
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	public static boolean matchesUrlPattern(String url, String pattern) {
		if(pattern.contains("*")) {
			// 解析星号
			String[] parts = pattern.split("\\*");
			int lastIndex = 0;
			boolean result = false;
			for(String part : parts) {
				if(!part.isEmpty()) {
					int currentIndex = url.indexOf(part, lastIndex);
					if(currentIndex != -1) {
						lastIndex = currentIndex;
						result = true;
						continue;
					}
					// 只要有一次不匹配就不通过
					result = false;
					break;
				}
			}
			return result;
		}else {
			// 否则当为URL的尾部时则通过
			return url.endsWith(pattern);
		}
	}
}
