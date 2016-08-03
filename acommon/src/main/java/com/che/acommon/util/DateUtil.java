package com.che.acommon.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Che on 2016/07/25.
 */
public class DateUtil {

    /**
     * 获取当前日期时间字符串
     *
     * @return
     */
    public static String getNowDateTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(now);
        return date;
    }

    /**
     * 获取当前的时分字符串
     *
     * @return
     */
    public static String getNowHourMin() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String date = sdf.format(now);
        return date;
    }

    /**
     * 获取当前的时分字符串
     *
     * @return
     */
    public static Date stringToDate(String dateString, String format) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }
}
