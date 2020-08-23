package com.dlong.creeper.util;

import java.io.File;

public class PathBuilder {
    private StringBuilder path;

    public PathBuilder() {
        this.path = new StringBuilder();
    }

    public PathBuilder project(){
        path.append(System.getProperty("user.dir"));
        return this;
    }

    public PathBuilder prev(){
        int idx;
        if((idx=path.lastIndexOf(File.separator))!=-1){
            path=new StringBuilder(path.substring(0,idx));
        }
        return this;
    }

    public PathBuilder next(String name){
        path.append(File.separator+name);
        return this;
    }

    public String build() {
        return path.toString();
    }

    public static void main(String[] args) {
        String build = new PathBuilder().project().prev().build();
        System.out.println(build);
    }
}
