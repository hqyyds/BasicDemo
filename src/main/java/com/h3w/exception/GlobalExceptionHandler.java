package com.h3w.exception;
import com.h3w.ResultObject;
import com.h3w.enums.CommonEnum;
import io.lettuce.core.RedisCommandTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * 如果使用@RestControllerAdvice 注解
 * 则会将返回的数据类型转换成JSON格式
 * @author hyyds
 * @date 2021/6/16
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = CustomException.class)
    @ResponseBody
    public ResultObject bizExceptionHandler(HttpServletRequest req, CustomException e){
        logger.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return ResultObject.error(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, NullPointerException e){
        logger.error("发生空指针异常！原因是：{}",e);
        return ResultObject.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value =ClassNotFoundException.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, ClassNotFoundException e){
        logger.error("发生资源未找到异常！原因是：{}",e);
        return ResultObject.error(CommonEnum.NOT_FOUND);
    }

    @ExceptionHandler(value = RedisConnectionFailureException.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, RedisConnectionFailureException e){
        logger.error("Redis连接失败异常！原因是：{}",e);
        return ResultObject.error(CommonEnum.CONNECTION_FAIL);
    }

    @ExceptionHandler(value = RedisCommandTimeoutException.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, RedisCommandTimeoutException e){
        logger.error("Redis连接超时异常！原因是：{}",e);
        return ResultObject.error(CommonEnum.CONNECTION_FAIL);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, AccessDeniedException e){
        logger.error("无权限访问异常！原因是：{}",e);
        return ResultObject.error(CommonEnum.ACCESS_DEND);
    }

    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public ResultObject exceptionHandler(HttpServletRequest req, Exception e){
        logger.error("未知异常！原因是:",e);
        return ResultObject.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}
