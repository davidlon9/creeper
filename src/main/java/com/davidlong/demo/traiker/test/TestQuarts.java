package com.davidlong.demo.traiker.test;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class TestQuarts {
    public static void main(String[] args) {
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class)
                .withDescription("调用JobDemo")
                .withIdentity("Job's name", "Job's Group")
                .build();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("count",new AtomicInteger(0));
        CalendarIntervalTrigger  trigger = TriggerBuilder.newTrigger().withIdentity("trigger1","triggerGroup1")
                .startAt(new Date())
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule().withInterval(1, DateBuilder.IntervalUnit.SECOND))
                .build();

        try {
            boolean parallel=true;
            Scheduler defaultScheduler = new StdSchedulerFactory().getScheduler();
            synchronized (defaultScheduler){
                defaultScheduler.scheduleJob(jobDetail,trigger);
                defaultScheduler.start();
                if(!parallel){
                    defaultScheduler.wait();
                }
            }
            System.out.println("end");
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

   public static class HelloJob implements Job {

        public HelloJob() {
        }

        public void execute(JobExecutionContext context)
                throws JobExecutionException
        {
            System.err.println("Hello!  DefaultJob is executing.");

            JobDataMap mergedJobDataMap = context.getJobDetail().getJobDataMap();
            AtomicInteger count = (AtomicInteger) mergedJobDataMap.get("count");
            int i = count.addAndGet(1);
            synchronized (context.getScheduler()){
                if(i>=5){
                    shutdown(context);
                }
            }
        }

       private void shutdown(JobExecutionContext context) {
           try {
               context.getScheduler().shutdown();
           } catch (SchedulerException e) {
               e.printStackTrace();
           }finally {
               context.getScheduler().notify();
           }
       }
   }
}
