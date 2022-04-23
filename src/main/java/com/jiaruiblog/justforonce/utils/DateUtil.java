package com.jiaruiblog.justforonce.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date getDate(String dateStr) {
        if (StringUtil.isNullOrEmpty(dateStr)) {
            return new Date(0);
        } else {
            return new Date(dateStr);
        }
    }

    public static String dateToStr(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(date);
    }
}
