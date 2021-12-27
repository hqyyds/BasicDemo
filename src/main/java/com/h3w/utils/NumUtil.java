package com.h3w.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理数字的Util、计算百分比
 *
 * @author hyyds
 * @date 2021/6/16
 */
public class NumUtil {
    /**
     * 转换为BigDecimal
     *
     * @param o
     * @return BigDecimal
     * @author fantasy
     * @date 2013-8-27
     */
    public static BigDecimal toBig(Object o) {
        if (o == null || o.toString().equals("") || o.toString().equals("NaN")) {
            return new BigDecimal(0);
        }
        return new BigDecimal(o.toString());
    }

    /**
     * 计算百分比
     *
     * @param divisor
     * @param dividend
     * @return String
     * @author fantasy
     * @date 2013-8-27
     */
    public static String getPercent(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return "";
        }
        NumberFormat percent = NumberFormat.getPercentInstance();
        //建立百分比格式化引用
        percent.setMaximumFractionDigits(2);
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0)) || a.equals(toBig(0.0)) || b.equals(toBig(0.0))) {
            return "0.00%";
        }
        BigDecimal c = a.divide(b, 4, BigDecimal.ROUND_DOWN);
        return percent.format(c);
    }

    //根据sql算的百分比格式化
    public static String formatPercent(Object divisor) {
        NumberFormat percent = NumberFormat.getPercentInstance();
        BigDecimal c = toBig(divisor);
        BigDecimal setScale = c.divide(toBig(100));
        return percent.format(setScale);
    }

    /**
     * 计算百分比返回int
     *
     * @param divisor
     * @param dividend
     * @return
     */
    public static Integer getPercentInt(Object divisor, Object dividend) {
        BigDecimal c = getPercentBigDecimal(divisor, dividend);
        return c.intValue();
    }

    public static Float getPercentFloat(Object divisor, Object dividend) {
        BigDecimal c = getPercentBigDecimal(divisor, dividend);
        return c.floatValue();
    }

    public static BigDecimal getPercentBigDecimal(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return toBig(0);
        }
        NumberFormat percent = NumberFormat.getPercentInstance();
        //建立百分比格式化引用
        percent.setMaximumFractionDigits(2);
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0)) || a.equals(toBig(0.0)) || b.equals(toBig(0.0))) {
            return toBig(0);
        }
        BigDecimal c = a.divide(b, 4, BigDecimal.ROUND_DOWN);
        c = c.multiply(toBig(100));
        return c;
    }

    /**
     * 计算比例
     *
     * @param divisor
     * @param dividend
     * @return String
     * @author fantasy
     * @date 2013-10-9
     */
    public static String divideNumber(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return "";
        }
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0))) {
            return "0";
        }
        BigDecimal c = a.divide(b, 2, BigDecimal.ROUND_DOWN);
        return c.toString();
    }

    /**
     * 去两个数的平均值，四舍五入
     *
     * @param divisor
     * @param dividend
     * @return int
     * @author fantasy
     * @date 2013-11-6
     */
    public static int averageNumber(Object divisor, Object dividend) {
        if (divisor == null || dividend == null) {
            return 0;
        }
        BigDecimal a = toBig(divisor);
        BigDecimal b = toBig(dividend);
        if (a.equals(toBig(0)) || b.equals(toBig(0))) {
            return 0;
        }
        BigDecimal c = a.divide(b, 0, BigDecimal.ROUND_HALF_UP);
        return c.intValue();
    }

    /**
     * 计算所有的平均值
     *
     * @param objects
     * @return
     */
    public static BigDecimal averageNumber(List<Object> objects) {
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) != null) {
                sum = sum.add(toBig(objects.get(i)));
            }
        }
        if (sum.equals(toBig(0))) {
            return toBig(0);
        }
        System.out.println("总和：" + sum);
        BigDecimal c = sum.divide(toBig(objects.size()), 0, BigDecimal.ROUND_HALF_UP);
        return c;
    }

    //保留小数位
    public static String getFloatSring(Float f) {
        //Float totalvalue = (float)(Math.round(total*100))/100;
        //DecimalFormat   fnum  =   new  DecimalFormat("##0.00");
        DecimalFormat fnum = new DecimalFormat("##0");
        String dd = fnum.format(f);
        return dd;
    }

    /**
     * 判断是否为数字，包括整数和浮点数
     *
     * @param str 传入的字符串
     * @return 是整数返回true, 否则返回false
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[0-9]*.\\d*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    /**
     * 最大值
     *
     * @param score
     * @param basicvalue
     * @param maxscore
     * @return
     */
    public Integer getBigDecimalScore(BigDecimal score, BigDecimal basicvalue, BigDecimal maxscore) {
        score = score.add(basicvalue);
        if (score.compareTo(maxscore) != 1) {
            return score.intValue();
        } else {
            return maxscore.intValue();
        }
    }

    /**
     * 不够位数的在前面补0，保留code的长度位数字
     *
     * @param code
     * @param i    加量
     * @return
     */
    public static String autoGenericCode(String code, int i) {
        String result = "";
        // 保留code的位数
        result = String.format("%0" + code.length() + "d", Integer.parseInt(code) + i);

        return result;
    }
}
