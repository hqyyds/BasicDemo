package com.h3w.quartz;

import com.h3w.config.QuartzSchedulerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 项目启动时执行定时任务
 */
@Component
public class MyStartupRunner implements CommandLineRunner {

    @Autowired
    public QuartzSchedulerConfig scheduleJobs;

    @Override
    public void run(String... args) throws Exception {
//        scheduleJobs.startJob();
//        System.out.println(">>>>>>>>>>>>>>>定时任务开始执行<<<<<<<<<<<<<");
    }
}
