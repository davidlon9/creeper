package com.dlong.creeper;

import com.dlong.creeper.control.*;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static List<Class<? extends MoveAction>> needRecordActions = Arrays.asList(ForwardAction.class, JumpAction.class, BreakAction.class, ContinueAction.class);
}
