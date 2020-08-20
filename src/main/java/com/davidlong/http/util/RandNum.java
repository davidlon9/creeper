package com.davidlong.http.util;

import java.util.Random;

public class RandNum {
    public static Integer nextInt(int min,int max){
        return min+new Random().nextInt(max-min);
    }
}
