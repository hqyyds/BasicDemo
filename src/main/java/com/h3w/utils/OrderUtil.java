package com.h3w.utils;

public class OrderUtil {
    public static String creatOrderCode() {
        int r1 = (int) (Math.random() * (10));//产生2个0-9的随机数
        int r2 = (int) (Math.random() * (10));
        long now = System.currentTimeMillis();//一个13位的时间戳
        String paymentID = String.valueOf(r1) + String.valueOf(r2) + String.valueOf(now);// 订单ID
        return paymentID;
    }

    public static void main(String arg[]) {
        System.out.println(creatOrderCode());
    }
}
