package com.h3w.utils;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author <a href="mailto:xietianjiao@h3w.com.cn">xietj</a>
 * @version 1.0
 * 2018年1月4日 上午11:01:00
 */
public class AESUtil {
	/**
	 * @author miracle.qu
	 * @see AES算法加密明文
	 * @param data 明文
	 * @param key 密钥，长度16
	 * @param iv 偏移量，长度16
	 * @return 密文
	 */
	public static String encryptAES(String data,String key,String iv) throws Exception {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = data.getBytes();
			int plaintextLength = dataBytes.length;

			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
			}

			byte[] plaintext = new byte[plaintextLength];
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
			byte[] encrypted = cipher.doFinal(plaintext);


			return Base64.encode(encrypted).trim();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @author miracle.qu
	 * @see AES算法解密密文
	 * @param data 密文
	 * @param key 密钥，长度16
	 * @param iv 偏移量，长度16
	 * @return 明文
	 */
	public static String decryptAES(String data,String key,String iv) throws Exception {
		try
		{
			byte[] encrypted1 = Base64.decode(data);

			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original,"utf8");
			return originalString.trim();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public static void main(String[] args) throws Exception {
		String key = "!@#$%^&*87654321";
		String iv = "!@#$%^&*87654321";
		String s = AESUtil.encryptAES("李君", key, iv);
		System.out.println(s);
		String uid ="D4lfTtPMyZSQnxa8+WNABQ==";
		System.out.println(AESUtil.decryptAES(uid, key, iv));
	}
}
