package com.h3w.exception;

/**
 * 基础异常类，自定义的错误描述枚举类需实现该接口
 * @author hyyds
 * @date 2021/6/16
 */
public interface BaseErrorInfoInterface {
    /** 错误码*/
    Integer getResultCode();

    /** 错误描述*/
    String getResultMsg();
}
