package com.h3w.quartz;

import com.alibaba.fastjson.JSON;
import com.h3w.annotation.PerResource;
import com.h3w.entity.Resource;
import com.h3w.entity.Role;
import com.h3w.entity.RoleResource;
import com.h3w.service.SysService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 实现Job接口
 *
 * @author yvan
 */
public class StartupQuartzJob implements Job {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    SysService sysService;


    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("初始化开始：" + System.currentTimeMillis());
        initApiResource();
        System.out.println("初始化结束：" + System.currentTimeMillis());
    }


    //初始化接口权限
    public void initApiResource() {
        System.out.println("初始化接口权限开始——————————————————————");
        // TODO 业务
        //获取使用RestController注解的所有controller层类
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);//获取到 demo1Controller -> {Demo1Controller@5595}
        for (Map.Entry<String, Object> entry : controllers.entrySet()) {//遍历每个controller层
//            System.out.println(entry.getKey());//demo1Controller
            Object value = entry.getValue();
            Class<?> aClass = AopUtils.getTargetClass(value);//获取class
//            System.out.println(aClass.isAnnotationPresent(RequestMapping.class));//true
//            RequestMapping annotation = aClass.getAnnotation(RequestMapping.class);//获取注解详情
//            RequestMapping declaredAnnotation = aClass.getDeclaredAnnotation(RequestMapping.class);
//            PerResource per = (PerResource) aClass.getAnnotation(PerResource.class);
//            List<Method> methods = Arrays.asList(aClass.getMethods());//获取包含父级所有方法
            List<Method> declaredMethods = Arrays.asList(aClass.getDeclaredMethods());//获取当前类方法
            for (int i = 0; i < declaredMethods.size(); i++) {
                //判断这个方法有没有这个注解
                if (declaredMethods.get(i).isAnnotationPresent(PerResource.class)) {
                    PerResource per = declaredMethods.get(i).getAnnotation(PerResource.class);
                    System.out.println(JSON.toJSONString(per));
                    saveResource(per);
                }
            }
        }
        System.out.println("初始化接口权限结束——————————————————————");
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
