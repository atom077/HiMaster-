package com.whohim.springboot;



import org.mybatis.spring.annotation.MapperScan;


import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


@SpringBootApplication
@MapperScan("com.whohim.springboot.dao")
public class Application {


    public static void main(String[] args) throws SchedulerException, IOException, InterruptedException {
        SpringApplication.run(Application.class, args);

        /**
         * 设定一个定时器每两秒检查一次数据
         */
        //创建调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //定义一个触发器
        Trigger trigger = newTrigger().withIdentity("trigger", "group") //定义名称和所属的组
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(60) //每隔2秒执行一次
                        .withRepeatCount(99999999)) //总共执行*次(第一次执行不基数)
                .build();
        //定义一个JobDetail
        JobDetail job = newJob(ReadDataJob.class) //指定干活的类MailJob
                .usingJobData("reaspberry", "HiMasterbeta0120180815") //定义属性
                .build();
        //调度加入这个job
        scheduler.scheduleJob(job, trigger);
        //启动
        scheduler.start();
        //等待4秒，让前面的任务都执行完了之后，再关闭调度器


        /**
         * 计时器
         */
    }

}