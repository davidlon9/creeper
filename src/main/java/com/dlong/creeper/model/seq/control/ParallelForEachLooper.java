package com.dlong.creeper.model.seq.control;


public class ParallelForEachLooper extends ForEachLooper {
    private Integer parallelism;

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }
}
