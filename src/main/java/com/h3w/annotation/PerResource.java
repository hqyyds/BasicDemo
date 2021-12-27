package com.h3w.annotation;

import com.h3w.enums.FunEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口权限注解
 *
 * @author hyyds
 * @date 2021/6/16
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerResource {
    String url() default "";

    String name() default "";

    int type() default 1;

    FunEnum fun() default FunEnum.SELECT;

    String description() default "";

    String roles() default "";
}
