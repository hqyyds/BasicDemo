package com.h3w.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 实现Job接口
 * @author hyyds
 * @date 2021/6/16
 */
//@org.springframework.context.annotation.Configuration
//@EnableScheduling
//    @Scheduled(cron = "0 47 9 28 6 *")
public class SchedulerQuartzJob1 implements Job{

    private void before(){

    }

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
//        before();
        System.out.println("开始："+System.currentTimeMillis());
        // TODO 业务
        System.out.println("结束："+System.currentTimeMillis());
//        after();
    }

    private void after(){
        System.out.println("任务开始执行");
    }

}
