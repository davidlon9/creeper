package com.dlong.creeper.resolver.looper;

import com.dlong.creeper.annotation.control.looper.scheduler.Scheduler;
import com.dlong.creeper.exception.AnnotationNotFoundException;
import com.dlong.creeper.model.seq.control.ScheduleLooper;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Map;

public class ScheduleLooperResolver extends BaseLooperResolver<ScheduleLooper>{
    public ScheduleLooperResolver(AnnotatedElement target) {
        super(target,Scheduler.class);
    }

    @Override
    public ScheduleLooper resolve() throws AnnotationNotFoundException {
        ScheduleLooper scheduleLooper = super.resolve(new ScheduleLooper());

        Map<String, Object> looperAttributes = AnnotationUtils.getAnnotationAttributes(getAnnotation());

        Annotation triggerAnno = (Annotation) looperAttributes.get("trigger");
        Map<String,Object> triggerAttributes = AnnotationUtils.getAnnotationAttributes(triggerAnno);

        String startTimeExpr = (String) triggerAttributes.get("startTimeExpr");
        String endTimeExpr = (String) triggerAttributes.get("endTimeExpr");
        Integer timeInterval = (Integer) triggerAttributes.get("timeInterval");
        String intervalUnit = (String) triggerAttributes.get("intervalUnit");
        Integer repeatCount = (Integer) triggerAttributes.get("repeatCount");
        Boolean repeatForever = (Boolean) triggerAttributes.get("repeatForever");
        Integer delay = (Integer) triggerAttributes.get("delay");

        ScheduleLooper.Trigger trigger = new ScheduleLooper.Trigger();
        trigger.setStartTimeExpr(startTimeExpr);
        trigger.setEndTimeExpr(endTimeExpr);
        trigger.setTimeInterval(timeInterval);
        trigger.setIntervalUnit(intervalUnit);
        trigger.setRepeatCount(repeatCount);
        trigger.setRepeatForever(repeatForever);
        trigger.setDelay(delay);
        scheduleLooper.setTrigger(trigger);
        return scheduleLooper;
    }
}
