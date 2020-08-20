package com.davidlong.demo.traiker.util;

public class Unicoder {
    public static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格*/
        String[] strs = unicode.split("\\\\u");
        String returnStr = "";
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr += (char) Integer.valueOf(strs[i].toLowerCase(), 16).intValue();
        }
        return returnStr;
    }

    public static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16).toUpperCase();
        }
        return returnStr;
    }

    public static String cnToUnicode(String cn,boolean chinese) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if(chinese){
                if(String.valueOf(c).matches("[\u4e00-\u9fa5]")){
                    returnStr += "\\u" + Integer.toString(c, 16).toUpperCase();
                }else{
                    returnStr +=c;
                }
            }else{
                returnStr += "\\u" + Integer.toString(c, 16).toUpperCase();
            }
        }
        return returnStr;
    }
}
