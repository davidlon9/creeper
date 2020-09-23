package com.dlong.creeper.util;

public class JsoupUtil {
    public static String getVarValue(String html,String varName){
        if(html == null) return null;
        String[] split = html.split("\n");
        for (String line : split) {
            if (JsoupUtil.lineIsVarPair(line, varName)) {
                return getVarValue(line);
            }
        }
        return null;
    }

    public static String getVarValue(String varpair){
        if(varpair == null) return null;
        int i = varpair.indexOf("=");
        if(i!=-1){
            String e = varpair.substring(i+1,varpair.length());
            return removeQWrap(removeLastSemicolon(e.trim()));
        }
        return null;
    }

    public static boolean lineIsVarPair(String line,String varName){
        return line.contains("var") && line.contains(varName);
    }

    /**
     * 移除最后一个分号
     * @return
     */
    public static String removeLastSemicolon(String str){
        if(str==null) return null;
        return str.lastIndexOf(";")==str.length()-1?str.substring(0,str.length()-1):str;
    }

    /**
     * 移除包裹字符串的引号
     * @return
     */
    public static String removeQWrap(String str){
        if(str==null) return null;
        char c1 = str.charAt(0);
        char c2 = str.charAt(str.length() - 1);
        if(c1!=c2){
            return str;
        }
        if(c1=='"' || c1=='\''){
            return str.substring(1,str.length()-1);
        }
        return str;
    }
}
