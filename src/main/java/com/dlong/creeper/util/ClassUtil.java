package com.dlong.creeper.util;

public class ClassUtil {
    public static Class getOutterClass(Class clz){
        if (clz.isMemberClass()) {
            return getOutterClass(clz.getEnclosingClass());
        }else{
            return clz;
        }
    }
}
