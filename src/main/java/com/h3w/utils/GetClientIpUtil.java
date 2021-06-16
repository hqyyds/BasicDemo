
package com.h3w.utils;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:xietianjiao@h3w.com.cn">xietj</a>
 * @version 1.0
 * 2014-6-26 下午3:46:44
 */
public class GetClientIpUtil {

	public static String getRemoteIpAddr(HttpServletRequest request) {
		  String ip = request.getHeader("x-forwarded-for");
		  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		   ip = request.getHeader("Proxy-Client-IP");
		  }
		  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		   ip = request.getHeader("WL-Proxy-Client-IP");
		  }
		  if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		   ip = request.getRemoteAddr();
		  }
		  //return ip;
		  String[] ips = ip.split(",");
		  return ips[0];
		 }
	
	
}

