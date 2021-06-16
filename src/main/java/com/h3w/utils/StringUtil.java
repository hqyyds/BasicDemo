
package com.h3w.utils;

import java.math.BigDecimal;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

	public static boolean isBlank(String str){
		return str==null || "".equals(str);
	}
	
	public static boolean isNotBlank(String str){
		return str!=null && !"".equals(str);
	}
	
	public static Integer stringToInt(String intStr){
		try{
			BigDecimal d = new BigDecimal(intStr);
			int num =d.intValue();
			return num;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * java生成随机数字和字母组合
	 * @param
	 * @return
	 */
	public static String getCharAndNumr(int length) {
		String val = "";
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			// 输出字母还是数字
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			// 字符串
			if ("char".equalsIgnoreCase(charOrNum)) {
				// 取得大写字母还是小写字母
				int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (choice + random.nextInt(26));
			} else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}



	public static boolean matchReg(String str,String reg){
		// 要验证的字符串
		// 正则表达式规则
		String regEx = reg;
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		// 查找字符串中是否有匹配正则表达式的字符/字符串
		return matcher.find();
	}

	public static String matchRegString(String str,String reg){
		// 要验证的字符串
		// 正则表达式规则
		String regEx = reg;
		// 编译正则表达式
		Pattern pattern = Pattern.compile(regEx);
		// 忽略大小写的写法
		// Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		// 查找字符串中是否有匹配正则表达式的字符/字符串
		if(matcher.find()){
			return matcher.group();
		}
		return "";
	}
}

