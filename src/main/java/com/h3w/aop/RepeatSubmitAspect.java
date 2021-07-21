package com.h3w.aop;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.h3w.annotation.NoRepeatSubmit;
import com.h3w.exception.CustomException;
import com.h3w.utils.RedisUtil;
import com.h3w.utils.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 重复提交AOP
 * @author hyyds
 * @date 2021/6/16
 */
@Aspect
@Component
public class RepeatSubmitAspect {


    @Autowired
    HttpServletRequest request;
    @Autowired
    RedisUtil redisUtil;
    private static final Cache<String, Object> CACHES = CacheBuilder.newBuilder()
            // 最大缓存 100 个
            .maximumSize(1000)
            // 设置写缓存后 5 秒钟过期
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Pointcut("@annotation(noRepeatSubmit)")
    public void pointCut(NoRepeatSubmit noRepeatSubmit) {
    }

    @Around("pointCut(noRepeatSubmit)")
    public Object around(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) throws Throwable {
        // 此处可以用token或者JSessionId
        String token = request.getHeader("token");
        String path = request.getServletPath()+"/";

        if(StringUtil.isNotBlank(noRepeatSubmit.param())){
            path += generateKeyBySpEL(noRepeatSubmit.param(), pjp);
        }
        String key = getKey(token, path);
        if (StringUtil.isNotBlank(key)) {
            if (CACHES.getIfPresent(key) != null) {
                throw new CustomException(500,"操作过于频繁，请稍后重试");
            }
            // 如果是第一次请求,就将 key 当前对象压入缓存中
            CACHES.put(key, key);
            //可用redis设置缓存
//            redisUtil.set(key,key,lockTime);
        }
        return pjp.proceed();
//        try {
//            return pjp.proceed();
//        } finally {
//            // 手动将缓存清除,实际情况下只要接口处理完成就可以执行下一次请求
////            CACHES.invalidate(key);
//        }
    }

    private String getKey(String token, String path) {
        return token + path;
    }

    //解析El表达式
    public String generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        Expression expression = parser.parseExpression(spELString);
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        Object o = expression.getValue(context);
        return o!= null?o.toString():"";
    }
}
