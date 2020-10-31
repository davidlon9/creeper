package com.dlong.creeper.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldUtil {
    public static List<Field> getDeclaredFieldsWithParent(Class<?> clz){
        List<Field> fields = new ArrayList<>();
        addDeclaredParentFields(clz,fields);
        fields.addAll(Arrays.asList(clz.getDeclaredFields()));
        return fields;
    }

    private static void addDeclaredParentFields(Class<?> clz, List<Field> fields){
        Class<?> superclass = clz.getSuperclass();
        if(superclass!=null){
            addDeclaredParentFields(superclass, fields);
            fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
        }
    }
}
