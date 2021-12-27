package com.h3w.aop;

import com.h3w.annotation.PerResource;
import com.h3w.entity.Resource;
import com.h3w.entity.Role;
import com.h3w.entity.RoleResource;
import com.h3w.service.SysService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口权限注解AOP
 *
 * @author hyyds
 * @date 2021/6/16
 */
@Component
@Aspect
public class PerResourceAop {
    @Autowired
    SysService sysService;

    @Pointcut("@annotation(com.h3w.annotation.PerResource)")
    private void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = ((MethodSignature) pjp.getSignature());
        //得到拦截的方法
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(PerResource.class)) {
            //获取方法上注解中表明的权限
            PerResource per = (PerResource) method.getAnnotation(PerResource.class);
            saveResource(per);
        }
        return pjp.proceed();
    }

    public void saveResource(PerResource per) {
        Resource res = sysService.getResourceByUrl(per.url());
        List<RoleResource> roleResources = new ArrayList<>();
        String roles = per.roles();
        String[] roleArr;
        if (roles.equals("*")) {
            List<String> rclist = new ArrayList<>();
            List<Role> list = sysService.findRoleAll();
            for (Role role : list) {
                rclist.add(role.getCode());
            }
            roleArr = rclist.toArray(new String[rclist.size()]);
        } else {
            roleArr = roles.split(",");
        }
        if (res == null) {
            res = new Resource();
            res.setRoleResources(roleResources);
        } else {
            roleResources = res.getRoleResources();
            roleResources.clear();
        }
        res.setUrl(per.url());
        res.setName(per.name());
        res.setType(per.type());
        res.setDescription(per.description());
        res.setFun(String.valueOf(per.fun()));
        res.setRoles(per.roles());
        for (int i = 0; i < roleArr.length; i++) {
            RoleResource rr = new RoleResource();
            rr.setRole(sysService.getRoleByCode(roleArr[i]));
            rr.setResource(res);
            roleResources.add(rr);
        }
        sysService.saveResource(res);
    }
}
