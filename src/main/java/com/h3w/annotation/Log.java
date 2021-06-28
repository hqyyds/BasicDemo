package com.h3w.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志注解
 * @author hyyds
 * @date 2021/6/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {

    int optype() default 0;
    String action() default "";
    String dataid() default "";
    String tbname() default "";
    String content() default "";
}
