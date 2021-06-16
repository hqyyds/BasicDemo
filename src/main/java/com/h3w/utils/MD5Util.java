package com.h3w.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;


/**
 * @author tfq
 * @datetime 2011-10-13
 */
public class MD5Util {
	private static final String HEX_NUMS_STR="0123456789abcdef";
	private static final Integer SALT_LENGTH = 0;//12;

	/** 
	* 锟斤拷16锟斤拷锟斤拷锟街凤拷转锟斤拷锟斤拷锟街斤拷锟斤拷锟斤拷 
	* @param hex 
	* @return 
	*/
	public static byte[] hexStringToByte(String hex) {
	   int len = (hex.length() / 2);
	   byte[] result = new byte[len];
	   char[] hexChars = hex.toCharArray();
	   for (int i = 0; i < len; i++) {
	    int pos = i * 2;
	    result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 
	        | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
	   }
	   return result;
	}


	/**
	* 锟斤拷指锟斤拷byte锟斤拷锟斤拷转锟斤拷锟斤拷16锟斤拷锟斤拷锟街凤拷
	* @param b
	* @return
	*/
	public static String byteToHexString(byte[] b) {
	   StringBuffer hexString = new StringBuffer();
	   for (int i = 0; i < b.length; i++) {
	    String hex = Integer.toHexString(b[i] & 0xFF);
	    if (hex.length() == 1) {
	     hex = '0' + hex;
	    }
	    hexString.append(hex);
	   }
	   return hexString.toString();
	}

	/**
	* 楠岃瘉瀵嗙爜
	* @param password
	* @param passwordInDb
	* @return
	* @throws NoSuchAlgorithmException
	* @throws UnsupportedEncodingException
	*/
	public static boolean validPassword(String password, String passwordInDb)
	    throws NoSuchAlgorithmException, UnsupportedEncodingException {
	   //锟斤拷16锟斤拷锟斤拷锟街凤拷锟绞斤拷锟斤拷锟阶拷锟斤拷锟斤拷纸锟斤拷锟斤拷锟�
	   byte[] pwdInDb = hexStringToByte(passwordInDb);
	   //锟斤拷锟斤拷锟轿憋拷
	   byte[] salt = new byte[SALT_LENGTH];
	   //锟斤拷锟轿达拷锟斤拷菘锟斤拷斜锟斤拷锟侥匡拷锟斤拷锟街斤拷锟斤拷锟斤拷锟斤拷锟斤拷取锟斤拷4
	   System.arraycopy(pwdInDb, 0, salt, 0, SALT_LENGTH);
	   //锟斤拷锟斤拷锟斤拷息摘要锟斤拷锟斤拷
	   MessageDigest md = MessageDigest.getInstance("MD5");
	   //锟斤拷锟斤拷锟斤拷荽锟斤拷锟斤拷锟较⒄拷锟斤拷锟�
	   md.update(salt);
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟捷达拷锟斤拷锟斤拷息摘要锟斤拷锟斤拷
	   md.update(password.getBytes("UTF-8"));
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟较⒄�
	   byte[] digest = md.digest();
	   //锟斤拷锟斤拷一锟斤拷锟斤拷锟捷匡拷锟叫匡拷锟斤拷锟斤拷息摘要锟侥憋拷
	   byte[] digestInDb = new byte[pwdInDb.length - SALT_LENGTH];
	   //取锟斤拷锟斤拷菘锟斤拷锌锟斤拷锟斤拷锟斤拷息摘要
	   System.arraycopy(pwdInDb, SALT_LENGTH, digestInDb, 0, digestInDb.length);
	   //锟饺较革拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷傻锟斤拷锟较⒄拷锟斤拷锟捷匡拷锟斤拷锟斤拷息摘要锟角凤拷锟斤拷同
	   if (Arrays.equals(digest, digestInDb)) {
	    //锟斤拷锟斤拷锟斤拷确锟斤拷锟截匡拷锟斤拷匹锟斤拷锟斤拷息
	    return true;
	   } else {
	    //锟斤拷锟筋不锟斤拷确锟斤拷锟截匡拷锟筋不匹锟斤拷锟斤拷息
	    return false;
	   }
	}

	/**
	* 寰楀埌瀵嗙爜
	* @param password
	* @return
	* @throws NoSuchAlgorithmException
	* @throws UnsupportedEncodingException
	*/
	public static String getEncryptedPwd(String password)
	    throws NoSuchAlgorithmException, UnsupportedEncodingException {
	   //锟斤拷锟斤拷锟斤拷芎锟侥匡拷锟斤拷锟斤拷锟斤拷锟�
	   byte[] pwd = null;
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	   SecureRandom random = new SecureRandom();
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟�
	   byte[] salt = new byte[SALT_LENGTH];
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟轿憋拷锟斤拷
	   random.nextBytes(salt);

	   //锟斤拷锟斤拷锟斤拷息摘要锟斤拷锟斤拷
	   MessageDigest md = null;
	   //锟斤拷锟斤拷锟斤拷息摘要
	   md = MessageDigest.getInstance("MD5");
	   //锟斤拷锟斤拷锟斤拷荽锟斤拷锟斤拷锟较⒄拷锟斤拷锟�
	   md.update(salt);
	   //锟斤拷锟斤拷锟斤拷锟斤拷锟捷达拷锟斤拷锟斤拷息摘要锟斤拷锟斤拷
	   md.update(password.getBytes("UTF-8"));
	   //锟斤拷锟斤拷锟较⒄拷锟斤拷纸锟斤拷锟斤拷锟�
	   byte[] digest = md.digest();

	   //锟斤拷为要锟节匡拷锟斤拷锟斤拷纸锟斤拷锟斤拷锟斤拷写锟斤拷锟轿ｏ拷锟斤拷锟皆硷拷锟斤拷锟轿碉拷锟街节筹拷锟斤拷
	   pwd = new byte[digest.length + SALT_LENGTH];
	   //锟斤拷锟轿碉拷锟街节匡拷锟斤拷锟斤拷锟斤拷傻募锟斤拷芸锟斤拷锟斤拷纸锟斤拷锟斤拷锟斤拷前12锟斤拷锟街节ｏ拷锟皆憋拷锟斤拷锟斤拷证锟斤拷锟斤拷时取锟斤拷锟斤拷
	   System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
	   //锟斤拷锟斤拷息摘要锟斤拷锟斤拷锟斤拷锟斤拷锟杰匡拷锟斤拷锟街斤拷锟斤拷锟斤拷拥锟�3锟斤拷锟街节匡拷始锟斤拷锟街斤拷
	   System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
	   //锟斤拷锟街斤拷锟斤拷锟斤拷锟绞斤拷锟斤拷芎锟侥匡拷锟斤拷转锟斤拷为16锟斤拷锟斤拷锟街凤拷锟绞斤拷目锟斤拷锟�
	   return byteToHexString(pwd);
	}
	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		System.out.println(MD5Util.getEncryptedPwd("12345678"));
		System.out.println("1321FE74D5BEC63887F25FA9E1001443F4EAF01616BDC21103".length());
		System.out.println(MD5Util.validPassword("admin", "21232f297a57a5a743894a0e4a801fc3"));
		
	}

}
