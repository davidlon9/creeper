package com.dlong.creeper;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.Random;

public class UserAgents {
    public static final Random RANDOM=new Random();

    public static final Header[] USER_AGENTS={
            //Chrome – win10
            new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36"),
            //Chrome 17.0 – MAC
            new BasicHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11"),
            //Chrome – win7
            new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1"),
            //QQ浏览器
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)"),
            //360
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)"),
            //搜狗浏览器 1.x
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)"),
            //傲游
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)"),
            //WinXP+ie6
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"),
            //WinXP+ie7
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)"),
            //WinXP+ie8
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; GTB7.0)"),
            //ie6
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)"),
            //ie7
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)"),
            //ie8
            new BasicHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)"),
            //ie9
            new BasicHeader("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"),
            //ie11
            new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko"),
            //Opera 11.11 – Windows
            new BasicHeader("User-Agent","Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11"),
            //Opera 11.11 – MAC
            new BasicHeader("User-Agent","Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11"),
            //safari 5.1 – Windows
            new BasicHeader("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50"),
            //safari 5.1 – MAC
            new BasicHeader("User-Agent","Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50"),
            //Firefox Win7:
            new BasicHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1"),
            //Firefox 4.0.1 – MAC
            new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0) Gecko/20100101 Firefox/6.0")
    };

    public static Header getRandomUserAgent(){
        return USER_AGENTS[RANDOM.nextInt(USER_AGENTS.length)];
    }
}
