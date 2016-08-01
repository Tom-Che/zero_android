package com.che.acommon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Che on 2016/07/25.
 */
public class DateUtil {
    public static String getNowTime() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(now);
        return date;
    }


    public static String getNowHourMin() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String date = sdf.format(now);
        return date;
    }
}
