package com.h3w.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoRepeatSubmit {
    /**
     * 锁的时间，默认为缓存的时间
     * @return
     */
    int seconds() default 5;
    /**
     * 参数
     * @return
     */
    String param() default "";
}
