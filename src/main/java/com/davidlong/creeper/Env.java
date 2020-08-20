package com.davidlong.creeper;

import com.davidlong.creeper.util.PathBuilder;

public class Env {
    public static final String CONFIG_DIR_PATH = new PathBuilder().project().next("config").build();

    public static final String LOG4J_PATH  = new PathBuilder().project().next("config").next("log4j.properties").build();
    public static final String USERS_PATH_QINGREN = new PathBuilder().project().next("config").next("qingren.txt").build();
    public static final String CONFIG_PATH = new PathBuilder().project().next("config").next("config.properties").build();


}
