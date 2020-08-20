package com.davidlong.creeper.util;

public class ClassUtil {
    public static Class getOutterClass(Class handleClass){
        if (handleClass.isMemberClass()) {
            return getOutterClass(handleClass.getEnclosingClass());
        }else{
            return handleClass;
        }
    }
}
