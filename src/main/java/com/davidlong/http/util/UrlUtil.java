package com.davidlong.http.util;

import com.davidlong.http.HttpConst;
import com.davidlong.http.model.Param;

public class UrlUtil {
    public static String host(String host){
        return checkScheme(host);
    }

    public static String host(String scheme,String host){
        return scheme + HttpConst.URI_BEGIN_IDENTIFIER + host;
    }

    public static String path(String path){
        return checkScheme(path);
    }

    public static String path(String host,String path){
        if(host.endsWith("/") && path.startsWith("/")){
            path=path.replaceFirst("/","");
        }
        return checkScheme(host+path);
    }

    public static String params(String uri,Param...params){
        StringBuilder sb=new StringBuilder(uri);
        for (Param param : params) {
            if (uri.contains("?")) {
                if(uri.endsWith("?") || uri.endsWith("&")){
                    sb.append(param.toString());
                }else{
                    sb.append("&").append(param.toString());
                }
            }else{
                sb.append("?").append(param.toString());
            }
        }
        return checkScheme(sb.toString());
    }

    public static String checkScheme(String uri){
        return uri.contains(HttpConst.SCHEME_HTTP) ?  uri : HttpConst.SCHEME_HTTP + HttpConst.URI_BEGIN_IDENTIFIER + uri;
    }
}
