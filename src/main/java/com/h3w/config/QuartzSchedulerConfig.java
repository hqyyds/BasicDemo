package com.h3w.config;

import java.util.Date;

import com.h3w.quartz.SchedulerQuartzJob1;
import com.h3w.quartz.StartupQuartzJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 任务调度处理
 *
 * @author hyyds
 * @date 2021/6/16
 */
@Configuration
public class QuartzSchedulerConfig {
    // 任务调度
    @Autowired
    private Scheduler scheduler;

    //简单的创建定时任务的方法：使用@Scheduled创建定时任务
//    @Scheduled(cron = "1/5 * * * * ?")
//    public void testScheduled(){
//        //执行自定义方法
//        System.out.println("任务已经启动...");
//    }

    /**
     * 开始执行所有任务
     *
     * @throws SchedulerException
     */
    public void startJob() throws SchedulerException {
        initJob(scheduler);
//        startJob1(scheduler);
//        startJob2(scheduler);
        scheduler.start();
    }

    /**
     * 获取Job信息
     *
     * @param name
     * @param group
     * @return
     * @throws SchedulerException
     */
    public String getJobInfo(String name, String group) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        return String.format("time:%s,state:%s", cronTrigger.getCronExpression(),
                scheduler.getTriggerState(triggerKey).name());
    }

    /**
     * 修改某个任务的执行时间
     *
     * @param name
     * @param group
     * @param time
     * @return
     * @throws SchedulerException
     */
    public boolean modifyJob(String name, String group, String time) throws SchedulerException {
        Date date = null;
        TriggerKey triggerKey = new TriggerKey(name, group);
        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        String oldTime = cronTrigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(time)) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                    .withSchedule(cronScheduleBuilder).build();
            date = scheduler.rescheduleJob(triggerKey, trigger);
        }
        return date != null;
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 暂停某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void pauseJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.pauseJob(jobKey);
    }

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 恢复某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void resumeJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.resumeJob(jobKey);
    }

    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     * @throws SchedulerException
     */
    public void deleteJob(String name, String group) throws SchedulerException {
        JobKey jobKey = new JobKey(name, group);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null)
            return;
        scheduler.deleteJob(jobKey);
    }

    //启动执行，只执行一次
    private void initJob(Scheduler scheduler) throws SchedulerException {

        // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
        // JobDetail 是具体Job实例
        JobDetail jobDetail = JobBuilder.newJob(StartupQuartzJob.class).withIdentity("initjob", "group1").build();
        SimpleTrigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("job1", "group1")
                .startAt(new Date())
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(3)
                                .withRepeatCount(0))//重复执行的次数，因为加入任务的时候马上执行了，所以不需要重复，否则会多一次。
                .build();
        scheduler.scheduleJob(jobDetail, simpleTrigger);
    }

    private void startJob1(Scheduler scheduler) throws SchedulerException {
        // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
        // JobDetail 是具体Job实例
        JobDetail jobDetail = JobBuilder.newJob(SchedulerQuartzJob1.class).withIdentity("job1", "group1").build();
        // 基于表达式构建触发器
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0/5 * * * * ?");
        // CronTrigger表达式触发器 继承于Trigger
        // TriggerBuilder 用于构建触发器实例
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("job1", "group1")
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

//    private void startJob2(Scheduler scheduler) throws SchedulerException {
//        JobDetail jobDetail = JobBuilder.newJob(SchedulerQuartzJob2.class).withIdentity("job2", "group2").build();
//        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?");
//        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("job2", "group2")
//                .withSchedule(cronScheduleBuilder).build();
//        scheduler.scheduleJob(jobDetail, cronTrigger);
//    }
}
