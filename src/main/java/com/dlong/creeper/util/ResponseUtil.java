package com.dlong.creeper.util;

import org.apache.http.HttpResponse;

public class ResponseUtil {
    public static boolean isSuccess(HttpResponse response){
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200 || (statusCode>200 && statusCode<300);
    }
}
