
package com.h3w;

import com.h3w.utils.AESUtil;

import java.util.ResourceBundle;

/**
 * ϵͳ����
 * @author <a href="mailto:xietianjiao@h3w.com.cn">xietj</a>
 * @version 1.0
 * 2016-5-13 ����1:24:39
 */
public class StaticConstans {
	public static ResourceBundle bundle = null;

	public static final String SESSION_CODE_KEY = "code";
	public static final String SESSION_USER_KEY = "sys_user";
	public static final String COOKIE_AUTO_ID = "ts_auto_id";
	public static final String PASSWORD_INIT = "123456";//初始密码

	public static final Integer STATUS_0 = 0;
	public static final Integer STATUS_1 = 1;
	public static final Integer STATUS_2 = 2;
	public static Long exptime = null;

	static{
		bundle = ResourceBundle.getBundle( "constant");
		String timestr = bundle.getString("exptime");
		String time;
		try {
			String key ="&^%$#@*~87654321";
			String iv = "&^%$#@*~87654321";
			time = AESUtil.decryptAES(timestr, key, iv);
			exptime = Long.valueOf(time);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getProperty(String key){
		return bundle.getString(key);
	}
}

