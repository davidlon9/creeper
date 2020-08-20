package com.davidlong.creeper.execution.looper;

import com.davidlong.creeper.annotation.control.ExecutionMode;
import com.davidlong.creeper.exception.ExecutionException;
import com.davidlong.creeper.execution.base.LoopableExecutor;
import com.davidlong.creeper.model.ExecutionResult;
import com.davidlong.creeper.model.LoopExecutionResult;
import com.davidlong.creeper.model.Multiple;
import com.davidlong.creeper.model.seq.LoopableEntity;
import com.davidlong.creeper.model.seq.RequestChainEntity;
import com.davidlong.creeper.model.seq.control.ScheduleLooper;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleExecuteLooper<T extends LoopableEntity> extends BaseExecuteLooper<T> {
    private static Logger logger=Logger.getLogger(ScheduleExecuteLooper.class);
    private static Map<String,DateBuilder.IntervalUnit> intervalUnitMap=new HashMap<>(10);

    static {
        intervalUnitMap.put("MILLISECOND",DateBuilder.IntervalUnit.MILLISECOND);
        intervalUnitMap.put("SECOND",DateBuilder.IntervalUnit.SECOND);
        intervalUnitMap.put("MINUTE",DateBuilder.IntervalUnit.MINUTE);
        intervalUnitMap.put("HOUR",DateBuilder.IntervalUnit.HOUR);
        intervalUnitMap.put("DAY",DateBuilder.IntervalUnit.DAY);
        intervalUnitMap.put("MONTH",DateBuilder.IntervalUnit.MONTH);
        intervalUnitMap.put("YEAR",DateBuilder.IntervalUnit.YEAR);
    }

    public ScheduleExecuteLooper(LoopableExecutor<T> executor) {
        super(executor,ScheduleLooper.class);
    }

    @Override
    public LoopExecutionResult<T> doLoop(T loopableEntity) throws ExecutionException, IOException {
        ScheduleLooper looper = (ScheduleLooper) loopableEntity.getLooper();
        ScheduleLooper.Trigger looperTrigger = looper.getTrigger();
        Long startTime = getContext().getExpressionParser().parse(looperTrigger.getStartTimeExpr(), Long.class);
        Long endTime = getContext().getExpressionParser().parse(looperTrigger.getEndTimeExpr(), Long.class);
        LoopExecutionResult<T> loopResult = new LoopExecutionResult<>(loopableEntity);

        String name = loopableEntity.getName();
        RequestChainEntity parent = loopableEntity.getParent();
        String groupName;
        if(parent!=null){
            groupName="Group-"+parent.getName();
        }else{
            groupName="Group-root";
        }
        JobDetail jobDetail = JobBuilder.newJob(DefaultJob.class).withDescription("Job-"+name).withIdentity("Job-"+name, "Job-"+groupName).build();

        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        jobDataMap.put("executor",executor);
        jobDataMap.put("entity",loopableEntity);
        jobDataMap.put("result",loopResult);
        jobDataMap.put("repeatForever",looperTrigger.isRepeatForever());
        jobDataMap.put("repeatCount",looperTrigger.getRepeatCount());
        jobDataMap.put("count",new AtomicInteger(0));

        CalendarIntervalTrigger trigger = buildTrigger(looperTrigger, startTime, endTime, name, groupName);

        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            synchronized (scheduler) {
                scheduler.scheduleJob(jobDetail,trigger);
                scheduler.startDelayed(looperTrigger.getDelay());
                if(looper.getExecutionMode()== ExecutionMode.SEQUENTIAL){
                    //顺序执行，等待Schedule执行结束
                    logger.info("waiting for scheduler to finish job");
                    scheduler.wait();
                    logger.info("ScheduleExecuteLooper finished!!!");
                }else{
                    //并行执行，默认执行下一Seq
                    loopResult.setNextSeq(getContext().getSequntialFinder().findNextSeq(loopableEntity));
                    logger.info("ScheduleExecuteLooper finished!!!");
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return loopResult;
    }

    private CalendarIntervalTrigger buildTrigger(ScheduleLooper.Trigger looperTrigger, Long startTime, Long endTime, String name, String groupName) {
        TriggerBuilder<CalendarIntervalTrigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity("Trigger-" + name, "Trigger-"+groupName)
                .startAt(new Date(startTime))
                .withSchedule(CalendarIntervalScheduleBuilder.calendarIntervalSchedule()
                        .withInterval(looperTrigger.getTimeInterval(), getIntervalUnit(looperTrigger.getIntervalUnit()))
                        .withMisfireHandlingInstructionDoNothing()
                );
        if(endTime!=null){
            triggerBuilder.endAt(new Date(endTime));
        }
        return triggerBuilder.build();
    }

    private DateBuilder.IntervalUnit getIntervalUnit(String intervalUnit){
        return intervalUnitMap.get(intervalUnit);
    }

    @Override
    public boolean isIgnoreParallelThreadHandle() {
        return true;
    }

    public static class DefaultJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException{
            JobDataMap dataMap = context.getMergedJobDataMap();
            Scheduler scheduler = context.getScheduler();

            LoopExecutionResult result = (LoopExecutionResult) dataMap.get("result");
            LoopableEntity entity = (LoopableEntity) dataMap.get("entity");

            Multiple multiple = entity instanceof Multiple ? (Multiple) entity:null;
            if (isMultipleShutdown(multiple)) {
                result.setOtherThreadSuccessed(true);
                logger.warn("ScheduleExecuteLooper is shutdown probably cause by other thread successed!");
                synchronized (scheduler) {
                    shutdown(scheduler);
                }
            }

            LoopableExecutor executor = (LoopableExecutor) dataMap.get("executor");
            boolean repeatForever = dataMap.getBoolean("repeatForever");
            int repeatCount =  dataMap.getInt("repeatCount");
            AtomicInteger atomicInteger = (AtomicInteger) dataMap.get("count");

            try {
                int count = atomicInteger.addAndGet(1);
                logger.info("* Loop "+atomicInteger.get()+" of "+entity+" will be execute by ScheduleExecuteLooper");

                ExecutionResult innerResult = executor.doExecute(entity);
                result.addLoopResult(innerResult);

                synchronized (scheduler){
                    if(!repeatForever && (count>=repeatCount || isBreak(innerResult))){
                        result.setNextSeq(innerResult.getNextSeq());
                        result.setLoopOver(true);
                        shutdown(scheduler);
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        private void shutdown(Scheduler scheduler) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                e.printStackTrace();
            }finally {
                scheduler.notify();
            }
        }
    }
}
