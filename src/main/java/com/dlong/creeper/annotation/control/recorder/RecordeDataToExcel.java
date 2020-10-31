package com.dlong.creeper.annotation.control.recorder;

import com.dlong.creeper.model.seq.recorder.WriteStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RecordeDataToExcel {
    String dataListContextKey();
    String excelPath();
    String urlColName();
    WriteStrategy writeStrategy() default WriteStrategy.LoopEnd;
}
