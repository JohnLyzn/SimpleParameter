package com.fy.sparam.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 格式化工具类
 *
 * @author linjie
 * @since 4.5.0
 */
public class FormatUtils {
	
	/**
	 * 把列表中的null去除
	 * 
	 * @param list 列表
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public static void removeNullInList(List<?> list) {
		if(list != null && !list.isEmpty()) {
			list.removeAll(Collections.singleton(null));
		}
	}
	
	/**
	 * 把多个数组合成一个
	 * 
	 * @param main 主数组, 如果为null返回null
	 * @param arrs 要拼接上去的其它数组, 如果没有会返回主数组
	 * @return 合并完成的数组
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	@SafeVarargs
	public static <T> T[] concatArrays(T[] main, T[]...arrs) {
		if(main != null) {
			if(arrs == null || arrs.length == 0) {
				return main;
			}
			// 计算总长度
			int totalLen = main.length;
			for(T[] arr : arrs) {
				if(arr != null) {
					totalLen += arr.length;
				}
			}
			// 拼接
			int offset = main.length;
			T[] result = Arrays.copyOf(main, totalLen);
			for(T[] arr : arrs) {
				if(arr != null) {
					System.arraycopy(arr, 0, result, offset, arr.length);
					offset += arr.length;
				}
			}
			return result;
		}
		return null;
	}
	
	/**
	 * 将数组字符串"[a, b, c...]"格式化为字符串列表
	 * @param arrayStr 数组字符串
	 * @return 字符串列表
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public static List<String> arrayStr2List(String arrayStr) {
		if(arrayStr != null && !arrayStr.isEmpty()){
			arrayStr = formatArrayStr(arrayStr);
			List<String> res = new LinkedList<String>();
			for(String arrayElement : arrayStr.split(",")) {
				res.add(arrayElement);
			}
			// 去掉null
			res.removeAll(Collections.singleton(null));
			return res;
		}
		return Collections.emptyList();
	}
	
	/**
	 * 格式化字符串数组的字符串, 去掉多余的空格,多余的逗号, 左中括号, 右中括号
	 * 
	 * @param wfArrayStr 格式不正确的字符串数组
	 * @return 格式化好的字符串数组的字符串
	 *
	 * @author linjie
	 * @since 4.5.0
	 */
	public static String formatArrayStr(String wfArrayStr) {
		if(wfArrayStr != null && !wfArrayStr.isEmpty()){
			// 去掉所有' ', '[', ']', '"'
			wfArrayStr = wfArrayStr.replaceAll("\"|\\u005B|\\u005D", "");
			// 连续的多个空格合并为一个
			wfArrayStr = wfArrayStr.replaceAll("[ ]{2,}", " ");
			// 去掉逗号和正文的前后的空格
			wfArrayStr = wfArrayStr.replaceAll("[ ]+,|,[ ]+", ",");
			// 去掉相邻的的逗号
			wfArrayStr = wfArrayStr.replaceAll(",{2,}|,$|^,", ",");
			// 去掉开头和结尾的逗号
			wfArrayStr = wfArrayStr.replaceAll(",$|^,", "");
		}
		return wfArrayStr;
	}
	
	/**
	 * 禁止实例化
	 * 
	 * @author linjie
	 * @since 4.5.0
	 */
	private FormatUtils() {}
}
