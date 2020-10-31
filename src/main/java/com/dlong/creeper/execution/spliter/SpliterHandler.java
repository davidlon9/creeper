package com.dlong.creeper.execution.spliter;

import java.util.List;

public interface SpliterHandler {
    Object beforeSplitExecuteHandle(List<List<Object>> splitList, Integer idx);

    Object afterSplitExecuteHandle(List<List<Object>> splitList, Integer idx, Exception e);
}
