package com.h3w.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 转换Object类型
 */
public class CastValueTypeUtil {

    public static Object parseValue(Class<?> parameterTypes, String value) {

        if (value == null || value.trim().length() == 0) {
            return null;
        }
        value = value.trim();

        if (Byte.class.equals(parameterTypes) || Byte.TYPE.equals(parameterTypes)) {
            return parseByte(value);
        } else if (Boolean.class.equals(parameterTypes) || Boolean.TYPE.equals(parameterTypes)) {
            return parseBoolean(value);
        }/* else if (Character.class.equals(fieldType) || Character.TYPE.equals(fieldType)) {
			 return value.toCharArray()[0];
		}*/ else if (String.class.equals(parameterTypes)) {
            return value;
        } else if (Short.class.equals(parameterTypes) || Short.TYPE.equals(parameterTypes)) {
            return parseShort(value);
        } else if (Integer.class.equals(parameterTypes) || Integer.TYPE.equals(parameterTypes)) {
            return parseInt(value);
        } else if (Long.class.equals(parameterTypes) || Long.TYPE.equals(parameterTypes)) {
            return parseLong(value);
        } else if (Float.class.equals(parameterTypes) || Float.TYPE.equals(parameterTypes)) {
            return parseFloat(value);
        } else if (Double.class.equals(parameterTypes) || Double.TYPE.equals(parameterTypes)) {
            return parseDouble(value);
        } else if (Date.class.equals(parameterTypes)) {
            return parseDate(value);
        } else {
            throw new RuntimeException("request illeagal type, type must be Integer not int Long not long etc, type=" + parameterTypes);
        }
    }

    public static Byte parseByte(String value) {
        try {
            value = value.replaceAll("　", "");
            return Byte.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseByte but input illegal input=" + value, e);
        }
    }

    public static Boolean parseBoolean(String value) {
        value = value.replaceAll("　", "");
        if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
            return Boolean.TRUE;
        } else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
            return Boolean.FALSE;
        } else {
            throw new RuntimeException("parseBoolean but input illegal input=" + value);
        }
    }

    public static Integer parseInt(String value) {
        try {
            value = value.replaceAll("　", "");
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseInt but input illegal input=" + value, e);
        }
    }

    public static Short parseShort(String value) {
        try {
            value = value.replaceAll("　", "");
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseShort but input illegal input=" + value, e);
        }
    }

    public static Long parseLong(String value) {
        try {
            value = value.replaceAll("　", "");
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseLong but input illegal input=" + value, e);
        }
    }

    public static Float parseFloat(String value) {
        try {
            value = value.replaceAll("　", "");
            return Float.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseFloat but input illegal input=" + value, e);
        }
    }

    public static Double parseDouble(String value) {
        try {
            value = value.replaceAll("　", "");
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            throw new RuntimeException("parseDouble but input illegal input=" + value, e);
        }
    }

    public static Date parseDate(String value) {
        try {
            String datePattern = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException("parseDate but input illegal input=" + value, e);
        }
    }

}
