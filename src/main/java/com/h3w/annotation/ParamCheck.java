package com.h3w.annotation;

import org.springframework.core.annotation.Order;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数验证注解
 * @author hyyds
 * @date 2021/6/16
 *
 * @Retention 作用：标识此注解的生命周期，有三种可选值
 * 1.RetentionPolicy.SOURCE:在源文件中有效（即源文件保留）
 * 2.RetentionPolicy.CLASS:在class文件中有效（即class保留）
 * 3.RetentionPolicy.RUNTIME:在运行时有效（即运行时保留）
 *
 *  @Target 作用：标识此注解能用在什么地方
 * 1.ElementType.CONSTRUCTOR:用于构造器
 * 2.ElementType.FIELD:用于属性
 * 3.ElementType.LOCAL_VARIABLE:用于局部变量
 * 4.ElementType.METHOD:用于方法
 * 5.ElementType.PACKAGE:用于包
 * 6.ElementType.PARAMETER:用于参数
 * 7.ElementType.TYPE:用于类、接口(包括注解类型) 或enum声明
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamCheck {
    /**
     * 是否非空,默认不能为空
     */
    boolean notNull() default true;

    /**
     * 默认值
     * @return
     */
    String defaultValue() default "";

    /**
     * 最大长度
     * @return
     */
    int length() default 0;
}
