package com.davidlong.http.resolver.util;

public class WrapUtil {
    public static String enBrackets(String str,boolean space){
        return "["+space(space)+str+space(space)+"]";
    }

    public static String enAngleBrackets(String str,boolean space){
        return "<"+space(space)+str+space(space)+">";
    }

    public static String enMinus(String str,boolean space){
        return "-"+space(space)+str+space(space)+"-";
    }

    public static String enDoubleQuote(String str,boolean space){
        return "\""+space(space)+str+space(space)+"\"";
    }

    public static String enQuote(String str,boolean space){
        return "'"+space(space)+str+space(space)+"'";
    }

    public static String enBrackets(String str){
        return enBrackets(str,false);
    }

    public static String enAngleBrackets(String str){
        return enAngleBrackets(str,false);
    }

    public static String enDoubleQuote(String str){
        return enDoubleQuote(str,false);
    }

    public static String enQuote(String str){
        return enQuote(str,false);
    }

    public static String enMinus(String str){
        return enMinus(str,false);
    }

    private static String space(boolean b){
        return b?" ":"";
    }
}
