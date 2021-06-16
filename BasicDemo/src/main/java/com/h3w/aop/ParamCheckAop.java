package com.h3w.aop;

import com.h3w.annotation.ParamCheck;
import com.h3w.exception.CustomException;
import com.h3w.utils.CastValueTypeUtil;
import com.h3w.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 参数效验AOP
 */
@Component
@Aspect
public class ParamCheckAop {

    /**
     * 定义有一个切入点，范围为web包下的类
     * 切入点表达式决定了用注解方式的方法切还是针对某个路径下的所有类和方法进行切，方法必须是返回void类型
     */
    @Pointcut("execution(public * com.h3w.controller..*.*(..))")
    public void checkParam() {
    }

    /**
     * 方法执行前执行
     * @param joinPoint
     */
    @Before("checkParam()")
    public void doBefore(JoinPoint joinPoint) {
    }

    @Around("checkParam()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = ((MethodSignature) pjp.getSignature());
        //得到拦截的方法
        Method method = signature.getMethod();
        //获取方法参数注解，返回二维数组是因为某些参数可能存在多个注解
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return pjp.proceed();
        }
        //获取方法参数名
        String[] paramNames = signature.getParameterNames();
        //获取参数值
        Object[] paranValues = pjp.getArgs();
        //获取方法参数类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                //如果该参数前面的注解不为空并且是ParamCheck的实例，并且notNull()=true,并且默认值为空，则进行非空校验
                if (parameterAnnotations[i][j] != null && parameterAnnotations[i][j] instanceof ParamCheck && ((ParamCheck) parameterAnnotations[i][j]).notNull() && StringUtils.isEmpty(((ParamCheck)parameterAnnotations[i][j]).defaultValue())) {
                    paramIsNull(paramNames[i], paranValues[i], parameterTypes[i] == null ? null : parameterTypes[i].getName());
                }
                //如果该参数前面的注解不为空并且是ParamCheck的实例，并且默认值不为空，并且参数值为空，则进行赋默认值
                if(parameterAnnotations[i][j] != null && parameterAnnotations[i][j] instanceof ParamCheck && !StringUtils.isEmpty(((ParamCheck)parameterAnnotations[i][j]).defaultValue()) && (paranValues[i] == null || StringUtils.isEmpty(paranValues[i].toString()))){
                    paranValues[i] = putParam(((ParamCheck)parameterAnnotations[i][j]).defaultValue(), parameterTypes[i]);
                }
                //如果该参数前面的注解不为空并且是ParamCheck的实例，并且默认值不为空，并且参数值不为空，则进行赋默认值
                if(parameterAnnotations[i][j] != null && parameterAnnotations[i][j] instanceof ParamCheck &&((ParamCheck) parameterAnnotations[i][j]).length()>0 && StringUtils.isNotBlank(paranValues[i].toString())){
                    paramLength(paramNames[i], paranValues[i],((ParamCheck) parameterAnnotations[i][j]).length());
                }
            }
        }
        return pjp.proceed(paranValues);
    }

    /**
     * 在切入点return内容之后切入内容（可以用来对处理返回值做一些加工处理）
     * TODO 留着做以后处理返回值用
     * @param joinPoint
     */
    @AfterReturning(value = "checkParam()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {

    }

    /**
     * 参数非空校验，如果参数为空，则抛出ParamIsNullException异常
     * @param paramName
     * @param value
     * @param parameterType
     */
    private void paramIsNull(String paramName, Object value, String parameterType) {
        if (value == null || "".equals(value.toString().trim())) {
            throw new CustomException(300,"请求参数名"+paramName+"，不能为空");
        }
        if ("undefined".equals(value.toString().trim())){
            throw new CustomException(300,"请求参数名"+paramName+"，不能为undefined！");
        }
    }

    private void paramLength(String paramName, Object value,int length) {
        if (value.toString().length()>length) {
            throw new CustomException(300,"请求参数名"+paramName+"，长度不能超过"+length);
        }
    }

    private Object putParam(Object value, Class<?> parameterType){
        return CastValueTypeUtil.parseValue(parameterType, value.toString());
    }

}
