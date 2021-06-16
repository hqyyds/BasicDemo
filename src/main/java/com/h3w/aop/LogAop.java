package com.h3w.aop;

import com.h3w.annotation.Log;
import com.h3w.controller.LoginController;
import com.h3w.entity.User;
import com.h3w.service.UserService;
import com.h3w.utils.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

@Component
@Aspect
public class LogAop {
    @Autowired
    LoginController loginController;
    @Autowired
    UserService userService;

    @Pointcut("@annotation(com.h3w.annotation.Log)")
    private void pointCut(){ }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

    }

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        long beginTime = System.currentTimeMillis();
        // 执行方法
        result = pjp.proceed();
        // 执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;
        MethodSignature signature = ((MethodSignature) pjp.getSignature());
        //得到拦截的方法
        Method method = signature.getMethod();
        if(method.isAnnotationPresent(Log.class)) {
            //获取方法上注解中表明的权限
            Log log = (Log) method.getAnnotation(Log.class);
            String dataid = generateKeyBySpEL(log.dataid(), pjp);
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 获取请求 IP
            String ip = request.getRemoteAddr();
            saveLog(ip,1,log.action(),log.action(),dataid, String.valueOf(time));
        }
        return result;
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
        return expression.getValue(context).toString();
    }

    public int saveLog(String ip,Integer op,String action,String content,String dataid,String tbname){
        //保存日志
        User user = loginController.getCurrentUser();
        com.h3w.entity.Log log = new com.h3w.entity.Log();
        log.setOptype(op);
        log.setAction(action);
        log.setIp(ip);
        log.setLogtime(new Date());
        log.setUserid(user.getId());
        log.setDeptid(user.getDept().getId());
        log.setRealname(user.getRealname());
        log.setDataid(dataid);
        log.setTbname(tbname);
        log.setContent(content);
        userService.saveLog(log);
        return 0;
    }
}
