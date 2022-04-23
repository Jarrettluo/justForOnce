package com.jiaruiblog.justforonce.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String manyBlanksToOne(String str) {
        if(!isNullOrEmpty(str)){
            Pattern p = Pattern.compile("\\s+");
            Matcher m = p.matcher(str);
            str= m.replaceAll(" ");
        }
        return str;
    }
}