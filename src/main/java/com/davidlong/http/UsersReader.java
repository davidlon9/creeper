package com.davidlong.http;

import com.davidlong.http.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UsersReader {
    public static Map<String,String> qingren(){
        HashMap<String, String> map = new HashMap<>();
        try {
            FileUtil.copyIfNotExist(Env.USERS_PATH_QINGREN,"qingren.txt");
            String read = FileUtil.read(new File(Env.USERS_PATH_QINGREN));
            String[] split = read.split("\n");
            if(!"".equals(read) && split.length>0){
                for (int i = 0; i < split.length; i++) {
                    String[] up = split[i].split(",");
                    String username = up[0];
                    String password = up[1];
                    map.put(username,password);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main(String[] args) {
        Map<String, String> qingren = qingren();
        for (Map.Entry<String, String> stringStringEntry : qingren.entrySet()) {
            System.out.println(stringStringEntry.getKey()+"  "+stringStringEntry.getValue());
        }
    }
}
