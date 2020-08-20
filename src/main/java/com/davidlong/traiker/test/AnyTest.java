package com.davidlong.traiker.test;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class AnyTest {
    public static void main(String[] args) {
        Executor executor = Executor.newInstance();
        try {
            Content content = executor.execute(Request.Get("http://api.web.21ds.cn/platform/getTbCategory?apkey=7925568b-3833-7d4a-713d-0b79058e02fc&cid=1625")).returnContent();
            System.out.println(content.asString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
